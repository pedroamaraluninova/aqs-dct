package aquasmart.services.datapull.beans.dataschema;

import utils.json.JSON;
import aquasmart.services.metamap.utils.STATIC.DatasetService.OUTPUT.Data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReasonEmpty implements Reason {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	String label, value;
	
	
	
	public ReasonEmpty(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}



	@Override
	public JsonNode toJson() {
		ObjectNode root = JSON.newObject();
		
		root.put( Data.DataRow.Reason.LABEL, label);
		root.put( Data.DataRow.Reason.VALUE, value );
		root.put( Data.DataRow.Reason.MESSAGE, "missing value" ); 
		
		return root;
	}

}
