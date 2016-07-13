package aquasmart.services.datapull;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import utils.cache.Cache;
import utils.cache.FileSystemCache;
import utils.http.HttpUtils;
import aquasmart.services.datapull.beans.dataschema.Reason;
import aquasmart.services.datapull.beans.dataschema.RowSet;
import aquasmart.services.datapull.beans.dataschema.StagingData;
import aquasmart.services.datapull.beans.metaschema.DatasetMetadata;
import aquasmart.services.metamap.utils.STATIC;
import aquasmart.services.metamap.utils.STATIC.DatasetService;


public class CachingRESTDatasetPullService extends RESTDatasetPullService
{
	String datasetID;
	Cache<StagingData> datasetCache ;
	Cache<DatasetMetadata> metadataCache ;
	
	public CachingRESTDatasetPullService(String datasetID) 
			throws FileNotFoundException, IOException, ClassNotFoundException 
	{
		this( STATIC.DatasetService.BASE_URL, datasetID);
	}

	public CachingRESTDatasetPullService(String url, String datasetID) 
			throws FileNotFoundException, IOException, ClassNotFoundException  
	{
		super(url);
		new File(STATIC.Cache.CACHE_LOCATION).mkdirs();
		this.datasetCache = new FileSystemCache<StagingData>
								(STATIC.Cache.CACHE_LOCATION + 
										datasetID + "/" + STATIC.Cache.DATASET_CACHE_FILE );
		
		this.metadataCache = new FileSystemCache<DatasetMetadata>
								(STATIC.Cache.CACHE_LOCATION + 
										datasetID + "/" + STATIC.Cache.METADATA_CACHE_FILE );
	}
	
	@Override
	public List<List<String>> getData(String datasetID, String... selectedColumns) 
			throws IOException, ClassNotFoundException
	{
		if( datasetCache.exists() )
		{
			if( ! datasetCache.isLive() )
				datasetCache.recover();
		}
		else
			datasetCache.create(
					super.getAllData(datasetID) );
		
		StagingData stage = datasetCache.getCached();
		play.Logger.debug("stage="+stage);
		RowSet rows = stage.getRows();
		
		return rows.projection(selectedColumns);
	}
	
	@Override
	public DatasetMetadata getMetadata(String datasetID) 
			throws FileNotFoundException, ClassNotFoundException, IOException
	{
		if( metadataCache.exists() )
		{
			if( ! metadataCache.isLive() )
				metadataCache.recover();
		}
		else
			metadataCache.create( super.getMetadata(datasetID) );
		
		return metadataCache.getCached();
	}
	
	@Override
	protected StagingData getAllData(String datasetID) throws IOException, ClassNotFoundException
	{
		if( datasetCache.exists() )
		{
			if( ! datasetCache.isLive() )
				datasetCache.recover();
		}
		else
			datasetCache.create(
					super.getAllData(datasetID) );
		
		StagingData stage = datasetCache.getCached();
		super.theData = stage.getRows();
		return stage;
	}

	@Override
	public List<List<String>> getDataWithRowID(String datasetID,
			String... selectedColumns) throws IOException, ClassNotFoundException 
	{
		this.getAllData( datasetID );
		
		return this.theData.projectionWithRowID(selectedColumns);
	}
	
	@Override
	public void setOutlier(String rowID, String value, Reason reason) throws FileNotFoundException, IOException, ClassNotFoundException 
	{
		super.setOutlier( rowID, value, reason );
		
		//check if outlier data is cached
		//		if not: create new column outlier info object
		//insert new outlier info in object
		//add commit operation
		
	
		
	}
	


	
	
	@Override
	public void commit() throws ClassNotFoundException, IOException
	{ 
		datasetCache.commit();
	}
	
	@Override 
	public StagingData getDataObject(String datasetID) 
			throws FileNotFoundException, ClassNotFoundException, IOException
	{
		if( datasetCache.exists() )
		{
			if( ! datasetCache.isLive() )
				datasetCache.recover();
		}
		else
			datasetCache.create(
					super.getAllData(datasetID) );
		
		return datasetCache.getCached();
	}

	public void commitMeta(DatasetMetadata meta) throws FileNotFoundException, ClassNotFoundException, IOException 
	{
		
		this.metadataCache.commit(meta);
		
	}
	
	@Override
	public void destroy() throws IOException
	{
		this.datasetCache.destroy();
		this.metadataCache.destroy();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
}
