package aquasmart.services.metamap.beans;


import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import utils.json.JsonSerializable;
import utils.tuples.Tuple2;
import aquasmart.services.metamap.utils.STATIC.Json.OUTPUT;

public class SpeciesList implements JsonSerializable 
{

	
	List<String> species;
	List<Tuple2<String,List<String>>> map;
	
	public SpeciesList( List<String> species, List<Tuple2<String,List<String>>> map )
	{
		this.species = species;
		this.map = map;
	}
	
	@Override
	public JsonNode toJson() 
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode root = factory.objectNode();
		ArrayNode arr = root.putArray(OUTPUT.GET_SPECIES.RESULTS);
		ArrayNode mapArr = root.putArray(OUTPUT.GET_SPECIES.MAP);
		
		species.stream().forEach(
			x -> arr.add(x)
		); 
		
		map.stream().forEach(
			x -> {
				ObjectNode node = factory.objectNode();
				node.put( "key", x.one );
				ArrayNode values = node.putArray( "value" );
				x.two.stream().forEach( y -> values.add(y) );
				mapArr.add( node );
			}
		);
		
		return root;
	}
	
	
	public JsonNode dummy() 
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode root = factory.objectNode();
		ArrayNode arr = root.putArray(OUTPUT.GET_SPECIES.RESULTS);
		ArrayNode mapArr = root.putArray(OUTPUT.GET_SPECIES.MAP);
		
		Arrays.asList("http://ble#Bream","http://ble#Bass").stream().forEach(
			x -> arr.add(x)
		); 
		
	
	
		mapArr.add( makeMapEntry("http://ble#Bream","Bream","SEA_BREAM") );
		mapArr.add( makeMapEntry("http://ble#Bass","BASS","SEA_BASS") );
		
		return root;
	}

	private ObjectNode makeMapEntry(String key, String ... vals)
	{
		JsonNodeFactory factory = JsonNodeFactory.instance;
		ObjectNode node = factory.objectNode();
		node.put( "key", key);
		ArrayNode values = node.putArray( "value" );
		Arrays.asList(vals).stream().forEach( y -> values.add(y) );
		return node;
	}
}
