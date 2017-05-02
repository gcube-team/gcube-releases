package org.gcube.contentmanagement.timeseries.geotools.finder;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;

/**
 * applies an ecological model for filtering the csquares codes to only those having a probability higher than a threshold
 * 
 * @author coro
 * 
 */
public class ProbabilityFilter {

	static double threshold = 0.3;
	ConnectionsManager connManager;
//	private static final String getFilteredCodes = "select distinct csquarecode from hspec_suitable where (speciesid = '%1$s' or speciesid = '%2$s')and probability > " + threshold + " and csquarecode IN (%3$s)";
	private static final String getFilteredCodes = "select distinct csquarecode from hspec_native where (speciesid = '%1$s' or speciesid = '%2$s')and probability > " + threshold + " and csquarecode IN (%3$s)";

	public ProbabilityFilter(ConnectionsManager connManager) {
		this.connManager = connManager;
	}

	// filter the csquares according to the probabilities on aquamaps
	public List<String> FilterOnProbability(List<String> csquarecodes, String speciesid) {
		List<String> csquaresFiltered = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		int m = csquarecodes.size();
		for (int i = 0; i < m; i++) {
			String csquare = csquarecodes.get(i);
			sb.append("'" + csquare + "'");
			if (i < m - 1)
				sb.append(",");
		}

		String query = String.format(getFilteredCodes, speciesid, speciesid.toUpperCase(), sb.toString());
		AnalysisLogger.getLogger().trace("FilterOnProbability->FILTER ON PROBABILITY - APPLICATING");
//		AnalysisLogger.getLogger().trace("FilterOnProbability->FILTER ON PROBABILITY : " + query);
		try {

			List<Object> filteredsquares = connManager.AquamapsQuery(query);
			if (filteredsquares != null) {
				AnalysisLogger.getLogger().trace("FilterOnProbability->FOUND SQUARES : " + filteredsquares.size());

				for (Object fsquare : filteredsquares) {
					csquaresFiltered.add((String) fsquare);
				}
			}
			else
				AnalysisLogger.getLogger().trace("FilterOnProbability->NO SQUARES FOUND!");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return csquaresFiltered;
	}

}
