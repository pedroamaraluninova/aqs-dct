package utils.json;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface specification for objects
 * that include funcionality to convert its state
 * to JSON format
 * @author pedroamaral
 *
 */
public interface JsonSerializable
{
	/**
	 * Returns the JSON representation of this object
	 * @return a jackson JsonNode
	 */
	public JsonNode toJson();

}
