package aquasmart.services.metamap.beans;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aquasmart.services.metamap.beans.statistics.Statistics;
import aquasmart.services.metamap.beans.statistics.TextualStatistics;
import aquasmart.services.metamap.stats.Frequency;
import aquasmart.services.metamap.utils.STATIC.DOntology;

import com.fasterxml.jackson.databind.JsonNode;

public class TextualDomain extends Domain 
{
	private Stream<Frequency<String>> valueDistribution;
	private Statistics<String> stats;
	public TextualDomain(String columnLabel)
	{
		super(DomainType.TEXTUAL, null, columnLabel);
		this.stats = null;
	}
	
	

	@Override
	public ValidationScenario validate(String value, String species) 
	{
		//everything is valid, the textual domain has no bounds
		return new ValidationScenario(true);
	}

	public static Domain instanceFromJson(JsonNode domainJson) 
	{
		return new TextualDomain(null);
	}

	@Override
	public String getDataType() {
		return DOntology.Attribute.DataType.TEXTUAL_TYPE;
	}
	
	/**
	 * PRE: produceStats must be called 
	 * Returns an ordered collection of pairs of value and frequency of 
	 * occurence in the dataset
	 * @return a java.util.Stream<Frequency<String>> containing the ordered
	 * results
	 */
	public Stream<Frequency<String>> getValueDistribution()
	{
		return this.valueDistribution;
	}
	
	@Override
	public void produceStats(List<String> values) 
	{
		this.valueDistribution = 
			values.parallelStream().collect( Collectors.groupingBy( x -> x) )
				.entrySet().parallelStream()
				.map( x -> new Frequency<String>( x.getKey(), x.getValue().size() ) )
				.sorted( (x,y) -> new Integer(x.frequency).compareTo(y.frequency)*-1 );
	}



	@Override
	public Statistics<String> getStatistics() 
	{
		if(stats != null)
			return stats;
		stats = new TextualStatistics(
				valueDistribution.collect( Collectors.toList() ) );
		return stats;
	}
	
}
