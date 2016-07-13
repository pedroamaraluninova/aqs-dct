package aquasmart.services.datapull.beans.metaschema;

import java.io.Serializable;

import aquasmart.services.metamap.beans.statistics.Statistics;
import aquasmart.services.metamap.utils.STATIC.DatasetService;
import aquasmart.services.metamap.utils.STATIC.DatasetService.OUTPUT.GetMetadata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import utils.json.JSON;
import utils.json.JsonDeserializable;
import utils.json.JsonSerializable;

public class MetaItem implements Serializable, JsonSerializable
{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	String label, name, type, format, concept;
	
	DataSamples samples;
	Statistics stats;
	public MetaItem(String label, String name, String type, String format,
			String concept) {
		this(label,name,type,format,concept,null);
	}

	
	public MetaItem(String label, String name, String type, String format,
			String concept, DataSamples samples) {
		super();
		this.label = label;
		this.name = name;
		this.type = type;
		this.format = format;
		this.concept = concept;
		this.samples = samples;
		this.stats = null;
	}

	
	@Override
	public String toString() {
		return "MetaItem [label=" + label + ", name=" + name + ", type=" + type
				+ ", format=" + format + ", concept=" + concept + ", samples="
				+ samples + ", stats=" + stats + "]";
	}
	
	public String getLabel() {
		return label;
	}


	public String getName() {
		return name;
	}


	public String getType() {
		return type;
	}


	public String getFormat() {
		return format;
	}


	public String getConcept() {
		return concept;
	}


	public DataSamples getSamples() {
		return samples;
	}

	public MetaItem setConcept(String concept)
	{
		this.concept = concept;
		play.Logger.debug(this.toString());
		return this;
	}
	
	public MetaItem setSamples(Statistics stats)
	{
		this.stats = stats;
		return this;
	}
	
	

	@Override
	public JsonNode toJson() {
		ObjectNode root = JSON.newObject();
		
		root.put( GetMetadata.Metadata.LABEL, this.label );
		root.put( GetMetadata.Metadata.NAME, this.name );
		root.put( GetMetadata.Metadata.TYPE, this.type );
		
		if(format != null)
			root.put( GetMetadata.Metadata.FORMAT, this.format );
		
		root.put( GetMetadata.Metadata.CONCEPT, this.concept );

		if( stats != null )
			root.set( GetMetadata.Metadata.SAMPLES, stats.toJson() );
		
		return root;
	}
	
	public static MetaItem fromJson(JsonNode root) 
	{
		
		String label = root.get( GetMetadata.Metadata.LABEL).asText(),
				name = root.get( GetMetadata.Metadata.NAME).asText(),
				type = root.get( GetMetadata.Metadata.TYPE).asText(),
				format = null, concept = null;
		
		if( root.has( GetMetadata.Metadata.FORMAT) )
			format = root.get( GetMetadata.Metadata.FORMAT ).asText();
		
		if( root.has( GetMetadata.Metadata.CONCEPT ) )
			concept = root.get( GetMetadata.Metadata.CONCEPT).asText();
		
		DataSamples samp = null;
		if( root.has( GetMetadata.Metadata.SAMPLES) )
			samp = DataSamplesFactory.fromJson( root.get( GetMetadata.Metadata.SAMPLES) );
		
		return new MetaItem( label, name, type, format, concept, samp );
	}

}
