package gr.cite.geoanalytics.functions.experiments;

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
import gr.cite.geoanalytics.functions.functions.Attribute;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.output.object.ExtradataField;
import gr.cite.geoanalytics.functions.output.object.Helper;
import gr.cite.geoanalytics.geospatial.operations.LayerOperations;
import gr.cite.geoanalytics.geospatial.retrieval.FeatureRetrievalHelper;

public class AquacultureAlgorithmExecutor {

	// private static final long serialVersionUID = -7071249895885075726L;

	private static final Logger logger = LoggerFactory.getLogger(AquacultureAlgorithmExecutor.class);

	public AquacultureAlgorithmExecutor(int scanStepMeters) {
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
	public List<ShapeMessenger> executeForPartition(double minimumX, double minimumY, double maximumX, double maximumY, Iterable<CoordinateFilter> filters,
			Function function, String crs, FeatureSource<SimpleFeatureType, SimpleFeature> costal) throws Exception {

		double distance = JTS.orthodromicDistance(new Coordinate(minimumX, maximumY), new Coordinate(maximumX, maximumY), CRS.decode(crs));

		int totalMeters = (int) distance;

		int samplesPerDimension = totalMeters / scanStepMeters;

		double sampleDistance = (maximumX - minimumX) / samplesPerDimension;

		List<ShapeMessenger> shapeMesengers = new ArrayList<ShapeMessenger>();

		Iterable<SimpleFeature> costalFeatures = FeatureRetrievalHelper.getShapes(costal, "SHP_Geography", minimumX, minimumY, maximumX, maximumY, crs);

		GeometryFactory gFactory = JTSFactoryFinder.getGeometryFactory();

		for (double x = minimumX; x < maximumX; x += sampleDistance) {
			for (double y = minimumY; y < maximumY; y += sampleDistance) {

				boolean contained = false;

				Point point = gFactory.createPoint(new Coordinate(x, y));

				Iterator<SimpleFeature> iterator = costalFeatures.iterator();

				while (iterator.hasNext()) {
					SimpleFeature feature = iterator.next();
					Geometry geometry = (Geometry) feature.getDefaultGeometry();
					try {
						if (point.within(geometry) && !isExcludedByFilter(filters, point))
							contained = true;
					} catch (Exception ex) {
						System.out.println("Skipping a geometry check... Reason: " + ex.getMessage());
						logger.debug("Skipping a geometry check... Reason: " + ex.getMessage());
					}
				}

				if (contained == true) {
					try {
						List<Attribute> result = function.execute(x, y);
						
						List<ExtradataField> extraFields = new ArrayList<>();
						
						String valueSting = "";
						for(Attribute attribute : result){
							extraFields.add(new ExtradataField(attribute.getName(), attribute.getValue()));
							valueSting += attribute.getValue().toString();
						}
						
						String extraData = Helper.formExtradataField(extraFields.toArray(new ExtradataField[extraFields.size()]));
						Shape shape = new Shape();
						shape.setGeography(point);
						shape.setCode(crs);
						shape.setExtraData(extraData);
						// shape.setLayerID(UUID.randomUUID()); SHOULD NOT ADD A
						// LAYER ID, IT SHOULD BE ADDED JUST BEFORE THE
						// INSERTION PHASE (when it's known)
						shapeMesengers.add(new ShapeMessenger(shape));
						
						logger.info("Managed to calculate value " + valueSting + " for: " + x + " - " + y);
					} catch (Exception ex) {
						System.out.println("Could either not compute the function for the point " + x + " , " + y
								+ " or package it in a shape object. Details: " + ex.getMessage());
						logger.debug("Could either not compute the function for the point " + x + " , " + y + " or package it in a shape object. Details: "
								+ ex.getMessage());
					}
				}

			}
		}

		System.out.println("COMPUTED ANOTHER " + shapeMesengers.size() + " SHAPEMESSENGERS");
		logger.debug("COMPUTED ANOTHER " + shapeMesengers.size() + " SHAPEMESSENGERS");

		function.destroy();
		
		return shapeMesengers;
	}
}
