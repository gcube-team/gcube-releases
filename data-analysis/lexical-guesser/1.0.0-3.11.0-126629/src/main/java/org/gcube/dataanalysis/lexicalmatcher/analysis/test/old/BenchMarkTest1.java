package org.gcube.dataanalysis.lexicalmatcher.analysis.test.old;

import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;

public class BenchMarkTest1 {

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
			
			//bench 2
			LexicalLogger.getLogger().warn("----------------------BENCH 2-------------------------");
			seriesName = "import_bdefb470_5cea_11df_a0a6_909e7d074592";
			column = "field2";
			correctFamily = "area";
			correctColumn = "name_en";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 2-----------------------\n");
			
			//bench 3
			LexicalLogger.getLogger().warn("----------------------BENCH 3-------------------------");
			seriesName = "import_bdefb470_5cea_11df_a0a6_909e7d074592";
			column = "field4";
			correctFamily = "species";
			correctColumn = "scientific_name";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 3-----------------------\n");
			
			//bench 4
			LexicalLogger.getLogger().warn("----------------------BENCH 4-------------------------");
			seriesName = "import_bdefb470_5cea_11df_a0a6_909e7d074592";
			column = "field3";
			correctFamily = "species";
			correctColumn = "scientific_name";
//			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 4-----------------------\n");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
