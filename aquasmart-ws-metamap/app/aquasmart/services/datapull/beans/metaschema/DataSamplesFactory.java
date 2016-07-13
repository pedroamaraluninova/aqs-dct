package aquasmart.services.datapull.beans.metaschema;

import com.fasterxml.jackson.databind.JsonNode;

public class DataSamplesFactory 
{

	
	
	public static DataSamples fromJson( JsonNode json )
	{
		String sampleDataType = json.get( "type" ).asText();
		
		if(sampleDataType.equals( "number" ))
			return NumericSamples.dummy().fromJson(json);
		else if(sampleDataType.equals( "time" ))
			return TemporalSamples.dummy().fromJson(json);
		else
			return CategoricalSamples.dummy().fromJson(json);
	}
}
