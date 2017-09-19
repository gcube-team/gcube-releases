package org.gcube.dataanalysis.lexicalmatcher.analysis.test.old;

import java.util.ArrayList;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;

public class TestSingleExternalCfgProduction {

	public static void main(String[] args) {

		try {
			
			
			String configPath = ".";
			CategoryGuesser guesser = new CategoryGuesser();
			//bench 1 
			LexicalLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String singleton = "Faroe Island";
//			String singleton = "Mitella pollicipes";
//			String singleton = "policipes";
//			String singleton = "";
//			String family = "rdf0a7fb500dd3d11e0b8d1d1e2e7ba4f9d";
			
			String family = "COUNTRY_OLD";
			String column = "field6";
			
			LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
			
			//CHANGE THIS TO ENHANCE THE RECALL
			conf.setEntryAcceptanceThreshold(30);
			conf.setReferenceChunksToTake(-1);
			conf.setTimeSeriesChunksToTake(-1);
			conf.setUseSimpleDistance(false);
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
			
			guesser.initSingleMatcher(conf,column );
			
			guesser.runGuesser(singleton, null, family,column );
			
			ArrayList<SingleResult> detailedResults = guesser.getDetailedMatches();
			
			LexicalLogger.getLogger().warn("Detailed Match on Name :"+singleton);
			
			CategoryGuesser.showResults(detailedResults);
			
			LexicalLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
