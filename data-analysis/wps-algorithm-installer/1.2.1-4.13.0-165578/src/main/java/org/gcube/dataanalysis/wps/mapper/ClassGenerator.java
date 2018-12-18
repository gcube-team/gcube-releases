package org.gcube.dataanalysis.wps.mapper;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.interfaces.GenericAlgorithm;
import org.gcube.dataanalysis.ecoengine.interfaces.Model;
import org.gcube.dataanalysis.ecoengine.processing.factories.ProcessorsFactory;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.StatisticalTypeToWPSType;

public class ClassGenerator {

	private String configPath = "./cfg/";
	private String generationPath = "./src/main/java/org/gcube/dataanalysis/wps/statisticalmanager/synchserver/mappedclasses/";
	private StatisticalTypeToWPSType converter;

	private String packageString = null;
	private String javaFileName = null;
	
	public ClassGenerator(String algorithmName, String implementation, String generationPath, String configPath) throws Exception {
		this.generationPath = generationPath;
		this.configPath = configPath;
		converter = new StatisticalTypeToWPSType();
		this.javaFileName = generateEcologicalEngineClasses(algorithmName, implementation);
	}
	
	public String getPackageString() {
		return packageString;
	}



	public String getJavaFileName() {
		return javaFileName;
	}

	private String generateEcologicalEngineClasses(String algorithmName, String implementation) throws Exception {
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setConfigPath(configPath);
		config.setAlgorithmClassLoader(Thread.currentThread().getContextClassLoader());
		// set scope etc..
		HashMap<String, List<String>> algorithms = ProcessorsFactory.getAllFeatures(config);
		for (String algorithmSet : algorithms.keySet()) {
			List<String> parametersList = algorithms.get(algorithmSet);
			System.out.println(algorithmSet + ":" + parametersList.toString());
			for (String algorithm : parametersList) {

				if (!algorithm.equals(algorithmName))
					continue;

				// got an algorithm
				System.out.println("Algorithm: " + algorithm);
				String description = ""; // get this information
				StringBuffer classWriter = new StringBuffer();
				List<StatisticalType> inputs = null;
				StatisticalType outputs = null;
				// build class preamble
				config.setAgent(algorithm);
				config.setModel(algorithm);
				config.setAlgorithmClassLoader(Thread.currentThread().getContextClassLoader());
				String interfaceString = "";
				Object impl = Class.forName(implementation).newInstance();
				if ((impl instanceof GenericAlgorithm))
				{
					GenericAlgorithm ga = (GenericAlgorithm)impl;
					inputs = ga.getInputParameters();
					description = ga.getDescription();
					try
					{
						outputs = ga.getOutput();
					}
					catch (Exception e)
					{
						outputs = null;
					}
				}
				else if (impl instanceof Model){
					Model ca = (Model)impl;
					inputs = ca.getInputParameters();
					description = ca.getDescription();
					try
					{
						outputs = ca.getOutput();
					}
					catch (Exception e)
					{
						outputs = null;
					}
				}
				else if ((impl instanceof ComputationalAgent))
				{
					ComputationalAgent ca = (ComputationalAgent)impl;
					inputs = ca.getInputParameters();
					description = ca.getDescription();
					try
					{
						outputs = ca.getOutput();
					}
					catch (Exception e)
					{
						outputs = null;
					}
				}
				else
				{
					throw new Exception("invalid algorithm class "+impl.getClass());
				}
				try{
					if (algorithmSet.equals("DISTRIBUTIONS")) {
						packageString = "generators";
						interfaceString = "IGenerator";
					} else if (algorithmSet.equals("TRANSDUCERS")) {
						packageString = "transducerers";
						interfaceString = "ITransducer";
					} else if (algorithmSet.equals("MODELS")) {
						packageString = "modellers";
						interfaceString = "IModeller";
					} else if (algorithmSet.equals("CLUSTERERS")) {
						packageString = "clusterers";
						interfaceString = "IClusterer";
					} else if (algorithmSet.equals("TEMPORAL_ANALYSIS")) {

					} else if (algorithmSet.equals("EVALUATORS")) {
						packageString = "evaluators";
						interfaceString = "IEvaluator";
					}
				}catch(Exception e){
					System.out.println("Error in retrieving output:");
					e.printStackTrace();
				}
				classWriter.append(((String) StatisticalTypeToWPSType.templates.get("package")).replace("#PACKAGE#", packageString) + "\n" + ((String) StatisticalTypeToWPSType.templates.get("import")) + "\n");
				System.out.println("Class preamble: \n" + classWriter.toString());

				// build class description
				String classdescription = (String) StatisticalTypeToWPSType.templates.get("description");
				//modification of 20/07/15
				classdescription = classdescription.replace("#TITLE#", algorithm).replace("#ABSTRACT#", description).replace("#CLASSNAME#", algorithm).replace("#PACKAGE#", packageString);
				System.out.println("Class description : \n" + classdescription);
				String classdefinition = (String) StatisticalTypeToWPSType.templates.get("class_definition");
				classdefinition = classdefinition.replace("#CLASSNAME#", algorithm).replace("#INTERFACE#", interfaceString);
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
				File dirs = new File(generationPath + packageString);
				if (!dirs.exists()) dirs.mkdirs();
				FileTools.saveString(generationPath + packageString+"/"+algorithm + ".java", classWriter.toString(), true, "UTF-8");
				return generationPath + packageString+"/"+algorithm + ".java";
			}
		}
		return null;
	}

}
