package aquasmart.services.metamap.beans;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utils.tuples.Tuple2;
import utils.tuples.Tuple3;
import aquasmart.services.metamap.beans.statistics.Statistics;
import aquasmart.services.metamap.utils.STATIC.*;

import com.fasterxml.jackson.databind.JsonNode;


public abstract class Domain
{
	public enum DomainType { CATEGORICAL, TEXTUAL, NUMERIC, TEMPORAL };
	
	public DomainType type;
	
	public HashMap<String,String> speciesMap;
	
	protected String label;
	
	public Domain(DomainType type, HashMap<String,String> speciesMap, String label)
	{
		this.type = type;
		this.speciesMap = speciesMap;
		this.label = label;
	}
	
	public DomainType getType()
	{
		return this.type;
	}
	
	public abstract ValidationScenario validate(String value, String species) ;
	
	public abstract String getDataType();
	
	//return triple with reason (do Outlier class!!!)
	//isValid must return pair of boolean (isValid) and the reason for not being valid
	public List<Outlier> detectOutliers(Stream<Tuple3<String,String,String>> data)
	{ 
		return data
				.distinct()
				.map( x -> new Tuple3<String,String,String>( x.one, x.two.replace("\"","").replace("\\", "/"), x.three ) )
				.filter( x -> ! x.one.trim().equals("") )
				.map( x -> new Tuple2<String,ValidationScenario>( x.three, validate( x.one , x.two ) ) )
				.filter( x -> ! x.two.isValid() )
				.map( x ->  x.two.generateOutlier( x.one ) )
				.collect(Collectors.toList());
	}

	public static Domain fromJson(String domainType, JsonNode domainJson) throws ParseException 
	{
		if(domainType.equals(Domains.CATEGORIC))
			return CategoricalDomain.instanceFromJson(domainJson);
		else if(domainType.equals(Domains.TEMPORAL))
			return TemporalDomain.instanceFromJson(domainJson);
		else if(domainType.equals(Domains.NUMERIC))
			return NumericDomain.instanceFromJson(domainJson);
		else //if(domainType.equals(STATIC.Domains.TEXTUAL))
			return TextualDomain.instanceFromJson(domainJson);
	}

	public abstract void produceStats(List<String> values);
	
	public abstract Statistics getStatistics();
}
