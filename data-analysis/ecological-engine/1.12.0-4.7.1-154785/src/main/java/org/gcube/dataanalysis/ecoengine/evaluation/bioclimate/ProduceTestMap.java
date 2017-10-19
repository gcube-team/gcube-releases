package org.gcube.dataanalysis.ecoengine.evaluation.bioclimate;

import org.gcube.dataanalysis.ecoengine.user.GeneratorT;
import org.gcube.dataanalysis.ecoengine.user.ModelerT;

public class ProduceTestMap {

	public static void main(String[] args) throws Exception {
		String configPath = "./cfg/";
		
		final String[] csquareTables = { "hcaf_d_2016_PARABOLIC", "hcaf_d_2020_PARABOLIC", "hcaf_d_2024_PARABOLIC", "hcaf_d_2028_PARABOLIC", "hcaf_d_2032_PARABOLIC", "hcaf_d_2036_PARABOLIC", "hcaf_d_2040_PARABOLIC", "hcaf_d_2044_PARABOLIC"};
		final String [] envelopeOutputTable = {"hspen_validation_1","hspen_validation_2","hspen_validation_3","hspen_validation_4","hspen_validation_5","hspen_validation_6","hspen_validation_7","hspen_validation_8"};
		final String [] finalDistributionTable = { "hspec_v_0", "hspec_v_1", "hspec_v_2", "hspec_v_3", "hspec_v_4", "hspec_v_5", "hspec_v_6", "hspec_v_7", "hspec_v_8"};
		
		/*
		String [] csquareTable = {"hcaf_d","hcaf_d_2050"};
		String [] envelopeOutputTable = {"hspen_micro_0","hspen_micro_2050"};
		String [] finalDistributionTable = {"hspec_0","hspec_2050"};
		*/
		
		String preprocessedTable = "maxminlat_hspen";
		String envelopeTable = "hspen_validation";
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
			ModelerT.train(ModelerT.getTrainingConfigHSPEN(modelName, envelopeOutputTable[i], occurrenceCells,envelopeTable,csquareTables[i],configPath));
			// Generate
			GeneratorT.generate(GeneratorT.getGenerationConfig(numberOfResources, generatorName, envelopeOutputTable[i], preprocessedTable, speciesCode, userName, csquareTables[i], finalDistributionTable[i], configPath));
		}
	}

}
