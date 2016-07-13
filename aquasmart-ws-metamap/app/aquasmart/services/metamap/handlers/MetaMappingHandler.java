package aquasmart.services.metamap.handlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import utils.tuples.Tuple2;
import utils.tuples.Tuple3;
import aquasmart.services.metamap.beans.Concept;
import aquasmart.services.metamap.beans.Domain;
import aquasmart.services.metamap.beans.OrderedConceptSet;
import aquasmart.services.metamap.beans.Outlier;
import aquasmart.services.metamap.beans.SpeciesList;
import aquasmart.services.metamap.beans.TimeFormat;
import aquasmart.services.metamap.model.exception.InexistentAttributeException;

/**
 * Handler interface that defines the behavior of a 
 * class that executes the main operations of the service
 * @author Pedro Amaral
 *
 */
public interface MetaMappingHandler
{

	/**
	 * Searches the semantic triple store for a concept that 
	 * matches the parameterized keyword
	 * @param keyword
	 * @return
	 */
	public OrderedConceptSet searchAvailableConcepts(String keyword);
	
	/**
	 * Updates the semantic triple store with the information about 
	 * the dataset identified by datasetID having the column at columnIndex
	 * and with name colName associated with the meaning encompassed by the concept
	 * identified by conceptURI
	 * @param datasetID
	 * @param columnIndex
	 * @param colName
	 * @param conceptURI
	 * @return the identifier for the newly created dataset_attribute entity
	 */
	public String selectConcept(String datasetID, int columnIndex, String colName, 
			String conceptURI);
	
	/**
	 * Creates a new concept in the ontology
	 * @param name the name of the concept
	 * @param desc the description of the concept
	 * @param lang the language of the text of the two previous parameters
	 * @param isAnonymous a boolean value that determines if this concept should be anonymous
	 * @return a java.lang.String containing the textual representation of the URI of the
	 * newly created concept
	 */
	public String createConcept(String name, String desc, String lang, 
			boolean isAnonymous);
 
	/**
	 * Creates a new dataset in the ontology knowledge base
	 * @param datasetName the name of the dataset
	 * @param formats a set of time value formats associated with a specific temporal precision
	 * @return an URI corresponding to the newly created dataset reference in the ontology
	 */
	public String createDataset(String datasetName, List<TimeFormat> formats);

	
	/**
	 * Creates a new dataset in the ontology knowledge base
	 * @param datasetID a unique identifier for this dataset
	 * @param datasetName the name of the dataset
	 * @param formats a set of time value formats associated with a specific temporal precision
	 * @return an URI corresponding to the newly created dataset reference in the ontology
	 */
	public String createDataset(String datasetID, String datasetName, List<TimeFormat> formats);

	
	/**
	 * Checks the values in "stream" against the allowed domain defined by the attribute
	 * identified by "attributeURI"
	 * @param datasetURI the identifier of the dataset
	 * @param attributeURI the identifier of the attribute
	 * @param stream a java.util.Stream<String> containing all the values that instantiate
	 * the attribute in a dataset
	 * @return
	 * @throws ParseException 
	 */
	public List<Outlier> checkForOutliers(String datasetURI, String attributeURI, 
			Stream<Tuple3<String,String,String>> stream, String label, HashMap<String,String> tempSpeciesMap)  
			throws ParseException; 

	
	/**
	 * Creates a new attribute in the persistent data layer
	 * @param name the name of the attribute
	 * @param conceptURI the identifier for the concept associated with this attribute
	 * @param domainType the type of the declared domain ("Temporal", "Textual", "Numeric", "Categoric"
	 * @param domain an instance of Domain containing the information about the domain of this attribute
	 * @return the identifier for the newly created attribute
	 */
	public String createAttribute(String name, String conceptURI,
			String domainType, Domain domain);

	/**
	 * Computes a set of statistics for the attribute instantiation in the dataset
	 * @param attributeURI the identifier of the attribute of the dataset
	 * @param values a java.util.List<String> containing all the values of the
	 * column of the dataset that represents the attribute identified by "attributeURI"
	 * @throws ParseException 
	 */
	public Domain produceStatistics(String attributeURI, List<String> values,
			String datasetURI, String columnLabel, HashMap<String,String> tempSpeciesMap)  throws ParseException;

	
	/**
	 * Maps the parameterized dataset attribute identifier to an
	 * attribute definition identifier and a dataset identifier
	 * @param datasetAttributeURI the identifer for the dataset attribute
	 * @return a tuple containing the datasetID and the attributeID, in this order
	 */
	public Tuple2<String,String> getAttribute(String datasetAttributeURI);

	/**
	 * Maps the parameterized table header name to an attribute
	 * @param headerName the value of the table header
	 * @return an URI to the attribute
	 * @throws InexistentAttributeException if the header name does not match
	 * any existing attribute in the model
	 */
	public String mapToAttribute(String headerName) throws InexistentAttributeException;

	public String getAttributeInfo(String datasetAttributeID);
	
	public String attributeInfo(String attributeID);

	public Concept getConcept(String conceptURI);

	public SpeciesList getAllSpecies();

	void updateSpeciesValue(String speciesURI, String value) throws FileNotFoundException, IOException;

	public String createSpecies(String speciesName) throws IOException;


	

	
}
