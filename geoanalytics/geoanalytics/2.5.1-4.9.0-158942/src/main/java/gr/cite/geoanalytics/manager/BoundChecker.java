package gr.cite.geoanalytics.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import gr.cite.geoanalytics.dataaccess.geoserverbridge.elements.Bounds;

public class BoundChecker {
	private static Map<String, CoordinateReferenceSystem> crSystems = new ConcurrentHashMap<String, CoordinateReferenceSystem>();
	
	public BoundChecker(String crs, Bounds bbox) {
		
	}
}
