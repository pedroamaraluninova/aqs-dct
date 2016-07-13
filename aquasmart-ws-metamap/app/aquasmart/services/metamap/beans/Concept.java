package aquasmart.services.metamap.beans;


import utils.json.JsonSerializable;


/**
 * Representation of a semantic concept
 * that can be converted to json
 * @author Pedro Amaral
 *
 */
public interface Concept extends JsonSerializable
{
	/**
	 * Returns the name of the concept (human-readable)
	 * @return
	 */
	public String getName();
	
	/**
	 * Returns the description of the concept (human-readable)
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Returns the URI that identifies thes concept
	 * @return URI as java.lang.String
	 */
	public String getURI();

	//only in java8, ALL IMPLEMENTATIONS MUST INCLUDE THIS METHOD!!!
	//public static Concept create(List<String> result);
}
