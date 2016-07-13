package aquasmart.services.metamap.beans;

import java.util.List;

import utils.json.JsonSerializable;

/**
 * Representation of a set of ordered concepts that can be
 * converted to a JSON representation
 * @author pedroamaral
 *
 */
public interface OrderedConceptSet extends JsonSerializable 
{

	/**
	 * Returns the ordered list of concepts
	 * @return a java.util.LinkedList containing Concept objects
	 */
	public List<Concept> getConcepts();
	
}
