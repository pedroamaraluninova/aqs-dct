package aquasmart.services.metamap.beans.statistics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import aquasmart.services.metamap.stats.Frequency;
import aquasmart.services.metamap.utils.STATIC.Domains;
import aquasmart.services.metamap.utils.STATIC.Json.OUTPUT;
import utils.json.JSON;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TemporalStatistics extends Statistics<Date> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Frequency<Date>> valueDistribution;
	Date min, max;
	
	public TemporalStatistics(List<Frequency<Date>> dist, Date min, Date max) 
	{
		this.valueDistribution = dist;
		this.max = max;
		this.min = min;
	}

	@Override
	public JsonNode toJson() {
		ObjectNode root = JSON.newObject();
		
		root.set( OUTPUT.STATISTICS.VALUE_DISTRIBUTION,
				super.convert(valueDistribution));
		root.put( OUTPUT.STATISTICS.MIN, convert(min) );
		root.put( OUTPUT.STATISTICS.MAX, convert(max) );
		root.set( OUTPUT.STATISTICS.MODE , convert( getMode() ) );
		root.put( OUTPUT.STATISTICS.TYPE, Domains.TEMPORAL );
		
		return root;
	}
	
	private Frequency<Date> getMode() {
		return valueDistribution.get(0);
	}

	private String convert(Date d)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(d);
	}

	@Override
	public ObjectNode convert(Frequency<Date> freq) {
		ObjectNode jsonFreq = JSON.newObject();
		jsonFreq.put(OUTPUT.STATISTICS.FREQUENCY, freq.frequency);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		jsonFreq.put(OUTPUT.STATISTICS.VALUE, formatter.format(freq.value) );
		return jsonFreq;

	}

}
