package aquasmart.services.datapull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import aquasmart.services.datapull.beans.dataschema.Reason;
import aquasmart.services.datapull.beans.dataschema.ReasonContinuous;
import aquasmart.services.datapull.beans.dataschema.StagingData;
import aquasmart.services.datapull.beans.metaschema.DatasetMetadata;


/**
 * This interface provides an abstraction for the 
 * operations made available by the AQUASMART dataset data-pull service 
 * 
 * Any implementation of this interface must allow a constructor with no
 * arguments and (optionally) a constructor that takes a String parameter,
 * the entry-point URL of the service
 * 
 * @author amaral
 *
 */
public interface DatasetPullService 
{

	/**
	 * This method provides a wrapper to the services' operation for
	 * retrieving the metadata information of a dataset
	 * @param datasetID the identifier of the targeted dataset
	 * @return a POJO containing the returned information
	 * @throws Exception 
	 */
	public DatasetMetadata getMetadata(String datasetID) throws Exception ;
	
	public String  getMetadataS(String datasetID) throws Exception ;
	/**
	 * This method provides a wrapper to the services' operation for
	 * retrieving a vertical section of the dataset
	 * @param datasetID the identifier of the targeted dataset
	 * @param selectedColumns an array containing the indexes of the columns
	 * that will be returned
	 * @return a POJO that represents a set of rows
	 * @throws Exception
	 */
	public List<List<String>> getData(String datasetID, String... selectedColumns) 
			throws Exception ;

	public List<List<String>> getDataWithRowID(String datasetID,
			String... selectedColumns) throws IOException, ClassNotFoundException;

	public void setOutlier(String rowID, String value, Reason reason) 
			throws FileNotFoundException, IOException, ClassNotFoundException;

	public void commit() throws ClassNotFoundException, IOException;
	
	public StagingData getDataObject(String datasetID) throws IOException, ClassNotFoundException;

	public void destroy() throws IOException;

	
}
