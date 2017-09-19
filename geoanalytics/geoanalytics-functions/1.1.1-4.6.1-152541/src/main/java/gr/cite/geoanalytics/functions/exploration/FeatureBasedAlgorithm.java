package gr.cite.geoanalytics.functions.exploration;

import java.util.Iterator;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.*;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.*;

import gr.cite.geoanalytics.functions.filters.CoordinateFilter;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.output.FeatureStore;
import gr.cite.geoanalytics.geospatial.operations.LayerOperations;
import gr.cite.geoanalytics.geospatial.retrieval.FeatureRetrievalHelper;
import gr.cite.geoanalytics.geospatial.retrieval.RasterRetrievalHelper;

public class FeatureBasedAlgorithm {

	public FeatureBasedAlgorithm(int scanStepMeters) {
		this.scanStepMeters = scanStepMeters;
	}
	
	private int scanStepMeters;
	
	public void execute(
			String sst,
			FeatureSource<SimpleFeatureType, SimpleFeature> paraktiesSource, 
			FeatureSource<SimpleFeatureType, SimpleFeature> natura2000Source,
			double minimumX, double minimumY, double maximumX, double maximumY,
			Iterable<CoordinateFilter> filters,
			Function function, 
			FeatureStore store, 
			String crs) throws Exception {

		double distance = JTS.orthodromicDistance(new Coordinate(minimumX, maximumY), new Coordinate(maximumX, maximumY), CRS.decode(crs));
		int totalMeters = (int) distance;

		int samplesPerDimension = totalMeters / scanStepMeters;

		double sampleDistance = (maximumX - minimumX) / samplesPerDimension;
		
		GeometryFactory gFactory = store.getGeometryFactory();
		
		SimpleFeatureBuilder fBuilder = store.getFeatureBuilder();
		
		Iterable<SimpleFeature> paraktiesFeatures = FeatureRetrievalHelper.getShapes(paraktiesSource, "SHP_Geography", minimumX, minimumY, maximumX, maximumY, crs);
		
//		Iterable<SimpleFeature> natura2000Features = FeatureRetrievalHelper.getShapes(natura2000Source, "the_geom", minimumX, minimumY, maximumX, maximumY, crs);
		Iterable<SimpleFeature> natura2000Features = FeatureRetrievalHelper.getShapes(natura2000Source, "SHP_Geography", minimumX, minimumY, maximumX, maximumY, crs);
		
		Iterator<SimpleFeature> iterator = LayerOperations.difference(paraktiesFeatures, natura2000Features).iterator();
		
		GridCoverage2D coverage = RasterRetrievalHelper.getCoverage(sst);

		while (iterator.hasNext()) {
			SimpleFeature feature = (SimpleFeature)iterator.next();
			
			BoundingBox bounds = feature.getBounds();
			
			double y = bounds.getMinY(), x = bounds.getMinX();
			
			while (y < bounds.getMaxY()) {
				x = bounds.getMinX();
				while (x < bounds.getMaxX()) {
					Geometry geometry = (Geometry)feature.getDefaultGeometry();
					Point point = gFactory.createPoint(new Coordinate(x, y));
					if(point.within(geometry) && !isExcludedByFilter(filters, point) ){
						fBuilder.add(point);
						fBuilder.add(function.execute(x, y));
						fBuilder.add(((float[]) coverage.evaluate(new DirectPosition2D(coverage.getCoordinateReferenceSystem2D(), x, y)))[0]);
						store.addFeature(fBuilder.buildFeature(null));
					}

					x += sampleDistance;
				}
				y += sampleDistance;
			}
		}
		
		store.commit();
	}
	
	private boolean isExcludedByFilter(Iterable<CoordinateFilter> filters, Point point) throws Exception {
		for (CoordinateFilter filter : filters) {
			if (filter.exclude(point.getX(), point.getY()))
				return true;
		}
		return false;
	}
}
