package org.gcube.dataanalysis.lexicalmatcher.analysis.test;

import java.util.ArrayList;
import java.util.List;

import org.gcube.dataanalysis.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.Category;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.SingleResult;
import org.gcube.dataanalysis.lexicalmatcher.analysis.run.CategoryGuesser;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;

public class TestSingleExternalCfgProduction {

	public static void main(String[] args) {

		try {
			
			CategoryGuesser guesser = new CategoryGuesser();
			
			//bench 1 
			LexicalLogger.getLogger().warn("----------------------BENCH 1-------------------------");
			String singleton = "Faroe Island";

			String family = "COUNTRY_OLD";
			String column = "field6";
			
			LexicalEngineConfiguration conf = new LexicalEngineConfiguration();
			/*
			conf.setReferenceTable("codelist1733371938");
			conf.setReferenceColumn("ifield14");
			conf.setNameHuman("ifield1");
			conf.setIdColumn("ifield0");
			conf.setDescription("ifield2");
			*/
			
			ArrayList<Category> categories = new ArrayList<Category>();
			//human name, index, table name, description
			categories.add(new Category("COUNTRY_OLD","39c98800-dd3c-11e0-b8d1-d1e2e7ba4f9d","rdf39c98800dd3c11e0b8d1d1e2e7ba4f9d","country"));
			categories.add(new Category("CONTINENT_OLD","1d5d51f0-dd42-11e0-b8d3-d1e2e7ba4f9d","rdf1d5d51f0dd4211e0b8d3d1e2e7ba4f9d","continent reference data"));
			categories.add(new Category("SPECIES_OLD","0a7fb500-dd3d-11e0-b8d1-d1e2e7ba4f9d","rdf0a7fb500dd3d11e0b8d1d1e2e7ba4f9d","species"));
			categories.add(new Category("CodeListCountry","4c8d93a0-edc2-11e0-93e4-f6a9821baa29","rdf4c8d93a0edc211e093e4f6a9821baa29","Country"));
			categories.add(new Category("CL_DIVISION","1140bdf0-dd2c-11e0-9220-ae17b3db32b7","rdf1140bdf0dd2c11e09220ae17b3db32b7","undefined"));
			
			conf.setCategories(categories);
			
			//CHANGE THIS TO ENHANCE THE RECALL
			conf.setEntryAcceptanceThreshold(30);
			conf.setReferenceChunksToTake(-1);
			conf.setTimeSeriesChunksToTake(-1);
			conf.setUseSimpleDistance(false);
			
			//database Parameters
			conf.setDatabaseUserName("gcube");
			conf.setDatabasePassword("d4science2");
//			conf.setDatabaseDriver("org.postgresql.Driver");
			conf.setDatabaseURL("jdbc:postgresql://localhost/testdb");
			conf.setDatabaseDialect("org.hibernate.dialect.PostgreSQLDialect");
			
			guesser.runGuesser(singleton, conf, family,column );
			
			ArrayList<SingleResult> detailedResults = guesser.getDetailedMatches();
			
			LexicalLogger.getLogger().warn("Detailed Match on Name :"+singleton);
			
			CategoryGuesser.showResults(detailedResults);
			
			LexicalLogger.getLogger().warn("--------------------END BENCH 1-----------------------\n");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
