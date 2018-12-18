package org.gcube.dataanalysis.ecoengine.test.experiments.latimeria;

import java.util.HashMap;
import java.util.List;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.evaluation.DiscrepancyAnalysis;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.test.PresetConfigGenerator;
import org.gcube.dataanalysis.ecoengine.test.regression.Regressor;

public class ExperimentsForLatimeria {

	
	static String absenceRandomTable = "absence_data_latimeria_random";
	static String absenceStaticTable = "absence_data_latimeria";
	static String presenceTable = "presence_data_latimeria_2";
	static String presenceTableNoEarth = "presence_data_latimeria_sea";
	static String envelopeTable = "hspen_latimeria";
	
	static String aquamapsSuitableTable = "hspec_suitable_latimeria_chalumnae";
	static String aquamapsNativeTable = "hspec_native_latimeria_chalumnae";
	static String nnsuitableTable = "hspec_suitable_neural_latimeria_chalumnae";
	static String nnsuitableRandomTable = "hspec_suitable_neural_latimeria_chalumnae_random";
	static String nnnativeTable = "hspec_native_neural_latimeria_chalumnae";
	static String nnnativeRandomTable = "hspec_native_neural_latimeria_chalumnae_random";
	static String hcaf= "hcaf_d";
	static String filteredhcaf= "bboxed_hcaf_d";
	
	static String speciesID = "Fis-30189";
	static String staticsuitable = "staticsuitable";
	static String randomsuitable = "randomsuitable";
	static String staticnative = "staticnative";
	static String randomnative = "randomnative";
	static int numberOfPoints = 34;
	
	static String nnname = "neuralname";
	static float x1 = 95.346678f;
	static float y1 = -9.18887f;
	static float x2 = 125.668944f;
	static float y2 = 12.983148f;

	public static void generateHSPENTable() throws Exception{
		System.out.println("*****************************HSPEN FILTER**********************************");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(PresetConfigGenerator.configHSPENfilter(envelopeTable, speciesID));
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	}
	
	public static void generateAquamapsNativeSuitable() throws Exception{
		List<ComputationalAgent> generators = null;
		System.out.println("*****************************AQUAMAPS SUITABLE**********************************");
		generators = GeneratorsFactory.getGenerators(PresetConfigGenerator.configAquamapsSuitable(aquamapsSuitableTable,envelopeTable));
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		
		System.out.println("*****************************AQUAMAPS NATIVE**********************************");
		generators = GeneratorsFactory.getGenerators(PresetConfigGenerator.configAquamapsNative(aquamapsNativeTable,envelopeTable));
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		
	}
	
	public static void generatePresenceTable() throws Exception{
		System.out.println("*****************************PRESENCE TABLE**********************************");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(PresetConfigGenerator.configPresenceTable(presenceTable, -1, speciesID));
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
		
	}
	
	public static void generateRandomAbsenceTable() throws Exception{
		System.out.println("*****************************RANDOM ABSENCES**********************************");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(PresetConfigGenerator.configAbsenceTable(true, absenceRandomTable, aquamapsNativeTable, numberOfPoints, speciesID));
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	}

	public static void generateAbsenceTable() throws Exception{
		System.out.println("*****************************ABSENCES**********************************");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(PresetConfigGenerator.configAbsenceTable(false, absenceStaticTable, aquamapsNativeTable, numberOfPoints, speciesID));
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	}

	public static void trainNeuralNetworks() throws Exception{
		
		List<ComputationalAgent> modelers = null;
		System.out.println("*****************************TRAINING NN SUITABLE WITH STATIC ABSENCES**********************************");
		modelers = ModelersFactory.getModelers(PresetConfigGenerator.configSuitableNeuralNetworkTraining(presenceTable,absenceStaticTable,staticsuitable,speciesID,"100"+AlgorithmConfiguration.getListSeparator()+"2",nnname));
		modelers.get(0).init();
		Regressor.process(modelers.get(0));
		modelers = null;
		/*
		System.out.println("*****************************TRAINING NN SUITABLE WITH RANDOM ABSENCES**********************************");
		modelers = ModelersFactory.getModelers(PresetConfigGenerator.configSuitableNeuralNetworkTraining(presenceTable,absenceRandomTable,staticsuitable,speciesID,"100"+AlgorithmConfiguration.getListSeparator()+"2"));
		modelers.get(0).init();
		Regressor.process(modelers.get(0));
		modelers = null;
		*/
		
		System.out.println("*****************************TRAINING NN NATIVE WITH STATIC ABSENCES**********************************");
		modelers = ModelersFactory.getModelers(PresetConfigGenerator.configNativeNeuralNetworkTraining(presenceTable,absenceStaticTable,staticnative,speciesID,"100",nnname));
		modelers.get(0).init();
		Regressor.process(modelers.get(0));
		modelers = null;
		/*
		System.out.println("*****************************TRAINING NN NATIVE WITH RANDOM ABSENCES**********************************");
		modelers = ModelersFactory.getModelers(PresetConfigGenerator.configNativeNeuralNetworkTraining(presenceTable,absenceRandomTable,staticsuitable,speciesID,"100"));
		modelers.get(0).init();
		Regressor.process(modelers.get(0));
		modelers = null;
		*/
	}

	public static void trainSuitableNeuralNetworks() throws Exception{
		
		List<ComputationalAgent> modelers = null;
		System.out.println("*****************************TRAINING NN SUITABLE WITH STATIC ABSENCES**********************************");
		modelers = ModelersFactory.getModelers(PresetConfigGenerator.configSuitableNeuralNetworkTraining(presenceTable,absenceStaticTable,staticsuitable,speciesID,"100"+AlgorithmConfiguration.getListSeparator()+"2",nnname));
		modelers.get(0).init();
		Regressor.process(modelers.get(0));
		modelers = null;
	}
	
	public static void trainNativeNeuralNetworks() throws Exception{
	
		System.out.println("*****************************TRAINING NN NATIVE WITH STATIC ABSENCES**********************************");
		List<ComputationalAgent> modelers = null;
		modelers = ModelersFactory.getModelers(PresetConfigGenerator.configNativeNeuralNetworkTraining(presenceTable,absenceStaticTable,staticnative,speciesID,"100"+AlgorithmConfiguration.getListSeparator()+"2",nnname));
		modelers.get(0).init();
		Regressor.process(modelers.get(0));
		modelers = null;
		
	}

	
	public static void generateAquamapsNativeSuitableNeuralNetwokrs() throws Exception{
		List<ComputationalAgent> generators = null;
		System.out.println("*****************************GENERATING NN SUITABLE WITH STATIC ABSENCES**********************************");
		generators = GeneratorsFactory.getGenerators(PresetConfigGenerator.configAquamapsNNSuitable(nnsuitableTable,staticsuitable,envelopeTable,speciesID,nnname));
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		
		/*
		System.out.println("*****************************GENERATING NN SUITABLE WITH RANDOM ABSENCES**********************************");
		generators = GeneratorsFactory.getGenerators(PresetConfigGenerator.configAquamapsNNSuitable(nnsuitableRandomTable,randomsuitable,envelopeTable,speciesID));
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		*/
		
		System.out.println("*****************************GENERATING NN NATIVE WITH STATIC ABSENCES**********************************");
		generators = GeneratorsFactory.getGenerators(PresetConfigGenerator.configAquamapsNNNative(nnnativeTable,staticnative,envelopeTable,speciesID,nnname));
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		/*
		System.out.println("*****************************GENERATING NN NATIVE WITH RANDOM ABSENCES**********************************");
		generators = GeneratorsFactory.getGenerators(PresetConfigGenerator.configAquamapsNNNative(nnnativeRandomTable,randomnative,envelopeTable,speciesID));
		generators.get(0).init();
		Regressor.process(generators.get(0));
		generators = null;
		*/
	}
	
	public static void calcdiscrepancy(String table1,String table2) throws Exception{
		System.out.println("*****************************DISCREPANCY: "+table1+" vs "+table2+"************************************");
		List<ComputationalAgent>  evaluators = null;
		evaluators = EvaluatorsFactory.getEvaluators(PresetConfigGenerator.configDiscrepancyAnalysis(table1, table2));
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		PrimitiveType output = (PrimitiveType) evaluators.get(0).getOutput(); 
		HashMap<String, Object> out = (HashMap<String, Object>)output.getContent();
		DiscrepancyAnalysis.visualizeResults(out);
		evaluators = null;
	}
	
	public static void calcquality(String table,String presenceTable, String absenceTable) throws Exception{
		System.out.println("*****************************QUALITY: "+table+" vs "+presenceTable+" and "+absenceTable+"************************************");
		List<ComputationalAgent>  evaluators = null;
		evaluators = EvaluatorsFactory.getEvaluators(PresetConfigGenerator.configQualityAnalysis(presenceTable,absenceTable,table));
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		PrimitiveType output = (PrimitiveType) evaluators.get(0).getOutput(); 
		HashMap<String, Object> out = (HashMap<String, Object>)output.getContent();
		DiscrepancyAnalysis.visualizeResults(out);
		evaluators = null;
	}
	
	
	public static void generateHCAFFilter() throws Exception{
		System.out.println("*****************************HCAF FILTER ON : "+x1+","+y1+","+x2+","+y2+"************************************");
		List<ComputationalAgent> trans = null;
		trans = TransducerersFactory.getTransducerers(PresetConfigGenerator.configHCAFfilter(filteredhcaf, x1, y1, x2, y2));
		trans.get(0).init();
		Regressor.process(trans.get(0));
		trans = null;
	}
	
	public static void calcHRS(String hcaf,String absenceTable,String presenceTable) throws Exception{
		System.out.println("*****************************HRS: "+absenceTable+","+presenceTable+" vs "+hcaf+"************************************");
		List<ComputationalAgent>  evaluators = null;
		evaluators = EvaluatorsFactory.getEvaluators(PresetConfigGenerator.configHRSAnalysis(filteredhcaf, absenceTable, presenceTable));
		evaluators.get(0).init();
		Regressor.process(evaluators.get(0));
		PrimitiveType output = (PrimitiveType) evaluators.get(0).getOutput(); 
		HashMap<String, Object> out = (HashMap<String, Object>)output.getContent();
		DiscrepancyAnalysis.visualizeResults(out);
		evaluators = null;
	}
	
	public static void main1(String[] args) throws Exception{
		//generate hspen table for the species
		generateHSPENTable();
	
		//generate Aquamaps distributions
		generateAquamapsNativeSuitable();
			
		//generate presence and absence hcafs
		generatePresenceTable();
		generateAbsenceTable();
		generateHCAFFilter();
		
//		generateRandomAbsenceTable(); NOTE: in this case the randoms are equal to the static
		
		//train the neural networks on these tables
		trainNeuralNetworks();
		//project the neural networks
		generateAquamapsNativeSuitableNeuralNetwokrs();
		
		//Analysis
		calcdiscrepancy(aquamapsSuitableTable, nnsuitableTable);
//		calcdiscrepancy(aquamapsSuitableTable, nnsuitableRandomTable);
		calcdiscrepancy(aquamapsNativeTable, nnnativeTable);
//		calcdiscrepancy(aquamapsNativeTable, nnnativeRandomTable);
		
		calcquality(aquamapsSuitableTable, presenceTable, absenceStaticTable);
//		calcquality(aquamapsSuitableTable, presenceTable, absenceRandomTable);
		
		calcquality(nnsuitableTable, presenceTable, absenceStaticTable);
//		calcquality(nnsuitableTable, presenceTable, absenceRandomTable);
		
		calcquality(nnsuitableRandomTable, presenceTable, absenceStaticTable);
//		calcquality(nnsuitableRandomTable, presenceTable, absenceRandomTable);

		//filter the hcaf on Indonesia
		
//		calcHRS(filteredhcaf,absenceStaticTable);
//		calcHRS(filteredhcaf,absenceRandomTable);
	}
	
	
	public static void main(String[] args) throws Exception{
		//generate hspen table for the species
//		generateHSPENTable();
		//generate Aquamaps distributions
//		generateAquamapsNativeSuitable();
	
		//generate presence and absence hcafs
//		generatePresenceTable();
//		generateAbsenceTable();
//		generateHCAFFilter();
		//train the neural networks on these tables
//		trainSuitableNeuralNetworks();
		trainNativeNeuralNetworks();
		//project the neural networks
//		generateAquamapsNativeSuitableNeuralNetwokrs();
		//Analysis

		/*
		calcdiscrepancy(aquamapsSuitableTable, nnsuitableTable);
		calcdiscrepancy(aquamapsNativeTable, nnnativeTable);
	
		calcdiscrepancy(aquamapsSuitableTable, nnsuitableTable);
		calcdiscrepancy(aquamapsNativeTable, nnnativeTable);
		*/
		/*
		calcquality(aquamapsSuitableTable, presenceTableNoEarth, absenceStaticTable);
		calcquality(nnsuitableTable, presenceTableNoEarth, absenceStaticTable);
		*/
//		calcquality(aquamapsSuitableTable, presenceTableNoEarth, absenceStaticTable);
//		calcquality(aquamapsNativeTable, presenceTableNoEarth, absenceStaticTable);
//		calcquality(nnnativeTable, presenceTableNoEarth, absenceStaticTable);
		
		/*
		calcHRS(filteredhcaf,absenceStaticTable,presenceTableNoEarth);
		calcHRS(filteredhcaf,null,presenceTableNoEarth);
		*/
		
		
		
	}
}
