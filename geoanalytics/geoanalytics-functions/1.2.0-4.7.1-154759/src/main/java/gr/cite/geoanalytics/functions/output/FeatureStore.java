package gr.cite.geoanalytics.functions.output;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.GeometryFactory;

public interface FeatureStore {
	
	void addFeature(SimpleFeature feature);
	
	GeometryFactory getGeometryFactory();
	
	SimpleFeatureBuilder getFeatureBuilder();
	
	void commit() throws Exception;
}
