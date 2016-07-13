package aquasmart.services.datapull.beans.metaschema;

import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;

public class TemporalSamples implements DataSamples 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Date min, max, modalValue;	
	int modalValueFrequency;

	public TemporalSamples(Date min, Date max, Date modalValue,
			int modalValueFrequency) {
		super();
		this.min = min;
		this.max = max;
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

	public static TemporalSamples dummy()
	{
		return new TemporalSamples(null,null,null,0);
	}
	
}
