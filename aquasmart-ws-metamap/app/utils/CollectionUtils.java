package utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import utils.tuples.Tuple2;

public class CollectionUtils {

	public static <X,Y> HashMap<X, Y> invertMap(
			HashMap<Y, X> labelToIndex) 
	{
		HashMap<X,Y> inverted = new HashMap<X,Y>();
		Iterator<Entry<Y, X>> it = labelToIndex.entrySet().iterator();
		
		if(! it.hasNext() )
			return inverted;
		
		for( Entry<Y,X> entry = it.next();  it.hasNext() ; entry = it.next() )
			inverted.put( entry.getValue(), entry.getKey() );
		
		return inverted;
	}

	public static <X,Y> HashMap<X, List<Y>> invertMapWithCollisions(Map<Y, X> map) 
	{
		HashMap<X, List<Y>> groupingMap = new 
				HashMap<X, List<Y>>();
		
		map.entrySet().forEach( x -> {
			
			List<Y> list = CollectionUtils.getOrNew( 
					groupingMap.get( x.getValue() ) );
			
			list.add( x.getKey() );
			groupingMap.put( x.getValue(), list );
			
		});
		
		return groupingMap ;
	}
	
	public static <X,Y> List<Tuple2<X,Y>> mapToList( Map<X,Y> map)
	{
		List< Tuple2<X,Y> > newList = new LinkedList< Tuple2<X,Y> >();
		
		map.entrySet().stream().forEach( 
				x -> newList.add( 
						new Tuple2<X,Y>( x.getKey(), x.getValue() ) ) 
		);
		
		return newList;
		
	}
	
	public static <X,Y> Y find( X key, List<Tuple2<X,Y>> list)
	{
		
		Iterator<Tuple2<X, Y>> it = list.iterator();
		
		while( it.hasNext() ) 
		{
			Tuple2<X,Y> next = it.next();
			if( key.equals(next.one) )
				return next.two;
		}
		
		return null;
		
	}
	
	public static <X> List<X> getOrNew(List<X> list)
	{
		if( list == null )
			return new java.util.LinkedList<X>();
		return list;
	}

	public static HashMap<String, String> getInvertedFlattened(
			HashMap<String, List<String>> map) 
	{
		HashMap<String, String> newMap = new HashMap<String,String>();
		
		Iterator<Entry<String, List<String>>> keyIt = map.entrySet().iterator();
		
		while(keyIt.hasNext())
		{
			Entry<String, List<String>> next = keyIt.next();
			
			Iterator<String> valIt = next.getValue().iterator();
			
			while(valIt.hasNext())
				newMap.put(valIt.next(), next.getKey() );
		}
		
		
		return newMap;
	}

	
	
	
	
}
