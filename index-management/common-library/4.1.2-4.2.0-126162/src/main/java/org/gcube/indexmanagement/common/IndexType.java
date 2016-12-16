package org.gcube.indexmanagement.common;

import java.io.Serializable;
import java.util.List;

import org.gcube.rest.commons.resourceawareservice.resources.Resource;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.is.discovery.ISInformationCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IndexType implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final String INDEX_TYPE_GENERIC_RESOURCE_PREFIX = "IndexType_";
	
	/** The field name of an index type that indicates the document id of a record*/
	public static final String DOCID_FIELD = "ObjectID";
	
	/** The field name of an index type that indicates the full payload of a record*/
	public static final String PAYLOAD_FIELD = "fullpayload";
	
	/** The field name of an index type that indicates the docStatistics of a record*/
	public static final String STATS_FIELD = "docStatistics";
	
	/** The field name of an index type that indicates the ranking of a record*/
	public static final String SCORE_FIELD = "rank";
	
	/** The field name for the collection id of a record*/
	public static final String COLLECTION_FIELD = "gDocCollectionID";
	
	/** The field name for the collection language of a record*/
	public static final String LANGUAGE_FIELD = "gDocCollectionLang";
	
	/** Seperator for the field info published*/
	public static final String SEPERATOR_FIELD_INFO = ":";
	
	/** Wildcard character */
	public static final String WILDCARD = "*";

	/** Snippet character */
	public static final String SNIPPET = "S";
	
	/** Tag for presentable fields in the field info published*/
	public static final String PRESENTABLE_TAG = "p";
	
	/** Tag for searchable fields in the field info published*/
	public static final String SEARCHABLE_TAG = "s";

	public static final String RESULTSNO_EVENT = "resultsNumber";
	public static final String RESULTSNOFINAL_EVENT = "resultsNumberFinal";
	
	public static final String LANG_UNKNOWN = "unknown";
	/**
	 * Default searchable field for Geo Index
	 */
	public static final String GEOFIELD = "geo";
	/**
	 * Optional Default Fields for geo results
	 */
	public static final String RATIO = "ratio";
	public static final String APXCOUNT = "apxcount";
	public static final String DOCNR = "docNr";
	public static final String MBR = "mbr";
	public static final String[] GEODEFAULT = {RATIO, APXCOUNT, DOCNR, MBR, COLLECTION_FIELD, LANGUAGE_FIELD};
	
	/** The logger that this class uses */
	private static final Logger log = LoggerFactory.getLogger(IndexType.class);
	
	private transient InformationCollector icollector = new ISInformationCollector();
	
	/** The name of this index type */
    protected String indexTypeID;
    
    /** The definition of this index type */
    protected String indexType;
    
    /**
     * Class constructor.
     * @param indexTypeName the name of this index type
     */
    protected IndexType(String indexTypeName) {
    	this.indexTypeID = indexTypeName;
    }
    
    /**
     * Returns the index type name.
     * @return
     */
    public String getIndexTypeName() {
    	return indexTypeID;
    }
    
    /**
     * Returns the index type XML definition
     * @return
     */
    public String getIndexTypeAsString() {
    	return indexType;
    }
    
	/**
	 * Retrieves an index type definition stored as a generic resource in
	 * the DIS and returns it as a string.
	 * 
	 * @param indexTypeName the name of the index type to retrieve
	 * @param scope the scope that the index type belongs to
	 * @return the contents of the generic resource
	 */
	public String retrieveIndexTypeGenericResource(String scope) throws Exception {
		String resourceName = INDEX_TYPE_GENERIC_RESOURCE_PREFIX + indexTypeID;

		
		List<Resource> resources = icollector.getGenericResourcesByName(resourceName, scope);
		
		if (resources ==  null || resources.size() == 0){
			throw new Exception("Generic resource not found.");
		}
		
		indexType = resources.get(0).getBodyAsString();
		return indexType;
	}
}
