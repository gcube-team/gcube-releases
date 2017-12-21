package org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.workflows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.GenericWorkflow;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherOutput;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.SingleEntry;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.FuzzyMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.GsayMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.LevensteinMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.TrigramMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.parsers.YasmeenParser;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Matcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public class BiOnymWF extends GenericWorkflow{

	public BiOnymWF (String sandboxFolder, int maxResults, HashMap<String,String> globalparameters){
		super(sandboxFolder, globalparameters);
		parser=new YasmeenParser();
		matchersList = new ArrayList<Matcher>();
		
		matchersList.add(new GsayMatcher(sandboxFolder, 0.6, maxResults, globalparameters));
		matchersList.add(new FuzzyMatcher(sandboxFolder, 0.6, maxResults, globalparameters));
		matchersList.add(new LevensteinMatcher(sandboxFolder, 0.4, maxResults, globalparameters));
		matchersList.add(new TrigramMatcher(sandboxFolder, 0.4, maxResults, globalparameters));
		
		postprocessor = null;
	}
	
	@Override
	protected MatcherOutput mergeOutputs(List<MatcherOutput> outslist) {
		MatcherOutput merged = new MatcherOutput();
		int k =0;
		for (MatcherOutput out:outslist){
				int nelems = out.getEntriesNumber();
				for (int j=0;j<nelems;j++){
					SingleEntry se = out.entries.get(j);
//					if (se.originalName.equalsIgnoreCase("Arvoglhssus thoro Kyle, 1913"))
//						System.out.println("->Stop");
					if (!merged.contains(se.originalName,se.targetScientificName, se.targetAuthor, se.targetID)){
						merged.addEntry(k, se.originalName, se.parsedScientificName, se.parsedAuthorship,
							se.matchingScore, se.targetDataSource, se.targetID, 
							se.targetScientificName, se.targetAuthor, se.otherElements);
						k++;
					}
						
				}
			
		}
		return merged;
	}
	
	
	public static void mainTest(String[] args) throws Exception{
		String sandboxFolder = "./PARALLEL_PROCESSING";
		HashMap<String,String> globalparameters = new HashMap<String, String>();
		globalparameters.put(YasmeenGlobalParameters.activatePreParsingProcessing, "true");
		globalparameters.put(YasmeenGlobalParameters.parserInputFileParam, "inputParser.txt");
		globalparameters.put(YasmeenGlobalParameters.parserOutputFileParam, "outputParser.txt");
		globalparameters.put(YasmeenGlobalParameters.parserNameParam, "SIMPLE");
		globalparameters.put(YasmeenGlobalParameters.taxaAuthorityFileParam, "ASFIS");
		globalparameters.put(YasmeenGlobalParameters.useStemmedGenusAndSpecies, "false");
		
		BiOnymWF bionym = new BiOnymWF(sandboxFolder, 10, globalparameters);
		
		List<String> rawEntries = new ArrayList<String>();
		rawEntries.add("Salmo lucidus Richardson, 1836");
		rawEntries.add("Perca nilotica Linnaeus, 1758");
		
		MatcherOutput output = bionym.executeChainedWorkflow(rawEntries);
		int nEntries = output.getEntriesNumber();
		for (int i=0;i<nEntries;i++)
			System.out.println(output.getEntry(i));
	}
	
	
}
