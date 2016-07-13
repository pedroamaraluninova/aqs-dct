package utils.json;

import com.fasterxml.jackson.databind.JsonNode;

public interface JsonDeserializable<X> 
{

	
	public X fromJson( JsonNode root);
}
