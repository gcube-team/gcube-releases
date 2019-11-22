package org.gcube.data.analysis.wps;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.wps.repository.GcubeAlgorithmRepository;
import org.gcube.dataanalysis.ecoengine.processing.factories.ProcessorsFactory;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.InfrastructureDialoguer;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.ConfigurationManager;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.EnvironmentVariableManager;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.TokenManager;
import org.n52.wps.commons.WPSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCapabilitiesBuilder {

	public static String processString = "<wps:Process wps:processVersion=\"1.1.0\">\n\t<ows:Identifier>#CLASS#</ows:Identifier>\n\t<ows:Title>#TITLE#</ows:Title>\n</wps:Process>";

	private static final Logger LOGGER = LoggerFactory.getLogger(GetCapabilitiesBuilder.class);

	public String getClassification(String algorithmName, ConfigurationManager configManager) throws Exception {
		// get algorithms classification:
		LOGGER.debug("Searching for a classification of " + algorithmName);
		HashMap<String, List<String>> algorithmsClassification = ProcessorsFactory
				.getAllFeaturesUser(configManager.getConfig());
		String rightClassification = "Others";
		for (String classification : algorithmsClassification.keySet()) {
			List<String> algorithms = algorithmsClassification.get(classification);
			if (algorithms.contains(algorithmName)) {
				LOGGER.debug("Found classification" + classification);
				return classification;
			}
		}
		LOGGER.debug("No classification found for " + algorithmName);
		return rightClassification;
	}

	public String buildGetCapabilities(Map<String, String[]> parameters, EnvironmentVariableManager env) throws Exception {

		LinkedHashMap<String, Object> basicInputs = new LinkedHashMap<String, Object>();
		// DONE get scope and username from SmartGears to build the get
		// capabilities
		/*
		 * OLD CODE if (parameters != null) { if
		 * (parameters.get(ConfigurationManager.scopeParameter) != null)
		 * basicInputs.put(ConfigurationManager.scopeParameter,
		 * parameters.get(ConfigurationManager.scopeParameter)[0]); if
		 * (parameters.get(ConfigurationManager.usernameParameter) != null)
		 * basicInputs.put(ConfigurationManager.usernameParameter,
		 * parameters.get(ConfigurationManager.usernameParameter)[0]); } else
		 * {// case for testing purposes only if
		 * (AbstractEcologicalEngineMapper.simulationMode){
		 * basicInputs.put(ConfigurationManager.scopeParameter,
		 * ConfigurationManager.defaultScope);
		 * basicInputs.put(ConfigurationManager.usernameParameter,
		 * ConfigurationManager.defaultUsername); } }
		 */
		ConfigurationManager configManager = new ConfigurationManager(env);
		TokenManager tokenm = new TokenManager();
		tokenm.getCredentials();
		String scope = tokenm.getScope();
		String username = tokenm.getUserName();
		String token = tokenm.getToken();
		basicInputs.put(ConfigurationManager.scopeParameter, scope);
		basicInputs.put(ConfigurationManager.usernameParameter, username);
		basicInputs.put(ConfigurationManager.tokenParameter, token);

		configManager.configAlgorithmEnvironment(basicInputs);
		LOGGER.debug("Initializing Capabilities Skeleton in scope " + configManager.getScope() + " with user "
				+ configManager.getUsername());
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/wpsCapabilitiesSkeleton.xml");
		String stringTemplate = IOUtils.toString(is, "UTF-8");

		// TODO: GET HOSTNAME AND PORT from container
		String protocol = WPSConfig.getInstance().getWPSConfig().getServer().getProtocol();
		String host = WPSConfig.getInstance().getWPSConfig().getServer().getHostname();
		String port = WPSConfig.getInstance().getWPSConfig().getServer().getHostport();
		stringTemplate = stringTemplate.replace("#PROTOCOL#", protocol).replace("#HOST#", host).replace("#PORT#", port);

		LOGGER.debug("Protocol: "+protocol);
		LOGGER.debug("Host: " + host);
		LOGGER.debug("Port: " + port);

		LinkedHashMap<String, String> allalgorithms = new LinkedHashMap<String, String>();
		/*
		 * String packageS =
		 * "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses";
		 * List<Class<?>> classes = null; try{
		 * LOGGER.debug("Taking classes from /classes"); classes =
		 * GetCapabilitiesChecker.find(packageS); }catch(Exception e){
		 * LOGGER.debug("Taking classes from the Jar");
		 * classes=GetCapabilitiesChecker.getClassesInSamePackageFromJar(
		 * packageS); }
		 */

		LOGGER.info("using classloader class {} ",
				Thread.currentThread().getContextClassLoader().getClass().getSimpleName());

		Set<Class<?>> algorithmsClass = GcubeAlgorithmRepository.getAllAlgorithms();

		LOGGER.info("class found with annotation Algorithm are {}", algorithmsClass.size());

		for (Class<?> classfind : algorithmsClass) {
			org.n52.wps.algorithm.annotation.Algorithm algorithmInfo = classfind
					.getAnnotation(org.n52.wps.algorithm.annotation.Algorithm.class);
			if (algorithmInfo != null) {
				LOGGER.debug("Retrieving local declared Algorithm: " + algorithmInfo.title());
				allalgorithms.put(algorithmInfo.title(), classfind.getName());
			}
		}

		LOGGER.debug("Getting algorithms from the infrastructure");
		InfrastructureDialoguer dialoguer = new InfrastructureDialoguer(configManager.getScope());
		List<String> algorithmsInScope = dialoguer.getAlgorithmsInScope();
		LOGGER.debug("Found {}  algorithms in scope {} ", algorithmsInScope.size(), ScopeProvider.instance.get());
		StringBuffer capabilities = new StringBuffer();

		// TO eliminate duplicate coming from IS
		Set<String> algorithmsSet = new HashSet<String>(algorithmsInScope);
		algorithmsSet.addAll(
				dialoguer.getPrivateAlgorithmsInScope(AuthorizationProvider.instance.get().getClient().getId()));

		for (String algorithmInScope : algorithmsSet) {
			String classAlgorithm = allalgorithms.get(algorithmInScope);
			if (classAlgorithm != null) {
				LOGGER.debug("Approving " + classAlgorithm + " to capabilities ");
				String algorithmTitle = getClassification(algorithmInScope, configManager) + ":" + algorithmInScope;
				// String algorithmTitle = algorithmInScope;
				capabilities
						.append(processString.replace("#TITLE#", algorithmTitle).replace("#CLASS#", classAlgorithm));
			}
		}

		stringTemplate = stringTemplate.replace("#PROCESSES#", capabilities.toString());
		LOGGER.debug("Get capabilities built");
		return stringTemplate;
	}

}
