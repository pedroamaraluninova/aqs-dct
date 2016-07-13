package aquasmart.services.metamap.beans;

import aquasmart.services.datapull.beans.dataschema.Reason;

public class ValidationScenario 
{

	
	boolean isValid;
	Reason invalidationReason;
	String value;
	
	public ValidationScenario(boolean isValid, Reason invalidationReason,
			String value) {
		super();
		this.isValid = isValid;
		this.invalidationReason = invalidationReason;
		this.value = value;

	}
	
	public ValidationScenario(boolean isValid)
	{
		this(isValid,null,null);
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
	public Outlier generateOutlier(String rowID)
	{
		return new Outlier( value, rowID, invalidationReason);
	}
	
}
