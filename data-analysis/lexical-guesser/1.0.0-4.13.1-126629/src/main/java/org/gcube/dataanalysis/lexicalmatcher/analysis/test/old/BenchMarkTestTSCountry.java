package org.gcube.dataanalysis.lexicalmatcher.analysis.test.old;

import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;

public class BenchMarkTestTSCountry {

	public static void main(String[] args) {

		try {
			int attempts = 1;
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser();
			//bench 1 
			LexicalLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "import_bdefb470_5cea_11df_a0a6_909e7d074592";
			String column = "field1";
			String correctFamily = "country";
			String correctColumn = "name_en";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
