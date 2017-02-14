package org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces;

import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherInput;

public interface Parser {
	
	public void init (String sandboxfolder, HashMap<String, String> parameters);
	
	public MatcherInput parse (List<String> rawnames) throws Exception;
}
