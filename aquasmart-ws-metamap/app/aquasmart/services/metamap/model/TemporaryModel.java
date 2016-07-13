package aquasmart.services.metamap.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import utils.tuples.Tuple2;

public class TemporaryModel 
{

	File file;
	HashMap<String,List<String>> map;
	
	private TemporaryModel() throws FileNotFoundException, IOException
	{
		
		map = new HashMap<String,List<String>>();
		file = new File("speciesMap");
		
		if( file.exists() )
			readFromDisk();
		else
			file.createNewFile();
		
	}
	
	private void readFromDisk() throws FileNotFoundException, IOException 
	{
		ObjectInputStream ois = new ObjectInputStream(
									new FileInputStream(file));
		
		readMap(ois);
		
		
		ois.close();
		
	}
	
	private void readMap(ObjectInputStream ois) throws IOException
	{
		int numEntries = ois.readInt();
		
		for(int i = 0 ; i < numEntries ; i++)
		{
			String speciesURI = ois.readUTF();
			int numMappings = ois.readInt();
			List<String> mappings = new LinkedList<String>();
			for(int j = 0 ; j < numMappings; j++)
				mappings.add( ois.readUTF());
			
			map.put(speciesURI, mappings);
		
		}
	}

	public String addSpecies(String species) throws FileNotFoundException, IOException 
	{
		String uri = auxAddSpecies(species,species);
		commit();
		return uri;
	}
	
	public boolean addToSpecies(String species, String value)
	{
		List<String> list = map.get( species ) ;
		if(list == null)
			return false;
		
		list.add(value);
		return true;
	}
	
	public void commit() throws FileNotFoundException, IOException 
	{
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(file));


		oos.writeInt(map.size());
		Iterator<Entry<String, List<String>>> it = map.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<String, List<String>> next = it.next();
			String speciesURI = next.getKey();
			int numMappings = next.getValue().size();
			
			oos.writeUTF(speciesURI);
			oos.writeInt(numMappings);
			
			List<String> mappings = next.getValue();
			for(int j = 0 ; j < numMappings; j++)
				oos.writeUTF( mappings.get(j) );
			
			
			
		}
	
		oos.close();
	}

	private String auxAddSpecies(String uri, String value)
	{
		List<String> list = map.get(uri);
		if(list != null)
			return auxAddSpecies( uri+"X", value );
		
		list = new LinkedList<String>();
		list.add(value);
		
		map.put(uri, list);
		return uri;
	}
	
	
	public HashMap<String,String> speciesMap()
	{
		return utils.CollectionUtils.getInvertedFlattened(map);
	}

	private static TemporaryModel instance;
	
	public static TemporaryModel getInstance() throws FileNotFoundException, IOException 
	{
		if(instance == null)
			instance = new TemporaryModel();
		return instance;
	}
}
