package aquasmart.services.datapull.beans.dataschema;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import utils.json.JsonSerializable;
import utils.tuples.Tuple2;
import aquasmart.services.datapull.beans.metaschema.DatasetMetadata;
import aquasmart.services.metamap.utils.STATIC.DatasetService.OUTPUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;


/**
 * This class represents a set of rows, formally, a list of lists of strings
 * @author amaral
 *
 */
public class RowSet implements Serializable, JsonSerializable
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Row> rows;
	
	HashMap<String,Integer> labelToIndex ;

	public RowSet(List<Row> rows, HashMap<String,Integer> labelToIndex) 
	{
		super();
		this.rows = rows;
		this.labelToIndex = labelToIndex;

		
	}
	
	public String getCell( int row, int column)
	{
		return rows.get( row ).get( column );
	}
	
	public Row getRow(int row)
	{
		return rows.get(row);
	}
	
	public String getValue(int row, int column)
	{
		return rows.get(row).get(column);
	}
	
	public List<Row> getFull()
	{
		return rows;
	}
	
	public static RowSet fromJson(ArrayNode node, DatasetMetadata meta) throws JsonProcessingException
	{
		int index = 0;  
		LinkedList<Row> rows = new LinkedList<Row>();
		HashMap<String,Integer> labelToIndex = new HashMap<String,Integer>();
		boolean gotLabels = false;
		ObjectMapper mapper = new ObjectMapper();
	
		//get field labels
		List<String> fields = meta.getMetadata().stream()
				.map( x -> x.getLabel() )
				.collect( Collectors.toList() );
		
		//init label to index map
		Iterator<String> it = fields.iterator();
		for(int i = 0; it.hasNext(); i++ ) labelToIndex.put(it.next(), i);
		
		List<String> jsonRows = new LinkedList<String>();
		
		
		for( JsonNode jsonRow : node)
		{
			LinkedList<String> row = new LinkedList<String>();
			String rowID = jsonRow.get(OUTPUT.Data.DataRow.ROW_ID).asText(),
					rowIndex = jsonRow.get(OUTPUT.Data.DataRow.ROW_NUMBER).asText(),
					systemID = jsonRow.get(OUTPUT.Data.DataRow.SYSTEM_ID).asText();
			
			JsonNode content = jsonRow.get(OUTPUT.Data.DataRow.RowContent.FIELD);
		
	
			Iterator<String> fieldsIt = fields.iterator();
			for(int i = 0; fieldsIt.hasNext() ; i++)
			{
				String currentField = fieldsIt.next();

				JsonNode value = content.get( currentField );
				
				row.addLast( value != null ? value.asText() : "" );
			}
			
			rows.addLast( new Row( row , rowID , systemID , rowIndex, 
					mapper.writeValueAsString(content) ) );
			
			if( !gotLabels )
				gotLabels = true;
			
		}
		
		return new RowSet( rows, labelToIndex );
	}
	
	private static <T> List<T> merge(Iterator<T> it, List<T> list, HashMap<T,Integer> explored)
	{
		while(it.hasNext())
		{
			T elem = it.next();
			if(explored.get(elem) != null)
			{
				explored.put(elem, list.size());
				list.add(elem);
			}
		}
			;
		return list;
	}
	
	public Stream<Tuple2<String, String>> getAs( 
			Function<Row,Tuple2<String,String>> fun)
	{
		return this.rows.stream().map( fun );
	}
	
	public List<String> projection( int col)
	{
		//System.out.println("RowSet.projection( column="+col+" )");
		return rows.stream().map( x -> x.get(col) )
					.collect( Collectors.toList() );	
	}
	
	public List<String> projection(String column)
	{
		//System.out.println("RowSet.projection( column="+column+" )");
		return this.projection( this.labelToIndex.get( column ) ); 
	}

	public List<List<String>> projection(String... selectedColumns) 
	{
		if(selectedColumns.length == 0)
			return rows.stream().map( x -> x.getRowContents())
								.collect(Collectors.toList());
		
		List<String> selected = Arrays.asList(selectedColumns);
		return
			rows.stream()
				.map( 
					x -> selected.stream()
							.map( y -> x.get( this.labelToIndex.get(y) ) )
							.collect( Collectors.toList() )
				).collect( Collectors.toList() );	
	}

	@Override
	public JsonNode toJson() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<List<String>> projectionWithRowID(String[] selectedColumns) 
	{
		if(selectedColumns.length == 0)
			return rows.stream().map( x -> x.getRowContents())
								.collect(Collectors.toList());
		
		List<String> selected = Arrays.asList(selectedColumns);
		return
			rows.stream()
				.map( 
					x -> { 
						List<String> proj = selected.stream()
							.map( y -> x.get( this.labelToIndex.get(y) ) )
							.collect( Collectors.toList() );
						proj.add( x.id );
						return proj;
					}
							
				).collect( Collectors.toList() );	
		
	}

	public void setOutlier(String rowID, String value, Reason reason) 
	{
		this.rows = 
				rows.stream()
					.map( x -> x.id.equals(rowID) ? x.setOutlier(reason) : x )
					.collect( Collectors.toList() ); 
		
	}
	
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
	{
		int size = ois.readInt();
		LinkedList<Row> auxRows = new LinkedList<Row>();
		for(int i = 0; i < size ; i++)
			auxRows.addLast( (Row) ois.readObject() ); 
		
		this.rows = auxRows;
		
		int mapSize = ois.readInt();
		HashMap<String,Integer> auxMap = new HashMap<String,Integer>();
		for(int i = 0; i < mapSize; i++)
			auxMap.put( ois.readUTF(), ois.readInt() );
		this.labelToIndex = auxMap;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException
	{
		oos.writeInt(rows.size());
		for( Row r : rows) oos.writeObject(r);
		
		oos.writeInt(labelToIndex.size());
		for( Entry<String,Integer> e : labelToIndex.entrySet())
		{
			oos.writeUTF(e.getKey());
			oos.writeInt(e.getValue());
		}
	}
}
