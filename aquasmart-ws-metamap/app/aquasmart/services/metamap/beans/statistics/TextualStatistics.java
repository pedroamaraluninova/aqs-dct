package aquasmart.services.metamap.beans.statistics;


import java.util.List;

import aquasmart.services.metamap.stats.Frequency;
import aquasmart.services.metamap.utils.STATIC.Domains;
import aquasmart.services.metamap.utils.STATIC.Json.OUTPUT;
import utils.json.JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TextualStatistics extends Statistics<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Frequency<String>> valueDistribution;
	public TextualStatistics(List<Frequency<String>> valueDistribution) 
	{
		this.valueDistribution = valueDistribution;
	}

	
	
	@Override
	public JsonNode toJson() 
	{
		ObjectNode root = JSON.newObject();
		
		root.set( OUTPUT.STATISTICS.VALUE_DISTRIBUTION,
				super.convert(valueDistribution));
		root.set( OUTPUT.STATISTICS.MODE , convert( getMode() ) );
		root.put( OUTPUT.STATISTICS.TYPE, Domains.TEXTUAL );
		
		return root;
	}


	private Frequency<String> getMode() {
		return valueDistribution.get(0);
	}

	@Override
	public ObjectNode convert(Frequency<String> freq) 
	{
		ObjectNode jsonFreq = JSON.newObject();
		jsonFreq.put(OUTPUT.STATISTICS.FREQUENCY, freq.frequency);
		jsonFreq.put(OUTPUT.STATISTICS.VALUE, freq.value);
		return jsonFreq;
	}

}
