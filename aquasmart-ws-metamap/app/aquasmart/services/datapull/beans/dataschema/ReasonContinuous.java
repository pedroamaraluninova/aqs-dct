package aquasmart.services.datapull.beans.dataschema;

import utils.json.JSON;
import aquasmart.services.metamap.utils.STATIC.DatasetService.OUTPUT.Data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReasonContinuous implements Reason {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String min, max, value, columnLabel;
	
	

	public ReasonContinuous(String min, String max, String value,
			String columnLabel) {
		super();
		this.min = min;
		this.max = max;
		this.value = value;
		this.columnLabel = columnLabel;
	}



	@Override
	public JsonNode toJson() {
		ObjectNode root = JSON.newObject();
		
		root.put( Data.DataRow.Reason.LABEL, columnLabel); 
		root.put( Data.DataRow.Reason.VALUE, value);
		root.put( Data.DataRow.Reason.MIN, min);
		root.put( Data.DataRow.Reason.MAX, max);
		
		return root;
		
	}

}
