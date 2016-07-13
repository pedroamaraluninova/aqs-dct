package aquasmart.services.metamap.beans.statistics;

import java.io.Serializable;
import java.util.List;

import play.libs.Json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import aquasmart.services.metamap.stats.Frequency;
import utils.json.JsonSerializable;


public abstract class Statistics<T extends Serializable> implements JsonSerializable , Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract ObjectNode convert(Frequency<T> freq);
	
	public ArrayNode convert(List<Frequency<T>> dist)
	{
		
		ArrayNode result = dist.parallelStream().collect( 
				() -> Json.newArray(),
				(accum,elem) -> accum.add( convert(elem) ),
				(accum,accum2) -> accum.addAll( accum2 )
		);

		return result;
	}
	
}
