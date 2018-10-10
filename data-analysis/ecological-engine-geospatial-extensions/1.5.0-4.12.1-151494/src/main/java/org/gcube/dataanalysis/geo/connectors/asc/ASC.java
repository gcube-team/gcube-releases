package org.gcube.dataanalysis.geo.connectors.asc;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;

public class ASC implements GISDataConnector{

	@Override
	public List<Double> getFeaturesInTimeInstantAndArea(String layerURL, String layerName, int time, List<Tuple<Double>> coordinates3d, double BBxL, double BBxR, double BByL, double BByR) throws Exception{
		AnalysisLogger.getLogger().debug("managing ASC File");
		AscDataExplorer asc = new AscDataExplorer(layerURL);
		List<Double>features = asc.retrieveDataFromAsc(coordinates3d,time);
		AnalysisLogger.getLogger().debug("ASC File managed");
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


}
