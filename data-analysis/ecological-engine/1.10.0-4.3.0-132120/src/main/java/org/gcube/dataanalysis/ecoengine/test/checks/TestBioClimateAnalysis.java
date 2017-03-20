package org.gcube.dataanalysis.ecoengine.test.checks;

import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.BioClimateAnalysis;

public class TestBioClimateAnalysis {
	

	public static void main(String args[]) throws Exception{
		
		String dburl = "jdbc:postgresql://node49.p.d4science.research-infrastructures.eu/aquamaps";
		String dbUser = "gcube";
		String dbPassword = "bilico1980";
		
		BioClimateAnalysis bioClimate=new BioClimateAnalysis("./cfg/","./",dburl, dbUser, dbPassword, true);

		final String [] hcafTable = {
		"hcaf_d",
		"hcaf_d_2015_LINEAR_01338580273835",
		"hcaf_d_2018_LINEAR_11338580276548",
		"hcaf_d_2021_LINEAR_21338580279237",
		"hcaf_d_2024_LINEAR_31338580282780",
		"hcaf_d_2027_LINEAR_41338580283400",
		"hcaf_d_2030_LINEAR_51338580284030",
		"hcaf_d_2033_LINEAR_61338580284663",
		"hcaf_d_2036_LINEAR_71338580285205",
		"hcaf_d_2039_LINEAR_81338580285958",
		"hcaf_d_2042_LINEAR_91338580286545",
		"hcaf_d_2050"
		};
		
		final String [] hcafTableNames = {
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test"
				};
		
		
		final String [] hspecTables = {
				"hspec2012_06_01_14_28_21_588",
				"hspec2012_05_31_17_41_13_631"
				};
				
		final String [] hspecTableNames = {
				"htest",
				"htest",
				};
		
//		bioClimate.hcafEvolutionAnalysis(hcafTable, hcafTableNames);
//		bioClimate.globalEvolutionAnalysis(hcafTable, hspecTables, hcafTableNames, hspecTableNames, "probability", "csquare", 0.01f);
		bioClimate.globalEvolutionAnalysis(null, hspecTables, null, hspecTableNames, "probability", "csquare", 0.01f);
	}
	
	
	public static void main1(String args[]) throws Exception{
		
		String dburl = "jdbc:postgresql://node49.p.d4science.research-infrastructures.eu/aquamaps";
		String dbUser = "gcube";
		String dbPassword = "bilico1980";
		
		BioClimateAnalysis bioClimate=new BioClimateAnalysis("./cfg/","./",dburl, dbUser, dbPassword, true);
		
		
		
		final String [] envelopeTables = {
		"hspen2012_06_01_21_52_47_460",
		"hspen2012_06_01_21_52_47_485",
		"hspen2012_06_01_21_52_47_615",
		"hspen2012_06_01_21_52_46_795",
		"hspen2012_06_02_03_26_13_154",
		"hspen2012_06_02_03_26_16_534",
		"hspen2012_06_02_03_26_43_412",
		"hspen2012_06_02_03_27_26_762",
		"hspen2012_06_02_08_54_48_004",
		"hspen2012_06_02_08_55_53_415"
		};
		/*
		final String [] envelopeTablesNames = {
				"hspen2012_06_01_21_52_47_460",
				"hspen2012_06_01_21_52_47_485",
				"hspen2012_06_01_21_52_47_615",
				"hspen2012_06_01_21_52_46_795",
				"hspen2012_06_02_03_26_13_154",
				"hspen2012_06_02_03_26_16_534",
				"hspen2012_06_02_03_26_43_412",
				"hspen2012_06_02_03_27_26_762",
				"hspen2012_06_02_08_54_48_004",
				"hspen2012_06_02_08_55_53_415"
				};
		*/
		
		final String [] envelopeTablesNames = {
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test",
				"test"
				};
		
		
		bioClimate.speciesEvolutionAnalysis(envelopeTables,envelopeTablesNames, BioClimateAnalysis.salinityMinFeature, BioClimateAnalysis.salinityDefaultRange);
		
	}
}
