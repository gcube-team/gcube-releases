package org.gcube.contentmanagement.lexicalmatcher.analysis.examples;

import java.util.ArrayList;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.contentmanagement.lexicalmatcher.analysis.run.CategoryGuesser;

public class ExampleGuessingExternalCfg {

	public static void main(String[] args) {

		try {
			
			String configPath = "./";
			CategoryGuesser guesser = new CategoryGuesser(configPath);
			
			
			//bench 1 
			System.out.println("----------------------BENCH 1-------------------------");
			String seriesName = "import_532bba80_1c8f_11df_a4ee_87804054691e";
			String column = "field2";
			LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
			conf.setCategoryDiscardDifferencialThreshold(10);
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
			conf.setDatabasePassword("ash_ash80");
			conf.setDatabaseDriver("com.mysql.jdbc.Driver");
			conf.setDatabaseURL("jdbc:mysql://localhost/timeseries");
			conf.setDatabaseDialect("org.hibernate.dialect.MySQLDialect");
			conf.setDatabaseAutomaticTestTable("connectiontesttable");
			conf.setDatabaseIdleConnectionTestPeriod("3600");

			//reference parameters
			conf.setReferenceTable("reference_table");
			conf.setReferenceColumn("table_name");
			conf.setIdColumn("id");
			conf.setNameHuman("name_human");
			conf.setDescription("description");
			
			guesser.init(conf);
			
			guesser.runGuesser(seriesName, column, conf);
			ArrayList<SingleResult> results = guesser.getClassification(); 
			CategoryGuesser.showResults(results);
			
			System.out.println("--------------------END BENCH 1-----------------------\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
