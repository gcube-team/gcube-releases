package org.gcube.dataanalysis.ecoengine.test.checks;

import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.InputTable;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.evaluation.DiscrepancyAnalysis;
import org.gcube.dataanalysis.ecoengine.evaluation.bioclimate.InterpolateTables.INTERPOLATIONFUNCTIONS;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.Evaluator;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class TestDBNEXTEvaluators {
	
public static void main(String[] args) throws Exception {
		
		System.out.println("TEST 1");
		
		/*
		trans = TransducerersFactory.getTransducerers(testConfigLocal());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		
		trans = TransducerersFactory.getTransducerers(testConfigLocal2());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		
		trans = TransducerersFactory.getTransducerers(testConfigLocal3());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		
		trans = TransducerersFactory.getTransducerers(testConfigLocal4());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		*/
		
		/*	
		trans = TransducerersFactory.getTransducerers(testConfigLocal8());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	
		trans = TransducerersFactory.getTransducerers(testConfigLocal6());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	*/
//		List<Evaluator> trans = null;
//		trans = EvaluatorsFactory.getEvaluators(testConfigLocal12());
		List<ComputationalAgent> trans = TransducerersFactory.getTransducerers(testConfigLocal5());
		trans.get(0).init();
		Regressor.process(trans.get(0));
		
//		PrimitiveType output = (PrimitiveType) trans.get(0).getOutput(); 
//		HashMap<String, String> out = (HashMap<String, String>)output.getContent();
//		DiscrepancyAnalysis.visualizeResults(out);
		
		StatisticalType output = trans.get(0).getOutput();
		
		
		trans = null;
		
}

	
	private static AlgorithmConfiguration testConfigLocal() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("BIOCLIMATE_HSPEC");
		config.setParam("HSPEC_TABLE_LIST", "hspec_validation"+AlgorithmConfiguration.getListSeparator()+"hspec_validation2");
		config.setParam("HSPEC_TABLE_NAMES", "test"+AlgorithmConfiguration.getListSeparator()+"test");
		config.setParam("Threshold", "0.5");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal2() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("BIOCLIMATE_HCAF");
		config.setParam("HCAF_TABLE_LIST","hcaf_d"+AlgorithmConfiguration.getListSeparator()+"hcaf_d_2016_linear_01332632269756"+AlgorithmConfiguration.getListSeparator()+"hcaf_d_2016_linear_01336062995861"+AlgorithmConfiguration.getListSeparator()+"hcaf_d_2050");
		config.setParam("HCAF_TABLE_NAMES", 	"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test");

		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal3() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("BIOCLIMATE_HSPEN");
		config.setParam("HSPEN_TABLE_LIST","hspen"+AlgorithmConfiguration.getListSeparator()+"hspen_2016"+AlgorithmConfiguration.getListSeparator()+"hspen_2020"+AlgorithmConfiguration.getListSeparator()+"hspen_2050");
		config.setParam("HSPEN_TABLE_NAMES", 	"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test"+AlgorithmConfiguration.getListSeparator()+"test");

		return config;
	}
	
	
	private static AlgorithmConfiguration testConfigLocal4() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("HCAF_INTERPOLATION");
		
		config.setParam("FirstHCAF","hcaf_d");
		config.setParam("SecondHCAF","hcaf_d_2050");
		config.setParam("YearStart","2012");
		config.setParam("YearEnd","2050");
		config.setParam("NumberOfInterpolations","2");
		config.setParam("InterpolationFunction",INTERPOLATIONFUNCTIONS.LINEAR.name());
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal5() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("OCCURRENCES_MERGER");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("recordedByColumn", "recordedby");
		config.setParam("scientificNameColumn", "scientificname");
		config.setParam("eventDateColumn", "eventdate");
		config.setParam("lastModificationColumn", "modified");
//		config.setParam("leftTableName", "speciesset1");
//		config.setParam("rightTableName", "speciesset2");
		config.setParam("leftTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
		config.setParam("rightTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
//		config.setParam("leftTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
//		config.setParam("rightTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		config.setParam("leftTableName", "occurrencesdeleted");
		config.setParam("rightTableName", "occurrencesdeleted");
		
		config.setParam("finalTableName", "occurrencesmerged");
		config.setParam("spatialTolerance", "0");
		config.setParam("confidence", "100");
		
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal5b() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("OCCURRENCES_SUBTRACTION");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("recordedByColumn", "recordedby");
		config.setParam("scientificNameColumn", "scientificname");
		config.setParam("eventDateColumn", "eventdate");
		config.setParam("lastModificationColumn", "modified");
	
//		config.setParam("rightTableName", "occurrence_species2");
//		config.setParam("leftTableName", "occurrence_species1");
	
		/*
		config.setParam("rightTableName", "occurrence_species_id1e8f7b48_b99a_48a3_8b52_89976fd79cd4");
		config.setParam("leftTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		*/
		//"processedoccurrences_id_e7b77fc2_f1cf_4a46_b7b7_898b663b65dd" OBIS
		//"processedoccurrences_id_bd3fdae3_a64e_4215_8eb3_c1bd95981dd2" GBIF
		
//		config.setParam("leftTableName", "processedoccurrences_id_e7b77fc2_f1cf_4a46_b7b7_898b663b65dd");
//		config.setParam("rightTableName", "processedoccurrences_id_bd3fdae3_a64e_4215_8eb3_c1bd95981dd2");
		config.setParam("leftTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
		config.setParam("rightTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
		
		config.setParam("leftTableName", "occurrencesdeleted");
		config.setParam("rightTableName", "occurrencesdeleted");
		
//		config.setParam("leftTableName", "speciesset1");
//		config.setParam("rightTableName", "speciesset2");
		
		config.setParam("finalTableName", "occurrencessubtractedarticle3");
		config.setParam("spatialTolerance", "0");
		config.setParam("confidence", "100");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}

	
	private static AlgorithmConfiguration testConfigLocal5c() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("OCCURRENCES_INTERSECTOR");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("recordedByColumn", "recordedby");
		config.setParam("scientificNameColumn", "scientificname");
		config.setParam("eventDateColumn", "eventdate");
		config.setParam("lastModificationColumn", "modified");
		/*
		config.setParam("rightTableName", "occurrence_species2");
		config.setParam("leftTableName", "occurrence_species1");
		*/
		/*
		config.setParam("rightTableName", "occurrence_species_id1e8f7b48_b99a_48a3_8b52_89976fd79cd4");
		config.setParam("leftTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		*/
//		config.setParam("leftTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
//		config.setParam("rightTableName", "occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893");
		
//		config.setParam("leftTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
//		config.setParam("rightTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
		
		config.setParam("leftTableName", "occurrencesdeleted");
		config.setParam("rightTableName", "occurrencesdeleted");
		
//		config.setParam("leftTableName", "speciesset1");
//		config.setParam("rightTableName", "speciesset2");
		
		config.setParam("finalTableName", "occurrencesintersected");
		config.setParam("spatialTolerance", "0");
		config.setParam("confidence", "100");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}

	
	private static AlgorithmConfiguration testConfigLocal5d() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("OCCURRENCES_DUPLICATES_DELETER");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("recordedByColumn", "recordedby");
		config.setParam("scientificNameColumn", "scientificname");
		config.setParam("eventDateColumn", "eventdate");
		config.setParam("lastModificationColumn", "modified");
		config.setParam("OccurrencePointsTableName", "occurrence_species_idb1a80ed3_0b07_4481_b8f0_95b821b2d4c9");
		
		config.setParam("finalTableName", "occurrencesdeleted");
		config.setParam("spatialTolerance", "0");
		config.setParam("confidence", "100");
		
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		
		return config;
	}
	private static AlgorithmConfiguration testConfigLocal6() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		config.setAgent("OCCURRENCES_INSEAS_ONEARTH");
		
		config.setParam("longitudeColumn", "decimallongitude");
		config.setParam("latitudeColumn", "decimallatitude");
		config.setParam("OccurrencePointsTableName", "whitesharkoccurrences2");
		config.setParam("finalTableName", "whitesharkoccurrencesfilteredseas");
		config.setParam("FilterType", "IN_THE_WATER");
//		config.setParam("FilterType", "ON_EARTH");
		
		return config;
	}

	private static AlgorithmConfiguration testConfigLocal7() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		
		config.setNumberOfResources(1);
		config.setAgent("QUALITY_ANALYSIS");
		config.setParam("PositiveCasesTable","presence_hcafid_317683c4_3474_4648_ba5b_9cd832db73cf");
		config.setParam("NegativeCasesTable","absence_hcafid_c4bb5bae_b875_4753_974b_c4b3ad616a24");
		config.setParam("PositiveCasesTableKeyColumn","csquarecode");
		config.setParam("NegativeCasesTableKeyColumn","csquarecode");
		config.setParam("DistributionTable","hspec_id_4c34b12e_8606_4134_9ce7_341137d8a323");
		config.setParam("DistributionTableKeyColumn","csquarecode");
		config.setParam("DistributionTableProbabilityColumn","probability");
		config.setParam("PositiveThreshold","0.8");
		config.setParam("NegativeThreshold","0.3");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}

	private static AlgorithmConfiguration testConfigLocal11() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		
		config.setNumberOfResources(1);
		config.setAgent("HRS");
		
		config.setParam("ProjectingAreaTable","hspen_filteredid_16d7291d_7b11_48f3_af3a_1b2eef38efee");
		config.setParam("OptionalCondition","where oceanarea>0");
		config.setParam("PositiveCasesTable","presence_hcafid_317683c4_3474_4648_ba5b_9cd832db73cf");
		config.setParam("NegativeCasesTable","absence_hcafid_c4bb5bae_b875_4753_974b_c4b3ad616a24");
		config.setParam("FeaturesColumns",	"depthmean"+AlgorithmConfiguration.getListSeparator()+"depthmax"+AlgorithmConfiguration.getListSeparator()+"depthmin"+AlgorithmConfiguration.getListSeparator()+" sstanmean"+AlgorithmConfiguration.getListSeparator()+"sbtanmean"+AlgorithmConfiguration.getListSeparator()+"salinitymean"+AlgorithmConfiguration.getListSeparator()+"salinitybmean"+AlgorithmConfiguration.getListSeparator()+" primprodmean"+AlgorithmConfiguration.getListSeparator()+"iceconann"+AlgorithmConfiguration.getListSeparator()+"landdist"+AlgorithmConfiguration.getListSeparator()+"oceanarea");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}
	
	private static AlgorithmConfiguration testConfigLocal12() {
		
		AlgorithmConfiguration config = Regressor.getConfig();
		
		config.setNumberOfResources(1);
		config.setAgent("DISCREPANCY_ANALYSIS");
		
//		config.setParam("FirstTable", "hspec_id_686c508c_b64f_4ef9_9452_465465edbece");
		config.setParam("FirstTable", "hspec_id_bf4bb271_ed21_42cb_a2be_503979789055");
		config.setParam("SecondTable", "hspec_id_bf4bb271_ed21_42cb_a2be_503979789055");
		
		config.setParam("FirstTableCsquareColumn", "csquarecode");
		config.setParam("SecondTableCsquareColumn", "csquarecode");
		config.setParam("FirstTableProbabilityColumn", "probability");
		config.setParam("SecondTableProbabilityColumn", "probability");
		config.setParam("ComparisonThreshold", "0.1");
		
		config.setParam("DatabaseUserName","utente");
		config.setParam("DatabasePassword","d4science");
		config.setParam("DatabaseURL","jdbc:postgresql://dbtest.next.research-infrastructures.eu/testdb");
		config.setParam("DatabaseDriver","org.postgresql.Driver");
		
		return config;
	}

}
