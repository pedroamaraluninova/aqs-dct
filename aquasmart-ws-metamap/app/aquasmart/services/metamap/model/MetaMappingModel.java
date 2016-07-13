package aquasmart.services.metamap.model;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import utils.tuples.Tuple2;
import aquasmart.services.metamap.beans.Concept;
import aquasmart.services.metamap.beans.Domain;
import aquasmart.services.metamap.beans.OrderedConceptSet;
import aquasmart.services.metamap.beans.TimeFormat;
import aquasmart.services.metamap.model.exception.InexistentAttributeException;

public interface MetaMappingModel 
{	
	public OrderedConceptSet searchConceptsByKeyword(String query);
	
	public String assignAttributeToDataset(String datasetURI, int columnIndex, String conceptURI);

	public String createAnonymousConcept(String name);

	public String createConcept(String name, String desc, String lang);

	public String createDataset(String datasetName, List<TimeFormat> formats);
	
	public String createDataset(String datasetID, String datasetName, List<TimeFormat> formats);

	public Domain getDomainInfo(String attributeURI, String datasetURI, String label, HashMap<String,String> tempSpeciesMap) throws ParseException;

	public String createAttribute(String name, String conceptURI,
			String domainType, Domain domain);

	public Tuple2<String, String> getAttribute(String datasetAttributeURI);

	public String mapToAttribute(String headerName) throws InexistentAttributeException;

	public String getAttributeInfo(String attrURI);

	public Concept getConcept(String conceptURI);

	public HashMap<String,String> getSpeciesMap();
	
	public HashMap<String,String> getSpeciesMap( HashMap<String,String> existingMappings );
	
	public List<String> getSpecies();

	public void addDomainToAttribute(String attrURI, Domain absDomain);

	public void updateSpeciesValue(String speciesURI, String value);
}
