package gr.cite.geoanalytics.functions.exploration;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.geotools.data.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.*;
import org.opengis.geometry.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.*;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.functions.filters.CoordinateFilter;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.output.object.ExtradataField;
import gr.cite.geoanalytics.functions.output.object.Helper;
import gr.cite.geoanalytics.geospatial.operations.LayerOperations;
import gr.cite.geoanalytics.geospatial.retrieval.FeatureRetrievalHelper;

public class FeatureBasedAlgorithmParallelExecutor {

//	private static final long serialVersionUID = -7071249895885075726L;

	private static final Logger logger = LoggerFactory.getLogger(FeatureBasedAlgorithmParallelExecutor.class);
	
	public FeatureBasedAlgorithmParallelExecutor(int scanStepMeters) {
		this.scanStepMeters = scanStepMeters;
	}
	
	private int scanStepMeters;


	private boolean isExcludedByFilter(Iterable<CoordinateFilter> filters, Point point) throws Exception {
		for (CoordinateFilter filter : filters) {
			if (filter.exclude(point.getX(), point.getY()))
				return true;
		}
		return false;
	}
	
	
	@SuppressWarnings("unchecked") 
	public List<ShapeMessenger> executeForPartition(
			double minimumX, double minimumY, double maximumX, double maximumY,
			Iterable<CoordinateFilter> filters, 
			Function function, 
			GeometryFactory gFactory, 
			String crs,
			FeatureSource<SimpleFeatureType, SimpleFeature> ... layerSources
			) throws Exception {

		double distance = JTS.orthodromicDistance(new Coordinate(minimumX, maximumY), new Coordinate(maximumX, maximumY), CRS.decode(crs));

		int totalMeters = (int) distance;

		int samplesPerDimension = totalMeters / scanStepMeters;

		double sampleDistance = (maximumX - minimumX) / samplesPerDimension;

		List<ShapeMessenger> shapeMesengers = new ArrayList<ShapeMessenger>();
		
		Iterable<SimpleFeature> paraktiesFeatures = FeatureRetrievalHelper.getShapes(layerSources[0], "SHP_Geography", minimumX, minimumY, maximumX, maximumY, crs);
		
		Iterable<SimpleFeature> natura2000Features = FeatureRetrievalHelper.getShapes(layerSources[1], "SHP_Geography", minimumX, minimumY, maximumX, maximumY, crs);
		
		Iterable<SimpleFeature> difference = LayerOperations.difference(paraktiesFeatures, natura2000Features);
		
		
		for(double x = minimumX ; x < maximumX ; x += sampleDistance){
			for(double y = minimumY ; y < maximumY ; y += sampleDistance){
				
				boolean contained = false;
				
				Point point = gFactory.createPoint(new Coordinate(x, y));
				
				Iterator<SimpleFeature> iterator = difference.iterator();
				
				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					Geometry geometry = (Geometry) feature.getDefaultGeometry();
					try{
						if (point.within(geometry) && !isExcludedByFilter(filters, point))
							contained = true;
					}
					catch(IllegalArgumentException iae){
						logger.debug("Skipping a geometry check...");
					}
				}
				if(contained == true){
					String extraData = Helper.formExtradataField(new ExtradataField("function_result", new Double(function.execute(x, y))));
					Shape shape = new Shape();
					shape.setGeography(point);
					shape.setCode(crs);
					shape.setExtraData(extraData);
//					shape.setLayerID(UUID.randomUUID()); SHOULD NOT ADD A LAYER ID, IT SHOULD BE ADDED JUST BEFORE THE INSERTION PHASE (when it's known)
					shapeMesengers.add(new ShapeMessenger(shape));
				}
				
			}
		}
		
		logger.debug("COMPUTED ANOTHER "+shapeMesengers.size()+" SHAPEMESSENGERS");
		
		return shapeMesengers;
		
	}
	
	
	
}
