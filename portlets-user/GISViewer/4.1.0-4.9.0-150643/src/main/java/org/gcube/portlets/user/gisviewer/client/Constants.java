package org.gcube.portlets.user.gisviewer.client;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;


/**
 * The Class Constants.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 26, 2016
 */
public class Constants {

	public static String VERSION = "4.0.0";
	public static String GCUBE_TOKEN = "d7a4076c-e8c1-42fe-81e0-bdecb1e8074a"; //A STATIC GCUBE TOKEN FOR TEST
	public static final Float DEFAULT_XY_WPS_RESOLUTION = new Float(0.5);

	// LOG AND INFO PRINTING
	public static boolean printLog=false;

	/**
	 * Log.
	 *
	 * @param s the s
	 */
	public static void log(String s) {
		if (printLog)
			GWT.log(s);
	}

	/**
	 * Info.
	 *
	 * @param title the title
	 * @param text the text
	 */
	public static void info(String title, String text) {
		if (printLog)
			Info.display(title, text);
	}

	/**
	 * Alert.
	 *
	 * @param string the string
	 * @param string2 the string2
	 */
	public static void alert(String string, String string2) {
		if (printLog)
			MessageBox.alert(string, string2, null);
	}

	// LAYERS
	public static final double defaultOpacityLayers = 0.7;

	public static final String[] brightLayers = {
		"TrueMarble.16km.2700x1350",
		"Test.TrueMarble.16km.2700x1350_gf",
		"depthmean",
		"depthmean_annual",
//		"depthMean",
	};

	public static final String[][] defaultStyleTransects = {
		{"depth_style", "DepthMean", "depth"},
		{"primprod_style", "PrimProdMean", "primprod"},
		{"salinity_style", "SalinityMean", "salinity"},
		{"sst_style", "SSTAnMean", "sst"},
		{"biodiversity_style", "Biodiv", "biodiversity"},
		{"ice_style", "IceConAnn", "ice"},
		{"occurrence_style", "GoodCell", "occurrence"}
	};

	public static final String[] dataStoresWithTransect = {
		"aquamapsdb",
		"aquamapsgeomar"
	};

	public static final String borderLayer = "Test.TrueMarble.16km.2700x1350_gf";
	public static final String baseLayer = "TrueMarble.16km.2700x1350";
	public static boolean isBorderLayerVisible=false;

	public static final int MAX_WFS_FEATURES = 200; // zero for no limit

	//INTERFACE
	public static final int geoWindowWidth=1000;//900;
	public static final int geoWindowHeight=760;//550;
	public static final int geoWindowMinWidth=300;
	public static final int geoWindowMinHeight=300;
	public static final String geoWindowTitle="GIS Viewer "+ VERSION;
	public static final boolean geoWindowShadow = false;
	public static final boolean geoWindowDataPanelOpenedAtStart = true;
	public static String defaultProjection = "EPSG:4326";

	public static final String hcafLegendWidth = "400px";

	public static final int omHeight = 279; //379;
	public static final int omWidth = 701;
	public static final int omMaxHeight = 550;
	public static final int omMaxWidth = 930;

	public static final String panelsBodyStyle = "background-color:#FFFFFF";
	public static final int mapPanelSpacingX = 5;
	public static final int mapPanelSpacingY = 56;
	public static final String MessageLoadingLayersData = "Loading layers data...";
	public static final String FAO_DATA_STORE = "FI Geoserver";
	public static final String FAO_DATA_STORE_PREFIX = "fifao_";
	public static final int numZoomLevels = 12;

	public static final String defaultScope = "/gcube/devsec/devVRE";
//	public static final String defaultScope = "/d4science.research-infrastructures.eu/gCubeApps/EcologicalModelling";

	public static int openLayersMapDefaultZoom=2;
	public static boolean isOverViewMapVisible=false;

	public static boolean isSaveButtonEnabled = false;

	protected static int legendDialogMaxHeight = 500;
	protected static int legendDialogWidth = 200;

	/**
	 * The Enum Mode.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 5, 2016
	 */
	public static enum Mode {NORMAL, TEST};
	public static Mode MODE = Mode.NORMAL;

	public static boolean getLayerTitles = true;
	public static boolean layersDragEnabled = true;
	//protected static String defaultProjection = "EPSG:900913";//"EPSG:4326";
	public static boolean buttonSaveLayerEnabled = true;

	public static int minGisViewerHeight = 550;

	public static final String UNKNOWN_STYLE_NAME = "";

	//TRUE MARBLE OPEN LAYER REFERENCES
//	public final static String TRUE_MARBLE_TITLE = "True Marble";
//	public final static String TRUE_MARBLE_NAME = "truemarble";
//	public final static String TRUE_MARBLE_URL = "http://romeo.jrc.it/maps/mapserv.cgi?map=../mapfiles/acpmap_static.map&";

	//MAP SERVER PIVOT
	public static final String WXS = "wxs";
	//GEOSERVER PIVOT
	public static final String WMS = "/wms";

	//OUTPUT FORMAT
	public static final String CSV = "csv";
	public static final String JSON = "json";

	public static final String COOKIE_NAME = "GisViewerD4ScienceCK";

	public static final int OFFSET_ZINDEX = 50;

	public static String COLORSCALERANGE = "COLORSCALERANGE";
}
