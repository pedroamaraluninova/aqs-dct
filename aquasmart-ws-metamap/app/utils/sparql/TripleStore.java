package utils.sparql;

import java.util.List;

import utils.sparql.update.SparqlUpdateBuilder;

public interface TripleStore 
{

	public List<List<String>> executeRead(String query, String... selected);
	
	public void executeWrite(SparqlUpdateBuilder update);
	
	/**
	 * Creates a new unique URI from the base URI provided and the id. 
	 * This method tests the existing triple base so it can provide a truly unique URI 
	 * @param base 
	 * @return a java.lang.String representation of the newly created URI
	 */
	public String createURI(String base);


}
