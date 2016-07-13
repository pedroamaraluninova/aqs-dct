package aquasmart.services.metamap.handlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import utils.CollectionUtils;
import utils.tuples.*;
import aquasmart.services.datapull.beans.metaschema.DatasetMetadata;
import aquasmart.services.metamap.beans.Concept;
import aquasmart.services.metamap.beans.Domain;
import aquasmart.services.metamap.beans.OrderedConceptSet;
import aquasmart.services.metamap.beans.Outlier;
import aquasmart.services.metamap.beans.SpeciesList;
import aquasmart.services.metamap.beans.TimeFormat;
import aquasmart.services.metamap.model.MetaMappingModel;
import aquasmart.services.metamap.model.MetaMappingTripleStoreModel;
import aquasmart.services.metamap.model.TemporaryModel;
import aquasmart.services.metamap.model.exception.InexistentAttributeException;
import aquasmart.services.metamap.utils.STATIC;


public class MetaMappingHandlerImpl implements MetaMappingHandler
{

	
	public MetaMappingHandlerImpl() 
	{
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public OrderedConceptSet searchAvailableConcepts(String query) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_GLOSS);
		OrderedConceptSet concepts = store.searchConceptsByKeyword(query);
		return concepts;
	} 

	@Override
	public String selectConcept(String datasetID, int columnIndex,
			String colName, String conceptURI) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		return 
			store.assignAttributeToDataset(datasetID, columnIndex, conceptURI);
	}

	@Override
	public String createConcept(String name, String desc, String lang,
			boolean isAnonymous) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_GLOSS);
		if(isAnonymous)
			return store.createAnonymousConcept(name);
		else
			return store.createConcept(name,desc,lang);
		
	}
	
	@Override
	public String createSpecies(String speciesName) throws IOException 
	{
		TemporaryModel store = TemporaryModel.getInstance();
		return store.addSpecies(speciesName);

	}
	
	@Override
	public Concept getConcept(String conceptURI) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_GLOSS);
		Concept concept = store.getConcept(conceptURI);
		return concept;
	}
	
	@Override
	public String createDataset(String datasetName, List<TimeFormat> formats) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		return store.createDataset(datasetName,formats);
	}
	

	@Override
	public String createDataset(String datasetID, String datasetName,
			List<TimeFormat> formats) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		return store.createDataset(datasetID, datasetName, formats);
	}


	@Override
	public List<Outlier> checkForOutliers(String datasetURI, String attributeURI,
			Stream<Tuple3<String,String,String>> stream, String columnLabel, HashMap<String,String> tempSpecies) throws ParseException 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		Domain domain = store.getDomainInfo(attributeURI,datasetURI, columnLabel,tempSpecies);
		return domain.detectOutliers(stream);
		
	}
	
	@Override
	public String createAttribute(String name, String conceptURI,
			String domainType, Domain domain) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		return store.createAttribute(name,conceptURI,domainType,domain);
	}
	
	@Override
	public void updateSpeciesValue(String speciesURI, String value) throws FileNotFoundException, IOException
	{
		TemporaryModel tempStore = TemporaryModel.getInstance();
		if( tempStore.addToSpecies(speciesURI, value) )
			return;
		
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		store.updateSpeciesValue(speciesURI, value);
	}
	
	@Override
	public Domain produceStatistics(String attributeURI, 
					List<String> values, String datasetURI, String columnLabel
					, HashMap<String,String> tempSpecies) 
			throws ParseException 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		Domain domain = store.getDomainInfo(attributeURI,datasetURI, columnLabel,tempSpecies);
		domain.produceStats(values);
		return domain;
	}
	
	@Override
	public Tuple2<String, String> getAttribute(String datasetAttributeURI) 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		return store.getAttribute(datasetAttributeURI);
	}
	
	@Override
	public String mapToAttribute(String headerName) throws InexistentAttributeException 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		String escaped = escape(headerName);
		String attrURI = store.mapToAttribute(escaped);
		return attrURI;
	}
		
	
	private String escape(String headerName) 
	{
		return headerName
				.replace("(", "")
				.replace(")", "")
				.replace("%", "percent")
				.replace("+", "plus");
	}

	
	
	@Override
	public String getAttributeInfo(String datasetAttributeURI) 
	{
		
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		String attrURI = store.getAttribute(datasetAttributeURI).two;
		String conceptURI = store.getAttributeInfo(attrURI);
		play.Logger.debug("getAttributeInfo( dsAttributeURI= "+datasetAttributeURI
				+" ) = "+conceptURI);
		return conceptURI;

	}
	
	@Override
	public String attributeInfo(String attributeURI) 
	{
		
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		
		String conceptURI = store.getAttributeInfo(attributeURI);
	
		return conceptURI;

	}
	
	@Override
	public SpeciesList getAllSpecies() 
	{
		MetaMappingModel store = new MetaMappingTripleStoreModel(STATIC.TDB_DATASET);
		List<String> species = store.getSpecies();
		List<Tuple2<String,List<String>>> speciesMap = 
				CollectionUtils.mapToList( 
						CollectionUtils.invertMapWithCollisions( store.getSpeciesMap() ) ) ; 
		SpeciesList speciesList = new SpeciesList( species, speciesMap );
		return speciesList;
	}
	
	
	public static void main(String[] args)
	{
		
		MetaMappingHandlerImpl controller =
				new MetaMappingHandlerImpl();
		
		OrderedConceptSet ocs = 
				controller.searchAvailableConcepts("bleeeeee");
		
		System.out.println("result is:");
		System.out.println(ocs.toJson().toString()+"\n");
		
		controller.selectConcept("theDatasetURI", 12345, "theColName", "theConceptURI");
		
		System.out.println();
		
		String res = controller.createConcept("realName", "realDesc", "ingurishu", false);

		System.out.println("result is: " +res );
		System.out.println();
		
		res = controller.createConcept("anonName", "realDesc", "ingurishu", true);
		
		System.out.println("result is: " +res );
		System.out.println();
		
	}


















}
