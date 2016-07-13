package utils.sparql.query;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

public class JenaExecution implements SparqlExecution
{
	private Model model;
	private ResultSet resSet;
	private QuerySolution solution;
	
	public JenaExecution(Model m, String sparql)
	{
		this.model = m;
		this.query(sparql);
	}
	
	private void query(String sparql)
	{
		Query query = QueryFactory.create(sparql);
		
		QueryExecution qexec = 
				QueryExecutionFactory.create(query,this.model);
		this.resSet = qexec.execSelect();
	}
	
	@Override
	public boolean hasNext()
	{
		return this.resSet.hasNext();
	}
	
	@Override
	public JenaExecution next()
	{
		this.solution = this.resSet.nextSolution();
		return this;
	}
	

	@Override
	public String getValue(String var)
	{
		if(this.solution.contains(var))
			return this.solution.get(var).toString();
		
		return null;
	}
	
}
