package gr.cite.geoanalytics.functions.exploration.simann;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.FactoryRegistryException;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;

import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Coordinate;

import gr.cite.gaap.datatransferobjects.ShapeMessenger;
import gr.cite.geoanalytics.functions.common.model.BBox;
import gr.cite.geoanalytics.functions.common.model.GeoPoint;
import gr.cite.geoanalytics.functions.exceptions.NoNeighborFoundException;
import gr.cite.geoanalytics.functions.exploration.FeatureBasedAlgorithm;
import gr.cite.geoanalytics.functions.exploration.FeatureBasedAlgorithmParallelExecutor;
import gr.cite.geoanalytics.functions.exploration.FeatureBasedAlgorithmParallelExecutorSimAn;
import gr.cite.geoanalytics.geospatial.retrieval.RasterRetrievalHelper;

public class SAHelper {
	
	
	public static GeoPoint getRandomWithin(BBox bbox){
		Random r = new Random();
		double longitude = bbox.getMinLon() + (bbox.getMaxLon() - bbox.getMinLon()) * r.nextDouble();
		double latitude = bbox.getMinLat() + (bbox.getMaxLat() - bbox.getMinLat()) * r.nextDouble();
		return new GeoPoint(longitude, latitude);
	}
	
	
	public static boolean isWithin(GeoPoint point, BBox bbox){
		return (point.getLongitude() > bbox.getMinLon() && point.getLongitude() < bbox.getMaxLon() 
				&& point.getLatitude() > bbox.getMinLat() && point.getLatitude() < bbox.getMaxLat());
	}
	
	
	
	/*
	 * Choosing neighbors will also depend on your problem. 
	 * The main reason to limit the neighborhood is so that once you've found a decent solution, 
	 * even if you later move to a worse solution, you at least stay in the neighborhood. 
	 * The intuition is that most objective functions are somewhat smooth, 
	 * so good solutions will lie near other good solutions. So you need a neighborhood that's 
	 * small enough to keep you near good solutions, but large enough to let you find them quickly. 
	 * One thing you can try is decreasing the neighborhood over time (e.g. make it proportional to the temperature).
	 */
	
	public static GeoPoint getNeighbor(GeoPoint p, BBox bbox, double initTemp, double currentTemp, double absoluteTemp, double coolingRate) throws NoNeighborFoundException{
		
//	 	//this goes approximately from 0.25 to 0
//	    double scaledCoordStepping = (currentTemp / (initTemp-absoluteTemp) / 4);
		//this goes approximately from ~0.5 to 0
	    double scaledCoordStepping = 0.49999 * (Math.abs(currentTemp-absoluteTemp) / Math.abs(initTemp-absoluteTemp));
	    
		Random rand = new Random(System.nanoTime());
		
		List<PairInt> orientations = new ArrayList<>(Arrays.asList(
				new PairInt(-1,-1), new PairInt(-1,0), new PairInt(-1,1), new PairInt(0,-1), 
				new PairInt(0,-1), new PairInt(1,-1), new PairInt(1,0), new PairInt(1,-1)));

		while(orientations.size()!=0){
			
			Collections.shuffle(orientations, rand);
			
			PairInt orientationSign = orientations.remove(0);
			
			double longitudeDiff = (bbox.getMaxLon() - bbox.getMinLon()) * scaledCoordStepping * orientationSign.getX(); 
			double latitudeDiff = (bbox.getMaxLat() - bbox.getMinLat()) * scaledCoordStepping * orientationSign.getY(); 
			
			GeoPoint neighbor = new GeoPoint(p.getLongitude()+longitudeDiff, p.getLatitude()+latitudeDiff);
			if(isWithin(neighbor, bbox))
				return neighbor;
		}
		
		throw new NoNeighborFoundException("There was no neighbor found for point: "+p);
	}

}

class PairInt{
	int x;
	int y;
	public PairInt(int x, int y){this.x=x;this.y=y;}
	public int getX(){return x;}
	public int getY(){return y;}
}
