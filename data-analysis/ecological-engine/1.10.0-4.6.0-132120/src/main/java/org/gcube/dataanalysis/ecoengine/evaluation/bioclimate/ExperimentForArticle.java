package org.gcube.dataanalysis.ecoengine.evaluation.bioclimate;

import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.ecoengine.user.ModelerT;

public class ExperimentForArticle {

	public static void main(String[] args) throws Exception {
		String configPath = "./cfg/";
		
		final String[] csquareTables = {
				"hcaf_d_2016_linear_01332632269756",	
				"hcaf_d_2020_linear_11332632270082",	
				"hcaf_d_2024_linear_21332632270343",	
				"hcaf_d_2028_linear_31332632270608",	
				"hcaf_d_2032_linear_41332632270847",	
				"hcaf_d_2036_linear_51332632271080",	
				"hcaf_d_2040_linear_61332632271334",	
				"hcaf_d_2044_linear_71332632271560",	
				"hcaf_d_2050"
				};
		final String [] envelopeOutputTable = {"hspen_2016","hspen_2020","hspen_2024","hspen_2028","hspen_2032","hspen_2036","hspen_2040","hspen_2044","hspen_2050"};
		
		final String [] finalDistributionTable = { "hspec_2016", "hspec_2020", "hspec_2024", "hspec_2028", "hspec_2032", "hspec_2036", "hspec_2040", "hspec_2044", "hspec_2050"};
		
		/*
		String [] csquareTable = {"hcaf_d","hcaf_d_2050"};
		String [] envelopeOutputTable = {"hspen_micro_0","hspen_micro_2050"};
		String [] finalDistributionTable = {"hspec_0","hspec_2050"};
		*/
		
		String preprocessedTable = "maxminlat_hspen";
		String envelopeTable = "hspen";
		String occurrenceCells = "occurrenceCells";
		
		String commonkeycolumn = "csquarecode";
		String probabilitycolumn = "probability";
		
		int numberOfResources = 4;

		String speciesCode = "Fis-10199";
		String userName = "gianpaolo.coro";
		String modelName = "HSPEN";
		String generatorName = "AQUAMAPS_SUITABLE";
		String qualityOperationName = "QUALITY_ANALYSIS";
		String discrepancyOperationName = "DISCREPANCY_ANALYSIS";
		String finalDistributionKeyColumn = "csquarecode";

		for (int i =0;i<csquareTables.length;i++){
			// Train
			System.out.println("Generating-> "+ envelopeOutputTable[i]);
			ModelerT.train(ModelerT.getTrainingConfigHSPEN(modelName, envelopeOutputTable[i], occurrenceCells,envelopeTable,csquareTables[i],configPath));
			// Generate
//			GeneratorT.generate(GeneratorT.getGenerationConfig(numberOfResources, generatorName, envelopeOutputTable[i], preprocessedTable, speciesCode, userName, csquareTables[i], finalDistributionTable[i], configPath));
		}
		System.out.println("COMPUTATION FINISHED!");
	}

}
