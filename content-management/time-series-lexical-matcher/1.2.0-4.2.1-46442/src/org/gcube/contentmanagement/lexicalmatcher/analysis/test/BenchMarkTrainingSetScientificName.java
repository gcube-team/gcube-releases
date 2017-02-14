package org.gcube.contentmanagement.lexicalmatcher.analysis.test;

import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class BenchMarkTrainingSetScientificName {

	
public static void main(String[] args) {
		
		try {
			String configPath =".";
			int attempts = 1;
			CategoryGuesser guesser = new CategoryGuesser(configPath);
			
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "ref_species";
			String column = "scientific_name";
			String correctFamily = "species";
			String correctColumn = "scientific_name";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
					   
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
