package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.codegeneration;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ProcessorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.StatisticalTypeToWPSType;

public class ClassGenerator {

	public static String configPath = "./cfg/";
	public static String generationPath = "./src/main/java/org/gcube/dataanalysis/wps/statisticalmanager/synchserver/mappedclasses/";
	public StatisticalTypeToWPSType converter;

	public ClassGenerator() throws Exception {
		converter = new StatisticalTypeToWPSType();
	}

	public void generateEcologicalEngineClasses() throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(configPath);
		// set scope etc..
		HashMap<String, List<String>> algorithms = ProcessorsFactory.getAllFeatures(config);
		for (String algorithmSet : algorithms.keySet()) {
			List<String> parametersList = algorithms.get(algorithmSet);
			System.out.println(algorithmSet + ":" + parametersList.toString());

			for (String algorithm : parametersList) {
				// got an algorithm
				System.out.println("Algorithm: " + algorithm);
				String description = ""; // get this information
				String name = ""; // get this information
				StringBuffer classWriter = new StringBuffer();
				List<StatisticalType> inputs = null;
				StatisticalType outputs = null;
				name = algorithm;
				// build class preamble
				config.setAgent(algorithm);
				config.setModel(algorithm);
				String packageString = "";
				String interfaceString = "";
				try{
				if (algorithmSet.equals("DISTRIBUTIONS")) {
					packageString = "generators";
					interfaceString = "IGenerator";
					inputs = GeneratorsFactory.getAlgorithmParameters(configPath, algorithm);
					description = GeneratorsFactory.getDescription(configPath, algorithm);
					outputs = GeneratorsFactory.getAlgorithmOutput(configPath, algorithm);
				} else if (algorithmSet.equals("TRANSDUCERS")) {
					packageString = "transducerers";
					interfaceString = "ITransducer";
					inputs = TransducerersFactory.getTransducerParameters(config, algorithm);
					description = TransducerersFactory.getDescription(config, algorithm);
					outputs = TransducerersFactory.getTransducerOutput(config, algorithm);
				} else if (algorithmSet.equals("MODELS")) {
					packageString = "modellers";
					interfaceString = "IModeller";
					inputs = ModelersFactory.getModelParameters(configPath, algorithm);
					description = ModelersFactory.getDescription(configPath, algorithm);
					outputs = ModelersFactory.getModelOutput(configPath, algorithm);
				} else if (algorithmSet.equals("CLUSTERERS")) {
					packageString = "clusterers";
					interfaceString = "IClusterer";
					inputs = ClusterersFactory.getClustererParameters(configPath, algorithm);
					description = ClusterersFactory.getDescription(configPath, algorithm);
					outputs = ClusterersFactory.getClustererOutput(configPath, algorithm);
				} else if (algorithmSet.equals("TEMPORAL_ANALYSIS")) {

				} else if (algorithmSet.equals("EVALUATORS")) {
					packageString = "evaluators";
					interfaceString = "IEvaluator";
					inputs = EvaluatorsFactory.getEvaluatorParameters(configPath, algorithm);
					description = EvaluatorsFactory.getDescription(configPath, algorithm);
					outputs = EvaluatorsFactory.getEvaluatorOutput(configPath, algorithm);
				}
				}catch(Exception e){
					System.out.println("Error in retrieving output: "+e.getLocalizedMessage());
				}
				classWriter.append(((String) StatisticalTypeToWPSType.templates.get("package")).replace("#PACKAGE#", packageString) + "\n" + ((String) StatisticalTypeToWPSType.templates.get("import")) + "\n");
				System.out.println("Class preamble: \n" + classWriter.toString());
				
				// build class description
				String classdescription = (String) StatisticalTypeToWPSType.templates.get("description");
				//modification of 20/07/15
				classdescription = classdescription.replace("#TITLE#", name).replace("#ABSTRACT#", description).replace("#CLASSNAME#", name).replace("#PACKAGE#", packageString);
				System.out.println("Class description : \n" + classdescription);
				String classdefinition = (String) StatisticalTypeToWPSType.templates.get("class_definition");
				classdefinition = classdefinition.replace("#CLASSNAME#", name).replace("#INTERFACE#", interfaceString);
				System.out.println("Class definition: \n" + classdefinition);
				classWriter.append(classdescription + "\n");
				classWriter.append(classdefinition + "\n");
				// attach scope input deprecated!
//				classWriter.append((String) StatisticalTypeToWPSType.templates.get("scopeInput") + "\n");
//				classWriter.append((String) StatisticalTypeToWPSType.templates.get("usernameInput") + "\n");
				for (StatisticalType input : inputs) {
					System.out.println(input);
					String wpsInput = converter.convert2WPSType(input, true, config);
					if (wpsInput != null) {
						classWriter.append(wpsInput + "\n");
						System.out.println("Input:\n" + wpsInput);
					}
				}
				if (outputs != null) {
					System.out.println("Alg. Output:\n" + outputs);
					String wpsOutput = converter.convert2WPSType(outputs, false, config);
					classWriter.append(wpsOutput + "\n");
					System.out.println("Output:\n" + wpsOutput);
				}
				else
					System.out.println("Output is empty!");
				// add potential outputs
				classWriter.append((String) StatisticalTypeToWPSType.templates.getProperty("optionalOutput") + "\n");
				classWriter.append((String) StatisticalTypeToWPSType.templates.get("class_closure"));

				System.out.println("Class:\n" + classWriter.toString());
				System.out.println("Saving...");
				FileTools.saveString(generationPath + packageString+"/"+algorithm + ".java", classWriter.toString(), true, "UTF-8");
//				 break;
			}
//			 break;
		}
	}

	public static void main(String[] args) throws Exception {
		
		ClassGenerator generator = new ClassGenerator();
		generator.generateEcologicalEngineClasses();
		System.out.println("Finished!");
		
	}
}
