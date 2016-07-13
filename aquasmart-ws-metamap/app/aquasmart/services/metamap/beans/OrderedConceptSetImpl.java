package aquasmart.services.metamap.beans;

import java.util.List;

import aquasmart.services.metamap.utils.STATIC.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Implementation of OrderedConceptSet using 
 * a java.util.LinkedList to store the concepts 
 * @author pedroamaral
 *
 */
public class OrderedConceptSetImpl implements OrderedConceptSet {

	private List<Concept> concepts;
	
	
	public OrderedConceptSetImpl(List<Concept> concepts) {
		super();
		this.concepts = concepts;
	}

	@Override
	public JsonNode toJson() {
		
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode node = factory.objectNode(); 
		
		ArrayNode arr = node.putArray(Json.OUTPUT.SEARCH_CONCEPT.RESULTS);
		
		for(Concept c : concepts)
			arr.add(c.toJson());
		
		return node;
	}

	@Override
	public List<Concept> getConcepts() 
	{
		return this.concepts;
	}

}
