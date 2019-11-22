/**
 *
 */
package org.gcube.portlets.user.geoexplorer.client;


/**
 * The Class Constants.
 * @author ceras
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Nov 26, 2015
 */
public class Constants {

	public final static String VERSION = "2.14.0";
	public final static int windowWidth = 800;
	public final static int windowHeight = 550;
	public static final String geoWindowTitle="Geo Explorer "+VERSION;
	public static final int windowMinWidth=300;
	public static final int windowMinHeight=300;
	public static final String panelsBodyStyle = "background-color:#FFFFFF";

	public static final String SEARCH_FIELD_TITLE = "title";
	public static final String SEARCH_FIELD_VRE_CONTEXT = "VREContext"; //RESTICT SEARH TO VRE PRODUCTS ONLY

	public static final boolean reloadCswAtStartup = true; // false for test

	// LOG AND INFO PRINTING
	public static boolean printLog=false;
	public static boolean cacheEnabled = true;
	public static final boolean localLog = false;

	/**
	 * Log.
	 *
	 * @param s the s
	 */
	public static void log(String s) {
		if (printLog)
			System.out.println(s);
	}

	public static native void consoleLog(String message) /*-{
		console.log( "me:" + message );
	}-*/;



//	public static final String METADATA_CSS_URL = GWT.getModuleBaseURL() + "dumpmetadata.css";

	public static final long SECONDS_FOR_EACH_RELOAD = 60*30; // 30 minutes

//	public static final String defaultScope = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling";
//	public static final String defaultScope = "/gcube/devNext";
	public static final String defaultScope = "/pred4s/preprod/preVRE";

	public static final String NOT_FOUND = "NOT FOUND";


	//USED IN SERVLET MAP GENERATOR
	public static final String NAME_IMG_TRUE_MARBLE = "resources/baseLayer.png";
	public static final String NAME_IMG_BIG_TRUE_MARBLE = "resources/aquamapsTrueMarble.png";
	public static final String NAME_IMG_ERROR = "resources/error.png";


	//USED IN SERVLET METADATA ISO VIEW
	public static final String PRELOAD_LAYER = "images/preloadlayer.gif";
	public static final String GETBODYHTML = "getbodyhtml";
	public static final String GETHEADHTML = "getheadhtml";
	public static final String UUID = "UUID";
	public static final String RANDOM = "random";
	public static final int UPPERBOUND = Integer.MAX_VALUE;
	public static final String LOADPREVIEW = "loadpreview";
	public static final String SCOPE = "scope";
	public static final String CURRTAB = "viewtab";

	public static final int QUERY_SIMILARITY = 1;


	//GEONETWORK METADATA STYLES
	public static final String SIMPLE = "simple";
	public static final String ISOCORE = "ISOCORE";
	public static final String INSPIRE = "inspire";

	//HTTP PARAMS USED IN MAP PREVIEW GENERATOR
	public static final String LAYERWIDHT = "width";
	public static final String LAYERHEIGHT = "height";
	public static final String BBOX = "bbox";


	public static final String MESSAGE_METADATA_UUID_NOT_FOUND = "The metadata universally unique identifier (UUID) is null or empty. Re-call this page with a valid UUID";

	//SERVLETS
	public static final String METADATA_ISO19139_VIEW = "MetadataISO19139View";
	public static final String SUMMARY_METADATA_ISO19139_VIEW = "SummaryMetadataISO19139View";
	public static final String METADATA_ISO19139_SOURCE_VIEW = "MetadataISO19139SourceView";
	public static final String EMBEDDED_GEONETWORK_METADATA_ISO19139_VIEW = "EmbeddedGeonetworkMetadataISO19139View";
	public static final String MAP_PREVIEW_GENERATOR = "MapPreviewGenerator";

	public static final int CONNECTION_TIMEOUT = 0;


	//HTTP PARAMETERS USED BY SERVLETS
	public static final String WMS_REQUEST_PARAMETER = "wms_request";
	public static final String ADD_TRU_MARBLE_REQUEST_PARAMETER = "is_base_layer";

}




