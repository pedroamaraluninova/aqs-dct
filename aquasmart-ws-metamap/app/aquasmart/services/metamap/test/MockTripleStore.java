package aquasmart.services.metamap.test;

import java.util.Arrays;
import java.util.List;

import utils.sparql.TripleStore;
import utils.DebugUtils;
import utils.sparql.update.SparqlUpdateBuilder;

public class MockTripleStore extends DebugUtils implements TripleStore{

	
	public MockTripleStore()
	{
		//NOTHING
	}

	@Override
	public List<List<String>> executeRead(String query, String... selected) 
	{
		out("searchByKeyword", query, selected.toString());
		List<String> first = Arrays.asList("concept-1","desc-1","URI-1"),
				second = Arrays.asList("concept-2","desc-2","URI-2"),
				third = Arrays.asList("concept-3","desc-3","URI-3");
		
		return Arrays.asList(first,second,third);
	}

	@Override
	public void executeWrite(SparqlUpdateBuilder update) 
	{
		out("assignAttributeToDataset", update.toString());
		
	}

	@Override
	public String createURI(String base) 
	{
		out("createConcept", base);
		return base;
	}

}
