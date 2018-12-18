package org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces;

import java.util.HashMap;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherInput;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherOutput;

public interface Matcher {
	
	public void init(String sandboxfolder, double threshold, int maxResults, HashMap<String,String> parameters);
	
	public MatcherOutput match(MatcherInput input) throws Exception;
	
}
