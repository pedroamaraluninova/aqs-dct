package utils.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class FileSystemCache<X extends Serializable> implements Cache<X>
{

	X cachedItem ;
	File cacheFile ;

	
	public FileSystemCache(String cacheLocation) 
			throws FileNotFoundException, ClassNotFoundException, IOException
	{
		this.cachedItem = null ;
		this.cacheFile = new File( cacheLocation );
	}
	
	public boolean exists()
	{
		return cacheFile.exists() ;
	}
	
	public boolean isLive()
	{
		return cachedItem != null;
	}
	
	public void create(X object) 
			throws IOException, ClassNotFoundException 
	{

		play.Logger.debug("creating cache file");
		if( ! cacheFile.getParentFile().exists() )
			cacheFile.getParentFile().mkdirs();
		
		cacheFile.createNewFile();
		
		this.cachedItem = object;
		
		ObjectOutputStream oos = new ObjectOutputStream( 
									new FileOutputStream( cacheFile ) );
		
		oos.writeObject( this.cachedItem );
		oos.close();
		

		
	}

	@SuppressWarnings("unchecked")
	public void recover()
			throws FileNotFoundException, IOException, ClassNotFoundException 
	{

		ObjectInputStream ois = new ObjectInputStream(
									new FileInputStream( cacheFile ) );
		
		this.cachedItem = (X) ois.readObject();
		
		ois.close();
			
	}
	
	public void commit(X object) 
			throws IOException, ClassNotFoundException
	{
		this.cacheFile.delete();
		this.create( object );
		

	}
	
	public void commit() throws ClassNotFoundException, IOException
	{
		this.commit(this.cachedItem);
	}
	
	public X getCached()
	{
		return this.cachedItem;
	}
	
	public void destroy() throws IOException
	{
		this.cacheFile.delete();

	}


	
	
}
