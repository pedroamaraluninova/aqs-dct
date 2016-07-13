package aquasmart.services.datapull.beans.dataschema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import aquasmart.services.datapull.beans.metaschema.MetaItem;
import aquasmart.services.metamap.utils.STATIC.DatasetService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import utils.json.JSON;
import utils.json.JsonSerializable;

public class Row implements Serializable, JsonSerializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<String> rowContents;
	String id, systemID, rowIndex, jsonString;
	
	List<Reason> failureReasons;
	
	public Row(List<String> rowContents, String id, String systemID,
			String rowIndex, String jsonString) 
	{
		this( rowContents, id, systemID, rowIndex, new LinkedList<Reason>(),
				jsonString);
	}
	
	public Row(List<String> rowContents, String id, String systemID,
			String rowIndex, List<Reason> failures, String jsonString) 
	{
		super();
		this.rowContents = rowContents;
		this.id = id;
		this.systemID = systemID;
		this.rowIndex = rowIndex;
		this.failureReasons = failures;
		this.jsonString = jsonString;
	}

	
	
	public List<Reason> getFailureReasons() {
		return failureReasons;
	}

	public List<String> getRowContents() {
		return rowContents;
	}
	
	public String get(int index)
	{
		return rowContents.get(index);
	}
	
	public String getId() {
		return id;
	}
	
	public String getSystemID() {
		return systemID;
	}
	
	public String getRowIndex() {
		return rowIndex;
	}

	@Override
	public JsonNode toJson()
	{
		ObjectNode root = JSON.newObject();
		
		root.put( DatasetService.OUTPUT.Data.DataRow.ROW_ID, id);
		root.put( DatasetService.OUTPUT.Data.DataRow.SYSTEM_ID, systemID);
		root.put( DatasetService.OUTPUT.Data.DataRow.ROW_NUMBER, rowIndex);
		boolean isClean = failureReasons.size() == 0;
		
		root.put( DatasetService.OUTPUT.Data.DataRow.STATUS,  
				isClean ? "READY_TO_PROCESS" : "FAILED" );
		
		ObjectNode reasons = JSON.newObject();
		root.set( DatasetService.OUTPUT.Data.DataRow.Reason.FIELD, reasons);
		
		ArrayNode extra = reasons.putArray( DatasetService.OUTPUT.Data.DataRow.Reason.EXTRA );
		reasons.put( DatasetService.OUTPUT.Data.DataRow.Reason.CODE, "");
		
		for( Reason r : failureReasons )
			extra.add( r.toJson() );
		
		try {
			root.set( DatasetService.OUTPUT.Data.DataRow.RowContent.FIELD, 
					JSON.stringToJson(jsonString));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return root;
	}

	public Row setOutlier( Reason reason ) 
	{
		this.failureReasons.add(reason);
		return this;
	}

//	public JsonNode toJson() 
//	{
//		ObjectNode root = JSON.newObject();
//	
//		
//		root.put( DatasetService.OUTPUT.Data.DataRow.ROW_ID, id);
//		root.put( DatasetService.OUTPUT.Data.DataRow.SYSTEM_ID, systemID);
//		root.put( DatasetService.OUTPUT.Data.DataRow.ROW_NUMBER, rowIndex);
//		boolean isClean = failureReasons.size() == 0;
//		
//		root.put( DatasetService.OUTPUT.Data.DataRow.STATUS,  
//				isClean ? "READY_TO_PROCESS" : "FAILED" );
//		
//		ArrayNode reasons = root.putArray( DatasetService.OUTPUT.Data.DataRow.Reason.FIELD );
//		
//		for( Reason r : failureReasons )
//			reasons.add( r.toJson() );
//		
//		
//		
//		return root;
//	}
	
	
	
}
