package aquasmart.services.metamap.beans;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;




import aquasmart.services.datapull.beans.dataschema.ReasonDiscrete;
import aquasmart.services.metamap.beans.range.CategoricRange;
import aquasmart.services.metamap.beans.statistics.CategoricalStatistics;
import aquasmart.services.metamap.beans.statistics.Statistics;
import aquasmart.services.metamap.stats.Frequency;
import aquasmart.services.metamap.utils.STATIC.DOntology;
import aquasmart.services.metamap.utils.STATIC.Json.INPUT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import play.Logger;


public class CategoricalDomain extends Domain 
{
	//species -> allowedValues
	private HashMap<String,CategoricRange> allowedValues;
	private Stream<Frequency<String>> valueDistribution;
	private Statistics<String> stats;

	
	public CategoricalDomain( HashMap<String,List<String>> allowedValues, HashMap<String,String> speciesMap, String label)
	{
		super(DomainType.CATEGORICAL, speciesMap, label);
		
		this.allowedValues = new HashMap<String,CategoricRange>();
		
		allowedValues.entrySet().stream().forEach(
			x -> {
				List<String> trimmed = x.getValue().stream().map(
					y -> y.trim()
				).collect(Collectors.toList());
				this.allowedValues.put(x.getKey(), new CategoricRange(trimmed));
			}	
		);
		
		this.stats = null;

//		allowedValues.entrySet().stream().forEach( 
//			x -> 
//				Logger.debug( "For key '"+x.getKey() + "' has vals:\n\tValues are: "+x.getValue().toString() )
//		);
	}
	
	
	public List<String> getAllowedValues(String species)
	{
		return this.allowedValues.get(species).valueRange;
	}
	
	public HashMap<String,CategoricRange> getAllowedValuesMap()
	{
		return this.allowedValues;
	}


	@Override
	public ValidationScenario validate(String value, String species) 
	{
		String speciesURI = speciesMap.get(species);
//		Logger.debug("isValid: mapped '"+species+"' to '"+speciesURI+"'");
		if(value.equals("raanan") || value.equals("tzemach"))
			Logger.debug("trying species key '"+speciesURI+"'");
		if(value.equals("raanan") || value.equals("tzemach"))
		{
			Logger.debug("is allowed null? "+(allowedValues.get(speciesURI) == null)+"");
			Logger.debug("allowed contents: "+allowedValues.get(speciesURI).toString());
			
		}
//		System.out.println("CategoricalDomain.isValid( value='"+value+"', "+
//							"species='"+ species +"', mapped to '"+speciesURI+"'");
		List<String> allowed = allowedValues.get( speciesURI ).valueRange;
		if(value.equals("raanan") || value.equals("tzemach"))
		{
			Logger.debug("is allowed null? "+(allowed == null)+"");
			Logger.debug("allowed contents: "+allowed.toString());
			
		}
		List<String> outs = allowedValues.get( speciesURI ).valueRange.parallelStream()
						.filter( x -> x.equals(value.trim()))
						.collect(Collectors.toList());
//		Logger.debug("isValid: for value "+value);
//		Logger.debug("isValid: "+outs.toString());
		
		if(outs.size() > 0)
			return new ValidationScenario(true);
		else
			return new ValidationScenario(false, new ReasonDiscrete(super.label, value, allowed), value );
	}


	public static Domain instanceFromJson(JsonNode domainJson) 
	{
		
		HashMap<String,List<String>> rangesMap =
				new HashMap<String,List<String>>();
		if(domainJson.isArray())
		{
			ArrayNode rangesArray = (ArrayNode) domainJson;
			for(JsonNode range : rangesArray)
			{
				ArrayNode values = (ArrayNode) range.get(INPUT.CREATE_ATTRIBUTE.VALUES);
				String species = range.get(INPUT.CREATE_ATTRIBUTE.SPECIES).asText();
				List<String> allowedValues = new LinkedList<String>();
				
				for(JsonNode value : values)
					allowedValues.add(value.asText());
				rangesMap.put(species, allowedValues);
			}
		}
		return new CategoricalDomain(rangesMap,null,null);
	}

	@Override
	public String getDataType() {
		return DOntology.Attribute.DataType.CATEGORIC_TYPE;
	}


	@Override
	public void produceStats(List<String> values) 
	{
		this.valueDistribution = 
			values.parallelStream().map( x -> x.replace("\"","").replace("\\", "/") )
				.collect( Collectors.groupingBy( x -> x) )
				.entrySet().stream()
				.map( x -> new Frequency<String>( x.getKey(), x.getValue().size() ) )
				.sorted( (x,y) -> new Integer(x.frequency).compareTo(y.frequency)*-1 );
		
		
	}


	public Stream<Frequency<String>> getValueDistribution() {
		return valueDistribution;
	}

	
	@Override
	public Statistics<String> getStatistics()
	{
		if(stats != null)
			return stats;
		List<Frequency<String>> dist = this.valueDistribution.collect(Collectors.toList());
		stats = new CategoricalStatistics(dist);
		return stats;
	}

	

}
