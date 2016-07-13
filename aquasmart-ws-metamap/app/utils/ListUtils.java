package utils;

import java.util.List;

/**
 * Collection of static utility methods to handle, 
 * modify and create java.util.List collections
 * @author pedroamaral
 *
 */
public class ListUtils 
{


	/**
	 * Converts an open-ended argument set of type T to an
	 * ordered java.util.List of type T
	 * @param strings
	 * @return
	 */
	public static <T> List<T> list(T... strings)
	{
		List<T> list = new java.util.LinkedList<T>();
		for(T s : strings)
			list.add(s);
		return list;
	}
	
	
}
