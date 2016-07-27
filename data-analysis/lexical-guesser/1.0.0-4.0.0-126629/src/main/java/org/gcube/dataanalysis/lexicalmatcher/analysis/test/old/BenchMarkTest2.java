package org.gcube.dataanalysis.lexicalmatcher.analysis.test.old;

import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;

public class BenchMarkTest2 {

	public static void main(String[] args) {

		try {
			int attempts = 1;
			
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser();
			//bench 1 
			LexicalLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "import_2c97f580_35a0_11df_b8b3_aa10916debe6";
			String column = "field1";
			String correctFamily = "SPECIES";
			String correctColumn = "SCIENTIFIC_NAME";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
			
			
			//bench 2
			LexicalLogger.getLogger().warn("----------------------BENCH 2-------------------------");
			seriesName = "import_2c97f580_35a0_11df_b8b3_aa10916debe6";
			column = "field2";
			correctFamily = "COUNTRY";
			correctColumn = "ISO_3_CODE";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 2-----------------------\n");
			
			
			//bench 4
			LexicalLogger.getLogger().warn("----------------------BENCH 4-------------------------");
			seriesName = "import_2c97f580_35a0_11df_b8b3_aa10916debe6";
			column = "field3";
			correctFamily = "AREA";
			correctColumn = "NAME_EN";
			CategoryGuesser.AccuracyCalc(guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			LexicalLogger.getLogger().warn("--------------------END BENCH 4-----------------------\n");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
