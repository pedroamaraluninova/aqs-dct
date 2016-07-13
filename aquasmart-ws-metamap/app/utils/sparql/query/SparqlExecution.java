package utils.sparql.query;

/**
 * This interface defines the behaviour of
 * objects that consist of an execution of a SPARQL query
 * @author Pedro Amaral
 *
 */
public interface SparqlExecution 
{
	/**
	 * Returns the result of checking if there are
	 * results available for processing
	 * @return a boolean value
	 */
	public boolean hasNext();
	
	/**
	 * Advances the current line being processed to the next
	 * line available in results.
	 * PRE-CONDITION: hasNext() == true 
	 * @return this
	 */
	public SparqlExecution next();
	
	/**
	 * Returns a java.lang.String representation of the value
	 * identified in the SPARQL query as var
	 * @param var a variable alias of the SPARQL query
	 * @return null if there is no binding for the parameterized variable name
	 */
	public String getValue(String var);
}
