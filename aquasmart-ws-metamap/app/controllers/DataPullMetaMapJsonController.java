package controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import aquasmart.services.datapull.CachingRESTDatasetPullService;
import aquasmart.services.datapull.DatasetPullService;
import aquasmart.services.datapull.RESTDatasetPullService;
import aquasmart.services.datapull.beans.metaschema.DatasetMetadata;
import aquasmart.services.metamap.beans.Concept;
import aquasmart.services.metamap.beans.Domain;
import aquasmart.services.metamap.beans.OrderedConceptSet;
import aquasmart.services.metamap.beans.Outlier;
import aquasmart.services.metamap.beans.SpeciesList;
import aquasmart.services.metamap.beans.TimeFormat;
import aquasmart.services.metamap.handlers.MetaMappingHandler;
import aquasmart.services.metamap.handlers.MetaMappingHandlerImpl;
import aquasmart.services.datapull.beans.dataschema.*;
import aquasmart.services.metamap.model.TemporaryModel;
import aquasmart.services.metamap.model.exception.InexistentAttributeException;
import aquasmart.services.metamap.stats.Frequency;
import aquasmart.services.metamap.utils.STATIC;
import aquasmart.services.metamap.utils.STATIC.DatasetService;
import aquasmart.services.metamap.utils.STATIC.Domains;
import aquasmart.services.metamap.utils.STATIC.Json;
import aquasmart.services.metamap.utils.STATIC.Json.INPUT;
import utils.http.HttpUtils;
import utils.json.JSON;
import play.*;
import play.mvc.*;
import play.mvc.Http.Request;
import utils.tuples.Tuple2;
import utils.tuples.Tuple3;


/**
 * Controller responsible for implementing the meta-mapping
 * operations and exposing them through an HTTP-JSON interface
 * @author Pedro Amaral
 *
 */
public class DataPullMetaMapJsonController extends Controller 
{
	
	public void setCookie( Request req )
	{
		Map<String,String[]> heads = req.headers();

		HttpUtils.currentCookie = null;
		String[] cooks = heads.get("Cookie");
		if(cooks != null)
			for( String head : cooks ) 
			{
				if(HttpUtils.currentCookie == null)
					HttpUtils.currentCookie = head;
				else
					HttpUtils.currentCookie = " ;"+head;
			}
	}
	
	public Result index(String datasetID)
	{
		return ok( views.html.pull.main.render( datasetID, datasetID, datasetID) );
	}
       
    /**
     * Creates a new concept in the ontology
     * @return the URI of that concept, encoded in JSON format
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result createConcept() 
    {
    	
    	JsonNode request = request().body().asJson();
    	setCookie( request() );
    	String name = request.get(Json.INPUT.CREATE_CONCEPT.NAME).asText(),
    			desc = request.get(Json.INPUT.CREATE_CONCEPT.DESCRIPTION).asText(),
    			lang = request.get(Json.INPUT.CREATE_CONCEPT.LANGUAGE).asText();
    	boolean isAnonymous = request.get(Json.INPUT.CREATE_CONCEPT.ANONYMOUS).asBoolean();
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	String conceptURI = handler.createConcept(name, desc, lang, isAnonymous);
    	
    	JsonNode result = JSON.newObject(STATIC.Json.OUTPUT.CREATE_CONCEPT.URI, conceptURI);
    	
        return ok(result);
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result getConceptInfo()
    {
    	setCookie( request() );
    	JsonNode request = request().body().asJson();
    	String conceptURI = request.get(INPUT.CONCEPT_INFO.URI).asText();
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	Concept concept = handler.getConcept(conceptURI);
    	
    	return ok(concept.toJson());
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result createAttribute() throws ParseException, FileNotFoundException, ClassNotFoundException, IOException 
    {
    	setCookie( request() );
    	JsonNode request = request().body().asJson();
    	String name = request.get(Json.INPUT.CREATE_ATTRIBUTE.NAME).asText(),
    			attributeURI = request.get(Json.INPUT.CREATE_ATTRIBUTE.CONCEPT_URI).asText(),
    			domainType = request.get(Json.INPUT.CREATE_ATTRIBUTE.DOMAIN_TYPE).asText();
    	JsonNode domainJson = request.get(Json.INPUT.CREATE_ATTRIBUTE.DOMAIN);
    	Domain domain = Domain.fromJson(domainType, domainJson);
    	

    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	String attrURI = handler.
    			createAttribute(name, attributeURI, domainType, domain);
    	
    	JsonNode result = JSON.newObject(
    			Json.OUTPUT.CREATE_ATTRIBUTE.ATTRIBUTE_URI, attrURI);
    	
        return ok(result);
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result updateSpeciesValues() throws ParseException, FileNotFoundException, ClassNotFoundException, IOException 
    {
    	setCookie( request() );
    	JsonNode request = request().body().asJson();
    	JsonNode allUpdates = request.get( Json.INPUT.UPDATE_SPECIES.SPECIES);
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	
    	for( JsonNode species : allUpdates )
    	{
    		String speciesURI = species.get( Json.INPUT.UPDATE_SPECIES.SPECIES_URI ).asText();
    		JsonNode values = species.get( Json.INPUT.UPDATE_SPECIES.VALUES );
    		for( JsonNode value : values )
    			handler.updateSpeciesValue( speciesURI, value.asText() );
    	}
    	TemporaryModel.getInstance().commit();
    	
        return ok( utils.json.JSON.newObject("code","200") );
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result getAttributeInfo()
    {
    	setCookie( request() );
    	String datasetAttributeID =
    			request().body().asJson().get(
    					Json.INPUT.ATTRIBUTE_INFO.URI ).asText();
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	String conceptURI = handler.getAttributeInfo(datasetAttributeID);
    	
    	JsonNode result = JSON.newObject(
    			Json.OUTPUT.ATTRIBUTE_INFO.CONCEPT , conceptURI);
    	
    	return ok(result);
    			
    }
    public static boolean debug = true;
    /**
     * Associates a dataset to an attribute name, index and semantic concept
     * @return the URI for the newly created dataset attribute 
     * (connection between dataset and attribute)
     * @throws IOException 
     * @throws ClassNotFoundException 
     * @throws FileNotFoundException 
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result associateAttributeToDataset() throws FileNotFoundException, ClassNotFoundException, IOException 
    {
    	setCookie( request() );
    	
    	JsonNode request = request().body().asJson();
    	String datasetID = request.get(Json.INPUT.SELECT_CONCEPT.DATASETID).asText(),
    			colName = request.get(Json.INPUT.SELECT_CONCEPT.COL_NAME).asText(),
    			colLabel = request.get(Json.INPUT.SELECT_CONCEPT.COL_LABEL).asText(),
    			conceptURI = request.get(Json.INPUT.SELECT_CONCEPT.CONCEPT_URI).asText(),
				mongoDatasetID = request.get(Json.INPUT.CREATE_ATTRIBUTE.MONGODATASET_ID ).asText(),
    			concept = request.get(Json.INPUT.CREATE_ATTRIBUTE.CONCEPT ).asText();
    	int columnIndex = request.get(Json.INPUT.SELECT_CONCEPT.COL_INDEX).asInt();
    	
    	if(debug)
    		play.Logger.debug( "associateAttributeToDataset(concept= '" + concept + "', colname='"+colName+"')" );
    	
    	CachingRESTDatasetPullService cache = new CachingRESTDatasetPullService(mongoDatasetID);
    	DatasetMetadata meta = cache.getMetadata(mongoDatasetID);
    	meta.setItem(colLabel,concept);
    	cache.commitMeta(meta);

    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	String datasetAttrURI = handler.selectConcept(datasetID, columnIndex, colName, conceptURI);
    	JsonNode result = JSON.newObject(Json.OUTPUT.SELECT_CONCEPT.DATASET_ATTR_URI, datasetAttrURI);
        return ok(result);
    }
    
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result createSpecies() throws FileNotFoundException, ClassNotFoundException, IOException 
    {
    	setCookie( request() );
    	
    	JsonNode request = request().body().asJson();
    	String speciesName = request.get("speciesName").asText();
    	
    	if(debug)
    		play.Logger.debug( "createSpecies( name: '" + speciesName + "' )" );
    	
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	String speciesURI = handler.createSpecies( speciesName );
    	JsonNode result = JSON.newObject( "speciesURI", speciesURI );
        return ok(result);
    }
    
    /**
     * Searches the semantic triple store for a concept that matches the 
     * keyword supplied
     * @param keyword the keyword to match concepts against (in the json payload)
     * @return an ordered list of concept matches, encoded in JSON format
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result searchConcept() 
    {
    	setCookie( request() );
    	String keyword = request().body().asJson()
    						.get(Json.INPUT.SEARCH_CONCEPT.KEYWORD).asText();
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	OrderedConceptSet resultSet = handler.searchAvailableConcepts(keyword);
    	JsonNode result = resultSet.toJson();
        return ok(result);
    }
    
    /**
     * Creates a new dataset entity instantiation in the model
     * @param datasetName a name for the dataset (used in the generation of an identifier)
     * @param datasetID a unique identifier for the dataset
     * @return the URI identifier for this dataset, encoded in JSON format
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result createDataset()
    {
    	setCookie( request() );
    	JsonNode request = request().body().asJson();
    	String datasetName = request.get( INPUT.CREATE_DATASET.NAME ).asText(),
    			datasetID = request.get( INPUT.CREATE_DATASET.ID ).asText();
    	JsonNode formats = request.get( INPUT.CREATE_DATASET.FORMATS );
    	
    	if( ! formats.isArray()) 
    		return status(400, "Bad Request: json is not correctly formatted");
    	
    	List<TimeFormat> formatList = new java.util.LinkedList<TimeFormat>();
    	for(JsonNode format : formats)
    	{
    		String theFormat = format.get( INPUT.CREATE_DATASET.FORMAT ).asText(),
    				precision = format.get( INPUT.CREATE_DATASET.PRECISION ).asText();
    		formatList.add( new TimeFormat(theFormat,precision) );
    	}
    	
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	String datasetURI = handler.createDataset(datasetID, datasetName,formatList);
    	
    	JsonNode result = JSON.newObject(Json.OUTPUT.CREATE_DATASET.URI, datasetURI);
    	return ok(result);
    }
    
    /**
     * Analyzes the data for a set of values of an attribute in a dataset
     * Detects outliers in the provided data and produces a set of statistics
     * about it (mode, avgs, min/max ..)
     * @return a set of outlier values
     * @throws ParseException 
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result analyseData() throws ParseException, Exception
    {
    	
    	setCookie( request() );
    	JsonNode request = request().body().asJson();
    	String datasetAttributeURI = request.get(
    			Json.INPUT.CHECK_VALUES.ATTRIBUTE_ID ).asText(),
    			datasetID = request.get( INPUT.CHECK_VALUES.DATASET_ID ).asText(),
    			currentCol = request.get( INPUT.CHECK_VALUES.CURRENT ).asText(),
    			speciesCol = request.get( INPUT.CHECK_VALUES.SPECIES ).asText();
    	Logger.debug("Controller.analyseData( species='"+speciesCol+"', current='"+currentCol+"')");
//    	System.out.println();
//    	System.out.println("DataPullMetaMapJsonController.analyseData");
//    	System.out.println("\t\tdatasetID='"+datasetID+"', currentCol='"+currentCol+"', speciesCol='"+speciesCol+"'");
//    	System.out.println();
   	
    	DatasetPullService pull = new CachingRESTDatasetPullService(datasetID);
    	List<List<String>> rows = pull.getDataWithRowID( datasetID, currentCol, speciesCol );
    	
    	
    	//rows.stream().forEach( x -> System.out.println( x.toString() ) );
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	
    	Stream<Tuple3<String,String,String>> values = 
			rows.stream().map(
				x -> new Tuple3<String,String,String>( x.get(0), x.get(1) , x.get(2) )
			);
    	//<datasetURI,attributeURI>
    	Tuple2<String,String> URIs = handler.getAttribute(datasetAttributeURI);
    	String datasetURI = URIs.one, 
    			attributeURI = URIs.two;

    	//HashMap<String,String> tempSpeciesMap = TemporaryModel.getInstance().speciesMap(); 
    	
    	List<Outlier> outliers = handler.checkForOutliers( 
    			datasetURI, attributeURI, values , currentCol, new HashMap<String,String> ());
    	
    	//handler.produceStatistics(attributeURI, JSON.arrayAsList(values));
    	
    	ObjectNode root = JSON.newObject();
    	ArrayNode outliersArray = JSON.newArray(root, Json.OUTPUT.CHECK_VALUES.OUTLIERS);
    	
    
    	
    	for(Outlier outlier : outliers) 
    	{
    		outliersArray.add( outlier.getValue() );
    		pull.setOutlier( outlier.getRowID(), outlier.getValue(), outlier.getInvalidationReason() ) ;
    	}
    	
    	//implement this, makes caching service commit outliers to disk
    	pull.commit();
    	
    	return ok(root);
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result produceStatistics() throws ParseException, Exception
    {
    	setCookie( request() );
    	JsonNode request = request().body().asJson();
    	String datasetAttributeURI = request.get(
    			Json.INPUT.CHECK_VALUES.ATTRIBUTE_ID ).asText(),
    			datasetID = request.get( INPUT.CHECK_VALUES.DATASET_ID ).asText(),
    			currentCol = request.get( INPUT.CHECK_VALUES.CURRENT ).asText();
    	
      	DatasetPullService pull = new CachingRESTDatasetPullService(datasetID);
    	List<String> rs = pull.getData(datasetID, currentCol).stream()
    						.map( x -> x.get(0))
    						.collect( Collectors.toList() );
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	
    	Tuple2<String,String> URIs = handler.getAttribute(datasetAttributeURI);
    	String datasetURI = URIs.one, 
    			attributeURI = URIs.two;
    	//HashMap<String,String> tempSpeciesMap = TemporaryModel.getInstance().speciesMap(); 
    	Domain dom = handler.produceStatistics(
    				attributeURI, rs, datasetURI, currentCol, new HashMap<String,String>() );
    	
    	
    	CachingRESTDatasetPullService cache = new CachingRESTDatasetPullService(datasetID);
    	DatasetMetadata meta = cache.getMetadata(datasetID);
    	meta.setSamples(dom.getStatistics(),currentCol);
    	cache.commitMeta(meta);
    	
    	JsonNode result = dom.getStatistics().toJson();
    	
    	return ok(result);
    }
    
    
    /**
     * Maps from a dataset column header value to an Attribute definition
     * @param headerName the name of the column header (encoded in the json payload)
     * @return the attributes' URI or NONE in a JSON-coded value
     */
    @BodyParser.Of(BodyParser.Json.class)
    public Result mapToAttribute()
    {
    	setCookie( request() );
    	String headerName = request().body().asJson()
    							.get(Json.INPUT.MAP_ATTRIBUTE.HEADER).asText();
    	
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	
    	String attributeURI;
    	ObjectNode result;
    	
		try {
			attributeURI = handler.mapToAttribute(headerName);
			String conceptURI = handler.attributeInfo(attributeURI);
			result = JSON.newObject(Json.OUTPUT.MAP_ATTRIBUTE.URI, attributeURI);
			result.put( "conceptURI" , conceptURI);
		} catch (InexistentAttributeException e) {
			result = JSON.newObject(Json.OUTPUT.MAP_ATTRIBUTE.NONE, "nop");
		}
		
    	return ok(result);
    }

    
    public Result getSpecies()
    {
    	
    	setCookie( request() );
    	MetaMappingHandler handler = new MetaMappingHandlerImpl();
    	SpeciesList species = handler.getAllSpecies();
    	return ok( species.toJson() );
    }
    
    
    public Result getDistincts(String datasetID, String columnLabel, int index) 
    		throws JsonProcessingException, IOException
    {
    	if(datasetID.equals("XXX"))
    	{
    		String json = "{ \"index\" : 5 , \"values\" : [ \"Sea-Bream\" , \"Renato Sanches\" ] }";
    		return ok( utils.json.JSON.stringToJson(json));
    	}
    	
    	setCookie( request() );
		try {
			DatasetPullService pull = new CachingRESTDatasetPullService(datasetID);
	    	List<String> rs = pull.getData(datasetID,columnLabel)
									.stream()
									.flatMap( x -> x.stream() )
									.collect( Collectors.toList());

			JsonNode values = distincts(rs,index);
			
			return ok( values );
		} catch (Exception e) {
			
			e.printStackTrace();
			return internalServerError( e.getMessage() );
		}
	
		
    }
    
    public Result getDistribution(String datasetID, String type, String columnLabel)
    {

    	setCookie( request() );
    	try {
			DatasetPullService pull = new CachingRESTDatasetPullService(datasetID);
	    	List<String> rs = pull.getData(datasetID,columnLabel)
									.stream()
									.flatMap( x -> x.stream() )
									.collect( Collectors.toList() );
			JsonNode values = distributionOf( rs, type );
			
			return ok(values);
		} catch ( Exception e ) {
			
			e.printStackTrace();
			return internalServerError("Error in HTTP GET to data pull service");
		}
    }
    
    public JsonNode distributionOf(List<String> values, String domainType)
    {
    
    	
    	ObjectNode root = JSON.newObject();
    	
    	Stream<Entry<String, List<String>>> cleanDist = 
			values.stream()
				.filter( x ->  ! x.trim().equals("") )
				.collect( Collectors.groupingBy( x -> x ) )
				.entrySet().stream();
    	
    	if( domainType.equals(Domains.CATEGORIC) || domainType.equals(Domains.TEXTUAL) 
    			|| domainType.equals(Domains.TEMPORAL))
    	{
    		cleanDist
				.map( x -> new Frequency<String>( x.getKey(), x.getValue().size() ) )
				.sorted( (x,y) -> new Integer( y.frequency ).compareTo( x.frequency ) )
				.collect( 
						() -> JSON.newArray(root,"values"),
						(col,x) -> {
							ObjectNode val = JSON.newObject();
							val.put("value", x.value);
							val.put("freq", x.frequency);
							col.add(val);
						},
						(col,col2) -> col.addAll(col2)
				);
    	}
		
		else if(domainType.equals(Domains.NUMERIC))
		{
			cleanDist
				.map( x -> new Frequency<Double>( Double.parseDouble(x.getKey()), x.getValue().size() ) )
				.sorted( (x,y) -> new Integer( y.frequency ).compareTo( x.frequency ) )
				.collect( 
						() -> JSON.newArray(root,"values"),
						(col,x) -> {
							ObjectNode val = JSON.newObject();
							val.put("value", x.value);
							val.put("freq", x.frequency);
							col.add(val);
						},
						(col,col2) -> col.addAll(col2)
				);
		}

    	return root;
    				
    }
    
    public JsonNode distincts(List<String> values, int index)
    {
    
    	
    	ObjectNode root = JSON.newObject();
    	root.put("index", index);
    	Stream<Entry<String, List<String>>> cleanDist = 
			values.stream()
				.filter( x ->  ! x.trim().equals("") )
				.collect( Collectors.groupingBy( x -> x ) )
				.entrySet().stream();
        	
		cleanDist
			.map( x -> x.getKey() )
			.collect( 
					() -> JSON.newArray( root , "values" ) ,
					(col,x) -> col.add( x ) ,
					(col,col2) -> col.addAll( col2 )
			);

    	return root;
    				
    }
    
    
    public Result getMetadata(String datasetID) throws IOException, Exception
    {
    	setCookie( request() );
    	DatasetPullService pullService = new RESTDatasetPullService();
    	
    	DatasetMetadata metas = pullService.getMetadata(datasetID);
    	
    	return ok( metas.toJson() );   	
    }
    
    private void logJSON(ObjectNode obj) throws JsonProcessingException
    {
    	ObjectMapper mapper = new ObjectMapper();
		String input = mapper.writeValueAsString(obj);
		Logger.debug(input);
    }
    
    public Result executeDelivery( String datasetID )
    {
    	setCookie( request() );
    	String dataDeliveryURL = HttpUtils.buildRestURL( false, DatasetService.BASE_URL, datasetID ),
    			metaDeliveryURL = HttpUtils.buildRestURL( false, DatasetService.BASE_URL, 
    					datasetID, DatasetService.GET_META ) ;
    	DatasetPullService service;
		try {
			service = new CachingRESTDatasetPullService( datasetID );
			
			StagingData data = service.getDataObject( datasetID );
			ObjectNode dataPost = (ObjectNode) data.toJson();
			DatasetMetadata meta = service.getMetadata( datasetID );
			ObjectNode metaPost = (ObjectNode) meta.toJson();
	    	
			logJSON(dataPost);
			logJSON(metaPost);
			
			HttpUtils.jsonPATCH( dataDeliveryURL, dataPost );
	    	HttpUtils.jsonPATCH( metaDeliveryURL, metaPost );
	    	
	    	service.destroy();
	    	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.debug("FileNotFound",e);
			return internalServerError(e.getMessage());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			Logger.debug("ClassNotFoundException",e);
			return internalServerError(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Logger.debug("IOException",e);
			return internalServerError(e.getMessage());
		} catch (Exception e){
			Logger.debug(e.getClass().getCanonicalName(),e);
			return internalServerError(e.getMessage());
		}
    	
    	
    	//TODO: PUT/POST metadata set to the data-staging-service
    	//TODO: Erase caches
    	
    	return ok();
    }
    
    public Result javascriptRoutes() {
        response().setContentType("text/javascript");
        return ok(
            Routes.javascriptRouter("routes",
                controllers.routes.javascript.DataPullMetaMapJsonController.createConcept(),
                controllers.routes.javascript.DataPullMetaMapJsonController.associateAttributeToDataset(),
                controllers.routes.javascript.DataPullMetaMapJsonController.searchConcept(),
                controllers.routes.javascript.DataPullMetaMapJsonController.createDataset(),
                controllers.routes.javascript.DataPullMetaMapJsonController.analyseData(),
                controllers.routes.javascript.DataPullMetaMapJsonController.createAttribute(),
                controllers.routes.javascript.DataPullMetaMapJsonController.mapToAttribute(),
                controllers.routes.javascript.DataPullMetaMapJsonController.getAttributeInfo(),
                controllers.routes.javascript.DataPullMetaMapJsonController.produceStatistics(),
                controllers.routes.javascript.DataPullMetaMapJsonController.getConceptInfo(),
                controllers.routes.javascript.DataPullMetaMapJsonController.getSpecies(),
                controllers.routes.javascript.DataPullMetaMapJsonController.getDistincts(),
                controllers.routes.javascript.DataPullMetaMapJsonController.getDistribution(),
                controllers.routes.javascript.DataPullMetaMapJsonController.getMetadata(),
                controllers.routes.javascript.DataPullMetaMapJsonController.executeDelivery(),
                controllers.routes.javascript.DataPullMetaMapJsonController.updateSpeciesValues(),
                controllers.routes.javascript.DataPullMetaMapJsonController.createSpecies()
            )
        );
    }
    


}
