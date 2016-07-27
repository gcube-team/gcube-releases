package org.gcube.contentmanagement.lexicalmatcher.analysis.test;

import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class BenchMarkTestTSCountry {

	public static void main(String[] args) {

		try {
			int attempts = 1;
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser(configPath);
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "import_bdefb470_5cea_11df_a0a6_909e7d074592";
			String column = "field1";
			String correctFamily = "country";
			String correctColumn = "name_en";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
