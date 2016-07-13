package aquasmart.services.datapull.beans.metaschema;

import com.fasterxml.jackson.databind.JsonNode;

public class NumericSamples implements DataSamples {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String min, max, average, modalValue; 
	int modalValueFrequency;
	
	

	public NumericSamples(String min, String max, String average,
			String modalValue, int modalValueFrequency) {
		super();
		this.min = min;
		this.max = max;
		this.average = average;
		this.modalValue = modalValue;
		this.modalValueFrequency = modalValueFrequency;
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
	
	public static NumericSamples dummy()
	{
		return new NumericSamples(null,null,null,null,0);
	}

}
