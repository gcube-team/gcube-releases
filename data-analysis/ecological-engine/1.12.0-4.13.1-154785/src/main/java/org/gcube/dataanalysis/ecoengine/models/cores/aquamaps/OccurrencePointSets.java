package org.gcube.dataanalysis.ecoengine.models.cores.aquamaps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OccurrencePointSets {

	private Map<String,List<OccurrencePoint>> occurrenceMap;

	public OccurrencePointSets(){
		occurrenceMap = new HashMap<String, List<OccurrencePoint>>();
	}
	
	public void setOccurrenceMap(Map<String,List<OccurrencePoint>> occurrenceMap) {
		this.occurrenceMap = occurrenceMap;
	}

	public Map<String,List<OccurrencePoint>> getOccurrenceMap() {
		return occurrenceMap;
	}
	
	public void addOccurrencePointList(String name,List<OccurrencePoint> pointsList){
		occurrenceMap.put(name, pointsList);
	}
	
	public void addOccurrencePointList(EnvelopeModel name,List<OccurrencePoint> pointsList){
		occurrenceMap.put(""+name, pointsList);
	}
	
	public void addOccurrencePoint(String name,OccurrencePoint occurrencePoint){
		List<OccurrencePoint> occurrenceList = occurrenceMap.get(name);
		occurrenceList.add(occurrencePoint);
	}
	
	public void addOccurrencePoint(EnvelopeModel name,OccurrencePoint occurrencePoint){
		List<OccurrencePoint> occurrenceList = occurrenceMap.get(""+name);
		occurrenceList.add(occurrencePoint);
	}
	
}
