package aquasmart.services.metamap.beans;

import java.util.List;
import aquasmart.services.metamap.utils.STATIC.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Implementation of the Concept interface
 * @author pedroamaral
 *
 */
public class ConceptImpl implements Concept 
{

	
	private String name, description, uri;
	
	public ConceptImpl(String name, String description, String uri) {
		super();
		this.name = name;
		this.description = description;
		this.uri = uri;
	}

	@Override
	public JsonNode toJson() 
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode node = factory.objectNode();
		
		node.put(Json.OUTPUT.SEARCH_CONCEPT.NAME, this.name);
		node.put(Json.OUTPUT.SEARCH_CONCEPT.DESCRIPTION, this.description);
		node.put(Json.OUTPUT.SEARCH_CONCEPT.CONCEPT_URI, this.uri);
		
		return node;
	}

	@Override
	public String getName() 
	{
		return this.name;
	}

	@Override
	public String getDescription() 
	{
		return this.description;
	}

	@Override
	public String getURI() 
	{
		return this.uri;
	}


	public static Concept create(List<String> result) 
	{
		String name = result.get(1),
				desc = "Not available (yet?)",
				conceptURI = result.get(0);
		return new ConceptImpl(name,desc,conceptURI);
	}

}
