package aquasmart.services.metamap.beans;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import aquasmart.services.datapull.beans.dataschema.ReasonContinuous;
import aquasmart.services.metamap.beans.range.NumericRange;
import aquasmart.services.metamap.beans.statistics.NumericStatistics;
import aquasmart.services.metamap.beans.statistics.Statistics;
import aquasmart.services.metamap.stats.Frequency;
import aquasmart.services.metamap.utils.STATIC.DOntology;
import aquasmart.services.metamap.utils.STATIC.Json.INPUT;
import aquasmart.services.metamap.utils.STATIC.Precision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;



public class NumericDomain extends Domain 
{
	
	public enum NumericPrecision { INTEGER, REAL };
	
	private String unit;
	private NumericPrecision precision;
	private Stream<Frequency<Double>> valueDistribution;
	private double average,max,min;
	private HashMap<String,NumericRange> ranges;
	Statistics<Double> stats;

	public NumericDomain(HashMap<String,NumericRange> ranges, NumericPrecision precision, 
			String unit, HashMap<String,String> speciesMap, String label )
	{
		super(DomainType.NUMERIC, speciesMap, label);
		this.precision = precision;
		this.ranges = ranges;
		this.unit = unit;
		this.stats = null;
	}

	public HashMap<String,NumericRange> getRanges()
	{
		return this.ranges;
	}
	
	public double getStartValue(String species) 
	{
		return this.ranges.get(species).start;
	}

	public double getEndValue(String species) 
	{
		return this.ranges.get(species).end;
	}

	public NumericPrecision getPrecision() 
	{
		return this.precision;
	}
	
	public String getUnit()
	{
		return this.unit;
	}
	
	public ValidationScenario validate( String value , String species)
	{
		String speciesKey = speciesMap.get(species) ;
		
//		System.out.println("NumericDomain.isValid( value='"+value+"', species='"+species+"')");
//		System.out.println("\t\tmapped '"+species+"' to '"+speciesKey+"'");
		
		NumericRange range = this.ranges.get( speciesKey );
		//thank you java for not letting me use a switch, you so wonderful
		try
		{ //case INTEGER
			int intNumber = Integer.parseInt(value);
			if(intNumber <= range.end && intNumber >= range.start)
				return new ValidationScenario(true);
			else
				return new ValidationScenario( false, 
						new ReasonContinuous(""+range.start, ""+range.end, value, super.label), value );
		} catch (NumberFormatException e)
		{
		//case REAL:
//			System.out.println("Parsing value="+value);
			double doubleNumber = Double.parseDouble(value.replace(",", "."));
			if( doubleNumber <= range.end && doubleNumber >= range.start )
				return new ValidationScenario(true);
			else
				return new ValidationScenario( false, 
						new ReasonContinuous(""+range.start, ""+range.end, value, super.label), value );
		}
	}

	public static Domain instanceFromJson(JsonNode domainJson) 
	{
		String precision = null,
				unit = null;

		
		HashMap<String,NumericRange> rangesMap = 
				new  HashMap<String,NumericRange>();
		if(domainJson.isArray())
		{
			ArrayNode ranges = (ArrayNode) domainJson;
			for(JsonNode range : ranges)
			{
				precision = range.get(INPUT.CREATE_ATTRIBUTE.PRECISION).asText();
				unit = range.get(INPUT.CREATE_ATTRIBUTE.UNIT).asText();
				double lower = range.get(INPUT.CREATE_ATTRIBUTE.LOWER_BOUND).asDouble(),
						upper = range.get(INPUT.CREATE_ATTRIBUTE.UPPER_BOUND).asDouble();
				String species = range.get(INPUT.CREATE_ATTRIBUTE.SPECIES).asText();
				rangesMap.put( species, new NumericRange(lower,upper));
			}
		}
		NumericPrecision prec = NumericDomain.toNumericPrecision(precision);
		return new NumericDomain(rangesMap,prec,unit, null, null);
	}

	private static NumericPrecision toNumericPrecision(String precision) 
	{
		if( precision.equals(Precision.NumericPrecision.INTEGER) )
			return NumericPrecision.INTEGER;
		else //if( precision.equals(Precision.NumericPrecision.REAL) )
			return NumericPrecision.REAL;
	}

	@Override
	public String getDataType() {
		return DOntology.Attribute.DataType.NUMERIC_TYPE;
	}

	@Override
	public void produceStats(List<String> values) 
	{
//		int size = values.size();
		List<Double> cleanValues = values.parallelStream()
			.filter( x -> ! x.trim().equals(""))
			.map( x -> Double.parseDouble( x.trim().replace(",", ".") ) )
			.collect( Collectors.toList() );
		Map<Double,List<Double>> frequencyDist = 
				cleanValues.parallelStream()
				.collect( Collectors.groupingBy( x -> x) );
		
		this.valueDistribution = 
				frequencyDist.entrySet().parallelStream()
					.map( x -> new Frequency<Double>( x.getKey(), x.getValue().size() ) )
					.sorted( (x,y) -> new Integer(x.frequency).compareTo(y.frequency)*-1 );
		
//		this.average = frequencyDist.entrySet().stream()
//			.map(x -> x.getKey() * x.getValue().size() )
//			.collect( () -> 0.0 , (accum,val) -> accum += val , (accum,accum2) -> accum += accum2);
//		
//		this.average = average / size ;
		
		DoubleSummaryStatistics stats = cleanValues.parallelStream()
				.mapToDouble( x -> x )
				.summaryStatistics();
		
		this.average = stats.getAverage();
		this.max = stats.getMax();
		this.min = stats.getMin();
		
	}
	
	public Stream<Frequency<Double>> getValueDistribution() 
	{
		return this.valueDistribution;
	}

	public double getAverage() 
	{
		return this.average;
	}

	public double getMax() 
	{
		return this.max;
	}

	public double getMin() 
	{
		return this.min;
	}

	@Override
	public Statistics<Double> getStatistics() 
	{
		if(stats != null)
			return stats;
		List<Frequency<Double>> valueDist = valueDistribution.collect(Collectors.toList()); 
		stats = new NumericStatistics(valueDist, min, max, average);
		return stats;
	}
	
}
