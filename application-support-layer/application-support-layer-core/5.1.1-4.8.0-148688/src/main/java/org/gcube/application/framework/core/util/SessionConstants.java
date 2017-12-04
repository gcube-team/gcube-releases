package org.gcube.application.framework.core.util;

/**
 * @author Valia Tsagkalidou (KNUA)
 *
 */
public class SessionConstants {

	/**
	 * Used for generic resource name which contains the static search configuration
	 */
	public static final String ScenarioSchemaInfo = "ScenarioCollectionInfo";
	
	/**
	 * Used for a variable in the session representing a HashMap<String, List<SearchField>>:
	 * pairs: (schema, list of searchable fields for this schema)
	 */
	public static final String SchemataInfo = "SchemataInfo";
	
	/**
	 * Used for a variable in the session representing a HashMap<String, ArrayList<SchemaFieldPair>>:
	 * pairs: (abstract field, list of mappable field for each schema)
	 */
	public static final String abstractFieldsInfo = "AbstractFieldsInfo";
	
	/**
	 * Used for generic resource name which contains info about searchable fields per schema...
	 */
	public static final String MetadataSchemaInfo = "MetadataSchemaInfo";
	
	/**
	 * Used for generic resource name which contains info about mappable fields per abstarct field and for every schema
	 */
	public static final String SearchFieldsMappingAbstraction = "SearchFieldsMappingAbstraction";
	
	/**
	 * Used for a variable in the session representing the available collections
	 */
	public static final String Collections = "Collections";
	
	/**
	 * Used for a variable in the session representing the available queries (queries that the user has created)
	 */
	public static final String Queries = "Queries";	

	/**
	 * Used for a variable in the session that contains info for the geospatial search (boundinf box, time interval, etc) 
	 */
	public static final String Geospatial = "Geospatial";

	/**
	 * Used for a variable in the session representing the number of the current page number in the results.
	 */
	public static final String page_no = "page_no";

	/**
	 * Used for a variable in the session representing how many results were actually read from resultset last time
	 */
	public static final String resNo = "resNo";
	
	/**
	 * Used for a variable in the session representing the total number of result pages...
	 */
	public static final String page_total = "page_total";
	
	/**
	 * Used for a variable in the session representing the index in the current resultset part (where we stopped reading results last time)
	 */
	public static final String lastRes = "lastRes";
	
	/**
	 * Used for a variable in the session representing whether we have reach the last  page of results
	 */
	public static final String isLast = "isLast";
	
	/**
	 * Used for a variable in the session that contains info about whether we have go beyond the edges of results... (out of index)
	 */
	public static final String out_of_end = "out_of_end";
	
	/**
	 * the current resultset client
	 */
	public static final String rsClient = "rsClient";
	
	/**
	 * the results objects already processed from the resultset 
	 */
	public static final String theResultObjects = "theResultObjects";
	
	/**
	 * the thumbnail urls for each result object (already processed from the resultset )
	 */
	public static final String theThumbnails = "theThumbnails";
	
	/**
	 * the index in the result objects from where we should start reading
	 */
	public static final String startingPoint = "startingPoint";
	
	/**
	 * what type search was applied (Simple, Advanced, Browse, Quick, Google, etc)
	 */
	public static final String sourcePortlet = "sourcePortlet";
	
	/**
	 * the resultset EPR (not used any more)
	 */
	public static final String rsEPR = "rsEPR";		/* the Result Set EPR */
	
	/**
	 * whether to show result rank or not
	 */
	public static final String showRank = "showRank";
	
	/**
	 * an exception occured during search: message to the users
	 */
	public static final String searchException = "searchException";

	/**
	 * the id of the active query
	 */
	public static final String activeQueryNo = "activeQueryNo";
	
	/**
	 * the id of the active query to be presented
	 */
	public static final String activePresentationQueryNo = "activePresentationQueryNo";
	
	/**
	 * the collections hierarchy (groups - collections)
	 */
	public static final String collectionsHierarchy = "collectionsHierarchy";
}
