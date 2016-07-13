package utils.sparql.update;


public class SparqlTriple 
{

	public String resource;
	public String property;
	
	public String literalObj,
				lang;
	
	public String resourceObj;
	
	public boolean resourceObject;
	
	public SparqlTriple(String res, String prop, String obj, String lang)
	{
		this.resource = res;
		this.property = prop;
		this.literalObj = obj;
		this.lang = lang;
		this.resourceObject = false;
	}
	
	public SparqlTriple(String res, String prop, String obj)
	{
		this.resource = res;
		this.property = prop;
		this.resourceObj = obj;
		this.resourceObject = true;
	}
	
	public String toString()
	{
		String result = "TRIPLE( "+shorten(resource)+", "+shorten(property)+", ";
		
		result += shorten(resourceObject ? resourceObj : literalObj);
		
		return result;
			
	}
	
	private String shorten(String target)
	{
		String[] split = target.split("#");
		
		return split.length == 1 ? split[0] : split[1];
	}
	
}
