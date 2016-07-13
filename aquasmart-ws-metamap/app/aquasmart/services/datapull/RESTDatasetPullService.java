package aquasmart.services.datapull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import utils.http.HttpUtils;
import aquasmart.services.datapull.beans.dataschema.Reason;
import aquasmart.services.datapull.beans.dataschema.RowSet;
import aquasmart.services.datapull.beans.dataschema.StagingData;
import aquasmart.services.datapull.beans.metaschema.DatasetMetadata;
import aquasmart.services.metamap.utils.STATIC;
import aquasmart.services.metamap.utils.STATIC.DatasetService;



public class RESTDatasetPullService implements DatasetPullService 
{
	String url;
	RowSet theData;
	StagingData staging;
	
	public RESTDatasetPullService() 
	{
		this( STATIC.DatasetService.BASE_URL );
	}
	
	public RESTDatasetPullService(String url)
	{
		this.url = url;
	}

	@Override
	public DatasetMetadata getMetadata(String datasetID) 
			throws IOException, ClassNotFoundException 
	{
		
		String location = HttpUtils.buildRestURL( false , url  , 
				datasetID , DatasetService.GET_META);
		play.Logger.debug(location);
		JsonNode metaJson = HttpUtils.jsonGET(location);

		return DatasetMetadata.fromJson( metaJson.get( DatasetService.OUTPUT.Data.MESSAGE ) );
	}

	@Override
	public List<List<String>> getData(String datasetID, String... selectedColumns) 
			throws IOException, ClassNotFoundException 
	{	
		this.getAllData( datasetID );
		return this.theData.projection(selectedColumns);
	}

	@Override
	public String getMetadataS(String datasetID) throws Exception {
		String location = HttpUtils.buildRestURL( false , url  , 
				datasetID , DatasetService.GET_META);
		return HttpUtils.stringGET(location);
	}
	
	
	protected StagingData getAllData(String datasetID) throws IOException, ClassNotFoundException
	{
		String location = HttpUtils.buildRestURL( false , url , datasetID , 
				DatasetService.GET_DATA );
		
		JsonNode dataJson = HttpUtils.jsonGET( location ) ;
		this.staging = StagingData.fromJson( dataJson.get( 
				DatasetService.GetData.MESSAGE) , getMetadata(datasetID) ) ; 
		this.theData = staging.getRows() ;
		
		return this.staging;
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
		this.theData.setOutlier(rowID,value,reason); 
		
	}



	@Override
	public void commit() throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StagingData getDataObject(String datasetID) throws IOException, ClassNotFoundException {
		return getAllData(datasetID);
	}

	@Override
	public void destroy() throws IOException {
		// TODO Auto-generated method stub
		
	}



}
