package aquasmart.services.datapull.beans.dataschema;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import utils.json.JsonDeserializable;
import utils.json.JsonSerializable;

public class DataItem implements Serializable, JsonSerializable,
		JsonDeserializable<DataItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	String status, systemGeneratedID;
	
	int index;
	
	String code;

	List<Reason> failureReasons;
	
	@Override
	public DataItem fromJson(JsonNode root) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonNode toJson() {
		// TODO Auto-generated method stub
		return null;
	}

}
