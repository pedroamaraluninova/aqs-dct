package utils.cache;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

public interface Cache<X extends Serializable> 
{

	public boolean exists();
	
	public boolean isLive();
	
	public void create(X object) throws FileNotFoundException, IOException, ClassNotFoundException;
	
	public void recover() throws FileNotFoundException, IOException, ClassNotFoundException;
	
	public void commit(X object) throws FileNotFoundException, IOException, ClassNotFoundException;
	
	public void commit() throws ClassNotFoundException, IOException;
	
	public X getCached();
	
	public void destroy() throws IOException;
	
}
