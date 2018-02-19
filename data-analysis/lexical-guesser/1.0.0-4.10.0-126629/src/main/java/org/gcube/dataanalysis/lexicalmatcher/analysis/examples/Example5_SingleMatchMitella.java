package org.gcube.dataanalysis.lexicalmatcher.analysis.examples;

import java.util.ArrayList;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;


public class Example5_SingleMatchMitella {

	public static void main(String[] args) {

		try {
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser();
			//bench 1 
			LexicalLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String singleton = "Mirella policepes";
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
			
			LexicalLogger.getLogger().warn("Detailed Match on Name :"+singleton);
			
			CategoryGuesser.showResults(detailedResults);
			
			LexicalLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
