package utils.sparql.query;

import java.util.List;

import org.apache.jena.vocabulary.RDF;


/**
 * Represents a SPARQL query that can be constructed 
 * in a modular fashion
 * @author Pedro Amaral
 *
 */
public class SparqlQueryBuilder
{
	/**
	 * The final SPARQL query string
	 */
	private String query ;
	/**
	 * Lists that store the values that belong to
	 * the select and where clauses of the SPARQL query
	 */
	private List<String> select, where;
	/**
	 * Determines if the 
	 */
	boolean withDistinct;
	
	public SparqlQueryBuilder()
	{
		super();
		this.reset();
	}
	
	
	public void reset()
	{
		this.query = "";
		this.select = new java.util.LinkedList<String>();
		this.where = new java.util.LinkedList<String>();
		this.withDistinct = false;
	}
	
	/**
	 * Adds a set of selectors to the SELECT clause of the query
	 * @param distinct if the select should use distinct values clause
	 * @param selectors a set of selectors
	 * @return this
	 */
	public SparqlQueryBuilder select(boolean distinct, String... selectors)
	{
		this.withDistinct = this.withDistinct || distinct;
		for(String selector : selectors)
			select.add(selector);
		return this;
	}
	
	/**
	 * Adds a set of restrictions to the WHERE clause of the query
	 * @param clauses a set of restrictions (triple patterns)
	 * @return this
	 */
	public SparqlQueryBuilder where(String... clauses)
	{
		for(String clause : clauses)
			where.add(clause);
		return this;
	}
	
	/**
	 * Computes the final SPARQL query
	 * @return a java.lang.String containing a textual representation
	 * of the SPARQL query
	 */
	public String sparql()
	{
		String s = "SELECT ";
		s += (this.withDistinct ? "DISTINCT " : "" );
		
		for(String selector : select)
			s += selector + " ";
		
		s += "WHERE { ";
		
		for(String clause : where)
			s += clause + " ";
		
		query += s + "}";
		
		return query;
	}
	
	/**
	 * Constructs a SPARQL literal from the supplied parameter
	 * @param s the value to be converted to a SPARQL literal
	 * @return a java.lang.String containing a textual representation of the literal
	 */
	public String literal(String s)
	{
		return "\""+s+"\"";
	}
	
	/**
	 * Constructs a SPARQL-compliant URI from the supplied parameter
	 * @param uri a complete URI
	 * @return a SPARQL-compliant URI as a java.lang.String
	 */
	public String $(String uri)
	{
		return "<"+uri+">";
	}
	
	/**
	 * Constructs a SPARQL triple ended by the value of the parameter end
	 * @param subject the subject of the triple (must be a SPARQL-compliant URI or alias)
	 * @param prop the predicate/property of the triple (must be a SPARQL-compliant URI or alias)
	 * @param obj the object of the triple
	 * @param end " ", "." or ",", following the SPARQL syntax
	 * @return a java.lang.String containing a textual representation
	 * of the SPARQL triple
	 */
	public String triple(
			String subject, String prop, String obj, String end)
	{
		return subject + " " + prop + " " + obj + " " +  end ;
	}
	
	
	public String type( String subject, String type, String end)
	{
		return triple(subject, $(RDF.type.getURI()), type, end);
	}
	
	/**
	 * Concatenates a set of triples into the same value.
	 * This methods provides essentially syntactic-sugar for the framework
	 * @param stringTriples a set of triples
	 * @return a java.lang.String containing the concatenation of 
	 * the parametrized triples
	 */
	public String triples(String... stringTriples)
	{
		String concatenated = "";
		for(String trip : stringTriples)
			concatenated += trip + " ";
		return concatenated;
	}

	/**
	 * Constructs a UNION condition clause from the given parameters
	 * @param first the first clause or concatenated set of clauses
	 * @param second the second clause or concatenated set of clauses
	 * @return a java.lang.String containing a textual representation
	 * of the SPARQL-compliant UNION clause
	 */
	public String union(String first, String second) 
	{
		String result = "{ " ;
		result += first ;
		result += " } UNION { " ;
		result += second;
		result += " }" ;
		return result;
	}

	/**
	 * Constructs a SPARQL-compliant REGEX clause to be used inside filter conditions
	 * @param target the target of the regex test
	 * @param regex the regex expression to be tested
	 * @param caseSensitive true if the comparison should be case sensitive 
	 * @return a java.lang.String containing the SPARQL-compliant textual representation
	 * of the REGEX clause
	 */
	public String regex(String target, String regex, boolean caseSensitive) {
		
		return 
			"regex( " + target + " , " + regex + 
			(caseSensitive ? "" : ", \"i\"") + " )";
	}

	/**
	 * Constructs a SPARQL-compliant FILTER clause (works for multiple tests)
	 * @param isOr when using multiple FILTER tests, has value "true" if the conjunction
	 * is an OR and "false" if its an AND 
	 * @param regexes a set of regular expressions
	 * @return a java.lang.String representation of the SPARQL-compliant FILTER clause
	 */
	public String filter(boolean isOr, String... regexes) 
	{
		String boolOp = isOr ? " || " : " && ";
		
		String result = "FILTER ( ";
		
		for( int i = 0 ; i < regexes.length ; i++ )
		{
			if( i != 0 && i != regexes.length-1)
				result += boolOp;
			result += regexes[i];
		}
		
		result += " )";
		return result;
	}
	
	public String[] getAliases()
	{
		return this.select.toArray( new String[this.select.size()] );
	}

	public String optional(String... triples) 
	{
		String opt = "OPTIONAL {";
		
		for(String triple : triples)
			opt += " " + triple + " ";
		
		return opt + "}";
	}
	
}