package aquasmart.services.metamap.utils;

/**
 * This class holds all the string-encoded
 * tags and uris used in the meta-map service\n
 * Includes: (a) Language strings, (b) Ontology uris, (c) JSON tags
 * 
 * @author pedroamaral
 *
 */
public class STATIC 
{

	public static final String 
		ANONYMOUS_ATTRIBUTE_DESCRIPTION 
			= "Anonymous attribute. Made secret by the reporting company.",
		USER_HOME = "/opt/",
		TDB_GLOSS = USER_HOME + "tdb-glossary",
		TDB_DATASET = USER_HOME + "tdb-datasets";
	
	public class Cache
	{
		public static final String 
			CACHE_LOCATION = "metamap-cache/",
			DATASET_CACHE_FILE = "data.cache",
			METADATA_CACHE_FILE = "metadata.cache" ;
	}
	
	/**
	 * Final strings that represent
	 * urls or parameters of the data pull service
	 * @author amaral
	 *
	 */
	public static class DatasetService
	{
		public static  String 
			SERVICE_LOC = System.getenv("SERVICE_URL"),
			BASE_URL = SERVICE_LOC + "/api/staging/data";
				
		public static final String 
			
			GET_META = "metadata",
			DATASET_ID = "datasetID",
			GET_DATA = "data";
		
		public class GetData
		{
			public static final String
				INDEX = "index",
				INDEXES = "indexes",
				MESSAGE = "message";
				
		}
		
		public class OUTPUT
		{
			public class GetMetadata
			{
				public static final String
					CODE = "code",
					MESSAGE = "message",
					ID = "_id",
					DATASETID = "datasetId",
					DATA_TYPE = "data_type",
					COMPANY = "company",
					USER = "user",
					UPLOAD_STAMP = "upload_stamp";
				
				public class Metadata
				{
					public static final String
						FIELD = "metadata",
						NAME = "name",
						LABEL = "label",
						TYPE = "type",
						ID = "_id" ,
						FORMAT = "format",
						CONCEPT = "concept",
						SAMPLES = "samples";
				}
			}
			
			
			public class Data
			{
				public static final String
					VERSION = "version",
					DATASET_ID = "datasetId",
					COMPANY = "company",
					DATA_TYPE = "data_type",
					USER = "user",
					UPLOAD_STAMP = "upload_stamp",
					MESSAGE = "message",
					DATA = "data";
				
				public class DataRow 
				{
					public static final String 
						ROW_ID = "_id",
						ROW_NUMBER = "ndx",
						SYSTEM_ID = "system_generated_id",
						STATUS = "status";
					
					public class RowContent
					{
						public static final String
							FIELD = "data";
					}
					
					public class Reason
					{
						public static final String
							FIELD = "reason",
							LABEL = "label",
							VALUE = "value",
							ALLOWED = "allowed_values",
							MIN = "min",
							MAX = "max",
							MESSAGE = "missing value",
							EXTRA = "extra",
							CODE = "code";
					}
				}
			}
		}
		
		
	}
	
	/**
	 * Domain type definitions
	 * @author pedroamaral
	 *
	 */
	public class Domains
	{
		public static final String
			TEMPORAL = "temporal",
			CATEGORIC = "categoric",
			TEXTUAL = "textual",
			NUMERIC = "numeric";
	}
	
	/**
	 * Precision definitions by type
	 * @author pedroamaral
	 *
	 */
	public class Precision
	{
		public class TemporalPrecision
		{
			public static final String
				SECOND = "Second",
				MINUTE = "Minute",
				HOUR = "Hour",
				DAY = "Day",
				MONTH = "Month",
				YEAR = "Year";
		}
		
		public class NumericPrecision
		{
			public static final String
				INTEGER = "Integer",
				REAL = "Real";
		}
	}
	
	
	/**
	 * Holds the static final strings that represent
	 * language annotations
	 * @author Pedro Amaral
	 *
	 */
	public class Language 
	{
		public static final String 
			ENG = "en-US",
			PT = "pt-PT";
	}
		
	/**
	 * Holds the static final strings that represent
	 * URIs from the Glossary Ontology
	 * @author Pedro Amaral
	 *
	 */
	public class Ontology
	{
		public static final String 
			BASE = "http://www.owl-ontologies.com/OntoGlossary.owl#";
		
		public class CONCEPT {
			public static final String
				NAME = BASE + "Name",
				DEFINITION = BASE + "Definition",
				TYPE = BASE + "Terms";
		}  
	
	}
	
	/**
	 * URIs for the DATASET ONTOLOGY
	 * @author pedroamaral
	 *
	 */
	public class DOntology
	{
		

		public static final String
			BASE = "http://www.owl-ontologies.com/AquasmartDatasets#";


		public class Dataset 
		{

			public static final String
				TYPE = BASE + "Dataset",
				NAME = BASE + "name",
				NUMBER_OF_LINES = BASE + "numberOfLines",
				CONTAINS_ATTRIBUTE = BASE + "containsAttribute",
				TIME_FORMAT = BASE + "timeFormat";
			
			public class TimeFormat
			{
				public static final String
					TYPE = BASE + "TimeFormat",
					FORMAT = BASE + "format",
					FORMAT_PRECISION = BASE + "formatPrecision";
			}
		} 

		public class DatasetAttribute
		{

			public static final String
				TYPE = BASE + "DatasetAttribute",
				TABLE_INDEX = BASE + "tableIndex",
				IS_INSTANTIATION_OF = BASE + "isInstantiationOf",
				HAS_STATISTIC = BASE + "hasStatistic";
		}

		public class Attribute
		{
			public static final String
				TYPE = BASE + "Attribute",
				DATA_TYPE = BASE + "dataType",
				NAME = BASE + "name",
				HAS_VALUES_FROM = BASE + "hasValuesFrom",
				CONCEPT = BASE + "concept";

			public class DataType
			{
				public static final String
					TYPE = BASE + "DataType",
					NUMERIC_TYPE = BASE + "NumericType",
					CATEGORIC_TYPE = BASE + "CategoricType",
					TEMPORAL_TYPE = BASE + "TemporalType",
					TEXTUAL_TYPE = BASE + "TextualType";	
			}
		}

		public class Domain
		{
			public static final String
				TYPE = BASE + "Domain",
				SPECIES = BASE + "species",
				SPECIES_DOMAIN = BASE + "Domain_Species";

			public class TemporalDomain
			{
				public static final String
					TYPE = BASE + "TemporalDomain",
					PRECISION = BASE + "timePrecision",
					UPPER_BOUND = BASE + "timeUpperBound",
					LOWER_BOUND = BASE + "timeLowerBound",
					FORMAT = BASE + "format";

				public class TemporalPrecision
				{
					public static final String
						TYPE = BASE + "TemporalPrecision",
						SECOND = BASE + "SecondPrecision",
						MINUTE = BASE + "MinutePrecision",
						HOUR = BASE + "HourPrecision",
						DAY = BASE + "DayPrecision",
						MONTH = BASE + "MonthPrecision",
						YEAR = BASE + "YearPrecision";
				}
			}

			public class NumericDomain
			{
				public static final String
					TYPE = BASE + "NumericDomain",
					PRECISION = BASE + "numberPrecision",
					UPPER_BOUND = BASE + "upperBound",
					LOWER_BOUND = BASE + "lowerBound",
					UNIT = BASE + "unit";

				public class NumericPrecision
				{
					public static final String
						TYPE = BASE + "NumericPrecision",
						INTEGER = BASE + "IntegerPrecision",
						REAL = BASE + "RealPrecision";
				}
			}

			public class CategoricDomain
			{
				public static final String
					TYPE = BASE + "CategoricDomain",
					ENUMERATION = BASE + "enumeration";

				public class CategoricValue
				{
					public static final String
						TYPE = BASE + "CategoricValue",
						VALUE = BASE + "value",
						SPECIES = BASE + "is_species";
				}
			}

			public class TextualDomain
			{
				public static final String
					TYPE = BASE + "TextualDomain";
			}
		}

		public class AttributeStatistic
		{
			public static final String
				TYPE = BASE + "AttributeStatistic",
				MODE = BASE + "mode";

			public class TemporalStatistic
			{
				public static final String
					TYPE = BASE + "TemporalStatistic",
					MIN = BASE + "min",
					MAX = BASE + "max";
			}

			public class NumericStatistic
			{
				public static final String
					TYPE = BASE + "NumericStatistic",
					MIN = BASE + "min",
					MAX = BASE + "max",
					AVERAGE = BASE + "average";
			}
			public class TextualStatistic
			{
				public static final String 
					TYPE = BASE + "TextualStatistic";
			}
			public class CategoricStatistic
			{
				public static final String
					TYPE = BASE + "CategoricStatistic",
					DISTRIBUTION = BASE + "distribution";

				public class ValueDistribution
				{
					public static final String
						TYPE = BASE + "ValueDistribution",
						VALUE = BASE + "targetValue",
						OCCURENCES = BASE + "targetOccurences";
				}
			}
		}

	}
	
	
	/**
	 * Holds the static final strings that represent
	 * the tags used to describe inputs and outputs in json format
	 * @author Pedro Amaral
	 *
	 */
	public class Json
	{
		public class INPUT
		{
			
			public class UPDATE_SPECIES
			{
				public static final String
					SPECIES = "species",
					SPECIES_URI = "speciesURI",
					VALUES = "values";
			}
			
			public class CREATE_DATASET
			{
				public static final String
					NAME = "name",
					FORMATS = "formats",
					FORMAT = "format",
					PRECISION = "precision",
					ID = "id";
			}
			
			public class ATTRIBUTE_INFO
			{
				public static final String
					URI = "datasetAttributeURI";
			}
			
			public class SEARCH_CONCEPT
			{
				public static final String
					KEYWORD = "keyword";
			}
			
			public class MAP_ATTRIBUTE
			{
				public static final String
					HEADER = "header";
			}
			
			public class CREATE_CONCEPT
			{
				public static final String
					NAME = "name",
					DESCRIPTION = "description",
					LANGUAGE = "lang",
					ANONYMOUS = "anonymous";
			
			}
			
			public class CREATE_ATTRIBUTE
			{
				public static final String
					NAME = "name",
					CONCEPT_URI = "conceptURI",
					DOMAIN = "domain",
					DOMAIN_TYPE = "domainType",
					TEMPORAL_DOMAIN = "temporalDomain",
					NUMERIC_DOMAIN = "numericDomain",
					CATEGORIC_DOMAIN = "categoricDomain",
					TEXTUAL_DOMAIN = "textualDomain",
					PRECISION = "precision",
					LOWER_BOUND = "lowerBound",
					UPPER_BOUND = "upperBound",
					UNIT = "unit",
					VALUES = "values",
					FORMAT = "format",
					RANGES = "ranges",
					SPECIES = "species",
					CONCEPT = "concept",
					MONGODATASET_ID = "mongo";
			}
			
			public class SELECT_CONCEPT
			{
				public static final String
					DATASETID = "datasetID",
					COL_NAME = "columnName",
					CONCEPT_URI = "conceptURI",
					COL_INDEX = "columnIndex", 
					COL_LABEL = "columnLabel";
			}
			
			public class CHECK_VALUES
			{
				public static final String
					ATTRIBUTE_ID = "attrID",
					TYPE = "type",
					VALUES = "values",
					DATASET_ID = "datasetID",
					SPECIES = "speciesCol",
					CURRENT = "currentCol";
			}
			
			public class CONCEPT_INFO
			{
				public static final String
					URI = "conceptURI";
			}
		}
		
		/**
		 * JSON output format for the service's operations
		 * @author pedroamaral
		 *
		 */
		public class OUTPUT
		{
			
			public class GET_SPECIES 
			{
				public static final String
					RESULTS = "results",
					SPECIES = "species",
					VALUE = "value",
					MAP = "map";
			}
			
			public class ATTRIBUTE_INFO
			{
				public static final String
					CONCEPT = "conceptURI";
			}
			
			public class SEARCH_CONCEPT
			{
				public static final String
					CONCEPT_URI  = "conceptURI",
					NAME = "name",
					DESCRIPTION = "description",
					RESULTS = "results";
			}
			
			public class CREATE_ATTRIBUTE
			{
				public static final String
					ATTRIBUTE_URI = "attributeURI";
			}

			public class CREATE_DATASET
			{
				public static final String
					URI = "datasetURI";
			}
			
			public class CREATE_CONCEPT
			{
				public static final String 
					URI = "conceptURI";
			}
			
			public class CHECK_VALUES
			{
				public static final String
					OUTLIERS = "outliers";
			}

			public class SELECT_CONCEPT 
			{
				public static final String
					DATASET_ATTR_URI = "datasetAttributeURI";
			}
			
			public class MAP_ATTRIBUTE
			{
				public static final String
					URI = "attributeURI",
					NONE = "none";
			}
			
			public class STATISTICS
			{
				public static final String
					FREQUENCY = "frequency",
					VALUE = "value",
					VALUE_DISTRIBUTION = "valueDistribution",
					MIN = "min",
					MAX = "max",
					TYPE = "type",
					AVERAGE = "avg",
					MODE = "mode";
			}
		}
		
	}
	

	

}
