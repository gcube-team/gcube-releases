package org.gcube.dataanalysis.ecoengine.test.checks;

import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;

public class TestHSPECBioClimateAnalysisProd {
	

	public static void main(String args[]) throws Exception{
		
		String dburl = "jdbc:postgresql://node49.p.d4science.research-infrastructures.eu/aquamaps";
		String dbUser = "gcube";
		String dbPassword = "bilico1980";
		
		/*
		String dburl = "jdbc:postgresql://dbtest.research-infrastructures.eu/aquamapsorgupdated";
		String dbUser = "utente";
		String dbPassword = "d4science";
		*/
		
		BioClimateAnalysis bioClimate=new BioClimateAnalysis("./cfg/","./",dburl, dbUser, dbPassword, true);
		
		
		final String [] hspecTables = {
				 "hspec2012_07_02_17_14_10_063",
				 "hspec2012_07_05_21_47_13_772",
				 "hspec2012_07_05_21_47_13_801"
//				 "hspec2012_07_05_21_47_13_819",
//				 "hspec2012_07_05_21_47_13_842",
//				 "hspec2012_07_05_21_47_13_860",
//				 "hspec2012_07_05_21_47_13_888",
//				 "hspec2012_07_05_21_47_13_903",
//				 "hspec2012_07_05_21_47_13_917",
//				 "hspec2012_07_06_13_05_11_775"
		};
		
		final String [] hspecTableNames = {
				"HSPEC 2015 Suitable Parabolic ",
				"HSPEC 2018 Suitable Parabolic",
				"HSPEC 2021 Suitable Parabolic",
				"HSPEC 2024 Suitable Parabolic",
				"HSPEC 2027 Suitable Parabolic",
				"HSPEC 2030 Suitable Parabolic",
				"HSPEC 2033 Suitable Parabolic",
				"HSPEC 2036 Suitable Parabolic",
				"HSPEC 2039 Suitable Parabolic",
				"HSPEC 2042 Suitable Parabolic",
				};
		
		
		bioClimate.globalEvolutionAnalysis(null, hspecTables, null, hspecTableNames, "probability", "csquare", 0.8f);
//		bioClimate.speciesGeographicEvolutionAnalysis(hspecTables, hspecTableNames,0.8f);
		
	}
	
	
	
}
