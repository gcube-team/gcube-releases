package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.capabilities;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.processing.factories.ProcessorsFactory;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.InfrastructureDialoguer;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.ConfigurationManager;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.TokenManager;
import org.n52.wps.commons.WPSConfig;
import org.slf4j.LoggerFactory;

public class GetCapabilitiesBuilder {

	public static String processString = "<wps:Process wps:processVersion=\"1.1.0\">\n\t<ows:Identifier>#CLASS#</ows:Identifier>\n\t<ows:Title>#TITLE#</ows:Title>\n</wps:Process>";

	public String getClassification(String algorithmName, ConfigurationManager configManager) throws Exception{
		//get algorithms classification:
		AnalysisLogger.getLogger().debug("Searching for a classification of "+algorithmName);
		HashMap<String, List<String>> algorithmsClassification = ProcessorsFactory.getAllFeaturesUser(configManager.getConfig());
		String rightClassification = "Others";
		for (String classification:algorithmsClassification.keySet()){
			List<String> algorithms = algorithmsClassification.get(classification);
			if (algorithms.contains(algorithmName)){
				AnalysisLogger.getLogger().debug("Found classification"+classification);
				return classification; 
			}
		}
		AnalysisLogger.getLogger().debug("No classification found for "+algorithmName);
		return rightClassification;
	}
	
	public String buildGetCapabilities(Map<String, String[]> parameters) throws Exception {

		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		root.setLevel(ch.qos.logback.classic.Level.OFF);

		LinkedHashMap<String, Object> basicInputs = new LinkedHashMap<String, Object>();
		//DONE get scope and username from SmartGears to build the get capabilities
		/* OLD CODE
		if (parameters != null) {
			if (parameters.get(ConfigurationManager.scopeParameter) != null)
				basicInputs.put(ConfigurationManager.scopeParameter, parameters.get(ConfigurationManager.scopeParameter)[0]);
			if (parameters.get(ConfigurationManager.usernameParameter) != null)
				basicInputs.put(ConfigurationManager.usernameParameter, parameters.get(ConfigurationManager.usernameParameter)[0]);
		} else {// case for testing purposes only
			if (AbstractEcologicalEngineMapper.simulationMode){
				basicInputs.put(ConfigurationManager.scopeParameter, ConfigurationManager.defaultScope);
				basicInputs.put(ConfigurationManager.usernameParameter, ConfigurationManager.defaultUsername);
			}
		}
		*/
		ConfigurationManager configManager = new ConfigurationManager();
		TokenManager tokenm = new TokenManager();
		tokenm.getCredentials();
		String scope = tokenm.getScope();
		String username = tokenm.getUserName();
		String token = tokenm.getToken();
		basicInputs.put(ConfigurationManager.scopeParameter, scope);
		basicInputs.put(ConfigurationManager.usernameParameter, username);
		basicInputs.put(ConfigurationManager.tokenParameter, token);
		
		configManager.configAlgorithmEnvironment(basicInputs);
		AnalysisLogger.getLogger().debug("Initializing Capabilities Skeleton in scope " + configManager.getScope() + " with user " + configManager.getUsername());
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/wpsCapabilitiesSkeleton.xml");
		String stringTemplate = IOUtils.toString(is, "UTF-8");
		String host = InetAddress.getLocalHost().getCanonicalHostName();
		String port = WPSConfig.getInstance().getWPSConfig().getServer().getHostport();
		stringTemplate = stringTemplate.replace("#HOST#", host).replace("#PORT#", port);

		AnalysisLogger.getLogger().debug("Host: " + host + " Port: " + port);

		String packageS = "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses";

		LinkedHashMap<String, String> allalgorithms = new LinkedHashMap<String, String>();
		List<Class<?>> classes = null;
		try{
			AnalysisLogger.getLogger().debug("Taking classes from /classes");
			classes = GetCapabilitiesChecker.find(packageS);
		}catch(Exception e){
			AnalysisLogger.getLogger().debug("Taking classes from the Jar");
			classes=GetCapabilitiesChecker.getClassesInSamePackageFromJar(packageS);
		}
		for (Class<?> classfind : classes) {
			org.n52.wps.algorithm.annotation.Algorithm algorithmInfo = classfind.getAnnotation(org.n52.wps.algorithm.annotation.Algorithm.class);
			if (algorithmInfo != null) {
				AnalysisLogger.getLogger().debug("Retrieving local declared Algorithm: " + algorithmInfo.title());
				allalgorithms.put(algorithmInfo.title(), classfind.getName());
			}
		}

		AnalysisLogger.getLogger().debug("Getting algorithms from the infrastructure");
		InfrastructureDialoguer dialoguer = new InfrastructureDialoguer(configManager.getScope());
		List<String> algorithmsInScope = dialoguer.getAlgorithmsInScope();
		AnalysisLogger.getLogger().debug("Found " + algorithmsInScope.size() + " algorithms in scope ");
		StringBuffer capabilities = new StringBuffer();
		
		for (String algorithmInScope : algorithmsInScope) {
			String classAlgorithm = allalgorithms.get(algorithmInScope);
			if (classAlgorithm != null) {
				AnalysisLogger.getLogger().debug("Approving " + classAlgorithm + " to capabilities ");
				String algorithmTitle = getClassification(algorithmInScope, configManager)+":"+algorithmInScope;
//				String algorithmTitle = algorithmInScope;
				capabilities.append(processString.replace("#TITLE#", algorithmTitle).replace("#CLASS#", classAlgorithm));
			}
		}

		stringTemplate = stringTemplate.replace("#PROCESSES#", capabilities.toString());
		AnalysisLogger.getLogger().debug("Get capabilities built");
		// System.out.println("Template:\n"+stringTemplate);
		return stringTemplate;
	}

	

	public static void main(String[] args) throws Exception {
		 GetCapabilitiesBuilder builder = new GetCapabilitiesBuilder();
		 builder.buildGetCapabilities(null);
//		String packageS = "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses";
		
		// URL scannedUrl =
		// Thread.currentThread().getContextClassLoader().getResource(packageS);
		// System.out.println(scannedUrl);
		
//		GetCapabilitiesChecker.getClassesInSamePackageFromJar(packageS);
	}

}
