package aquasmart.services.metamap.beans;

import aquasmart.services.metamap.beans.TemporalDomain.TemporalPrecision;
import aquasmart.services.metamap.utils.STATIC.DOntology;
import aquasmart.services.metamap.utils.STATIC.Precision;

public class TimeFormat 
{
	private String format, precision;

	public TimeFormat(String format, String precision) {
		super();
		this.format = format;
		this.precision = precision;
	}

	public String getFormat() {
		return format;
	}

	public String getPrecision() {
		return precision;
	}
	
	public static String fromTemporalPrecision(TemporalPrecision precision)
	{
		if(precision == TemporalPrecision.SECOND)
			return DOntology.Domain.TemporalDomain.TemporalPrecision.SECOND;
		else if(precision == TemporalPrecision.MINUTE)
			return DOntology.Domain.TemporalDomain.TemporalPrecision.MINUTE;
		else if(precision == TemporalPrecision.HOUR)
			return DOntology.Domain.TemporalDomain.TemporalPrecision.HOUR;
		else if(precision == TemporalPrecision.DAY)
			return DOntology.Domain.TemporalDomain.TemporalPrecision.DAY;
		else if(precision == TemporalPrecision.MONTH)
			return DOntology.Domain.TemporalDomain.TemporalPrecision.MONTH;
		else //if(precision == TemporalPrecision.YEAR)
			return DOntology.Domain.TemporalDomain.TemporalPrecision.YEAR;
	}
	
	/**
	 * Converts from temporal precision URI to a TemporalPrecision
	 * enumerated type
	 * @param precision a string containing the URI precision to be converted
	 * @return a value of the TemporalPrecision enumeration
	 */
	public static TemporalPrecision toTemporalPrecision(String precision)
	{
		
		if(DOntology.Domain.TemporalDomain.TemporalPrecision.SECOND
				.equals(precision))
			return TemporalPrecision.SECOND;
		else if(DOntology.Domain.TemporalDomain.TemporalPrecision.MINUTE
				.equals(precision))
			return TemporalPrecision.MINUTE;
		else if(DOntology.Domain.TemporalDomain.TemporalPrecision.HOUR
				.equals(precision))
			return TemporalPrecision.HOUR;
		else if(DOntology.Domain.TemporalDomain.TemporalPrecision.DAY
				.equals(precision))
			return TemporalPrecision.DAY;
		else if(DOntology.Domain.TemporalDomain.TemporalPrecision.MONTH
				.equals(precision))
			return TemporalPrecision.MONTH;
		else //if(DOntology.Domain.TemporalDomain.TemporalPrecision.YEAR)
			return TemporalPrecision.YEAR;
	}
	
	/**
	 * Converts from temporal precision string to a TemporalPrecision
	 * enumerated type
	 * @param precision a string representing the precision to be converted
	 * @return a value of the TemporalPrecision enumeration
	 */
	public static TemporalPrecision asTemporalPrecision(String precision) 
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
}
