package org.gcube.contentmanagement.timeseries.geotools.vti.test.experiments;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.geoserverinterface.DataTransferUtl;
import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeoserverCaller;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.engine.MakeStyle;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterStylesGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(DataTransferUtl.class);
	
	GISInformation gisInfo;
	ConnectionsManager connectionsManager;
	String workspace;
	String datastore;
	EngineConfiguration geocfg;

	public ClusterStylesGenerator(String configFolder, String geoNetworkURL, String geoServerBackupURL, String geoNetworkUser, String geoNetworkPwd, String geoServerUser, String geoServerPwd, String geoServerDBURL, String geoServerDBUser, String geoServerDBPWD, String workspace, String datastore) {
		AnalysisLogger.setLogger(configFolder + AlgorithmConfiguration.defaultLoggerFile);
		this.workspace = workspace;
		this.datastore = datastore;
		gisInfo = new GISInformation();
		gisInfo.setGeoNetworkUrl(geoNetworkURL);
		gisInfo.setGeoNetworkUserName(geoNetworkUser);
		gisInfo.setGeoNetworkPwd(geoNetworkPwd);

		gisInfo.setGisDataStore(datastore);
		gisInfo.setGisUserName(geoServerUser);
		gisInfo.setGisPwd(geoServerPwd);
		gisInfo.setGisWorkspace(workspace);
		gisInfo.setGisUrl(geoServerBackupURL);

		geocfg = new EngineConfiguration();
		geocfg.setConfigPath(configFolder);
		geocfg.setDatabaseUserName(geoServerDBUser);
		geocfg.setDatabasePassword(geoServerDBPWD);
		geocfg.setDatabaseURL(geoServerDBURL);

	}

	public static String createPointsStyleMapString(String styleName, String styleAttribute, int numberOfClusters, Class typeValue, Object minValue,Object maxValue) throws Exception{
		
		String style = createStyleScatterColors(styleName, styleAttribute, numberOfClusters, typeValue, minValue, maxValue);
		style = style.replace("<sld:PolygonSymbolizer>", "<PointSymbolizer><Graphic><Mark><WellKnownName>square</WellKnownName>");
		style = style.replace("</sld:PolygonSymbolizer>", "</Mark><Size>6</Size></Graphic></PointSymbolizer>");
		return style;
	}
	
	public boolean generateStyleMap(String clusteringAlgorithmName, String clusterTable, String destinationMapTable, String datastore, String clusteridField,String outliersField) throws Exception {

		try {
			connectionsManager = new ConnectionsManager(geocfg.getConfigPath());
			connectionsManager.initGeoserverConnection(geocfg);
			AnalysisLogger.getLogger().trace("Connected to Geo Server DB");
			String randomSuffix = (""+Math.random()).replace(".","").substring(0,3);
			String styleName = clusteringAlgorithmName+"_"+destinationMapTable+"_"+randomSuffix;
			String styleNameOutliers = "outliers_"+styleName;
			String styleAttribute = clusteridField;
			AnalysisLogger.getLogger().trace("Getting Number of Clusters");

			List<Object> pointsN = DatabaseFactory.executeSQLQuery(String.format("select count(*) from (select distinct " + styleAttribute + " from %1$s) as a", destinationMapTable), connectionsManager.getGeoserverConnection());

			int numberOfClusters = Integer.parseInt("" + pointsN.get(0));

			System.out.println("Number Of Clusters " + numberOfClusters);

			String style = createPointsStyleMapString(styleName, styleAttribute, numberOfClusters, Integer.class, (numberOfClusters - 1), 0);
			String styleOutliers = createPointsStyleMapString(styleNameOutliers, outliersField, numberOfClusters, Boolean.class, Boolean.TRUE, Boolean.FALSE);
			// AnalysisLogger.getLogger().trace("Resulting Style : "+style);

			AnalysisLogger.getLogger().trace("Creating Style on GeoServer");

			GeoCaller gc = new GISOperations().getGeoCaller(gisInfo);
			String geoserverURL = gc.getGeoServerForLayer(destinationMapTable);
			AnalysisLogger.getLogger().trace("Selected Geoserver for layer "+destinationMapTable+" : "+geoserverURL);
			GeoserverCaller gsc = new GeoserverCaller(geoserverURL, gisInfo.getGisUserName(),gisInfo.getGisPwd());
			
			List<String> files = gsc.listStyles();

			AnalysisLogger.getLogger().trace("Checking if style is yet present");
			for (String filename : files) {
				filename = filename.trim();
				if (filename.equalsIgnoreCase(styleName)) {
					AnalysisLogger.getLogger().trace("Deleting previous style " + styleName);
					boolean del = new GISOperations().deleteStyle(gisInfo, filename);
					Thread.sleep(5000);
					AnalysisLogger.getLogger().trace("Deleted " + del);
				}
			}

			boolean toReturn = gsc.sendStyleSDL(style);
			AnalysisLogger.getLogger().trace("Sent Creation request to geoServer for clustering style " +styleName+" : "+ toReturn);
			toReturn = gsc.sendStyleSDL(styleOutliers);
			AnalysisLogger.getLogger().trace("Sent Creation request to geoServer for outliers style " + styleNameOutliers+" : "+toReturn);

			FeatureTypeRest ftr = gsc.getFeatureType(workspace, datastore, destinationMapTable);
			ArrayList<String> styles = new ArrayList<String>();
			styles.add(styleName);
			styles.add(styleNameOutliers);
			styles.add("point");

			boolean addedStyle = gsc.setLayer(ftr, styleName, styles);
			AnalysisLogger.getLogger().trace("Style "+styleName+" added to layer " + destinationMapTable + ": " + addedStyle);
			return addedStyle;
			
		} catch (Exception e) {
			throw e;
		} finally {
			AnalysisLogger.getLogger().trace("Connection closed");
			connectionsManager.getGeoserverConnection().close();
		}
	}

	public static void main(String[] args) throws Exception {
		String cfgPath = "./cfg/";
		String algorithm = "kmeans";
		String tablename = "occcluster_" + algorithm;
		String workspace = "aquamaps";
		String datastore = "timeseriesgisdb";
		String clusterfield = "clusterid";
		String outlierfield = "outlier";
		String geonetworkurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetworks";
		String geonetworkuser = "admin";
		String geonetworkpwd = "admin";
		String gisdatastore = "timeseriesgisdb";
		String gispwd = "gcube@geo2010";
		String gisurl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
		String gisuser = "admin";
		String geodb = "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb";
		String geouser = "postgres";
		String geopwd = "d4science2";
		ClusterStylesGenerator csg = new ClusterStylesGenerator(cfgPath, geonetworkurl, gisurl, geonetworkuser, geonetworkpwd, gisuser, gispwd, geodb, geouser, geopwd, workspace, datastore);
		csg.generateStyleMap(algorithm, tablename, gisdatastore, tablename.replace("_",""),clusterfield,outlierfield);
	}

	public static void main1(String[] args) throws Exception {

		String cfgPath = "./cfg/";
		// String algorithm = "dbscan";
		// String algorithm = "xmeans";
		String algorithm = "kmeans";
		String tablename = "occcluster_" + algorithm;
		String destinationMapTable = tablename.replace("_", "");
		String workspace = "aquamaps";
		String dataStore = "timeseriesgisdb";

		String styleName = "occurrencecluster10" + algorithm;
		String styleAttribute = "clusterid";

		AnalysisLogger.setLogger(cfgPath + AlgorithmConfiguration.defaultLoggerFile);

		GISInformation gisInfo = new GISInformation();
		gisInfo.setGeoNetworkUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geonetworks");
		gisInfo.setGeoNetworkUserName("admin");
		gisInfo.setGeoNetworkPwd("admin");

		gisInfo.setGisDataStore("timeseriesgisdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace(workspace);
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");

		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		configuration.setConfigPath(cfgPath);
		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");

		configuration.setTimeSeriesDatabase("jdbc:postgresql://localhost/testdb");
		configuration.setTimeSeriesUserName("gcube");
		configuration.setTimeSeriesPassword("d4science2");

		ConnectionsManager connectionsManager = new ConnectionsManager(cfgPath);
		EngineConfiguration tscfg = null;
		if (configuration.getTimeSeriesDatabase() != null) {
			tscfg = new EngineConfiguration();
			tscfg.setConfigPath(cfgPath);
			tscfg.setDatabaseUserName(configuration.getTimeSeriesUserName());
			tscfg.setDatabasePassword(configuration.getTimeSeriesPassword());
			tscfg.setDatabaseURL(configuration.getTimeSeriesDatabase());
		}
		EngineConfiguration geocfg = null;
		if (configuration.getGeoServerDatabase() != null) {
			geocfg = new EngineConfiguration();
			geocfg.setConfigPath(cfgPath);
			geocfg.setDatabaseUserName(configuration.getGeoServerUserName());
			geocfg.setDatabasePassword(configuration.getGeoServerPassword());
			geocfg.setDatabaseURL(configuration.getGeoServerDatabase());
			AnalysisLogger.getLogger().trace("Create Points Map-> connected to Geo Server");
			connectionsManager.initGeoserverConnection(geocfg);
		}

		connectionsManager.initTimeSeriesConnection(tscfg);
		connectionsManager.initGeoserverConnection(geocfg);

		// alter table : no more necessary
		/*
		 * String alterstat = String.format("alter table %1$s rename %2$s to %2$s_bak",destinationMapTable,styleAttribute); System.out.println("Modifying the cluster column "+alterstat); DatabaseFactory.executeSQLUpdate(alterstat, connectionsManager.getGeoserverConnection());
		 * 
		 * alterstat = String.format("alter table %1$s add %2$s integer",destinationMapTable,styleAttribute); System.out.println("Modifying the cluster column "+alterstat); DatabaseFactory.executeSQLUpdate(alterstat, connectionsManager.getGeoserverConnection());
		 * 
		 * alterstat = String.format("update %1$s set %2$s = %2$s_bak::integer",destinationMapTable,styleAttribute); System.out.println("Modifying the cluster column "+alterstat); DatabaseFactory.executeSQLUpdate("update occclusterkmeans set clusterid=clusterid_bak::integer", connectionsManager.getGeoserverConnection());
		 * 
		 * alterstat = String.format("alter table %1$s drop %2$s_bak;",destinationMapTable,styleAttribute); System.out.println("Modifying the cluster column "+alterstat); // DatabaseFactory.executeSQLUpdate(alterstat, connectionsManager.getGeoserverConnection());
		 */

		List<Object> pointsN = DatabaseFactory.executeSQLQuery(String.format("select count(*) from (select distinct " + styleAttribute + " from %1$s) as a", destinationMapTable), connectionsManager.getGeoserverConnection());
		int numberOfClusters = Integer.parseInt("" + pointsN.get(0));
		System.out.println("Number Of Clusters " + numberOfClusters);

		String style = MakeStyle.createStyleScatterColors(styleName, styleAttribute, numberOfClusters, Integer.class, (numberOfClusters - 1), 0);

		style = style.replace("<sld:PolygonSymbolizer>", "<PointSymbolizer>             <Graphic>              <Mark>                <WellKnownName>square</WellKnownName>");
		style = style.replace("</sld:PolygonSymbolizer>", "</Mark>               <Size>6</Size>            </Graphic>          </PointSymbolizer>");

		// AnalysisLogger.getLogger().trace("Resulting Style : "+style);

		AnalysisLogger.getLogger().trace("sending request to geoServer");

		GeoCaller gc = new GISOperations().getGeoCaller(gisInfo);

		List<String> files = gc.listStyles();

		for (String filename : files) {
			filename = filename.trim();
			// System.out.println(filename);
			if (filename.equalsIgnoreCase(styleName)) {
				AnalysisLogger.getLogger().trace("deleting previous style " + styleName);
				boolean del = new GISOperations().deleteStyle(gisInfo, filename);
				Thread.sleep(5000);
				AnalysisLogger.getLogger().trace("deleted " + del);
			}
		}

		boolean toReturn = gc.sendStyleSDL(style);
		AnalysisLogger.getLogger().trace("sent request to geoServer " + toReturn);
		FeatureTypeRest ftr = gc.getFeatureType(workspace, dataStore, destinationMapTable);
		ArrayList<String> styles = new ArrayList<String>();
		styles.add(styleName);

		// boolean addedStyle = gc.setLayer(ftr, "point", styles);
		boolean addedStyle = gc.setLayer(ftr, styleName, styles);
		AnalysisLogger.getLogger().trace("layer added " + addedStyle);
	}
	
	
	public static String createStyleScatterColors(String nameStyle, String attributeName, int nClasses,Class typeValue, Object maxValue, Object minValue) throws Exception {

		if (nClasses <= 0)
			throw new Exception("Invalid number of classes!!");
		Double fMax = Double.valueOf(0.0f);
		Double fMin = Double.valueOf(0.0);
		StyleBuilder sb = new StyleBuilder();
		Style style = sb.createStyle();
		style.setName(nameStyle);
		ArrayList<Rule> rules = new ArrayList<Rule>();
		
		if (typeValue == Integer.class) {
			fMax = (Integer) maxValue * Double.valueOf(1.0);
			Integer iMax = fMax.intValue();
			fMin = (Integer) minValue * Double.valueOf(1.0);
			Integer iMin = fMin.intValue();
			Integer d = ((iMax + 1) - iMin) / nClasses;
			if (d == 0)
				throw new Exception("Too many classes with these values!!");
			Integer start = iMin;
			int nColors = getColorsNumbers(start, iMax, d);
			ArrayList<Color> colors = MakeStyle.scatterColor(nColors);
			if (d == 1) {
				int color = 0;
				while (start <= iMax) {
					rules.add(addRule(sb, attributeName, start, colors.get(color)));
					start = start + d;
					color++;
				}
			} else {
				int color = 0;
				while (start <= iMax) {
					if (start == iMax)
						rules.add(addRule(sb, attributeName, start, colors.get(color)));
					else
						rules.add(addRule(sb, attributeName, start + d, start, colors.get(color)));
					start = start + d;
					color++;
				}
			}
		}
			else if (typeValue == Boolean.class){
				Color c1 = Color.red;
				rules.add(addRule(sb, attributeName, "true", c1));
			} 
		
		FeatureTypeStyle fts = sb.createFeatureTypeStyle("Feature", rules.toArray(new Rule[rules.size()]));
		style.featureTypeStyles().add(fts);
		SLDTransformer aTransformer = new SLDTransformer();
		aTransformer.setIndentation(4);
		String out = aTransformer.transform(style);
		logger.debug("Style String created!");
		return out;
	}
	
	private static int getColorsNumbers(Integer start, Integer iMax, Integer d) {
		int color = 0;
		while (start <= iMax) {
			start = start + d;
			color++;
		}
		return color;
	}
	
	public static Rule addRule(StyleBuilder sb, String attributeName, Object equal, Color fillColor) {
		Symbolizer polygonSymbolizer = sb.createPolygonSymbolizer(fillColor);
		Rule rule = sb.createRule(polygonSymbolizer);
		if (equal instanceof Double)
			equal = MakeStyle.roundDecimal((Double) equal, 2);
		rule.setTitle("=" + equal);
		FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
		Filter filter = filterFactory.equal(filterFactory.property(attributeName), filterFactory.literal(equal), true);
		rule.setFilter(filter);
		return rule;
	}
	
	
	private static Rule addRule(StyleBuilder sb, String attributeName, Object lessThe, Object greater, Color fillColor) {
		Symbolizer polygonSymbolizer = sb.createPolygonSymbolizer(fillColor);
		Rule rule = sb.createRule(polygonSymbolizer);
		if ((greater instanceof Double) || (greater instanceof Float))
			greater = MakeStyle.roundDecimal(((Number) greater).doubleValue(), 2);
		if ((lessThe instanceof Double) || (lessThe instanceof Float))
			lessThe = MakeStyle.roundDecimal(((Number) lessThe).doubleValue(), 2);
		rule.setTitle(">= " + greater + " - < " + lessThe);
		FilterFactory2 filterFactory = CommonFactoryFinder.getFilterFactory2(null);
		Filter filterThen = filterFactory.greaterOrEqual(filterFactory.property(attributeName), filterFactory.literal(greater), true);
		Filter filterElse = filterFactory.less(filterFactory.property(attributeName), filterFactory.literal(lessThe));
		Filter filter = filterFactory.and(filterThen, filterElse);
		rule.setFilter(filter);
		return rule;
	}


}
