package gr.cite.geoanalytics.geospatial.operations;

import java.util.ArrayList;
import java.util.List;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

public class LayerOperations {

	private static final Logger logger = LoggerFactory.getLogger(LayerOperations.class);
	
	public static Iterable<SimpleFeature> intersect(Iterable<SimpleFeature> collection1, Iterable<SimpleFeature> collection2) {
		List<SimpleFeature> intersection = new ArrayList<SimpleFeature>();
		for(SimpleFeature feature1 : collection1) {
			Geometry geometry1 = (Geometry) feature1.getDefaultGeometry();
			for(SimpleFeature feature2 : collection2) {
				Geometry geometry2 = (Geometry) feature2.getDefaultGeometry();
				if(geometry1.intersects(geometry2)){
					Geometry interGeometry = geometry1.intersection(geometry2);
					
					SimpleFeatureBuilder builder = new SimpleFeatureBuilder(feature1.getFeatureType());
					builder.add(interGeometry);
					intersection.add(builder.buildFeature(null));
				}
			}
		}
		return intersection;
	}
	
	public static Iterable<SimpleFeature> difference(Iterable<SimpleFeature> collection1, Iterable<SimpleFeature> collection2) {
		List<SimpleFeature> intersection = new ArrayList<SimpleFeature>();
		for(SimpleFeature feature1 : collection1) {
			Geometry geometry1 = (Geometry) feature1.getDefaultGeometry();
			
			for(SimpleFeature feature2 : collection2) {
				Geometry geometry2 = (Geometry) feature2.getDefaultGeometry();
				try{
					if(geometry1.overlaps(geometry2)){
						geometry1 = geometry1.difference(geometry2);
						if(geometry1.isEmpty()) break;
					}
				}
				catch(Exception ex){
					logger.debug("Skipped comparison of a two geometries intersection. Reason: "+ex.getMessage());
				}
			}
			
			if(!geometry1.isEmpty()) {
				SimpleFeatureBuilder builder = new SimpleFeatureBuilder(feature1.getFeatureType());
				builder.add(geometry1);
				intersection.add(builder.buildFeature(null));
			}
		}
		return intersection;
	}
}
