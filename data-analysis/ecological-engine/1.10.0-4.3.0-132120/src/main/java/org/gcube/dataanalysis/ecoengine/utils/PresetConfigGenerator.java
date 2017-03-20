package org.gcube.dataanalysis.ecoengine.utils;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class PresetConfigGenerator {

	public static AlgorithmConfiguration configAquamapsSuitable(String aquamapsSuitableTable, String envelopeTable) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_SUITABLE");
		config.setParam("DistributionTable", aquamapsSuitableTable);
		config.setParam("CsquarecodesTable", "hcaf_d");
		config.setParam("EnvelopeTable", envelopeTable);
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");
		config.setGcubeScope("/gcube");
		config.setParam("ServiceUserName", "gianpaolo.coro");

		return config;
	}

	public static AlgorithmConfiguration configAquamapsNative(String aquamapsNativeTable, String envelopeTable) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_NATIVE");
		config.setParam("DistributionTable", aquamapsNativeTable);
		config.setParam("CsquarecodesTable", "hcaf_d");
		config.setParam("EnvelopeTable", envelopeTable);
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");

		return config;
	}

	public static AlgorithmConfiguration configAquamapsNNSuitable(String tableName, String username, String envelopeTable, String speciesID, String nnname) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_SUITABLE_NEURALNETWORK");
		config.setParam("DistributionTable", tableName);
		config.setParam("CsquarecodesTable", "hcaf_d");
		config.setParam("EnvelopeTable", envelopeTable);
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");
		config.setParam("SpeciesName", speciesID);
		config.setParam("UserName", username);
		config.setParam("NeuralNetworkName", nnname);

		return config;
	}

	public static AlgorithmConfiguration configAquamapsNNNative(String tableName, String username, String envelopeTable, String speciesID, String nnname) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(5);
		config.setModel("AQUAMAPS_NATIVE_NEURALNETWORK");
		config.setParam("DistributionTable", tableName);
		config.setParam("CsquarecodesTable", "hcaf_d");
		config.setParam("EnvelopeTable", envelopeTable);
		config.setParam("OccurrencePointsTable", "occurrencecells");
		config.setParam("CreateTable", "true");
		config.setParam("SpeciesName", speciesID);
		config.setParam("UserName", username);
		config.setParam("NeuralNetworkName", nnname);
		return config;
	}

	public static AlgorithmConfiguration configSuitableNeuralNetworkTraining(String presenceTable, String absenceTable, String username, String speciesID, String neuronsAndLayers, String nnname) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(5);

		config.setModel("AQUAMAPSNN");
		config.setParam("AbsenceDataTable", absenceTable);
		config.setParam("PresenceDataTable", presenceTable);
		config.setParam("SpeciesName", speciesID);
		config.setParam("UserName", username);
		config.setParam("LayersNeurons", neuronsAndLayers);
		config.setParam("NeuralNetworkName", nnname);
		return config;
	}

	public static AlgorithmConfiguration configNativeNeuralNetworkTraining(String presenceTable, String absenceTable, String username, String speciesID, String neuronsAndLayers, String nnname) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(5);

		config.setModel("AQUAMAPSNN");
		config.setParam("AbsenceDataTable", absenceTable);
		config.setParam("PresenceDataTable", presenceTable);
		config.setParam("SpeciesName", speciesID);
		config.setParam("UserName", username);
		config.setParam("LayersNeurons", neuronsAndLayers);
		config.setParam("NeuralNetworkName", nnname);

		return config;
	}

	public static AlgorithmConfiguration configQualityAnalysis(String presenceTable, String absenceTable, String table) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("QUALITY_ANALYSIS");
		config.setParam("PositiveCasesTable", presenceTable);
		config.setParam("NegativeCasesTable", absenceTable);
		config.setParam("PositiveCasesTableKeyColumn", "csquarecode");
		config.setParam("NegativeCasesTableKeyColumn", "csquarecode");
		config.setParam("DistributionTable", table);
		config.setParam("DistributionTableKeyColumn", "csquarecode");
		config.setParam("DistributionTableProbabilityColumn", "probability");
		config.setParam("PositiveThreshold", "0.8");
		config.setParam("NegativeThreshold", "0.3");

		return config;
	}

	public static AlgorithmConfiguration configDiscrepancyAnalysis(String table1, String table2) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("DISCREPANCY_ANALYSIS");
		config.setParam("FirstTable", table1);
		config.setParam("SecondTable", table2);
		config.setParam("FirstTableCsquareColumn", "csquarecode");
		config.setParam("SecondTableCsquareColumn", "csquarecode");
		config.setParam("FirstTableProbabilityColumn", "probability");
		config.setParam("SecondTableProbabilityColumn", "probability");
		config.setParam("ComparisonThreshold", "0.1");

		return config;
	}

	public static AlgorithmConfiguration configHRSAnalysis(String projectiontable, String absenceTable, String presenceTable) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("HRS");
		config.setParam("ProjectingAreaTable", projectiontable);
		config.setParam("ProjectingAreaFeaturesOptionalCondition", "where oceanarea>0");
		config.setParam("FeaturesColumns", "depthmean" + AlgorithmConfiguration.getListSeparator() + "depthmax" + AlgorithmConfiguration.getListSeparator() + "depthmin" + AlgorithmConfiguration.getListSeparator() + " sstanmean" + AlgorithmConfiguration.getListSeparator() + "sbtanmean" + AlgorithmConfiguration.getListSeparator() + "salinitymean" + AlgorithmConfiguration.getListSeparator() + "salinitybmean" + AlgorithmConfiguration.getListSeparator() + " primprodmean" + AlgorithmConfiguration.getListSeparator() + "iceconann" + AlgorithmConfiguration.getListSeparator() + "landdist" + AlgorithmConfiguration.getListSeparator() + "oceanarea");
		config.setParam("PositiveCasesTable", presenceTable);
		config.setParam("NegativeCasesTable", absenceTable);

		return config;
	}

	public static AlgorithmConfiguration configAbsenceTable(boolean random, String absenceTable, String hspecTable, int numberOfPoints, String speciesCode) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("ABSENCE_CELLS_FROM_AQUAMAPS");

		config.setParam("RANDOM_TAKE", "" + random);
		config.setParam("FINAL_TABLE_NAME", absenceTable);
		config.setParam("AQUAMAPS_HSPEC", hspecTable);
		config.setParam("SPECIES_CODE", speciesCode);
		config.setParam("NUMBER_OF_POINTS", "" + numberOfPoints);

		return config;
	}

	public static AlgorithmConfiguration configPresenceTable(String presenceTable, int numberOfPoints, String speciesCode) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("PRESENCE_CELLS_GENERATION");
		config.setParam("Table_Label", presenceTable);
		config.setParam("Table_Name", presenceTable);
		config.setParam("Species_Code", speciesCode);
		config.setParam("Number_of_Points", "" + numberOfPoints);

		return config;
	}

	public static AlgorithmConfiguration configHCAFfilter(String table, float x1, float y1, float x2, float y2) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("HCAF_FILTER");

		config.setParam("FINAL_TABLE_NAME", table);

		config.setParam("BOUNDING_BOX_LEFT_LOWER_LONG", "" + x1);
		config.setParam("BOUNDING_BOX_RIGHT_UPPER_LONG", "" + x2);
		config.setParam("BOUNDING_BOX_LEFT_LOWER_LAT", "" + y1);
		config.setParam("BOUNDING_BOX_RIGHT_UPPER_LAT", "" + y2);

		return config;
	}

	public static AlgorithmConfiguration configHSPENfilter(String table, String speciesList) {

		AlgorithmConfiguration config = Regressor.getConfig();
		config.setNumberOfResources(1);
		config.setAgent("HSPEN_FILTER");

		config.setParam("FINAL_TABLE_NAME", table);
		config.setParam("SPECIES_CODES", speciesList);

		return config;
	}

}
