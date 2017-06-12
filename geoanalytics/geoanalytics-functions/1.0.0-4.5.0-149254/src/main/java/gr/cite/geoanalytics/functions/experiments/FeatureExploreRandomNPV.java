package gr.cite.geoanalytics.functions.experiments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.media.jai.JAI;
import javax.media.jai.OperationRegistry;

import org.geotools.data.*;
import org.geotools.factory.GeoTools;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.sun.media.jai.imageioimpl.ImageReadWriteSpi;

import gr.cite.geoanalytics.functions.exploration.FeatureBasedAlgorithm;
import gr.cite.geoanalytics.functions.filters.CityDistanceFilter;
import gr.cite.geoanalytics.functions.filters.CoordinateFilter;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.functions.NPVFunction;
import gr.cite.geoanalytics.functions.output.ShapefileStore;

public class FeatureExploreRandomNPV {

	public static final String FEATURE_TYPE_CRS = "EPSG:4326";

//	public static final int DISTANCE_METERS = 500;
	
	public static void main(String[] args) throws Exception{
		String parakties = "geoanalytics:303781d9-5aab-4178-bebf-4bbbc0e776eb";
		String cities = "geoanalytics:5f15dcf3-f770-431a-81cf-d2b0d2edae0c";
		Integer distanceMeters = 500;
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("layerName0", parakties);
		params.put("layerName1", cities);
		params.put("operationName", distanceMeters);
		
		initialize(params);
		
	}
	
	public static void initialize(Map<String, Object> UIParameters) throws Exception {
//		String parakties = (String)UIParameters.get("layerName0");
//		String cities = (String)UIParameters.get("layerName1");
//		int distanceMeters = 500;
		ClassLoader loader = (ClassLoader)UIParameters.get("loader");
		GeoTools.addClassLoader(loader);
		
		OperationRegistry registry = JAI.getDefaultInstance().getOperationRegistry();
		if(registry == null){
			System.out.println("Error with JAI initialization (needed for GeoTools).");
		}else {
			try {
                new ImageReadWriteSpi().updateRegistry(registry);
            } catch(IllegalArgumentException e) {
            	System.out.println("JAI instance is probably already registered");
            }
		}
		
		String parakties = "geoanalytics:303781d9-5aab-4178-bebf-4bbbc0e776eb";
		String cities = "geoanalytics:5f15dcf3-f770-431a-81cf-d2b0d2edae0c";
		Integer distanceMeters = 500;
		
		try{
			distanceMeters = (Integer)UIParameters.get("operationName");
		} catch(Exception e){
			System.out.println("....Failed at retrieving the integer directly from the hashmap....");
			distanceMeters = Integer.valueOf((String)UIParameters.get("operationName"));
		}
		
		
		String getCapabilitiesURL = "http://dl012.madgik.di.uoa.gr:8080/geoserver/wfs?REQUEST=GetCapabilities&version=1.1.0";

		Map<String, String> connectionParameters = new HashMap<String, String>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilitiesURL);

		DataStore data = DataStoreFinder.getDataStore(connectionParameters);
		
		DataStoreFinder.getAvailableDataStores().forEachRemaining(System.out::println);

//		String parakties = "geoanalytics:b46e7afe-5906-423f-962c-fff6eedc5aeb";
		String natura2000 = "geoanalytics:f10b6c12-1412-4021-ab6b-30c6d3bf9586";
//		String cities = "geoanalytics:1441ba92-9823-4972-bef0-6f3d0f36f413";
		String sst = "http://dl012.madgik.di.uoa.gr:8080/geoserver/wcs?request=GetCoverage&version=2.0.1&"
				+ "coverageid=geoanalytics__sst&format=geotiff";
		
		System.out.println("....Parakties....: " + parakties);
		System.out.println("....Cities....: " + cities);
		
		CoordinateReferenceSystem crs = CRS.decode(FEATURE_TYPE_CRS);

		String outputFolder = "/tmp/";
//		String outputFolder = "D:\\";
		
		double minimumX = 23;
		double minimumY = 34;
		double maximumX = 27;
		double maximumY = 36;
		
		FeatureSource<SimpleFeatureType, SimpleFeature> paraktiesSource = data.getFeatureSource(parakties);
		
		FeatureSource<SimpleFeatureType, SimpleFeature> natura2000Source = data.getFeatureSource(natura2000);
		
		FeatureSource<SimpleFeatureType, SimpleFeature> citiesSource = data.getFeatureSource(cities);
		
		ArrayList<CoordinateFilter> filters = new ArrayList<CoordinateFilter>();
		CityDistanceFilter cityDistanceFilter = new CityDistanceFilter();
		cityDistanceFilter.setCrs(crs);
		cityDistanceFilter.setCitiesSource(citiesSource);
		filters.add(cityDistanceFilter);
		
		Function function = new NPVFunction(sst);
		
		SimpleFeatureType shapefileType = DataUtilities.createType("Location",
            "the_geom:Point:srid=4326," + // <- the geometry attribute: Point type
            "npv:Integer," +  // npv
            "sst:Double"   // temperature
        );
		
		ShapefileStore shapefile = new ShapefileStore(outputFolder + "FeatureBasedAlgorithmMonlithic-" + UUID.randomUUID(), "analysis-sst.shp", FEATURE_TYPE_CRS, shapefileType);
		
		new FeatureBasedAlgorithm(distanceMeters).execute(
				sst,
				paraktiesSource,
				natura2000Source,
				minimumX, minimumY, maximumX, maximumY, 
				filters,
				function, 
				shapefile,
				FEATURE_TYPE_CRS);
		
		System.out.println("***********Done************");
		
	}
}
