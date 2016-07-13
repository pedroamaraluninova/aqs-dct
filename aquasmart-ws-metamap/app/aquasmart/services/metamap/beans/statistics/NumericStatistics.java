package aquasmart.services.metamap.beans.statistics;


import java.util.List;

import aquasmart.services.metamap.stats.Frequency;

import aquasmart.services.metamap.utils.STATIC.Domains;
import aquasmart.services.metamap.utils.STATIC.Json.OUTPUT;
import utils.json.JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NumericStatistics extends Statistics<Double>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Frequency<Double>> valueDistribution;
	double min, max, average;
	
	public NumericStatistics(List<Frequency<Double>> valueDist, double min,
			double max, double average) 
	{
		this.valueDistribution = valueDist;
		this.min = min;
		this.max = max;
		this.average = average;
	}

	@Override
	public JsonNode toJson() 
	{
		ObjectNode root = JSON.newObject();
		
		root.set( OUTPUT.STATISTICS.VALUE_DISTRIBUTION,
				super.convert(valueDistribution));
		root.put( OUTPUT.STATISTICS.MIN, min );
		root.put( OUTPUT.STATISTICS.MAX, max );
		root.put( OUTPUT.STATISTICS.AVERAGE, average);
		root.set( OUTPUT.STATISTICS.MODE , convert( getMode() ) );
		root.put( OUTPUT.STATISTICS.TYPE, Domains.NUMERIC );
		
		return root;
	}

	private Frequency<Double> getMode() {
		return valueDistribution.get(0);
	}
	
	@Override
	public ObjectNode convert(Frequency<Double> freq) {
		ObjectNode jsonFreq = JSON.newObject();
		jsonFreq.put(OUTPUT.STATISTICS.FREQUENCY, freq.frequency);
		jsonFreq.put(OUTPUT.STATISTICS.VALUE, freq.value);
		return jsonFreq;

	}

}
