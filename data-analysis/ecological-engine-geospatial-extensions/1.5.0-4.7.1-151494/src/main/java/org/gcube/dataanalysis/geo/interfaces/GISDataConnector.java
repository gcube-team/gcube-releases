package org.gcube.dataanalysis.geo.interfaces;

import java.util.List;

import org.gcube.dataanalysis.ecoengine.utils.Tuple;

public interface GISDataConnector {

	public List<Double> getFeaturesInTimeInstantAndArea(String layerURL, String layerName, int time, List<Tuple<Double>> coordinates3d, double BBxL,double BBxR, double BByL, double BByR) throws Exception;
	
	public double getMinZ(String layerURL, String layerName);
	
	public double getMaxZ(String layerURL, String layerName);
}
