package aquasmart.services.datapull.beans.dataschema;

import java.util.List;

import utils.json.JSON;
import aquasmart.services.metamap.utils.STATIC.DatasetService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReasonDiscrete implements Reason 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	String columnLabel, value;
	
	List<String> allowedValues;

 
	
	
	public ReasonDiscrete(String columnLabel, String value,
			List<String> allowedValues) {
		super();
		this.columnLabel = columnLabel;
		this.value = value;
		this.allowedValues = allowedValues;
	}




	@Override
	public JsonNode toJson() 
	{
		ObjectNode root = JSON.newObject();
		
		root.put( DatasetService.OUTPUT.Data.DataRow.Reason.LABEL, columnLabel );
		root.put( DatasetService.OUTPUT.Data.DataRow.Reason.VALUE, value);
		ArrayNode allowed = root.putArray( DatasetService.OUTPUT.Data.DataRow.Reason.ALLOWED);
		
		for( String allow : allowedValues )
			allowed.add( allow );
		
		return root;
	}

}
