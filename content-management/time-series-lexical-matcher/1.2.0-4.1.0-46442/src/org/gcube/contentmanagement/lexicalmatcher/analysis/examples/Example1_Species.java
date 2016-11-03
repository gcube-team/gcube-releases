package org.gcube.contentmanagement.lexicalmatcher.analysis.examples;


import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class Example1_Species {

	public static void main(String[] args) {

		try {
			int attempts = 1;
			
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser(configPath);
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "import_2c97f580_35a0_11df_b8b3_aa10916debe6";
			String column = "field1";
			String correctFamily = "SPECIES";
			String correctColumn = "SCIENTIFIC_NAME";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
