package org.gcube.portlets.user.speciesdiscovery.client;

public final class ConstantsSpeciesDiscovery {
	
	//USED IN MODEL CLASS
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ISLEAF = "isLeaf";
	public static final String BASETAXONID = "BaseTaxonID";
	public static final String BASETAXONNAME= "BaseTaxon";
	public static final String COUNTOF = "CountOf";
	public static final String RANK = "Rank";
//	public static final String GROUPEDIDLIST = "GroupedIDList";
	
	//USED IN CLASSIFICATION FILTER
	public static final String BIOTACLASSID = "BiotaID";
	public static final String BIOTACLASS = "Biota";
	public static final String ANIMALIACLASSID = "AnimaliaID";
	public static final String ANIMALIACLASS = "Animalia";
	public static final String PLANTEACLASSID = "PlanteaID";
	public static final String PLANTEACLASS = "Plantea";
	public static final String UNKNOWN = "Unknown";
	public static final String UNKNOWNID = "UnknownID";

	//USED IN GRID
	public static final String NORESULTS = "No results";
	public static final String NONE = "None";
	public static final String FILTER = "Filter: ";
	public static final String REMOVEFILTERTOOLTIP = "Remove current filter";
	public static final String VIEWDETAILS = "View Details (Only selected)";
	public static final String SELECTALL = "Check All Rows";
	public static final String DESELECTALL = "Uncheck All Rows";
	
	//USED IN CLASSIFICATION FILTER
//	public static final String[] MAIN_TAXONOMIC_RANK = new String[]{"kingdom", "phylum", "class", "order", "family", "genus", "species"};
	public static final String GROUPBYRANK = "Group results by";
	
	
	public static final String LOADINGSTYLE = "x-mask-loading";
	public static final String SORTMESSAGE = "Sort filter in alphabetical order (from A to Z)";
	public static final String REQUEST_DATA = "request data...";
	
	//USED IN SEARCH BORDER LAYPUT PANEL
	public final static int JOBPOLLINGMILLISECONDS = 20000;
	
	//USED IN SEARCH FORM PANEL
	protected static final String OCCURENCES = "Occurences";
	protected static final String CLASSIFICATION = "Classification";
	protected static final String COMMON_NAME = "Common name";
	protected static final String SCIENTIFIC_NAME = "Scientific name";
	
	//USED IN ADAVANCED FILTERT
	public static final String AVAILABLEFILTERBOUND = "Bounds filters (lower-upper) are available with following plug-in";
	public static final String AVAILABLEFILTERDATE = "Date filter (from-to) is available with following plug-in";
	public static final String AVAILABLEDATASOURCES = "Occurence points and classification search are available with following plug-in";
	
	//USED IN GISVIEWER
	public static final String LME = "lme";
	public static final String FAOAREA = "faoarea";
	public static final String EEZALL = "eezall";
	public static final String SST_AN_MEAN = "sstAnMean";
	public static final String SALINITY_MEAN = "salinityMean";
	public static final String PRIM_PROD_MEAN = "primProdMean";
	public static final String ENVIRONMENTS = "environments";
	public static final String DEPTH_MEAN_ANNUAL = "DepthMeanAnnual";
	public static final String WORLD_BORDERS = "worldborders";
	
	//ELAPSED TIME FORMATTER
	public static final String TIME_ZONE_UTC = "UTC";
	public static final String TIMEFORMAT_HH_MM_SS = "HH:mm:ss";
	
	//USED IN SERVER/SHARED PACKAGE
	public static final String NULL = "null";
	public static final String UNDEFINED = "Undefined";
	public static final String NOT_FOUND = "not found";
	public static int RESULT_ROW_LIMIT_ITEM_DETAILS = 250;
	public static int TAXONOMY_LIMIT_ITEMS_DETAILS = 100;
	
	//USED IN VIEW DETAILSWINDOW
	public static final String THE_MAX_NUMBER_OF_ITEMS_DISPLAYABLE_IS = "The max number of items displayable is ";
	public static final String ROW_LIMIT_REACHED = "Row limit reached";
	public static final String SAVE_OCCURENCES_POINTS_FROM_SELECTED_RESULTS = "Save occurences points from selected results.";
	public static final String SAVES_IN_CSV_FILE_FORMAT = "Saves in CSV file format";
	public static final String CSV = "CSV";
	public static final String SAVES_IN_DARWIN_CORE_FILE_FORMAT = "Saves in Darwin Core file format";
	public static final String DARWIN_CORE = "Darwin Core";
	public static final String OPEN_MODELLER_BY_DATA_SOURCE = "openModeller CSV (by Data Source)";
	public static final String OPEN_MODELLER = "openModeller CSV";
	public static final String PLAIN_CSV_BY_DATA_SOURCE = "plain CSV (by Data Source)";
	public static final String PLAIN_CSV = "plain CSV";
	public static final String SAVE_OCCURRENCES = "Save Occurrences";
	public static final String SHOW_IN_GIS_VIEWER = "Show in Gis Viewer";
//	public static final String SAVES_IN_DARWIN_CORE_ARCHIVE_FORMAT = "Saves in Darwin Core Archive format";
//	public static final String DARWIN_CORE_ARCHIVE = "Darwin Core Archive";
	public static final String SAVE_TAXONOMY_ITEMS = "Save Taxonomy Items";
	public final static String DETAILS = "Details";
	public final static String OCCURRENCEPOINTS = "Occurrence points";
	public final static int SCHEDULE_MILLIS_COUNT_OCCURRENCES = 2500;
	public static final int PAGE_SIZE = 25;
	public final static String DEFAULTLANGUAGE = "English";
	public final static String BYSCIENTIFICNAME = "by scientific name";
	public final static String SCIENTIFICNAME = "Scientific name";
	public final static String BYCOMMONNAME = "by common name";
	public static final String CAN_NOT_BE_RE_SUBMITTED_UNTIL_IT_HAS_COMPLETED = "can not be re-submitted until it has completed!";
	public static final String MESSAGE_CONFIRM_DELETE_JOB = "Are you sure you want to delete the job";
	public static final String CONFIRM_DELETE = "Confirm delete";
	public static final String ALERT = "Alert";
	public static final String IS_NOT_COMPLETED = "can not be saved until it has completed!";
	
	//USED IN TABLE FOR TAXONOMY ROW
	public static final String SAVES_IN_DARWIN_CORE_ARCHIVE_FORMAT = "Saves in Darwin Core Archive format";
	public static final String DARWIN_CORE_ARCHIVE = "Darwin Core Archive";
	public static final String SAVES_TAXONOMY_CHILDREN_FROM_RESULT = "Saves taxonomy children from result.";
	public static final String SAVE_TAXONOMY_CHILDREN = "Save Taxonomy Children";
	
	//SERVLETS
	public static final String RESULT_ROW_TABLE = "ResultRowTable";
	public static final String TAXONOMY_ROW_TABLE = "TaxonomyRowTable";

}
