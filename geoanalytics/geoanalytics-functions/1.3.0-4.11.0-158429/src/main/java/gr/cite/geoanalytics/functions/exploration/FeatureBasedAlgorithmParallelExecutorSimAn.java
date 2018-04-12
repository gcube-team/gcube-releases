package gr.cite.geoanalytics.functions.exploration;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.*;
import org.opengis.geometry.BoundingBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.*;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.dataaccess.entities.shape.Shape;
import gr.cite.geoanalytics.functions.common.model.BBox;
import gr.cite.geoanalytics.functions.common.model.GeoPoint;
import gr.cite.geoanalytics.functions.exceptions.NoNeighborFoundException;
import gr.cite.geoanalytics.functions.exploration.simann.SAHelper;
import gr.cite.geoanalytics.functions.filters.CoordinateFilter;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.output.object.ExtradataField;
import gr.cite.geoanalytics.functions.output.object.Helper;
import gr.cite.geoanalytics.geospatial.operations.LayerOperations;
import gr.cite.geoanalytics.geospatial.retrieval.FeatureRetrievalHelper;
import gr.cite.geoanalytics.geospatial.retrieval.RasterRetrievalHelper;

public class FeatureBasedAlgorithmParallelExecutorSimAn {

//	private static final long serialVersionUID = -7071249895885075726L;

	private static final Logger logger = LoggerFactory.getLogger(FeatureBasedAlgorithmParallelExecutorSimAn.class);
	
	public FeatureBasedAlgorithmParallelExecutorSimAn(int scanStepMeters) {
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
			Function function, 
			GeometryFactory gFactory, 
			String sst,
			String crs
			) throws Exception {

		double distance = JTS.orthodromicDistance(new Coordinate(minimumX, maximumY), new Coordinate(maximumX, maximumY), CRS.decode(crs));

		int totalMeters = (int) distance;

		int samplesPerDimension = totalMeters / scanStepMeters;

		double sampleDistance = (maximumX - minimumX) / samplesPerDimension;

		List<ShapeMessenger> shapeMesengers = new ArrayList<ShapeMessenger>();
		
		
		//TODO: change input of this function to be this BBox (push bbox creation outside this function, on it's caller)
		BBox bbox = new BBox(minimumX, maximumX, minimumY, maximumY);
		
		Random random = new Random();
		
		double initTemp = 10;//10000.0; //100;
		double currentTemp = initTemp;
		double coolingRate = 0.999;//0.9999; //0.99;
		double absoluteTemp = 0.01;//0.00001; // = 1; // =0.01;
		
		boolean objectiveIsMaximization = true;
		
		
		sst += ("&subset=http://www.opengis.net/def/axis/OGC/0/Long("+bbox.getMinLon()+","+bbox.getMaxLon()+")"
			  + "&subset=http://www.opengis.net/def/axis/OGC/0/Lat("+bbox.getMinLat()+","+bbox.getMaxLat()+")");
		
		
//		sst = "http://dionysus.di.uoa.gr:3000/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics:adbb5464-fae0-447c-b082-0f13c471f56d&format=geotiff"
//				+ "&subset=http://www.opengis.net/def/axis/OGC/0/Long("+bbox.getMinLon()+","+bbox.getMaxLon()+")"
//				+ "&subset=http://www.opengis.net/def/axis/OGC/0/Lat("+bbox.getMinLat()+","+bbox.getMaxLat()+")"
//				;
		
//		sst = "http://dl012.madgik.di.uoa.gr:8080/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics__sst&format=geotiff";
		
		GridCoverage2D gridCoverage = RasterRetrievalHelper.getCoverage(sst);
		
		System.out.println("Running for bbox : "+bbox);
		
		GeoPoint lastAcceptedPoint = SAHelper.getRandomWithin(bbox);
		float lastAcceptedCost = calculateCostFunction(lastAcceptedPoint, gridCoverage);
		
		while (currentTemp > absoluteTemp){
			
			GeoPoint neighbor;
			try{
				neighbor = SAHelper.getNeighbor(lastAcceptedPoint, bbox, initTemp, currentTemp, absoluteTemp, coolingRate);
			}
			catch(NoNeighborFoundException ex){
				System.out.println(ex.getMessage());
				break;
			}
			
			float newCost = calculateCostFunction(neighbor, gridCoverage);
			
			double acceptanceProb = acceptanceProbability(lastAcceptedCost, newCost, currentTemp, objectiveIsMaximization);
			
			double random01 = random.nextDouble();

//			System.out.println("Acceptance probability: "+acceptanceProb+ "\t random01: "+random01);
			
			if(acceptanceProb > random01){
				
//				//System.out.println("Switching: Old(point, cost): "+lastAcceptedPoint+ " "+lastAcceptedCost +"\t"+"New(point, cost): "+neighbor+ " "+newCost);
				System.out.println("Switching: Old cost: "+lastAcceptedCost +"\t"+"New cost: "+newCost);
				
				lastAcceptedCost = newCost;
				lastAcceptedPoint = neighbor;
			}
//			else
//				System.out.println("NOT SWITCHING: Old cost: "+lastAcceptedCost +"\t"+"New cost: "+newCost);

	        currentTemp *= coolingRate;
		
		}
		
		Point point = gFactory.createPoint(new Coordinate(lastAcceptedPoint.getLongitude(), lastAcceptedPoint.getLatitude()));
		String extraData = Helper.formExtradataField(new ExtradataField("function_result", new Double(lastAcceptedCost)));
		Shape shape = new Shape();
		shape.setGeography(point);
		shape.setCode(crs);
		shape.setExtraData(extraData);
//		shape.setLayerID(UUID.randomUUID()); SHOULD NOT ADD A LAYER ID, IT SHOULD BE ADDED JUST BEFORE THE INSERTION PHASE (when it's known)
		shapeMesengers.add(new ShapeMessenger(shape));
		
		
		logger.debug("COMPUTED ANOTHER "+shapeMesengers.size()+" SHAPEMESSENGERS");
		
		return shapeMesengers;
		
	}
	
	private float calculateCostFunction(GeoPoint point, GridCoverage2D temperatures){
		float [] result = (float[]) temperatures.evaluate(new DirectPosition2D(temperatures.getCoordinateReferenceSystem2D(), point.getLongitude(), point.getLatitude()) );
		return result[0];
	}
	
	
	
	private double acceptanceProbability(double prevFuncRes, double newFuncRes, double temperature, boolean objectiveIsMaximization) {
        if(!objectiveIsMaximization){ 
        	if(newFuncRes < prevFuncRes) 
        		return 1.0;
        }
        else
        	if(newFuncRes > prevFuncRes)
        		return 1.0;
        
        if(temperature<0.3)
        	temperature *=1;
        return Math.exp(-(Math.abs(newFuncRes - prevFuncRes)/temperature));
    }
	
//	public static void main(String [] args) throws Exception{
//		String sst = "http://dionysus.di.uoa.gr:3000/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics:adbb5464-fae0-447c-b082-0f13c471f56d&format=geotiff"
////				+ "&subset=http://www.opengis.net/def/axis/OGC/0/Long("+bbox.getMinLon()+","+bbox.getMaxLon()+")"
////				+ "&subset=http://www.opengis.net/def/axis/OGC/0/Lat("+bbox.getMinLat()+","+bbox.getMaxLat()+")"
//				;
//		
////		String sst = "http://dl012.madgik.di.uoa.gr:8080/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics__sst&format=geotiff";
//		
//		GridCoverage2D gridCoverage = RasterRetrievalHelper.getCoverage(sst);
//		
//		List<ShapeMessenger> results = new FeatureBasedAlgorithmParallelExecutorSimAn(500)
//				.executeForPartition(22.9065, 34.6641, 26.8616, 35.7874, new RandomNPV(), JTSFactoryFinder.getGeometryFactory(), sst, "EPSG:4326");
//		
//		System.out.println(results);
//		
//	}
	
}
