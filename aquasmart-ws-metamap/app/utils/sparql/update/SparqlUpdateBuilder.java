package utils.sparql.update;


import java.util.LinkedList;
import java.util.List;

import org.apache.jena.vocabulary.RDF;



public class SparqlUpdateBuilder
{
	

	public List<SparqlTriple> updates;

	
	public SparqlUpdateBuilder()
	{
		this.updates = new LinkedList<SparqlTriple>();

	}
	
	/**
	 * Triple with resource object without language
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return this
	 */
	public SparqlUpdateBuilder triple(String subject, String predicate, String object) {
		this.updates.add(new SparqlTriple(subject,predicate,object));
		return this;
	}
	
	
	/**
	 * Triple with literal object with (optional) language
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param lang if null the literal is considered to have no language tag
	 * @return this
	 */
	public SparqlUpdateBuilder triple(String subject, String predicate, 
			String object, String lang) 
	{
		this.updates.add(new SparqlTriple(subject,predicate,object,lang));
		return this;
	}

	public SparqlUpdateBuilder type(String subject, String type)
	{
		this.updates.add(new SparqlTriple(subject, RDF.type.getURI(), type));
		return this;
	}

	@Override
	public String toString()
	{
		String result = "";
		
		for( SparqlTriple trip : updates)
			result += trip.toString() + "\n";
		
		return result;
	}







	
}
