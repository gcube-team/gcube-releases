package org.gcube.contentmanagement.lexicalmatcher.analysis.test.old;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class BenchMarkTestExternalCfg {

	public static void main(String[] args) {

		try {
			int attempts = 1;
			
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser();
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String seriesName = "import_532bba80_1c8f_11df_a4ee_87804054691e";
			String column = "field2";
			String correctFamily = "ISSCAAP GROUP";
			String correctColumn = "NAME_EN";
			LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
			conf.setCategoryDiscardDifferencialThreshold(5);
			conf.setCategoryDiscardThreshold(0);
			conf.setChunkSize(25);
			conf.setEntryAcceptanceThreshold(50);
			conf.setNumberOfThreadsToUse(2);
			conf.setRandomTake(true);
			conf.setReferenceChunksToTake(20);
			conf.setTimeSeriesChunksToTake(1);
			conf.setUseSimpleDistance(false);
			
			//database Parameters
			conf.setDatabaseUserName("root");
//			conf.setDatabasePassword("password");
			conf.setDatabaseDriver("com.mysql.jdbc.Driver");
			conf.setDatabaseURL("jdbc:mysql://localhost/timeseries");
			conf.setDatabaseDialect("org.hibernate.dialect.MySQLDialect");
			conf.setDatabaseAutomaticTestTable("connectiontesttable");
			conf.setDatabaseIdleConnectionTestPeriod("3600");
			
			CategoryGuesser.AccuracyCalc(conf,guesser, configPath, seriesName, column, attempts, correctFamily, correctColumn);
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
