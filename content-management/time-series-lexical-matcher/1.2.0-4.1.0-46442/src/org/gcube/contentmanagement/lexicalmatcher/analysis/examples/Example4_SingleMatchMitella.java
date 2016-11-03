package org.gcube.contentmanagement.lexicalmatcher.analysis.examples;

import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;


public class Example4_SingleMatchMitella {

	public static void main(String[] args) {

		try {
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser(configPath);
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String singleton = "Mitella pollicipes";
//			String singleton = "policipes";
			String family = "species";
			String column = "scientific_name";
			
			LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
			
			//CHANGE THIS TO ENHANCE THE RECALL
			conf.setEntryAcceptanceThreshold(30);
			conf.setReferenceChunksToTake(-1);
			conf.setTimeSeriesChunksToTake(-1);
			conf.setUseSimpleDistance(false);
			
			guesser.runGuesser(configPath, singleton, conf, family,column );
			ArrayList<SingleResult> detailedResults = guesser.getDetailedMatches();
			
			AnalysisLogger.getLogger().warn("Detailed Match on Name :"+singleton);
			
			CategoryGuesser.showResults(detailedResults);
			
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
