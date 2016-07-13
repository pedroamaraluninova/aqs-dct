package aquasmart.services.metamap.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import aquasmart.services.datapull.beans.dataschema.ReasonContinuous;
import aquasmart.services.metamap.beans.range.TemporalRange;
import aquasmart.services.metamap.beans.statistics.Statistics;
import aquasmart.services.metamap.beans.statistics.TemporalStatistics;
import aquasmart.services.metamap.stats.Frequency;
import aquasmart.services.metamap.utils.STATIC.DOntology;
import aquasmart.services.metamap.utils.STATIC.Json.INPUT;
import aquasmart.services.metamap.utils.STATIC.Precision;
import utils.tuples.*;

public class TemporalDomain extends Domain 
{

	public enum TemporalPrecision { SECOND, MINUTE, HOUR,  DAY, MONTH, YEAR };
	
	private TemporalPrecision precision;
	private Date min, max;
	private String format;
	private Stream<Frequency<Date>> valueDistribution;
	private HashMap<String,TemporalRange> ranges;
	private SimpleDateFormat formatter;
	private Statistics<Date> stats;

	public TemporalDomain(TemporalPrecision precision,
			List<Tuple3<String,String,String>> ranges, String format,
			HashMap<String,String> speciesMap, String label) throws ParseException 
	{
		super(DomainType.TEMPORAL, speciesMap, label);
		this.precision = precision;
		this.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		this.ranges = new HashMap<String,TemporalRange>();
		
		ranges.stream().forEach( 
			x -> this.ranges.put(x.one, new TemporalRange(
						parse( x.two.replace("T"," ")),
						parse( x.three.replace("T"," "))
					) )
		);
		this.stats = null;
		this.format = format;
	}

	/**
	 * Parse a date and don't throw parse exception if the parse does
	 * not work
	 * Java8 lambda is shitty this way :(
	 * @param date
	 * @return
	 */
	private Date parse(String date) {
		
		// TODO Auto-generated method stub
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public HashMap<String,TemporalRange> getRanges()
	{
		return this.ranges;
	}

	public String getStartTime(String species) {
		return formatter.format(
					this.ranges.get(species).start ).replace(" ", "T");
	}


	public String getEndTime(String species) {
		return formatter.format(
					this.ranges.get(species).end ).replace(" ", "T");
	}

	public String getFormat()
	{
		return this.format;
	}
	
	public TemporalPrecision getPrecision() {
		return precision;
	}


	public Date getStart(String species) {
		return this.ranges.get(species).start;
	}


	public Date getEnd(String species) {
		return this.ranges.get(species).end;
	}


	public ValidationScenario validate( String value , String species)
	{
		TemporalRange range = this.ranges.get( speciesMap.get(species) );
		//Model date format is yyyy-mm-ddThh:mm:ss
		Date theDate = parseDate(value);
		if( ( theDate.after(range.start) && theDate.before(range.end) ) 
				|| ( theDate.equals(range.start) || theDate.equals(range.end) ) )
			return new ValidationScenario(true);
		else
			return new ValidationScenario(false,
					new ReasonContinuous(range.start.toString() , range.end.toString(), value, super.label ),
					value );
	}


	private Date parseDate(String value) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		
		String dateValue = value;
		
//		if( precision == TemporalPrecision.MINUTE)
//			dateValue += ":00";
//		else if( precision == TemporalPrecision.HOUR)
//			dateValue += ":00:00";
//		else if( precision == TemporalPrecision.DAY)
//			dateValue += " 00:00:00";
//		else if( precision == TemporalPrecision.MONTH)
//			dateValue += "-01 00:00:00";
//		else //if( precision == TemporalPrecision.YEAR)
//			dateValue += "-01-01 00:00:00";
		
		Date theDate = new Date();
		try {
			theDate = formatter.parse(dateValue);
		} catch (ParseException e) {
			theDate = new Date();
		}
		return theDate;
	}


	public static TemporalPrecision toTemporalPrecision(String precision) 
	{
		if( precision.equals(Precision.TemporalPrecision.SECOND) )
			return TemporalPrecision.SECOND;
		else if( precision.equals(Precision.TemporalPrecision.MINUTE) )
			return TemporalPrecision.MINUTE;
		else if( precision.equals(Precision.TemporalPrecision.HOUR) )
			return TemporalPrecision.HOUR;
		else if( precision.equals(Precision.TemporalPrecision.DAY) )
			return TemporalPrecision.DAY;
		else if( precision.equals(Precision.TemporalPrecision.MONTH) )
			return TemporalPrecision.MONTH;
		else //if( precision.equals(Precision.TemporalPrecision.YEAR))
			return TemporalPrecision.YEAR;
	}


	public static Domain instanceFromJson(JsonNode domainJson) throws ParseException 
	{
		List<Tuple3<String,String,String>> rangesList =
				new LinkedList<Tuple3<String,String,String>>();
		String precision = null , format = null;
		
		if(domainJson.isArray())
		{
			ArrayNode rangesArray = (ArrayNode) domainJson;
			for(JsonNode range : rangesArray)
			{
				precision = range.get(INPUT.CREATE_ATTRIBUTE.PRECISION).asText();
				format = range.get(INPUT.CREATE_ATTRIBUTE.FORMAT).asText();
				String lower = range.get(INPUT.CREATE_ATTRIBUTE.LOWER_BOUND).asText(),
						upper = range.get(INPUT.CREATE_ATTRIBUTE.UPPER_BOUND).asText(),
						species = range.get(INPUT.CREATE_ATTRIBUTE.SPECIES).asText();
				
				rangesList.add( new Tuple3<String,String,String>( species, upper, lower ) );
			}	
		}
		
		TemporalPrecision prec = TemporalDomain.toTemporalPrecision(precision);
		return new TemporalDomain( prec, rangesList, format, null, null );
	}
	
	@Override
	public String getDataType() 
	{
		return DOntology.Attribute.DataType.TEMPORAL_TYPE;
	}

	
	public Date getMin() 
	{
		return this.min;
	}


	public Date getMax() 
	{
		return this.max;
	}


	public Stream<Frequency<Date>> getValueDistribution() 
	{
		return this.valueDistribution;
	}


	@Override
	public void produceStats(List<String> values) 
	{
		HashMap<Date,Integer> map = values.parallelStream()
			.map( x -> parseDate(x) )
			.collect(
				() -> new HashMap<Date,Integer>(), 
				TemporalDomain::acumulate , 
				TemporalDomain::combine
					
		);
		this.valueDistribution = map.entrySet().parallelStream()
			.map( x -> new Frequency<Date>( x.getKey(), x.getValue() ) )
			.sorted( (x,y) -> new Integer(x.frequency).compareTo(y.frequency) * -1 );
//			.collect(
//				LinkedList<Frequency<Date>>::new , 
//				(accum,elem) -> accum.add(elem) ,
//				LinkedList<Frequency<Date>>::addAll);
		//combine(accum,accum2));
		
		this.min = map.keySet().parallelStream().min(Date::compareTo).get();
		this.max = map.keySet().parallelStream().max(Date::compareTo).get();


	}


	private static HashMap<Date,Integer> acumulate(HashMap<Date, Integer> accum, Date elem) 
	{
		return acumulate(accum,elem,1);
	}
	
	private static HashMap<Date,Integer> acumulate(HashMap<Date, Integer> accum, Date elem, Integer toSum) 
	{
		Integer count = accum.get(elem);
		if(count == null)
			count = toSum;
		count += toSum;
		accum.put(elem, count);
		return accum;
	}
	
	private static HashMap<Date,Integer> combine 
		(HashMap<Date, Integer> accum1 , HashMap<Date, Integer> accum2)
	{
		accum2.entrySet().forEach( x -> acumulate(accum1,x.getKey(),x.getValue()));
		return accum1;
	}


	@Override
	public Statistics<Date> getStatistics() 
	{
		if(stats != null)
			return stats;
		List<Frequency<Date>> dist = valueDistribution.collect(Collectors.toList());
		stats = new TemporalStatistics(dist,min,max);
		return stats;
	}
	
	
	
	
}
