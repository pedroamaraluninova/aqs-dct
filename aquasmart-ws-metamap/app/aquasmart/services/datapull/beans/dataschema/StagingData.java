package aquasmart.services.datapull.beans.dataschema;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import utils.json.JSON;
import utils.json.JsonDeserializable;
import utils.json.JsonSerializable;
import aquasmart.services.datapull.beans.metaschema.DatasetMetadata;
import aquasmart.services.metamap.utils.STATIC.DatasetService;;

public class StagingData implements Serializable, JsonSerializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int version, company, user;
	String datasetID, dataType, uploadStamp;
	
	RowSet rows;


	public StagingData(int version, int company, int user, String uploadStamp,
			String datasetID, RowSet rows) {
		super();
		this.version = version;
		this.company = company;
		this.user = user;
		this.uploadStamp = uploadStamp;
		this.datasetID = datasetID;
//		this.dataType = dataType;
		this.rows = rows;
	}

	public RowSet getRows() {
		return rows;
	}

	
	public static StagingData fromJson(JsonNode root, DatasetMetadata meta) 
			throws JsonProcessingException 
	{
		String company = root.get( DatasetService.OUTPUT.Data.COMPANY ).asText(),
//				dataType = root.get( DatasetService.OUTPUT.Data.DATA_TYPE ).asText(),
				user = root.get( DatasetService.OUTPUT.Data.USER ).asText(),
				uploadStamp = root.get( DatasetService.OUTPUT.Data.UPLOAD_STAMP ).asText(),
				datasetID = root.get( DatasetService.OUTPUT.Data.DATASET_ID ).asText(),
				version = root.get( DatasetService.OUTPUT.Data.VERSION ).asText();
		
		
		RowSet rows = RowSet.fromJson( (ArrayNode) root.get( DatasetService.OUTPUT.Data.DATA),
					meta );
		
		return new StagingData(Integer.parseInt(version), Integer.parseInt(company),
				Integer.parseInt(user), uploadStamp, datasetID,  rows);
	}

	@Override
	public JsonNode toJson() {
		ObjectNode root = JSON.newObject();
		
		root.put( DatasetService.OUTPUT.Data.COMPANY, company );
		root.put( DatasetService.OUTPUT.Data.USER, user );
		root.put( DatasetService.OUTPUT.Data.UPLOAD_STAMP, uploadStamp );
		root.put( DatasetService.OUTPUT.Data.DATASET_ID, datasetID );
		root.put( DatasetService.OUTPUT.Data.VERSION, version );
		
		ArrayNode dataItems = root.putArray( DatasetService.OUTPUT.Data.DATA );
		
		rows.rows.stream()
			.map( x -> x.toJson() )
			.forEach( x -> dataItems.add(x) );
	
		return root;
	}
	
	private void readObject(ObjectInputStream ois) 
			throws IOException, ClassNotFoundException
	{
		play.Logger.debug("reading staging data");
		uploadStamp = ois.readUTF();
		datasetID = ois.readUTF();
		
		company = ois.readInt();
		version = ois.readInt();
		user = ois.readInt();
		
		rows = (RowSet) ois.readObject();
	}
	
	private void writeObject(ObjectOutputStream oos) 
			throws IOException
	{
		play.Logger.debug("writting staging data");
		oos.writeUTF(uploadStamp);
		oos.writeUTF(datasetID);
		
		oos.writeInt(company);
		oos.writeInt(version);
		oos.writeInt(user);
		
		oos.writeObject(rows);
	}
	

	
	
	
	
}
