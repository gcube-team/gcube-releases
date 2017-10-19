package org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers;

import java.util.HashMap;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.YasmeenMatcher;


public class FuzzyMatcher extends YasmeenMatcher{

	public FuzzyMatcher(String sandboxfolder, double threshold, int maxResults, HashMap<String, String> parameters) {
		super(sandboxfolder,threshold, maxResults, parameters);
	}

	@Override
	protected String getMatchlets() {
		
		return "-mftm";
	}

	@Override
	protected String getLexicalDistancesWeights() {
		return "";
	}

	protected String getStemming(){
		return "-mftm";
	}
	
}
