package org.gcube.contentmanagement.lexicalmatcher.analysis.test.old;

import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class BenchMarkTrainingSet {

	
public static void main(String[] args) {
		
		try {
			String configPath =".";
			int attempts = 1;
			CategoryGuesser guesser = new CategoryGuesser();
			
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "ref_commission";
			String column = "name_en";
			String correctFamily = "commission";
			String correctColumn = "name_en";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
			AnalysisLogger.getLogger().warn("----------------------BENCH 2-------------------------");
			 seriesName = "ref_species";
			 column = "scientific_name";
			 correctFamily = "species";
			 correctColumn = "scientific_name";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 2-----------------------\n");
			
			
			AnalysisLogger.getLogger().warn("----------------------BENCH 3-------------------------");
			 seriesName = "ref_area";
			 column = "name_en";
			 correctFamily = "area";
			 correctColumn = "name_en";
//			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 3-----------------------\n");
			
			
			AnalysisLogger.getLogger().warn("----------------------BENCH 4-------------------------");
			 seriesName = "ref_ocean";
			 column = "name_en";
			 correctFamily = "ocean";
			 correctColumn = "name_en";
//			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 4-----------------------\n");
			
			
			AnalysisLogger.getLogger().warn("----------------------BENCH 5-------------------------");
			 seriesName = "ref_geo_region";
			 column = "name_en";
			 correctFamily = "geo region";
			 correctColumn = "name_en";
//			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 5-----------------------\n");
			
			
			AnalysisLogger.getLogger().warn("----------------------BENCH 6-------------------------");
			 seriesName = "ref_fa_region";
			 column = "name_en";
			 correctFamily = "fa region";
			 correctColumn = "name_en";
//			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 6-----------------------\n");
			
			
			AnalysisLogger.getLogger().warn("----------------------BENCH 7-------------------------");
			 seriesName = "ref_order";
			 column = "scientific_name";
			 correctFamily = "order";
			 correctColumn = "scientific_name";
//			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 7-----------------------\n");
			
			
			
		   
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
