package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.codegeneration;

import java.util.HashMap;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassGenerator.class);
	
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
			LOGGER.trace(algorithmSet + ":" + parametersList.toString());

			for (String algorithm : parametersList) {
				// got an algorithm
				LOGGER.trace("Algorithm: " + algorithm);
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
					LOGGER.error("Error in retrieving output: ",e);
				}
				classWriter.append(((String) StatisticalTypeToWPSType.templates.get("package")).replace("#PACKAGE#", packageString) + "\n" + ((String) StatisticalTypeToWPSType.templates.get("import")) + "\n");
				LOGGER.trace("Class preamble: \n" + classWriter.toString());
				
				// build class description
				String classdescription = (String) StatisticalTypeToWPSType.templates.get("description");
				//modification of 20/07/15
				classdescription = classdescription.replace("#TITLE#", name).replace("#ABSTRACT#", description).replace("#CLASSNAME#", name).replace("#PACKAGE#", packageString);
				LOGGER.trace("Class description : \n" + classdescription);
				String classdefinition = (String) StatisticalTypeToWPSType.templates.get("class_definition");
				classdefinition = classdefinition.replace("#CLASSNAME#", name).replace("#INTERFACE#", interfaceString);
				LOGGER.trace("Class definition: \n" + classdefinition);
				classWriter.append(classdescription + "\n");
				classWriter.append(classdefinition + "\n");
				// attach scope input deprecated!
//				classWriter.append((String) StatisticalTypeToWPSType.templates.get("scopeInput") + "\n");
//				classWriter.append((String) StatisticalTypeToWPSType.templates.get("usernameInput") + "\n");
				for (StatisticalType input : inputs) {
					LOGGER.trace("input is {}",input);
					String wpsInput = converter.convert2WPSType(input, true, config);
					if (wpsInput != null) {
						classWriter.append(wpsInput + "\n");
						LOGGER.trace("Input:\n {}", wpsInput);
					}
				}
				if (outputs != null) {
					LOGGER.trace("Alg. Output:\n {}", outputs);
					String wpsOutput = converter.convert2WPSType(outputs, false, config);
					classWriter.append(wpsOutput + "\n");
					LOGGER.trace("Output:\n  {}", wpsOutput);
				}
				else
					LOGGER.trace("Output is empty!");
				// add potential outputs
				classWriter.append((String) StatisticalTypeToWPSType.templates.getProperty("optionalOutput") + "\n");
				classWriter.append((String) StatisticalTypeToWPSType.templates.get("class_closure"));

				LOGGER.trace("Class:\n {}",classWriter.toString());
				LOGGER.trace("Saving...");
				FileTools.saveString(generationPath + packageString+"/"+algorithm + ".java", classWriter.toString(), true, "UTF-8");
//				 break;
			}
//			 break;
		}
	}

}
