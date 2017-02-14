package org.gcube.contentmanagement.timeseries.geotools.experiments;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.engine.TimeSeriesGISConverter;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISGroupInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISLayerInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISOperations;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation;
import org.gcube.contentmanagement.timeseries.geotools.gisconnectors.GISStyleInformation.Scales;

public class TestPointsCreationGeoServerDev {

	public static ArrayList<String> fulfilPoints(String delimiter) {
		ArrayList<String> points = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader("VTIExample.txt"));

			String line = reader.readLine();
			while (line != null) {
				if (line.trim().length() > 5) {
					String[] elems = line.split(",");
					if (elems.length == 7) {
						String point = "";
						int i = 0;

						for (String el : elems) {

							point += "'" + el + "'";
							if (i < elems.length - 1)
								point += ",";

							i++;
						}

						// String point = elements[1] + delimiter+ elements[2];
						points.add(point);
					}
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return points;
	}

	public static void setupPointTable3() {
		try {
			TimeSeriesGISConverter converter = new TimeSeriesGISConverter("./cfg/");
			String dropGeometriesTable = "drop table point_geometries_example";
			try {
				converter.connectionManager.AquamapsUpdate(dropGeometriesTable);
				AnalysisLogger.getLogger().debug("table has been deleted");
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("table was yet dropped");
			}

			String createGeometriesTable = "CREATE TABLE point_geometries_example(gid serial NOT NULL, msgnumber character varying, x real, y real, time character varying, vesselID integer, course real, speed real, type real)";
			try {
				converter.connectionManager.AquamapsUpdate(createGeometriesTable);
				AnalysisLogger.getLogger().debug("table has been created");
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("table was yet created");
			}

			ArrayList<String> points = fulfilPoints(", ");
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO point_geometries_example(gid , msgnumber , x , y , time , vesselID , course , speed, type) VALUES");
			int size = points.size();
			for (int i = 0; i < size; i++) {
				int r = (int) Math.round(2 * Math.random()) + 1;
				String insertion = "(DEFAULT, " + points.get(i) + "," + r + ")";
				if (i < size - 1)
					insertion += ", ";
				AnalysisLogger.getLogger().debug("insertion has been performed : " + insertion);
				sb.append(insertion);
			}
			AnalysisLogger.getLogger().debug("inserting into db..." + sb.toString());
			converter.connectionManager.AquamapsUpdate(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setupPointTable2() {
		try {
			TimeSeriesGISConverter converter = new TimeSeriesGISConverter("./cfg/");
			String dropGeometriesTable = "drop table point_geometries_example";
			try {
				converter.connectionManager.GeoserverUpdate(dropGeometriesTable);
				AnalysisLogger.getLogger().debug("table has been deleted");
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("table was yet dropped");
			}

			String createGeometriesTable = "CREATE TABLE point_geometries_example(gid serial NOT NULL)";
			try {
				converter.connectionManager.GeoserverUpdate(createGeometriesTable);
				AnalysisLogger.getLogger().debug("table has been created");
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("table was yet created");
			}
			try {
				String addGeometriesTable = "Select AddGeometryColumn('point_geometries_example','the_geom',4326,'POINT',2)";
				converter.connectionManager.GeoserverQuery(addGeometriesTable);
			} catch (Exception e2) {
				AnalysisLogger.getLogger().debug("table was yet created");
				e2.printStackTrace();
			}

			// String[] points = {"-45.52 44.98","-45.49 44.97","-45.49 45.04","-45.45 45.08","-45.40 45.12","-45.36 45.16"};
			ArrayList<String> points = fulfilPoints(", ");
			StringBuffer sb = new StringBuffer();
			sb.append("INSERT INTO point_geometries_example(gid, the_geom) VALUES");
			int size = points.size();
			for (int i = 0; i < size; i++) {
				int r = (int) Math.round(2 * Math.random()) + 1;
				String insertion = "(DEFAULT, ST_SetSRID(ST_MakePoint(" + points.get(i) + "),4326))";
				if (i < size - 1)
					insertion += ", ";
				AnalysisLogger.getLogger().debug("insertion has been performed : " + insertion);
				sb.append(insertion);
			}
			AnalysisLogger.getLogger().debug("inserting into db...");
			converter.connectionManager.GeoserverUpdate(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setupPointTable() {
		try {
			TimeSeriesGISConverter converter = new TimeSeriesGISConverter("./cfg/");
			String createGeometriesTable = "CREATE TABLE point_geometries_example(gid serial NOT NULL, the_geom geometry, maxspeciescountinacell real)";
			try {
				converter.connectionManager.GeoserverUpdate(createGeometriesTable);
				AnalysisLogger.getLogger().debug("table has been created");
			} catch (Exception e1) {
				AnalysisLogger.getLogger().debug("table was yet created");
			}

			// String[] points = {"-45.52 44.98","-45.49 44.97","-45.49 45.04","-45.45 45.08","-45.40 45.12","-45.36 45.16"};
			ArrayList<String> points = fulfilPoints(" ");

			for (int i = 0; i < points.size(); i++) {
				// String insertion = "INSERT INTO point_geometries_example(gid, the_geom, maxspeciescountinacell) VALUES (DEFAULT, ST_Geometry('point("+points[i]+")'),1)";
				String insertion = "INSERT INTO point_geometries_example(gid, the_geom, maxspeciescountinacell) VALUES (DEFAULT, ST_Geometry('point(" + points.get(i) + ")'),1)";
				AnalysisLogger.getLogger().debug("insertion has been performed : " + insertion);
				converter.connectionManager.GeoserverUpdate(insertion);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		setupPointTable3();
	}

	public static void main2(String[] args) {

		setupPointTable2();

		// set test table
		String testTable = "point_geometries_example";

		// setup the information object
		GISInformation gisInfo = new GISInformation();
		gisInfo.setGisDataStore("aquamapsdb");
		gisInfo.setGisPwd("gcube@geo2010");
		gisInfo.setGisWorkspace("aquamaps");
		gisInfo.setGisUrl("http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver");
		// gisInfo.setGisUrl("http://geoserver.d4science-ii.research-infrastructures.eu/geoserver");
		gisInfo.setGisUserName("admin");

		// CREATION OF A NEW STYLE
		GISStyleInformation newstyle = new GISStyleInformation();
		String stylename = "newstylejptest" + UUID.randomUUID();
		newstyle.setStyleName(stylename);
		Color c1 = Color.orange;
		Color c2 = Color.red;
		newstyle.setGradientBase(c1);
		newstyle.setGradientMax(c2);
		newstyle.setMax(10.00);
		newstyle.setMin(0.00);
		newstyle.setNumberOfClasses(2);
		newstyle.setScaleType(Scales.linear);
		newstyle.setStyleAttribute("maxspeciescountinacell");
		newstyle.setValuesType(Double.class);

		// Standard Style - not used in this example
		GISStyleInformation style = new GISStyleInformation();
		// style.setStyleName("point");
		style.setStyleName("VTISimpleClassification");

		GISStyleInformation style2 = new GISStyleInformation();
		style2.setStyleName("VTIBathymetryClassification");

		// setup the main layer to visualize
		GISLayerInformation gisLayer1 = new GISLayerInformation();
		gisLayer1 = new GISLayerInformation();
		gisLayer1.setDefaultStyle(style.getStyleName());
		gisLayer1.setLayerName(testTable);

		// setup the group
		GISGroupInformation gisGroup = new GISGroupInformation();
		gisGroup.setGroupName("testGroupJP" + UUID.randomUUID());
		System.out.println("GROUP NAME : " + gisGroup.getGroupName());
		gisGroup.setTemplateGroupName(GISOperations.TEMPLATEGROUP);
		// choose if this is a template group (depht, salinity etc) or a single layer group

		// add the layer to the visualizing ones
		gisInfo.addLayer(gisLayer1);
		// add the group to the generating ones
		gisInfo.setGroup(gisGroup);
		// associate the style to the layer
		gisInfo.addStyle(gisLayer1.getLayerName(), style2);
		gisInfo.addStyle(gisLayer1.getLayerName(), style);

		try {
			new GISOperations().createLayers(gisInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			// GISOperations.generateGisMap(gisInfo);
			new GISOperations().createGroupOnGeoServer(gisInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// GISOperations.generateGisMap(gisInfo);

		// OLD CODE
		/*
		 * try { createLayers(gisInfo); } catch (Exception e) { e.printStackTrace(); }
		 */
		/*
		 * try { // generateStyle(gisInfo,newstyle); } catch (Exception e) { e.printStackTrace(); }
		 */

	}

}
