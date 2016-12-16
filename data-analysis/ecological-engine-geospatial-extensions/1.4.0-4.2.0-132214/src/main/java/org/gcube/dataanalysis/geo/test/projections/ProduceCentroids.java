package org.gcube.dataanalysis.geo.test.projections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.geo.connectors.wfs.FeaturedPolygon;
import org.gcube.dataanalysis.geo.connectors.wfs.WFSDataExplorer;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class ProduceCentroids {
	static String layer = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/ows?service=wfs&version=1.0.0&request=GetFeature&srsName=urn:x-ogc:def:crs:EPSG:4326&TYPENAME=aquamaps:worldborders";
	static String layername = "aquamaps:worldborders";
	
	public static void main(String[] args) throws Exception{
		
		List<FeaturedPolygon> featuresInTime = new ArrayList<FeaturedPolygon>();
		AnalysisLogger.getLogger().debug("taking WFS features from layer: "+layer);
		featuresInTime = WFSDataExplorer.getFeatures(layer, layername, -180, -90, 180, 90);
		HashMap<String, Point> centroidsmap = new HashMap<String, Point>();
		HashMap<String, Geometry> polymap = new HashMap<String, Geometry>();
		for (FeaturedPolygon fpoly:featuresInTime){
//			Point centroid = fpoly.p.getCentroid();
			Geometry prevPoly = polymap.get(fpoly.features.get("cntry_name"));

			if (prevPoly!=null){
				prevPoly = prevPoly.union(fpoly.p);
			}
			else
				prevPoly = fpoly.p;
			
//			if ((""+fpoly.features).contains("United States"))
//				System.out.println("centroid:"+fpoly.p.getCentroid()+" now "+prevPoly.getCentroid());

			polymap.put(fpoly.features.get("cntry_name"),prevPoly);
		}
		
		for (String key:polymap.keySet()){
			Point centroid = polymap.get(key).getCentroid();
			System.out.println(centroid.getX()+","+centroid.getY()+","+key);
		}
	}

}
