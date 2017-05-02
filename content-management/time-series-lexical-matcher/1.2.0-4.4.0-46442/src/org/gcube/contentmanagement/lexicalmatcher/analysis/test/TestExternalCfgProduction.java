package org.gcube.contentmanagement.lexicalmatcher.analysis.test;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class TestExternalCfgProduction {

	public static void main(String[] args) {

		try {
			int attempts = 1;
			
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser(configPath);
			//bench 1 
			AnalysisLogger.getLogger().warn("----------------------BENCH 1-------------------------");
//			String seriesName = "rdmc366dfe0ddf511e086b1b1c5d6fb1c27";
			String seriesName = "IMPORT_6dfb6d90_ede6_11e0_8ab5_cc3abd547d67";
			
			String column = "field3";
			LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
			/*
			conf.setCategoryDiscardDifferencialThreshold(5);
			conf.setCategoryDiscardThreshold(0);
			conf.setChunkSize(25);
			conf.setEntryAcceptanceThreshold(50);
			conf.setNumberOfThreadsToUse(2);
			conf.setRandomTake(true);
			conf.setReferenceChunksToTake(20);
			conf.setTimeSeriesChunksToTake(1);
			conf.setUseSimpleDistance(false);
			*/
			
			//database Parameters
			conf.setDatabaseUserName("utente");
			conf.setDatabasePassword("d4science");
//			conf.setDatabaseDriver("org.postgresql.Driver");
			conf.setDatabaseURL("jdbc:postgresql://dbtest.next.research-infrastructures.eu/timeseries");
			conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
			conf.setDatabaseAutomaticTestTable("connectiontesttable");
			conf.setDatabaseIdleConnectionTestPeriod("3600");
			conf.setReferenceTable("codelist1733371938");
			conf.setReferenceColumn("ifield14");
			conf.setNameHuman("ifield1");
			conf.setIdColumn("ifield0");
			conf.setDescription("ifield2");
			guesser.runGuesser(seriesName, column, conf);
			guesser.showResults(guesser.getClassification());
//			AnalysisLogger.getLogger().warn();
			AnalysisLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
