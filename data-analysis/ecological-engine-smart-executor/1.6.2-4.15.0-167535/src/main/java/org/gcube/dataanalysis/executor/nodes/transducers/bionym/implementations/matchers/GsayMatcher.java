package org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers;

import java.util.HashMap;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.YasmeenMatcher;


public class GsayMatcher extends YasmeenMatcher{

	public GsayMatcher(String sandboxfolder, double threshold, int maxResults, HashMap<String, String> parameters) {
		super(sandboxfolder,threshold, maxResults,parameters);
	}

	@Override
	protected String getMatchlets() {
		
		return "-mgsay";
	}

	@Override
	protected String getLexicalDistancesWeights() {
		return "";
	}

	@Override
	protected String getStemming() {
		return "-mgsay";
	}

}
