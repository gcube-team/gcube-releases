package gr.cite.geoanalytics.functions.exploration;

import org.geotools.data.*;
import org.geotools.feature.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.*;
import org.opengis.feature.simple.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.*;

import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.output.FeatureStore;

public class ScanAlgorithm {

	public ScanAlgorithm(CoordinateReferenceSystem crs, int scanStepMeters) {
		this.crs = crs;
		this.scanStepMeters = scanStepMeters;
	}
	
	private int scanStepMeters;
	
	private CoordinateReferenceSystem crs;

	public void execute(FeatureSource<SimpleFeatureType, SimpleFeature> source, double minimumX, double minimumY, double maximumX, double maximumY, Function function, FeatureStore store) throws Exception {

		Query query = new Query();
		query.setCoordinateSystem(crs);
		FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source.getFeatures(query);

		double distance = JTS.orthodromicDistance(new Coordinate(minimumX, maximumY), new Coordinate(maximumX, maximumY), crs);
		int totalMeters = (int) distance;

		int samplesPerDimension = totalMeters / scanStepMeters;

		double sampleDistance = (maximumX - minimumX) / samplesPerDimension;

		double y = minimumY, x = minimumX;
		
		GeometryFactory gFactory = store.getGeometryFactory();
		
		SimpleFeatureBuilder fBuilder = store.getFeatureBuilder();
		
		while (y < maximumY) {
			x = minimumX;
			while (x < maximumX) {
				FeatureIterator<SimpleFeature> iterator = collection.features();
				try {
					while (iterator.hasNext()) {
						SimpleFeature feature = (SimpleFeature)iterator.next();
						Geometry geometry = (Geometry)feature.getDefaultGeometry();
						Point point = gFactory.createPoint(new Coordinate(x, y));
						if(point.within(geometry)){
							fBuilder.add(point);
							fBuilder.add(function.execute(x, y));
							store.addFeature(fBuilder.buildFeature(null));
						}
					}
				} finally {
					iterator.close();
				}

				x += sampleDistance;
			}
			y += sampleDistance;
		}
		
		store.commit();
	}
}
