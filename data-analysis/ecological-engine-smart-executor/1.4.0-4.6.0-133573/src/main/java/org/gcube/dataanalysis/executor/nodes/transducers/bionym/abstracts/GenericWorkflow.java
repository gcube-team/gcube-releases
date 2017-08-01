package org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Matcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Parser;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.PostProcessor;

public abstract class GenericWorkflow {
	
	protected HashMap<String,String> globalparameters;
	protected String sandboxFolder;
	
	protected Parser parser;
	protected List<Matcher> matchersList;
	protected PostProcessor postprocessor;
	
	
	public List<Matcher> getMatchersList(){
		return matchersList;
	}
	
	public void init(HashMap<String,String> globalparameters){
		this.globalparameters=globalparameters;
	}
	
	public GenericWorkflow(String sandboxFolder, HashMap<String,String> globalparameters){
		this.globalparameters = globalparameters;
		this.sandboxFolder = sandboxFolder;
		matchersList = new ArrayList<Matcher>();
		init(globalparameters);
	}
	
	//merges without repetitions
	protected abstract MatcherOutput mergeOutputs(List<MatcherOutput> outslist);
	
	public MatcherOutput executeChainedWorkflow(List<String> rawentries) throws Exception{
		//Preparsing and parsing
		long t0 = System.currentTimeMillis();
		parser.init(sandboxFolder, globalparameters);
		System.out.println("Parsing..");
		MatcherInput currentInput = parser.parse(rawentries);
		System.out.println("Parsed - Time: "+(System.currentTimeMillis()-t0));
		List<MatcherOutput> outputs = new ArrayList<MatcherOutput>();
		System.out.println("Matching..");
		//matching
		for (Matcher matcher:matchersList){
			MatcherOutput currentoutput = new MatcherOutput();
			if (currentoutput!=null){
				long t1 = System.currentTimeMillis();
				System.out.println("Matching with .."+matcher);
				currentoutput = matcher.match(currentInput);
				outputs.add(currentoutput);
				System.out.println("Matched - Time: "+(System.currentTimeMillis()-t1));
			}
		}
		System.out.println("Merging..");
		long t2=System.currentTimeMillis();
		MatcherOutput finaloutput = mergeOutputs(outputs);
		System.out.println("Merged - Time: "+(System.currentTimeMillis()-t2));
		if (postprocessor!=null){
			long t3 = System.currentTimeMillis();
			System.out.println("Postprocessing..");
			finaloutput = postprocessor.postprocessMatches(finaloutput);
			System.out.println("Postprocessed - Time: "+(System.currentTimeMillis()-t3));
		}
		System.out.println("END.");
		return finaloutput;
	}
	
	public void resetMatchers(List<Matcher> newmatchers){
		this.matchersList = null; 
		this.matchersList=newmatchers;
	}
}
