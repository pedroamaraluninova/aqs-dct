package aquasmart.services.metamap.beans;

import aquasmart.services.datapull.beans.dataschema.Reason;

public class Outlier 
{

	String value, rowID; 
	Reason invalidationReason;
	
	public Outlier(String value, String rowID, Reason invalidationReason) 
	{
		super();
		this.value = value;
		this.rowID = rowID;
		this.invalidationReason = invalidationReason;
	}

	public String getValue() {
		return value;
	}

	public String getRowID() {
		return rowID;
	}

	public Reason getInvalidationReason() {
		return invalidationReason;
	}
	
	
	
	
}
