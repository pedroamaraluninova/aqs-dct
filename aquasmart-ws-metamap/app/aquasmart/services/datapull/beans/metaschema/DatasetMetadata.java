package aquasmart.services.datapull.beans.metaschema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import aquasmart.services.datapull.beans.dataschema.Reason;
import aquasmart.services.datapull.beans.dataschema.ReasonContinuous;
import aquasmart.services.metamap.beans.statistics.Statistics;
import aquasmart.services.metamap.utils.STATIC;
import aquasmart.services.metamap.utils.STATIC.DatasetService.OUTPUT;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.media.jfxmedia.logging.Logger;

import utils.json.JSON;
import utils.json.JsonDeserializable;
import utils.json.JsonSerializable;

public class DatasetMetadata implements Serializable, JsonSerializable 
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String datasetID, company,  user, upload_stamp;
	
	List<MetaItem> metadata;

	public DatasetMetadata( String datasetID, String company, 
			String user, String upload_stamp ) {
		this( datasetID, company, user, upload_stamp, new LinkedList<MetaItem>() );
	}
	
	
	public DatasetMetadata(String datasetID, String company, 
			String user, String upload_stamp, List<MetaItem> metadata) {
		super();
		this.datasetID = datasetID;
		this.company = company;
	
		this.user = user;
		this.upload_stamp = upload_stamp;
		this.metadata = metadata;
	}
	

	public String getDatasetID() {
		return datasetID;
	}


	public String getCompany() {
		return company;
	}


	public String getUser() {
		return user;
	}


	public String getUpload_stamp() {
		return upload_stamp;
	}


	public List<MetaItem> getMetadata() {
		return metadata;
	}


	public static DatasetMetadata fromJson(JsonNode root) 
	{
		String datasetID = root.get( OUTPUT.GetMetadata.DATASETID ).asText(),
				company = root.get( OUTPUT.GetMetadata.COMPANY ).asText();
		JsonNode userNode =  root.get( OUTPUT.GetMetadata.USER ),
				upload_stampNode = root.get( OUTPUT.GetMetadata.UPLOAD_STAMP);
		
		List<MetaItem> metas = new LinkedList<MetaItem>();
		
		for(JsonNode meta : root.get( OUTPUT.GetMetadata.Metadata.FIELD ))
			if( ! meta.get( OUTPUT.GetMetadata.Metadata.LABEL).asText().equals("days_between_sampling") )
				metas.add( MetaItem.fromJson(meta) );
		
		String user = userNode != null ? userNode.asText() : "dct message: not assigned" ,
				upload_stamp = upload_stampNode != null ? 
									upload_stampNode.asText() : "dct message: not assigned";
		return new DatasetMetadata( datasetID, company,  user, upload_stamp, metas );
	}
	
	@Override
	public JsonNode toJson() {
		
		ObjectNode root = JSON.newObject();
		
		root.put( OUTPUT.GetMetadata.DATASETID, datasetID );
		root.put( OUTPUT.GetMetadata.COMPANY, company );

		root.put( OUTPUT.GetMetadata.USER, user );
		root.put( OUTPUT.GetMetadata.UPLOAD_STAMP, upload_stamp );
		
		ArrayNode arr = root.putArray( OUTPUT.GetMetadata.Metadata.FIELD );
		
		for( MetaItem mi : metadata )
			arr.add( mi.toJson() );
		
		return root;
	}

	
	private void readObject(ObjectInputStream ois)
			throws IOException, ClassNotFoundException
	{
		datasetID = ois.readUTF();

		company = ois.readUTF();
		user = ois.readUTF();
		upload_stamp = ois.readUTF();
		
		LinkedList<MetaItem> aux = new LinkedList<MetaItem>();
		int metadataSize = ois.readInt();
		
		String directoryURL = STATIC.Cache.CACHE_LOCATION +"/"+datasetID + "/";
		File directory = new File(directoryURL);
		directory.mkdirs();
		
		for(int i = 0; i< metadataSize; i++)
		{
//			play.Logger.debug("Reading cached meta item "+ i +
//					" in: "+directoryURL + i + ".meta");
			File file = new File( directoryURL + i + ".meta" );
			ObjectInputStream metaOIS = new ObjectInputStream(
					new FileInputStream( file ));
			
			aux.addLast( (MetaItem) metaOIS.readObject() );
		
			metaOIS.close();
		}
			
		metadata = aux;
	}
	
	
	private void writeObject(ObjectOutputStream oos)
		throws IOException
	{
		oos.writeUTF( datasetID );

		oos.writeUTF( company );
		oos.writeUTF( user );
		oos.writeUTF( upload_stamp );
		
		oos.writeInt( metadata.size() );
		
		String directoryURL = STATIC.Cache.CACHE_LOCATION +"/"+datasetID + "/";
		File directory = new File(directoryURL);
		if( ! directory.exists() )
			directory.mkdirs();
			
		for( int i = 0; i < metadata.size() ; i++ )
		{
//			play.Logger.debug("Caching meta item "+ i +
//					" from: "+ directoryURL + i + ".meta");
			File file = new File( directoryURL + i + ".meta" );
			ObjectOutputStream metaOOS = new ObjectOutputStream(
					new FileOutputStream( file ));
			play.Logger.debug( metadata.get(i).toString() );
			metaOOS.writeObject( metadata.get(i) );
			metaOOS.close();
		}
		
	}


	public void setItem(String label, String concept) {
		
		this.metadata = 
				this.metadata.stream()
					.map( 
						x -> x.getLabel().equals(label) ? x.setConcept(concept) : x )
					.collect( Collectors.toList() );
	}


	public void setSamples(Statistics statistics, String colLabel) 
	{
		this.metadata = 
				this.metadata.stream() 
					.map( 
						x -> x.getLabel().equals(colLabel) ? x.setSamples(statistics) : x )
					.collect( Collectors.toList() );
	}




}
