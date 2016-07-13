package aquasmart.services.metamap.model;


import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.vocabulary.RDF;

import play.Logger;
import utils.tuples.*;
import aquasmart.services.metamap.beans.CategoricalDomain;
import aquasmart.services.metamap.beans.Concept;
import aquasmart.services.metamap.beans.ConceptImpl;
import aquasmart.services.metamap.beans.Domain;
import aquasmart.services.metamap.beans.NumericDomain;
import aquasmart.services.metamap.beans.NumericDomain.NumericPrecision;
import aquasmart.services.metamap.beans.OrderedConceptSet;
import aquasmart.services.metamap.beans.OrderedConceptSetImpl;
import aquasmart.services.metamap.beans.TemporalDomain;
import aquasmart.services.metamap.beans.TemporalDomain.TemporalPrecision;
import aquasmart.services.metamap.beans.TextualDomain;
import aquasmart.services.metamap.beans.TimeFormat;
import aquasmart.services.metamap.beans.range.NumericRange;
import aquasmart.services.metamap.model.exception.InexistentAttributeException;
import aquasmart.services.metamap.utils.STATIC;
import aquasmart.services.metamap.utils.STATIC.DOntology;
import aquasmart.services.metamap.utils.STATIC.Ontology;
import utils.sparql.query.SparqlQueryBuilder;
import utils.sparql.update.SparqlUpdateBuilder; 
import utils.sparql.TDBTripleStore;
import utils.sparql.TripleStore;

public class MetaMappingTripleStoreModel implements MetaMappingModel 
{

	TripleStore store; 
	
	public MetaMappingTripleStoreModel()
	{
		//test implementation
		//this.store = new MockTripleStore();
		
		//actual implementation
		this.store = new TDBTripleStore();
	}
	
	public MetaMappingTripleStoreModel(String location)
	{
		//test implementation
		//this.store = new MockTripleStore();
		
		//actual implementation
		this.store = new TDBTripleStore(location);
	}
	

	
	@Override
	public OrderedConceptSet searchConceptsByKeyword(String query) 
	{

		
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		String alias = "?uri",
				litQuery = q.literal(query);
		
		q.select(true, alias, "?name")
		 .where(
			q.triple(alias, q.$( RDF.type.getURI() ), q.$( Ontology.CONCEPT.TYPE), "."),
			q.union(
				q.triples(
					q.triple(alias, q.$( Ontology.CONCEPT.NAME ), "?name", " " ),
					q.filter(true, q.regex("?name", litQuery, true))
				), 
				q.triples(
					q.triple(alias, q.$( Ontology.CONCEPT.DEFINITION), "?desc", " " ),
					q.filter(true, q.regex("?desc", litQuery, true))
				)
			)
		);
	
		String sparql = q.sparql();
		Logger.debug(sparql);
		
		List<List<String>> results = this.store.executeRead(sparql, alias, "?name");
		
		Logger.debug("result has "+ results.size() + " results" );
		List<Concept> concepts = new LinkedList<Concept>();
		for(List<String> result : results)
			concepts.add(ConceptImpl.create(result));
		
		return new OrderedConceptSetImpl(concepts);
		
	}

	@Override
	public String assignAttributeToDataset(String datasetURI, int columnIndex,
			String attributeURI) 
	{
		
		SparqlUpdateBuilder update = new SparqlUpdateBuilder();
		
		String datasetAttrURI = this.store.createURI(DOntology.BASE + "dataset_attr");
		update.triple(datasetURI, DOntology.Dataset.CONTAINS_ATTRIBUTE, datasetAttrURI)
			.type(datasetAttrURI, DOntology.DatasetAttribute.TYPE)
			.triple(datasetAttrURI, DOntology.DatasetAttribute.TABLE_INDEX, ""+columnIndex, null)
			.triple(datasetAttrURI, DOntology.DatasetAttribute.IS_INSTANTIATION_OF, attributeURI);
		
		this.store.executeWrite(update);
		
		return datasetAttrURI;
	}

	@Override
	public String createAnonymousConcept(String name) 
	{
		return this.createConcept(
				name, STATIC.ANONYMOUS_ATTRIBUTE_DESCRIPTION, 
				STATIC.Language.ENG);
	}

	@Override
	public String createConcept(String name, String desc, String lang) 
	{
		SparqlUpdateBuilder update = new SparqlUpdateBuilder();
				
		String conceptURI = this.store.createURI(Ontology.BASE + "concept");
		update.triple( conceptURI, RDF.type.getURI() ,Ontology.CONCEPT.TYPE)
			.triple(conceptURI, Ontology.CONCEPT.NAME, name, lang)
			.triple(conceptURI, Ontology.CONCEPT.DEFINITION, desc, lang);	
		
		this.store.executeWrite(update); 
		
		return conceptURI;
	}
	
	@Override
	public Concept getConcept(String conceptURI) 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		
		String concept = q.$(conceptURI),
				name = "?name",
				desc = "?desc";
		
		q.select(true, name, desc)
			.where(
				q.type(concept, q.$(Ontology.CONCEPT.TYPE), "."),
				q.triple(concept, q.$(Ontology.CONCEPT.NAME), name, "."),
				q.optional( 
					q.triple(concept, q.$(Ontology.CONCEPT.DEFINITION), desc , " ")
				)
		);
		
		List<String> results = 
				this.store.executeRead(q.sparql(), name, desc).get(0);
		
		String theName = results.get(0),
				theDesc = results.get(1);
		theDesc = theDesc == null ? "Not Available" : theDesc ;
		Concept theConcept = new ConceptImpl( theName , theDesc , concept);
		return theConcept;
	}


	@Override
	public String createDataset(String datasetName, List<TimeFormat> formats) 
	{
		return this.createDataset(datasetName, datasetName, formats);
	}
	
	@Override
	public String createDataset(String datasetID, String datasetName,
			List<TimeFormat> formats) {
		SparqlUpdateBuilder update = new SparqlUpdateBuilder();
		String baseURI = DOntology.BASE + "dataset" + "/" + datasetID , 
				datasetURI = this.store.createURI(baseURI);
		
		update.triple(datasetURI, RDF.type.getURI(), DOntology.Dataset.TYPE)
			.triple(datasetURI, DOntology.Dataset.NAME, datasetName, null);
		
		String formatBaseURI = 
				this.store.createURI(DOntology.BASE + datasetName + "_time");
		int count = 0;
		
		for(TimeFormat format : formats)
		{
			String 
				formatURI = formatBaseURI + "_" + count++,
				precision = TimeFormat.fromTemporalPrecision( 
					TimeFormat.asTemporalPrecision(format.getPrecision() ) );
			
			update.type(formatURI, DOntology.Dataset.TimeFormat.TYPE)
				.triple(datasetURI, DOntology.Dataset.TIME_FORMAT, formatURI)
				.triple(formatURI, DOntology.Dataset.TimeFormat.FORMAT, format.getFormat(), null)
				.triple(formatURI, DOntology.Dataset.TimeFormat.FORMAT_PRECISION, 
						precision);
		}
		Logger.debug(update.toString());
		this.store.executeWrite(update);
		
		return datasetURI;
	}

	@Override
	public Domain getDomainInfo(String attributeURI, String datasetURI, String label, HashMap<String,String> tempSpeciesMap) 
				throws ParseException 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		
		q.select(true, "?type")
			.where(
				q.triple( q.$(attributeURI) , q.$(DOntology.Attribute.DATA_TYPE), "?type", " ")
		);
		String dataType = this.store.executeRead(q.sparql(), "?type").get(0).get(0);
		
		if(dataType.equals(DOntology.Attribute.DataType.CATEGORIC_TYPE))
			return getCategoricDomain(attributeURI, label);
		else if(dataType.equals(DOntology.Attribute.DataType.NUMERIC_TYPE))
			return getNumericDomain(attributeURI, label);
		else if(dataType.equals(DOntology.Attribute.DataType.TEMPORAL_TYPE))
			return getTemporalDomain(attributeURI,datasetURI, label);
		else //if(dataType.equals(DOntology.Attribute.DataType.TEXTUAL_TYPE))
			return new TextualDomain(label);
	}

	public HashMap<String,String> getSpeciesMap()
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		q.select(true, "?value", "?species")
		.where(
			q.triple( "?cat" , q.$(RDF.type.getURI()), q.$( DOntology.Domain.CategoricDomain.CategoricValue.TYPE) , "."),
			q.triple( "?cat" , q.$(DOntology.Domain.CategoricDomain.CategoricValue.VALUE), "?value" , "."),
				q.triple( "?cat" , q.$( DOntology.Domain.CategoricDomain.CategoricValue.SPECIES) , "?species", "")
		);
		
		List<List<String>> speciesResult = this.store.executeRead( q.sparql(), "?value", "?species" );
		
		HashMap<String,String> speciesMap = new HashMap<String,String>();
		speciesResult.stream().forEach(
			x -> speciesMap.put( x.get(0), x.get(1) )
		);
		
		return speciesMap;
		
	}
	
	public HashMap<String,String> getSpeciesMap( HashMap<String,String> existingMappings )
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		q.select(true, "?value", "?species")
		.where(
			q.triple( "?cat" , q.$(RDF.type.getURI()), q.$( DOntology.Domain.CategoricDomain.CategoricValue.TYPE) , "."),
			q.triple( "?cat" , q.$(DOntology.Domain.CategoricDomain.CategoricValue.VALUE), "?value" , "."),
				q.triple( "?cat" , q.$( DOntology.Domain.CategoricDomain.CategoricValue.SPECIES) , "?species", "")
		);
		
		List<List<String>> speciesResult = this.store.executeRead( q.sparql(), "?value", "?species" );
		
		HashMap<String,String> speciesMap = existingMappings;
		speciesResult.stream().forEach(
			x -> speciesMap.put( x.get(0), x.get(1) )
		);
	
		return speciesMap;
		
	}
	
	private Domain getCategoricDomain(String attributeURI, String label) 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		
		q.select(true, "?val", "?species")
			.where( 
				q.type( q.$(attributeURI), q.$(DOntology.Attribute.TYPE), "."),
				q.triple( 
						q.$(attributeURI),
						q.$(DOntology.Attribute.HAS_VALUES_FROM), "?dom", "." ),
				q.type( "?dom", q.$(DOntology.Domain.CategoricDomain.TYPE), "." ),
				q.triple( "?dom", q.$(DOntology.Domain.CategoricDomain.ENUMERATION), "?catval", "."),
				q.triple( "?dom", q.$(DOntology.Domain.SPECIES), "?species", "."),
				q.triple("?catval", 
						q.$(DOntology.Domain.CategoricDomain.CategoricValue.VALUE), "?val", " "));
		
		
		List< List<String> > sparqlResults = this.store.executeRead(q.sparql(), "?val", "?species");

		//		Supplier<List<String>> sup = () -> new LinkedList<String>();
//		BiConsumer<List<String>,List<String>> accum = (list, x) -> list.addAll(x);
//		BiConsumer<List<String>,List<String>> comb = (v1,v2) -> v1.addAll(v2);
//		List<String> list = sparqlResults.stream().collect(
//				sup,
//				accum,
//				comb);
		HashMap<String,List<String>> allowed = new HashMap<String, List<String>>();
		
		sparqlResults.stream().forEach(
			x -> addToMap( allowed, x.get(1), x.get(0) )
		);
	
		return new CategoricalDomain( allowed, getSpeciesMap() , label);
	}

	private void addToMap(HashMap<String, List<String>> allowed,
			String key, String value) 
	{
		List<String> vals = allowed.get(key);
		if(vals == null)
			vals = new LinkedList<String>();
		vals.add(value);
		allowed.put(key, vals);
	}

	private Domain getNumericDomain(String attributeURI, String label) 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		
		q.select(true, "?prec", "?low", "?upper", "?unit", "?species")
			.where( 
				q.type( q.$(attributeURI), q.$(DOntology.Attribute.TYPE), "."),
				q.triple( 
						q.$(attributeURI),
						q.$(DOntology.Attribute.HAS_VALUES_FROM), "?dom", "." ),
				q.type( "?dom", q.$(DOntology.Domain.NumericDomain.TYPE), "." ),
				q.triple("?dom", 
					q.$(DOntology.Domain.NumericDomain.PRECISION), "?prec", "."),
				q.triple("?dom", q.$(DOntology.Domain.NumericDomain.LOWER_BOUND), "?low", "."),
				q.triple( "?dom", q.$(DOntology.Domain.SPECIES), "?species", "."),
				q.triple("?dom", 
						q.$(DOntology.Domain.NumericDomain.UPPER_BOUND), "?upper", "."),
				q.triple("?dom",
						q.$(DOntology.Domain.NumericDomain.UNIT), "?unit", " ")
		);
		
		List< List<String> > sparqlResults = 
				this.store.executeRead(q.sparql(), "?prec", 
						"?low", "?upper", "?unit", "?species");
		
		List<String> transversalVals = sparqlResults.get(0);
		HashMap<String,NumericRange> ranges = new HashMap<String,NumericRange>();
		
		sparqlResults.stream().forEach(
			x -> {
				double lowerBound = Double.parseDouble(x.get(1)),
						upperBound = Double.parseDouble(x.get(2));
				String species = x.get(4);
				ranges.put(species, new NumericRange(lowerBound,upperBound));
			}
		);
		
		NumericPrecision precision = toNumericPrecision(transversalVals.get(0));
		String unit = transversalVals.get(3);
	
		return new NumericDomain( ranges, precision, unit, getSpeciesMap(), label );
	}

	private Domain getTemporalDomain(String attributeURI, String datasetURI, String label) 
			throws ParseException 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		
		q.select(true, "?prec", "?low", "?upper", "?species")
			.where( 
				q.type( q.$(attributeURI), q.$(DOntology.Attribute.TYPE), "."),
				q.triple( 
						q.$(attributeURI),
						q.$(DOntology.Attribute.HAS_VALUES_FROM), "?dom", "." ),
				q.type( "?dom", q.$(DOntology.Domain.TemporalDomain.TYPE), "." ),
				q.triple("?dom", 
					q.$(DOntology.Domain.TemporalDomain.PRECISION), "?prec", "."),
				q.triple("?dom", q.$(DOntology.Domain.TemporalDomain.LOWER_BOUND), "?low", "."),
				q.triple("?dom", 
						q.$(DOntology.Domain.TemporalDomain.UPPER_BOUND), "?upper", ". "),
				q.triple( "?dom", q.$(DOntology.Domain.SPECIES), "?species", " ")
		);
		
		List<List<String>> sparqlResults = 
				this.store.executeRead(q.sparql(), "?prec", 
						"?low", "?upper", "?species" );
		List<String> transversalValues = sparqlResults.get(0);		
		String precisionURI = transversalValues.get(0);
		TemporalPrecision precision = TimeFormat.toTemporalPrecision(precisionURI);
		
		List<Tuple3<String,String,String>> ranges = sparqlResults.stream().map(
			x -> new Tuple3<String,String,String>( x.get(3), x.get(1), x.get(2) )
		).collect( Collectors.toList() );
	
		
		
		q = new SparqlQueryBuilder();
		String dataset = q.$(datasetURI);
		q.select(true, "?format")
			.where(
				q.type( dataset, q.$(DOntology.Dataset.TYPE), "."),
				q.triple( dataset, q.$(DOntology.Dataset.TIME_FORMAT), "?time", "."),
				q.type( "?time", q.$(DOntology.Dataset.TimeFormat.TYPE), "."),
				q.triple( "?time", q.$(DOntology.Dataset.TimeFormat.FORMAT), "?format", "."),
				q.triple( "?time", q.$(DOntology.Dataset.TimeFormat.FORMAT_PRECISION),
									q.$(precisionURI), " ")
		);
		String sparql = q.sparql();
		Logger.debug(sparql);
		List<String> sparqlFormatResults = this.store.executeRead(sparql, "?format").get(0);
		String format = sparqlFormatResults.get(0);
		
		return new TemporalDomain( precision, ranges, format, getSpeciesMap(), label );
	}

	@Override
	public String createAttribute(String name, String conceptURI,
			String domainType, Domain absDomain) 
	{
		SparqlUpdateBuilder update = new SparqlUpdateBuilder();
		String attrURI = this.store.createURI(DOntology.BASE + name);
		update.type(attrURI, DOntology.Attribute.TYPE)
			.triple(attrURI, DOntology.Attribute.DATA_TYPE, absDomain.getDataType())
			.triple(attrURI, DOntology.Attribute.CONCEPT, conceptURI);
		
		this.createDomain(absDomain, update, attrURI);
			
		this.store.executeWrite(update);
		
		return attrURI;
	}
	
	@Override
	public void addDomainToAttribute(String attrURI, Domain absDomain)
	{
		SparqlUpdateBuilder update = new SparqlUpdateBuilder();
		this.createDomain(absDomain, update, attrURI);
		this.store.executeWrite(update);
	
	}
	
	@Override
	public void updateSpeciesValue(String speciesURI, String value)
	{
		SparqlUpdateBuilder update = new SparqlUpdateBuilder();
		String valueURI = this.store.createURI(DOntology.BASE + "catValue");
		update.type( valueURI, DOntology.Domain.CategoricDomain.CategoricValue.TYPE )
				.triple( valueURI, DOntology.Domain.CategoricDomain.CategoricValue.VALUE, value, null )
				.triple( valueURI, DOntology.Domain.CategoricDomain.CategoricValue.SPECIES, speciesURI)
				.triple( DOntology.Domain.SPECIES_DOMAIN, 
							DOntology.Domain.CategoricDomain.ENUMERATION, valueURI );
		this.store.executeWrite( update );
		
	}

	@Override
	public Tuple2<String, String> getAttribute(String datasetAttributeURI) 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		String dsAlias = "?dsID",
				attrAlias = "?attrID",
				dsAttr = q.$(datasetAttributeURI);
		q.select(true, dsAlias, attrAlias)
		.where( 
			q.triple( dsAlias, q.$(DOntology.Dataset.CONTAINS_ATTRIBUTE), 
					dsAttr, "."),
			q.triple( dsAttr, q.$(DOntology.DatasetAttribute.IS_INSTANTIATION_OF),
					attrAlias, " ")
		);
		
		List<String> results = 
				this.store.executeRead(q.sparql(), q.getAliases()) .get(0);
		
		//<datasetURI,attributeURI>
		return new Tuple2<String,String>(results.get(0),results.get(1));
	}
	

	@Override
	public String mapToAttribute(String headerName) throws InexistentAttributeException
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		String attrAlias = "?attr",
				nameAlias = "?name";
		
		q.select(true, attrAlias)
		.where(
			q.triple(attrAlias, q.$(DOntology.Attribute.NAME), nameAlias, " "),
			q.filter(false, q.regex(nameAlias, q.literal(headerName), true))
		);
		
		List<List<String>> results = 
				this.store.executeRead(q.sparql(), q.getAliases());
		if(results.size() == 0)
			throw new InexistentAttributeException("Attribute \""+headerName+"\" does not exist");
		else
			return results.get(0).get(0);
	}
	
	@Override
	public String getAttributeInfo(String attrURI) 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		String alias = "?concept";
		
		q.select(true, alias)
		.where(
			q.triple( q.$(attrURI), q.$(DOntology.Attribute.CONCEPT), 
						alias, " ")
		);
		
		String conceptURI = this.store.executeRead(q.sparql(), alias)
										.get(0).get(0);
		return conceptURI;
	}
	

	
	private void createDomain( Domain absDomain,
			SparqlUpdateBuilder update, String attrURI) 
	{
		if( absDomain instanceof TemporalDomain)
			this.createTemporalDomain( (TemporalDomain) absDomain, update, attrURI);
		else if ( absDomain instanceof NumericDomain)
			this.createNumericDomain( (NumericDomain) absDomain,update, attrURI);
		else if ( absDomain instanceof CategoricalDomain)
			this.createCategoricalDomain((CategoricalDomain) absDomain, update, attrURI);
		else if ( absDomain instanceof TextualDomain)
		{
			String domainURI = this.store.createURI(DOntology.BASE + "domain");
			update.type(domainURI, DOntology.Domain.TextualDomain.TYPE)
				.triple(attrURI, DOntology.Attribute.HAS_VALUES_FROM, domainURI);
		}
	}

	private void createCategoricalDomain(CategoricalDomain domain, SparqlUpdateBuilder update, String attrURI) 
	{
		String baseDomainURI = this.store.createURI(DOntology.BASE + "domain");
		int i = 0;
		for( String species : domain.getAllowedValuesMap().keySet())
		{
			String domainURI = baseDomainURI + "_" + (i++) ;
			update.type(domainURI, DOntology.Domain.CategoricDomain.TYPE);
			
			for( String value : domain.getAllowedValues(species) )
			{
				String valueURI = this.store.createURI(DOntology.BASE + "catValue");
				update
					.triple(domainURI, 
						DOntology.Domain.CategoricDomain.ENUMERATION, valueURI)
					.type(valueURI, DOntology.Domain.CategoricDomain.CategoricValue.TYPE)
					.triple(valueURI,
						DOntology.Domain.CategoricDomain.CategoricValue.VALUE, value, null)
					.triple(attrURI, DOntology.Attribute.HAS_VALUES_FROM, domainURI)
					.triple(domainURI, DOntology.Domain.SPECIES, species);
			}
		}
	}

	private void createNumericDomain(NumericDomain domain,
			SparqlUpdateBuilder update, String attrURI) 
	{
		String baseDomainURI = this.store.createURI(DOntology.BASE + "domain");
		int i = 0;
		for(String species : domain.getRanges().keySet())
		{
			String domainURI = baseDomainURI + "_" + (i++) ;
			update.type(domainURI, DOntology.Domain.NumericDomain.TYPE)
				.triple(domainURI, 
						DOntology.Domain.NumericDomain.LOWER_BOUND, 
						domain.getStartValue(species)+"", null)
				.triple(domainURI, 
						DOntology.Domain.NumericDomain.UPPER_BOUND, 
						domain.getEndValue(species)+"", null)
				.triple(domainURI, 
						DOntology.Domain.NumericDomain.PRECISION, 
						fromNumericPrecision(domain.getPrecision()))
				.triple(domainURI, 
						DOntology.Domain.NumericDomain.UNIT, domain.getUnit(), null)
				.triple(attrURI, DOntology.Attribute.HAS_VALUES_FROM, domainURI)
				.triple(domainURI, DOntology.Domain.SPECIES, species);
		}
	}

	private void createTemporalDomain(TemporalDomain domain,
			SparqlUpdateBuilder update, String attrURI) 
	{
		String baseDomainURI = this.store.createURI(DOntology.BASE + "domain");
		int i = 0;
		for(String species : domain.getRanges().keySet() )
		{
			String domainURI = baseDomainURI + "_" + (i++) ;
			update.type(domainURI, DOntology.Domain.TemporalDomain.TYPE)
				.triple(domainURI, 
						DOntology.Domain.TemporalDomain.LOWER_BOUND, 
						domain.getStartTime(species), null)
				.triple(domainURI, 
						DOntology.Domain.TemporalDomain.UPPER_BOUND, 
						domain.getEndTime(species), null)
				.triple(domainURI, 
						DOntology.Domain.TemporalDomain.PRECISION, 
						TimeFormat.fromTemporalPrecision(domain.getPrecision()))
				.triple(domainURI, 
						DOntology.Domain.TemporalDomain.FORMAT, 
						domain.getFormat(), null)
				.triple(attrURI, DOntology.Attribute.HAS_VALUES_FROM, domainURI)
				.triple(domainURI, DOntology.Domain.SPECIES, species);
		}
	}


	
	public static NumericPrecision toNumericPrecision(String precision)
	{
		if( precision.equals(
				DOntology.Domain.NumericDomain.NumericPrecision.INTEGER) )
			return NumericPrecision.INTEGER; 
		else //if (precision == NumericPrecision.REAL )
			return NumericPrecision.REAL;
	}
	

	
	public static String fromNumericPrecision(NumericPrecision precision)
	{
		if(precision == NumericPrecision.INTEGER)
			return DOntology.Domain.NumericDomain.NumericPrecision.INTEGER;
		else //if (precision == NumericPrecision.REAL )
			return DOntology.Domain.NumericDomain.NumericPrecision.REAL;
	}

	@Override
	public List<String> getSpecies() 
	{
		SparqlQueryBuilder q = new SparqlQueryBuilder();
		
		q.select(true, "?species")
		.where(
			q.triple( "?cat" , q.$(RDF.type.getURI()), q.$( DOntology.Domain.CategoricDomain.CategoricValue.TYPE) , "."),
			q.triple( "?cat" , q.$(DOntology.Domain.CategoricDomain.CategoricValue.VALUE), "?value" , "."),
				q.triple( "?cat" , q.$( DOntology.Domain.CategoricDomain.CategoricValue.SPECIES) , "?species", "")
		);
		List<List<String>> rawResults = this.store.executeRead( q.sparql(), "?species");	
	
		List<String> flattened = new LinkedList<String>();
		rawResults.stream().forEach(
			x -> x.stream().forEach( y -> flattened.add(y) )
		);
		
		return flattened;
	}









	
	
	
	
	
	
	
	

	
}
