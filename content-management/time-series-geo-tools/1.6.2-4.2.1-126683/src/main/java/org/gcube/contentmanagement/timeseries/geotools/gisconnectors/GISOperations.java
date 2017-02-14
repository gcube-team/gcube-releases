package org.gcube.contentmanagement.timeseries.geotools.gisconnectors;

import java.awt.Color;
import java.util.List;
import java.util.UUID;

import org.gcube.common.geoserverinterface.GeoCaller;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeoserverMethodResearch;
import org.gcube.common.geoserverinterface.bean.BoundsRest;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.LayerRest;
import org.gcube.common.geoserverinterface.engine.MakeStyle;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation.Scales;

public class GISOperations {

	// ***************** Routines

	/**
	 * Creates a layer table as an inner join of a csquareCode-feature table with the world table
	 * 
	 * @param appTableName
	 *            the csquareCode-feature table
	 * @param layerName
	 *            the layer name used for generating the table id
	 * @param featureLabel
	 *            the feature label
	 * @param session
	 *            the session
	 * @return the generated layer table name
	 * @throws Exception
	 */

	GeoCaller geoCaller;
	
	public String getCurrentGeoServer(){
		if (geoCaller!=null)
		return geoCaller.getCurrentWmsGeoserver();
		else return "";
	}
	
	public static enum featuresTypes {
		integer, real
	};

	private static final String crs = "GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]]," + "AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295]," + "AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";

	public static String DEFAULTSTYLE = "Species_prob";
	public static String TEMPLATEGROUP = "groupTemplate";

	public static String createFulfilGeometriesStatement(String tableName) {

		String creationStatement = "UPDATE " + tableName + " SET the_geom = new_all_world.the_geom FROM new_all_world WHERE " + tableName + ".csquarecode = new_all_world.csquarecode;";
		return creationStatement;
	}

	public static String createLayerTableStatement(String tableName, String featureName, featuresTypes type) {
		/*
		 * CREATE TABLE gadusmacrocephalus20110519130351251 ( gid integer, csquarecode character varying(30), the_geom geometry, probability real ) WITH ( OIDS=FALSE ); ALTER TABLE gadusmacrocephalus20110519130351251 OWNER TO postgres;
		 */
		/*
		 * CREATE TABLE biofede2010_09_02_18_11_37_986 ( gid integer, csquarecode character varying(30), the_geom geometry, maxspeciescountinacell integer ) WITH ( OIDS=FALSE ); ALTER TABLE biofede2010_09_02_18_11_37_986 OWNER TO postgres;
		 */
		// String creationStatement = "create table "+tableName +"(gid integer, csquarecode character varying(30), the_geom geometry, "+featureName+" "+type+") WITH ( OIDS=FALSE ); ALTER TABLE "+tableName+" OWNER TO "+userName+";";
//		String creationStatement = "create table " + tableName + "(gid serial, csquarecode character varying(30), the_geom geometry, " + featureName + " " + type + ", details character varying) WITH ( OIDS=FALSE ); CREATE INDEX " + tableName + "_idx ON " + tableName + " USING btree (csquarecode);";
		String creationStatement = "create table " + tableName + "(gid serial, csquarecode character varying(30), the_geom geometry, " + featureName + " " + type + ", details character varying) WITH ( OIDS=FALSE ); ";
		return creationStatement;
	}

	
	/**
	 * builds up a GeoCaller which will call geonetwork for getting a geoserver reference and then will use the geoserver like in the old GeoserverCaller
	 * @param gisInfo
	 * @return
	 */
	public GeoCaller getGeoCaller(GISInformation gisInfo){
		try {
			if (geoCaller == null){
//				AnalysisLogger.getLogger().trace("-RECREATING GEOCALLER-");
				geoCaller = new GeoCaller(gisInfo.getGeoNetworkUrl(), gisInfo.getGeoNetworkUserName(),gisInfo.getGeoNetworkPwd(), 
						gisInfo.getGisUrl(), gisInfo.getGisUserName(),gisInfo.getGisPwd(), GeoserverMethodResearch.MOSTUNLOAD);
				return geoCaller;
			}
				else
				return geoCaller;
		}
		catch(Exception e){
			e.printStackTrace();
//			AnalysisLogger.getLogger().error("ERROR INSTANTIATING GEO CALLER: "+e.getLocalizedMessage(),e);
			return null;
		}
	}
	
	/**
	 * generated a style on a geoServer by asking to geonetwork
	 * @param gisInfo
	 * @param styleInformation
	 * @return
	 * @throws Exception
	 */
	private boolean generateStyle(GISInformation gisInfo, GISStyleInformation styleInformation) throws Exception {

		GeoCaller caller = getGeoCaller(gisInfo);
		String style = "";
		if (styleInformation.getScaleType() == GISStyleInformation.Scales.logarithm) {
			AnalysisLogger.getLogger().trace("log scale");
			if (styleInformation.getValuesType() == Integer.class)
				style = MakeStyle.createStyleLog(styleInformation.getStyleName(), styleInformation.getStyleAttribute(), styleInformation.getNumberOfClasses(), styleInformation.getGradientBase(), styleInformation.getGradientMax(), styleInformation.getValuesType(), styleInformation.getMax().intValue(), styleInformation.getMin().intValue());
			else
				style = MakeStyle.createStyleLog(styleInformation.getStyleName(), styleInformation.getStyleAttribute(), styleInformation.getNumberOfClasses(), styleInformation.getGradientBase(), styleInformation.getGradientMax(), styleInformation.getValuesType(), styleInformation.getMax(), styleInformation.getMin());
		} else {
			AnalysisLogger.getLogger().trace("linear scale"); 
			if (styleInformation.getValuesType() == Integer.class){
				AnalysisLogger.getLogger().trace("integer management"); 
				style = MakeStyle.createStyle(styleInformation.getStyleName(), styleInformation.getStyleAttribute(), styleInformation.getNumberOfClasses(), styleInformation.getGradientBase(), styleInformation.getGradientMax(), styleInformation.getValuesType(), styleInformation.getMax().intValue(), styleInformation.getMin().intValue());
			}
			else{
				AnalysisLogger.getLogger().trace("other num type management");
				style = MakeStyle.createStyle(styleInformation.getStyleName(), styleInformation.getStyleAttribute(), styleInformation.getNumberOfClasses(), styleInformation.getGradientBase(), styleInformation.getGradientMax(), styleInformation.getValuesType(), styleInformation.getMax(), styleInformation.getMin());
			}
		}
		
		boolean toReturn = false;
		AnalysisLogger.getLogger().trace("sending request to geoServer"); 
		toReturn = caller.sendStyleSDL(style);
		AnalysisLogger.getLogger().trace("sent request to geoServer "+toReturn); 
		return toReturn;
	}

	/**
	 * deletes a style on the selected geoserver
	 * @param gisInfo
	 * @param styleName
	 * @return
	 * @throws Exception
	 */
	public boolean deleteStyle(GISInformation gisInfo, String styleName) throws Exception {

		GeoCaller caller = getGeoCaller(gisInfo);
		
		caller.deleteStyleSDL(styleName, true);
		return true;
	}
	
	/**
	 * deletes a layer on the right geoserver by asking to geonetwork
	 * @param gisInfo
	 * @param layerName
	 * @return
	 * @throws Exception
	 */
	public boolean deleteLayer(GISInformation gisInfo, String layerName) throws Exception {
		GeoCaller caller = getGeoCaller(gisInfo);
		caller.deleteLayer(layerName);
		return true;
	}	
	
	/**
	 * deletes a group on the geoserver containing it
	 * @param gisInfo
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public boolean deleteGroup(GISInformation gisInfo, String groupName) throws Exception {
		GeoCaller caller = getGeoCaller(gisInfo);
		caller.deleteLayersGroup(groupName);
		return true;
	}	
	
	
	public boolean createLayers(GISInformation gisInfo) throws Exception {
		List<GISLayerInformation> gisLayers = gisInfo.getLayers();
		for (GISLayerInformation layerInfo : gisLayers) {
			boolean created = createLayer(gisInfo, layerInfo);
			if (!created)
				return false;
		}
		return true;
	}

	/**
	 * Creates a a layer on the most unloaded geoServer 
	 * Updates even the geonetwork with the new information
	 * @param gisInfo
	 * @param layerInformation
	 * @return
	 * @throws Exception
	 */
	private boolean createLayer(GISInformation gisInfo, GISLayerInformation layerInformation) throws Exception {
		GeoCaller caller = getGeoCaller(gisInfo);
		AnalysisLogger.getLogger().info("Current Geoserver in use: "+caller.getCurrentWmsGeoserver());
		
		FeatureTypeRest featureTypeRest = new FeatureTypeRest();
		AnalysisLogger.getLogger().info("GisDataStore:"+gisInfo.getGisDataStore());
		featureTypeRest.setDatastore(gisInfo.getGisDataStore());
		featureTypeRest.setEnabled(true);
		featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0, 180.0, -85.5, 90.0, "EPSG:4326"));
		featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0, 180.0, -85.5, 90.0, "EPSG:4326"));
		AnalysisLogger.getLogger().info("LayerName:"+layerInformation.getLayerName());
		featureTypeRest.setName(layerInformation.getLayerName());
		featureTypeRest.setNativeName(layerInformation.getLayerName());
		featureTypeRest.setProjectionPolicy("FORCE_DECLARED");
		featureTypeRest.setSrs("EPSG:4326");
		featureTypeRest.setNativeCRS(crs);
		//modification on 30/01/12 the title name is distinguished from the layername
		if ((layerInformation.getLayerTitle()!=null) && (layerInformation.getLayerTitle().length()>0)){
			AnalysisLogger.getLogger().info("LayerTitle:"+layerInformation.getLayerTitle());
			featureTypeRest.setTitle(layerInformation.getLayerTitle());
		}
		else{
			AnalysisLogger.getLogger().info("LayerTitle:"+layerInformation.getLayerName());
			featureTypeRest.setTitle(layerInformation.getLayerName());
		}
		AnalysisLogger.getLogger().info("GisWorkspace:"+gisInfo.getGisWorkspace());
		featureTypeRest.setWorkspace(gisInfo.getGisWorkspace());
		boolean addFeature = false;
		try {
			AnalysisLogger.getLogger().info("adding feature on GeoServer");
			//alerts the geonetwork to update the status of the layer
			addFeature = caller.addFeatureType(featureTypeRest,GeonetworkCategory.DATASETS);
			AnalysisLogger.getLogger().info("adding feature OK");
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().info("ERROR IN adding feature");
		}

		if (addFeature) {
			AnalysisLogger.getLogger().info("setting layer on GeoServer");
			boolean setLayerValue = caller.setLayer(featureTypeRest, layerInformation.getDefaultStyle(), gisInfo.getStylesNames(layerInformation.getLayerName()));
			AnalysisLogger.getLogger().info("setting layer OK");
			return setLayerValue;
		} else
			return false;
	}

	/**
	 * creates a new group on the most unloaded geoserver 
	 * @param gisInfo
	 * @return
	 * @throws Exception
	 */
	public boolean createNewGroupOnGeoServer(GISInformation gisInfo) throws Exception {

		GeoCaller caller = getGeoCaller(gisInfo);
		GroupRest gtemp = null;
		try{
		gtemp = caller.getLayerGroup(gisInfo.getGroup().getTemplateGroupName());
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		GroupRest g = new GroupRest();
		g.setBounds(gtemp.getBounds());

		int nlayers = gisInfo.getLayers().size();

		for (int i = 0; i < nlayers; i++) {
			String layerName = gisInfo.getLayers().get(i).getLayerName();
			g.addLayer(layerName);
			// adding styles
			List<GISStyleInformation> styles = gisInfo.getStyles().get(layerName);
			for (GISStyleInformation style : styles) {
				AnalysisLogger.getLogger().trace("adding style " + style.getStyleName() + " to layer " + layerName);
				g.addStyle(layerName, style.getStyleName());
			}
		}

		g.setName(gisInfo.getGroup().getGroupName());
		
		//updates the geonetwork and the geoserver with the new information
		boolean toReturn = caller.addLayersGroup(g,GeonetworkCategory.DATASETS);
		return toReturn;
	}

	/**
	 * Creates a new group on the geoserver using a previous template
	 * @param gisInfo
	 * @return
	 * @throws Exception
	 */
	public boolean createGroupOnGeoServer(GISInformation gisInfo) throws Exception {

		GeoCaller caller = getGeoCaller(gisInfo);
		AnalysisLogger.getLogger().trace("Current Geoserver in use: "+caller.getCurrentWmsGeoserver());
		GroupRest g =null;
		try{
			g = caller.getLayerGroup(gisInfo.getGroup().getTemplateGroupName());
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
		int nlayers = gisInfo.getLayers().size();

		for (int i = 0; i < nlayers; i++) {
			String layerName = gisInfo.getLayers().get(i).getLayerName();
			g.addLayer(layerName);
			// adding styles
			List<GISStyleInformation> styles = gisInfo.getStyles().get(layerName);
			for (GISStyleInformation style : styles) {
				AnalysisLogger.getLogger().trace("adding style " + style.getStyleName() + " to layer " + layerName);
				g.addStyle(layerName, style.getStyleName());
			}
		}

		g.setName(gisInfo.getGroup().getGroupName());

		boolean toReturn = caller.addLayersGroup(g,GeonetworkCategory.DATASETS);
		return toReturn;
	}

	
	/**
	 * generates a gis map blocking if a style creation failure happens
	 * @param gisInfo
	 * @return
	 */
	public boolean generateGisMap(GISInformation gisInfo) {
		return generateGisMap(gisInfo,false); 
	}
	
	/**
	 * generates a complete GIS MAP on a remote GEO SERVER
	 * 
	 * The user has to fill in the gisInfo object in order to generate the map
	 * 
	 **/
	public boolean generateGisMap(GISInformation gisInfo, boolean ignoreStylesFailure) {

		AnalysisLogger.getLogger().trace("GenerateGisMap-> generating Styles ... ");
		for (List<GISStyleInformation> styles : gisInfo.getStyles().values()) {
			for (GISStyleInformation style : styles) {
				if (!style.getStyleName().equals(DEFAULTSTYLE)) {
					try {
						AnalysisLogger.getLogger().trace("GenerateGisMap-> generating Style " + style.getStyleName() + " with " + style.getNumberOfClasses() + " classes ");
						generateStyle(gisInfo, style);
						AnalysisLogger.getLogger().trace("GenerateGisMap<- OK");
					} catch (Exception e) {
//						e.printStackTrace();
						AnalysisLogger.getLogger().trace("GenerateGisMap<-KO - Impossible to generate Style " + style.getStyleName() + " maybe yet existing "+e.getMessage());
						if (!ignoreStylesFailure)
							return false;
					}
				}
			}
		}

		
		AnalysisLogger.getLogger().trace("GenerateGisMap-> generating Layers ... ");
		try {
			createLayers(gisInfo);
			AnalysisLogger.getLogger().trace("GenerateGisMap<- OK");
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().trace("GenerateGisMap<-KO - Error in creating layers maybe yet existing");
		}

//		AnalysisLogger.getLogger().trace("GenerateGisMap-> generating Group ... ");

		boolean returning = true;
		//DEPRECATED : we will not generate groups but only layers
		/*
		try {
			AnalysisLogger.getLogger().trace("GenerateGisMap-> generating group " + gisInfo.getGroup().getGroupName());

			if (gisInfo.getGroup().isTemplateGroup())
				createGroupOnGeoServer(gisInfo);
			else
				createNewGroupOnGeoServer(gisInfo);
			AnalysisLogger.getLogger().trace("GenerateGisMap<- GEOGROUP OK");
			returning = true;
		} catch (Exception e) {
			AnalysisLogger.getLogger().trace("GenerateGisMap<-KO - Error in creating group " + gisInfo.getGroup().getGroupName() + " maybe yet existing");
		}
		*/
		
		return returning;
		
	}

	
	
	public static void main(String[] args) {

		// set test table
		AnalysisLogger.setLogger("./cfg/ALog.properties");
		
//		String testTable = "labalistesstellatus20111124164310556";
		String testTable = "labalistesstellatus20111124164310556";
		String testTableTitle = "abalistesstellatus Title";
		
		// setup the information object
		GISInformation gisInfo = new GISInformation();
			
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");

		gisInfo.setGisUserName("admin");

		// setup the main layer to visualize
		GISLayerInformation gisLayer1 = new GISLayerInformation();
		gisLayer1 = new GISLayerInformation();
		gisLayer1.setDefaultStyle("Species_prob");
		gisLayer1.setLayerName(testTable);
		gisLayer1.setLayerTitle(testTableTitle);

		// setup the group
		GISGroupInformation gisGroup = new GISGroupInformation();
		gisGroup.setGroupName("testGroupJP51");
		gisGroup.setTemplateGroupName(TEMPLATEGROUP);

		// choose if this is a template group (depht, salinity etc) or a single layer group
		gisGroup.setTemplateGroup(true);

		// Standard Style - not used in this example
		GISStyleInformation style = new GISStyleInformation();
		style.setStyleName(DEFAULTSTYLE);
		
		// CREATION OF A NEW STYLE
		GISStyleInformation newstyle = new GISStyleInformation();
		newstyle.setStyleName("newstylejptest" + UUID.randomUUID());
		Color c1 = Color.green;
		Color c2 = Color.blue;
		newstyle.setGradientBase(c1);
		newstyle.setGradientMax(c2);
		newstyle.setMax(1.00);
		newstyle.setMin(0.00);

		newstyle.setNumberOfClasses(4);
		newstyle.setScaleType(Scales.linear);
		newstyle.setStyleAttribute("probability");
		newstyle.setValuesType(Double.class);
		
		// add the layer to the visualizing ones
		gisInfo.addLayer(gisLayer1);
		// add the group to the generating ones
		gisInfo.setGroup(gisGroup);
		// associate the style to the layer
		// gisInfo.addStyle(gisLayer1.getLayerName(), newstyle);
		gisInfo.addStyle(gisLayer1.getLayerName(), style);
		
		GISOperations operations = new GISOperations();
		operations.generateGisMap(gisInfo,true);
		
		
		
		// OLD CODE
		/*
		 * try { createLayers(gisInfo); } catch (Exception e) { e.printStackTrace(); }
		 */
		/*
		 * try { // generateStyle(gisInfo,newstyle); } catch (Exception e) { e.printStackTrace(); }
		 */

	}

}
