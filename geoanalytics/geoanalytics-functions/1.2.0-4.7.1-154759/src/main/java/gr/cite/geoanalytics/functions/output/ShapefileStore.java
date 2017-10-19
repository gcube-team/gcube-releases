package gr.cite.geoanalytics.functions.output;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.GeometryFactory;

import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;

public class ShapefileStore implements FeatureStore {
	
	@Autowired private GeoanalyticsStore geoanalyticsStore;
	@Autowired private TrafficShaper trafficShaper;

	public ShapefileStore(String basepath, String fileName, String crs, SimpleFeatureType type) throws Exception {
		this.fullPath = basepath+"/"+fileName;
		
		this.crs = crs; 

		this.geometryFactory = JTSFactoryFinder.getGeometryFactory();

		this.featureType = type;

		this.featureBuilder = new SimpleFeatureBuilder(featureType);
		
		this.features = new ArrayList<>();
	}
	
	private String fullPath;
	
	private String crs;
	
	private GeometryFactory geometryFactory;
	
	private SimpleFeatureType featureType;
	
	private SimpleFeatureBuilder featureBuilder;
	
	private List<SimpleFeature> features;
	
	public void addFeature(SimpleFeature feature) {
		features.add(feature);
	}
	
	public void commit() throws Exception {
		
		File newFile = new File(getFullPath());
		
		try{newFile.mkdirs();}catch(Exception e){/* should not need to do smth */}
		
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		
		Map<String, Serializable> params = new HashMap<>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		
		DataStore newDataStore = dataStoreFactory.createNewDataStore(params);
		
		/*
		 * TYPE is used as a template to describe the file contents
		 */
		newDataStore.createSchema(featureType);
		/*
         * Write the features to the shapefile
         */
        Transaction transaction = new DefaultTransaction("create");

        String typeName = newDataStore.getTypeNames()[0];
        SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
        SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
        /*
         * The Shapefile format has a couple limitations:
         * - "the_geom" is always first, and used for the geometry attribute name
         * - "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon
         * - Attribute names are limited in length 
         * - Not all data types are supported (example Timestamp represented as Date)
         * 
         * Each data store has different limitations so check the resulting SimpleFeatureType.
         */
        System.out.println("SHAPE:"+SHAPE_TYPE);

        if (featureSource instanceof SimpleFeatureStore) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
            /*
             * SimpleFeatureStore has a method to add features from a
             * SimpleFeatureCollection object, so we use the ListFeatureCollection
             * class to wrap our list of features.
             */
            SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
            featureStore.setTransaction(transaction);
            try {
                featureStore.addFeatures(collection);
                transaction.commit();
                String geoanalyticsEndpoint2 = "http://localhost:8083/geoanalytics";
                String layerName = "ProducedLayer";
                String styleName = "point";
                String tenantID = "b2eb4dd5-d852-4915-a5e8-a2df2ec914b4";
                String creatorID = "0000000-0000-0000-0000-000000000001";
                String projectID = "efb21266-ba42-4091-84e8-bd40c782ac45";
                GosDefinition gosDefinition = new GosDefinition("localhost:8082", "http://localhost:8082/GeospatialOperationService", "http://localhost:8082/geoserver", "geoanalytics", "geoanalytics");
                
//                geoanalyticsStore.storeToGeoanalyticsLocal(geoanalyticsEndpoint2, layerName, styleName, tenantID, creatorID, projectID, gosDefinition);
            } catch (Exception problem) {
                problem.printStackTrace();
                transaction.rollback();
            } finally {
                transaction.close();
            }
        } else {
            System.out.println(typeName + " does not support read/write access");
        }
	}
	
	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getCrs() {
		return crs;
	}
	

	public void setCrs(String crs) {
		this.crs = crs;
	}
	

	public GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}
	

	public void setGeometryFactory(GeometryFactory geometryFactory) {
		this.geometryFactory = geometryFactory;
	}
	

	public SimpleFeatureType getFeatureType() {
		return featureType;
	}
	

	public void setFeatureType(SimpleFeatureType featureType) {
		this.featureType = featureType;
	}
	

	public SimpleFeatureBuilder getFeatureBuilder() {
		return featureBuilder;
	}
	

	public void setFeatureBuilder(SimpleFeatureBuilder featureBuilder) {
		this.featureBuilder = featureBuilder;
	}
	

	public List<SimpleFeature> getFeatures() {
		return features;
	}
	

	public void setFeatures(List<SimpleFeature> features) {
		this.features = features;
	}
}