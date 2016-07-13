package utils.json;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import utils.tuples.*;

/**
 * Helper class that provides static methods for cleaner
 * JSON generation supported by the jackson framework
 * @author Pedro Amaral
 *
 */
public class JSON {

	
	/**
	 * Creates a new JSON object containing the parametrized tag
	 * associated with the parametrized value
	 * @param tag a name for a json tag
	 * @param val the value for the tag in the json object
	 * @return a jackson ObjectNode containing the newly created object
	 */
	public static ObjectNode newObject(String tag, String val)
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode node = factory.objectNode();
		node.put(tag, val);
		return node;
	}
	

	
	public static ObjectNode newObject()
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode node = factory.objectNode();

		return node;
	}
	
	public static ArrayNode newArray(ObjectNode parentNode, String tag)
	{
		return parentNode.putArray(tag);
	}
	
	public static ObjectNode stringToJson(String json) throws JsonProcessingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		ObjectReader r = mapper.reader().without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	
		return (ObjectNode) r.readTree(json);
		
	}
	
	public static Stream<String> arrayAsStream(ArrayNode array)
	{
		return arrayAsList(array).parallelStream();
		
	}
	
	public static Stream<Tuple2<String,String>> arrayAsStream(ArrayNode array, Function<JsonNode,Tuple2<String,String>> ind)
	{
		return arrayAsList(array,ind).parallelStream();
		
	}

	public static List<String> arrayAsList(ArrayNode array) 
	{
		List<String> vals = new LinkedList<String>();
		
		for(JsonNode node : array)
			vals.add(node.asText());
		
		return vals;
	}
	
	public static List<Tuple2<String,String>> arrayAsList(ArrayNode array, Function<JsonNode,Tuple2<String,String>> ind)
	{
		List<Tuple2<String,String>> vals = new LinkedList<Tuple2<String,String>>();
		
		for(JsonNode node : array)
			vals.add(ind.apply(node));
		
		return vals;
	}
	

	
	
}
