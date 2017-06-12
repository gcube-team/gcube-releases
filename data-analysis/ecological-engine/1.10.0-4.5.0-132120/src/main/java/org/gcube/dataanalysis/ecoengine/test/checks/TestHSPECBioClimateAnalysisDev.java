package org.gcube.dataanalysis.ecoengine.test.checks;

import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;

public class TestHSPECBioClimateAnalysisDev {
	

	public static void main(String args[]) throws Exception{
		/*
		String dburl = "jdbc:postgresql://node49.p.d4science.research-infrastructures.eu/aquamaps";
		String dbUser = "gcube";
		String dbPassword = "bilico1980";
		*/
		
		String dburl = "jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated";
		String dbUser = "utente";
		String dbPassword = "d4science";
		
		BioClimateAnalysis bioClimate=new BioClimateAnalysis("./cfg/","./",dburl, dbUser, dbPassword, true);
		
		/*
		final String [] hspecTables = {
				"hspec2012_07_05_21_47_13_772",
"hspec2012_07_05_21_47_13_801",
"hspec2012_07_05_21_47_13_819",
"hspec2012_07_05_21_47_13_842",
"hspec2012_07_05_21_47_13_860",
"hspec2012_07_05_21_47_13_888",
"hspec2012_07_05_21_47_13_903",
"hspec2012_07_05_21_47_13_917"
		};
*/
		final String [] hspecTables = {
		"hspec2012_03_12_12_13_14_610",
		"hspec2012_03_12_15_07_50_820",
		"hspec2012_03_12_18_07_21_503",
		"hspec2012_03_12_23_59_57_744",
		"hspec2012_03_13_02_50_59_399",
		"hspec2012_03_13_10_22_31_865"
		};
		
		final String [] hspecTableNames = {
				"T1",
				"T2",
				"T3",
				"T4",
				"T5",
				"T6",
				"T7",
				"T8"
				};
		
		
//		bioClimate.globalEvolutionAnalysis(null, hspecTables, null, hspecTableNames, "probability", "csquare", 0.8f);
		bioClimate.speciesGeographicEvolutionAnalysis(hspecTables, hspecTableNames,0.8f);
		
	}
	
	
	
}
