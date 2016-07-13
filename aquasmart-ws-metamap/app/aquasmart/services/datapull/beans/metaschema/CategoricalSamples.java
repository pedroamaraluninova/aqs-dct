package aquasmart.services.datapull.beans.metaschema;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public class CategoricalSamples implements DataSamples {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String modalValue;
	List<String> allowedValues;

	
	
	public CategoricalSamples(String modalValue, List<String> allowedValues) {
		super();
		this.modalValue = modalValue;
		this.allowedValues = allowedValues;
	}

	@Override
	public JsonNode toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSamples fromJson(JsonNode root) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static CategoricalSamples dummy()
	{
		return new CategoricalSamples(null,null);
	}

}
