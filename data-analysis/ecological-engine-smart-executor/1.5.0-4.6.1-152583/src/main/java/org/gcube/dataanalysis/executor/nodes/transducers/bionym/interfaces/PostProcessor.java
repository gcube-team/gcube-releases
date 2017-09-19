package org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces;

import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherOutput;

public interface PostProcessor {
	
	public void init(HashMap<String,String> parameters);
	
	public MatcherOutput postprocessMatches(MatcherOutput rawnames);
	
	public HashMap<String,String> produceAnalytics();
	
}
