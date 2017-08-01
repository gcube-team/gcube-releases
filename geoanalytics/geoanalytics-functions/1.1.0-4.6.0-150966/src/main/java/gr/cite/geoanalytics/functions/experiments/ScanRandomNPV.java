package gr.cite.geoanalytics.functions.experiments;

import java.util.*;

import org.geotools.data.*;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import gr.cite.geoanalytics.functions.exploration.ScanAlgorithm;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.functions.RandomNPV;
import gr.cite.geoanalytics.functions.output.ShapefileStore;

public class ScanRandomNPV {

	public static final String FEATURE_TYPE_CRS = "EPSG:4326";

	public static final int DISTANCE_METERS = 100;

	public static void main(String[] args) throws Exception {
		String getCapabilities = "http://dl014.madgik.di.uoa.gr:8080/geoserver/wfs?REQUEST=GetCapabilities&version=1.1.0";

		Map<String, String> connectionParameters = new HashMap<String, String>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilities);

		DataStore data = DataStoreFinder.getDataStore(connectionParameters);

		String typeName = "geoanalytics:b46e7afe-5906-423f-962c-fff6eedc5aeb";

		CoordinateReferenceSystem crs = CRS.decode(FEATURE_TYPE_CRS);

		FeatureSource<SimpleFeatureType, SimpleFeature> source = data.getFeatureSource(typeName);
		double minimumX = source.getBounds().getMinimum(1);
		double minimumY = source.getBounds().getMinimum(0);
		double maximumX = source.getBounds().getMaximum(1);
		double maximumY = source.getBounds().getMaximum(0);

		System.out.println("Minimum X: " + minimumX);
		System.out.println("Minimum Y: " + minimumY);
		System.out.println("Maximum X: " + maximumX);
		System.out.println("Maximum Y: " + maximumY);
		
		Function function = new RandomNPV();
		
		SimpleFeatureType shapefileType = DataUtilities.createType("Location",
	            "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
	            "npv:Integer"  // npv
	        );
		
		ShapefileStore shapefile = new ShapefileStore("/tmp/ScanAlgorithm-"+UUID.randomUUID(), "output.shp", FEATURE_TYPE_CRS, shapefileType);
		new ScanAlgorithm(crs, DISTANCE_METERS).execute(
				source, 
				minimumX, minimumY, maximumX, maximumY, 
				function, 
				shapefile);
		
	}
}
