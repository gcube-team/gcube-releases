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
import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.*;

import gr.cite.clustermanager.exceptions.NoAvailableGos;
import gr.cite.clustermanager.model.layers.GosDefinition;
import gr.cite.clustermanager.trafficshaping.TrafficShaper;
import gr.cite.geoanalytics.functions.filters.CoordinateFilter;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.output.FeatureStore;
import gr.cite.geoanalytics.functions.output.GeoanalyticsStore;
import gr.cite.geoanalytics.geospatial.operations.LayerOperations;
import gr.cite.geoanalytics.geospatial.retrieval.FeatureRetrievalHelper;
import gr.cite.geoanalytics.geospatial.retrieval.RasterRetrievalHelper;

public class FeatureBasedAlgorithm {
	
	@Autowired private TrafficShaper trafficShaper;
	@Autowired private GeoanalyticsStore geoanalyticsStore; 

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
		
		
		GosDefinition gosDefinition = null;
		geoanalyticsStore.storeToGeoanalyticsLocal("execID", "layerName", "point", "b2eb4dd5-d852-4915-a5e8-a2df2ec914b4", "00000000-0000-0000-0000-000000000001", "5854e47b-c3e6-4d93-953c-187c52985ab9", gosDefinition, trafficShaper, function.getResultsSchema());
	
	}
	
	private boolean isExcludedByFilter(Iterable<CoordinateFilter> filters, Point point) throws Exception {
		for (CoordinateFilter filter : filters) {
			if (filter.exclude(point.getX(), point.getY()))
				return true;
		}
		return false;
	}
}
