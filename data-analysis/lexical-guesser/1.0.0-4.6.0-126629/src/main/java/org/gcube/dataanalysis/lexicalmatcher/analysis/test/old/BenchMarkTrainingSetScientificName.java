package org.gcube.dataanalysis.lexicalmatcher.analysis.test.old;

import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;

public class BenchMarkTrainingSetScientificName {

	
public static void main(String[] args) {
		
		try {
			String configPath =".";
			int attempts = 1;
			CategoryGuesser guesser = new CategoryGuesser();
			
			//bench 1 
			LexicalLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "ref_species";
			String column = "scientific_name";
			String correctFamily = "species";
			String correctColumn = "scientific_name";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
					   
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
