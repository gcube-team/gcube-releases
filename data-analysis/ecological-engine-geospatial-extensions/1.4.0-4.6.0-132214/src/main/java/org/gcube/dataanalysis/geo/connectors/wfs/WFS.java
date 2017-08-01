package org.gcube.dataanalysis.geo.connectors.wfs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;
import org.gcube.dataanalysis.geo.utils.CSquareCodesConverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class WFS implements GISDataConnector {

	List<FeaturedPolygon> featuresInTime;
	HashMap<Double,Map<String, String>> polygonsFeatures;

	@Override
	public List<Double> getFeaturesInTimeInstantAndArea(String layerURL, String layerName, int time, List<Tuple<Double>> coordinates3d, double BBxL, double BBxR, double BByL, double BByR) throws Exception {

		if (time>0)
			throw new Exception("Error Time Dimension is not supported for WFS!");
		if (layerURL == null)
			return null;

		featuresInTime = new ArrayList<FeaturedPolygon>();
		AnalysisLogger.getLogger().debug("taking WFS features from layer: "+layerURL);
		featuresInTime = WFSDataExplorer.getFeatures(layerURL, layerName, BBxL, BByL, BBxR, BByR);
		polygonsFeatures=new HashMap<Double, Map<String,String>>();
		int tsize = coordinates3d.size();
		AnalysisLogger.getLogger().debug("Intersecting " + tsize + " vs " + featuresInTime.size() + " elements");
		int ttc = 0;
		Double[] featuresarray = new Double[tsize];
		int k = 0;
		int intersections = 0;
		GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
		for (Tuple<Double> triplet : coordinates3d) {
			ArrayList<Double> elements = triplet.getElements();

			CoordinateArraySequence pcoords = new CoordinateArraySequence(new Coordinate[] { new Coordinate(elements.get(0), elements.get(1)), });
			//patch for MAP server
			if (layerURL.contains("/wxs")){
				pcoords = new CoordinateArraySequence(new Coordinate[] { new Coordinate(elements.get(1), elements.get(0)), });
			} 
			Point po = new Point(pcoords, factory);
			boolean found = false;

			for (FeaturedPolygon poly : featuresInTime) {
				/*check the polygons
				 * if (k==0)
					System.out.println(poly.p);
					*/
				if (poly != null && poly.p != null && poly.p.covers(po)) {
/*
					AnalysisLogger.getLogger().debug(poly.p.getCentroid()+
								"["+CSquareCodesConverter.convertAtResolution(poly.p.getCentroid().getY(), poly.p.getCentroid().getX(), 0.5)+"] "+
								"("+(poly.p.getCentroid().getX()-0.25)+" -- "+(poly.p.getCentroid().getX()+0.25) +" ; "+
								(poly.p.getCentroid().getY()-0.25)+" -- "+(poly.p.getCentroid().getY()+0.25) +") "+
							   	" ["+poly.features+"]"+
								" covers "+po+ "["+CSquareCodesConverter.convertAtResolution(po.getY(), po.getX(), 0.5)+"]");

					AnalysisLogger.getLogger().debug("{"+poly.p.contains(po)+
							";"+po.isWithinDistance(poly.p.getCentroid(), 0.24)+
							";"+po.isWithinDistance(poly.p.getCentroid(), 0.25)+
							";"+poly.p.getCentroid().distance(po)+
							";"+poly.p.distance(po)+
							";"+poly.p.convexHull().contains(po)+
							";"+poly.p.convexHull().touches(po)+
							";"+poly.p.touches(po)+
							";"+poly.p.crosses(po)+
							";"+po.crosses(poly.p)+
							";"+po.coveredBy(poly.p)+
							";"+po.intersection(poly.p)+
							";"+poly.p.contains(po)+
							";"+poly.p.covers(po)+
							"}");
	*/			
					featuresarray[k] = poly.value;
					polygonsFeatures.put(poly.value, poly.features);
					found = true;
					intersections++;
					break;
				}
			}

			po = null;

			if (!found) {
				featuresarray[k] = Double.NaN;
			}

			if (ttc % 10000 == 0) {
				AnalysisLogger.getLogger().debug("Status: " + ((double) ttc * 100d / (double) tsize));
			}
			ttc++;
			k++;
		}
		
		AnalysisLogger.getLogger().debug("WFS-> Found " + intersections + " intersections!");
		
		List<Double> features = Arrays.asList(featuresarray);
		return features;
	}

	
	@Override
	public double getMinZ(String layerURL, String layerName) {
		return 0;
	}

	@Override
	public double getMaxZ(String layerURL, String layerName) {
		return 0;
	}

	public HashMap<Double, Map<String, String>> getPolygonsFeatures() {
		return polygonsFeatures;
	}
}
