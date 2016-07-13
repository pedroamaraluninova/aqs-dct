package utils.sparql;

import java.util.LinkedList;
import java.util.List;

import utils.sparql.update.SparqlUpdateBuilder;

public class FusekiTripleStore implements TripleStore
{
	
	private List<String> triples;
	
	private String serverAddress;
	
	public FusekiTripleStore(String fusekiLocation)
	{
		this.serverAddress = fusekiLocation;
		this.triples = new LinkedList<String>();
	}


	private String compileUpdate() 
	{
		String compiled = "INSERT DATA { ";
		
		for(String triple : triples)
			compiled += triple;
		
		compiled += "}";
		return compiled;
	}



	@Override
	public List<List<String>> executeRead(String query, String... selected) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void executeWrite(SparqlUpdateBuilder update) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public String createURI(String base) {
		// TODO Auto-generated method stub
		return null;
	}


	
	
	
}
