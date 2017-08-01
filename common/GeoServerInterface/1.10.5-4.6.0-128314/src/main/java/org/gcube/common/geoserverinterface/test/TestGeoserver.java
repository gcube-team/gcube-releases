package org.gcube.common.geoserverinterface.test;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpException;
import org.gcube.common.geoserverinterface.GeoserverCaller;
import org.gcube.common.geoserverinterface.bean.BoundsRest;
import org.gcube.common.geoserverinterface.bean.CoverageTypeRest;
import org.gcube.common.geoserverinterface.bean.FeatureTypeRest;
import org.gcube.common.geoserverinterface.bean.GroupRest;
import org.gcube.common.geoserverinterface.bean.LayerRest;
import org.gcube.common.geoserverinterface.engine.MakeStyle;
import org.gcube.common.geoserverinterface.json.JSONException;


public class TestGeoserver {

	private static String geoserver_url = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver";
	private static GeoserverCaller geo_caller = new GeoserverCaller(geoserver_url, "admin", "gcube@geo2010");
	
	private static final String crs="GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
	"AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
	"AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";
	
	public static void main(String[] args) {
		try {
			
			GroupRest g = geo_caller.getLayerGroup("groupTemplate");
			
			FeatureTypeRest featureTypeRest=new FeatureTypeRest();
			featureTypeRest.setDatastore("aquamapsdb");
			featureTypeRest.setEnabled(true);
			featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
			featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
			featureTypeRest.setName("a4");
			featureTypeRest.setNativeName("pentanemusquinquarius20110519110610927");
			featureTypeRest.setProjectionPolicy("FORCE_DECLARED");
			featureTypeRest.setSrs("EPSG:4326");
			featureTypeRest.setNativeCRS(crs);
			featureTypeRest.setTitle("pentanemusquinquarius20110519110610927");
			featureTypeRest.setWorkspace("aquamaps");
			
			geo_caller.addFeatureType(featureTypeRest);
			
			Thread.sleep(5000);
			
			
			g.setName("prova_3");
			g.addLayer("a4");
			
			geo_caller.addLayersGroup(g);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void listLayers() throws Exception {
		ArrayList<String> ll = geo_caller.listLayers();
	}
	
	private static void createFeatureType() throws Exception {
		// submit a new geospatial data
		String crs = "GEOGCS[\"WGS 84\",";
		crs += "DATUM[\"World Geodetic System 1984\",";
		crs += "SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],";
		crs += "AUTHORITY[\"EPSG\",\"6326\"]],";
		crs += "PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],";
		crs += "UNIT[\"degree\", 0.017453292519943295],";
		crs += "AXIS[\"Geodetic longitude\", EAST],";
		crs += "AXIS[\"Geodetic latitude\", NORTH],";
		crs += "AUTHORITY[\"EPSG\",\"4326\"]]";
		
		FeatureTypeRest featureTypeRest = new FeatureTypeRest();
		featureTypeRest.setName("Fis_22964_occurr");
		featureTypeRest.setNativeName("Fis_22964_occurr");
		featureTypeRest.setTitle("Fish 22964 occurrance");
		featureTypeRest.setDatastore("aquamapsdb");
		featureTypeRest.setWorkspace("aquamaps");
		featureTypeRest.setEnabled(true);
		featureTypeRest.setLatLonBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
		featureTypeRest.setNativeBoundingBox(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326")); 
		featureTypeRest.setNativeCRS(crs);
		featureTypeRest.setProjectionPolicy("FORCE_DECLARED");
		featureTypeRest.setSrs("EPSG:4326");
		
		geo_caller.addFeatureType(featureTypeRest);
		
		Thread.sleep(6000);
		
		ArrayList<String> styles = new ArrayList<String>();
		styles.add("occurrence_style");
		geo_caller.setLayer(featureTypeRest, "occurrence_style", styles);
	}
	
	private static void modifyLayersGroup() throws Exception {
		GroupRest group = new GroupRest();
		// in this point you can to do some changes
		Thread.sleep(5000);
		geo_caller.modifyLayersGroup(group);
	}
	
	private static void modifyNameLayersGroup() throws Exception {
		GroupRest group = geo_caller.getLayerGroup("prova1");
		group.setName("newprova1");
		Thread.sleep(5000);
		
		geo_caller.deleteLayersGroup("prova1");
		Thread.sleep(5000);
			
		geo_caller.addLayersGroup(group);
	}
	
	private static void createStyle() throws Exception {
		
		//String xml = MakeStyle.createStyle("new_style_prova", "attributeName", 5, new Color(255, 0, 0), new Color(255, 255, 255), Integer.class, 7, 1);
		
		
		String xml = MakeStyle.createStyleLog("new_style_prova", "attributeName", 5, new Color(255, 0, 0), new Color(255, 255, 255), Integer.class, 33, 0);
		
		
		//String xml = MakeStyle.createStyle("new_style_prova", "attributeName", 1, new Color(255, 0, 0), new Color(255, 255, 255), Integer.class, 0, 0);
		System.out.println(xml);
		
//		geo_caller.sendStyleSDL(xml);
//		Thread.sleep(5000);
//		
//		geo_caller.modifyStyleSDL("new_style_prova", xml);
//		Thread.sleep(5000);
//		geo_caller.deleteStyleSDL("new_style_prova", true);
		
	}
	
	private static void deleteLayer() throws Exception {
		
		String name = "Fis_22964_occurr";
		
		geo_caller.deleteLayer(name);
	}
	
	private static GroupRest getLayerGroup(String groupName) throws Exception {
		
		return geo_caller.getLayerGroup(groupName);
	}
	
	private static void createGroup() throws Exception {
		
		GroupRest g = geo_caller.getLayerGroup("groupTemplate");
	
		g.setName("federico_test2");
		g.addLayer("fis_1427002010_06_01_18_52_12_903");
		g.addStyle("fis_1427002010_06_01_18_52_12_903", "Species_prob");
		
		//group.setBounds(new BoundsRest(-180.0,180.0,-85.5,90.0,"EPSG:4326"));
		
		
		
		geo_caller.addLayersGroup(g);
		
	}
	
}
