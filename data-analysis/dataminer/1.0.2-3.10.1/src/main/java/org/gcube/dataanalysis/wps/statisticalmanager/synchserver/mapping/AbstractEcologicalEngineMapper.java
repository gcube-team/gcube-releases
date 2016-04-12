package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.ComputationalAgent;
import org.gcube.dataanalysis.ecoengine.processing.factories.ClusterersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.EvaluatorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.GeneratorsFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.ModelersFactory;
import org.gcube.dataanalysis.ecoengine.processing.factories.TransducerersFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.DatabaseInfo;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.InfrastructureDialoguer;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure.TableCoherenceChecker;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.IClusterer;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.IEvaluator;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.IGenerator;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.IModeller;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.ITransducer;
import org.hibernate.SessionFactory;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.slf4j.LoggerFactory;

//DONE empty scope is not permitted
// DONE integrate transducerers on WPS
// DONE test transducerers on WPS
// DONE integrate charts
// DONE integrate evaluators
// DONE integrate clusterers
// DONE integrate models
// DONE integrate generators
// DONE distinguish algorithms according to their belonging package
// DONE manage models and generators search
// DONE enable generators when the properties file is fulfilled
// DONE Manage and test computations on D4Science
// DONE Introduce the "user name" parameter for all the computations
// DONE memory cache of the database parameters
// DONE test HRS on the WPS service
// DONE build a checker for the default values
// DONE Check algorithms descriptions on the WPS service
// DONE Multi-scope GetCapabilities
// DONE delete localhost from GetCapabilities
// DONE Limit the maximum number of acceptable computations (implementing an
// internal queue mechanism)
// DONE Add internal parameters: max number of computations and use storage
// DONE test with http links from the workspace
// DONE delete temporary files
// DONE Delete output tables
// DONE before deleting a table, check if it is not a system table
// DONE add time stamps!
// DONE store on storage manager if use storage is enabled: https://gcube.wiki.gcube-system.org/gcube/index.php/URI_Resolver
// DONE test of algorithms on the remote machine
// DONE solve the security issue on the server when using scope=/d4science.research-infrastructures.eu/gCubeApps 
// TODO Evaluation by the Taverna team
// TODO delete user and scope parameters from the algorithms 
// WONTFIX manage Gis Links
// WONTFIX Manage the order of the inputs in the WPS description (currently
// not
// supported by 52 N)

public class AbstractEcologicalEngineMapper extends AbstractAnnotatedAlgorithm {

	/**
	 * Deploying procedure: 1 - modify configuration files 2 - modify resource
	 * file: resources/templates/setup.cfg 3 - generate classes with
	 * ClassGenerator 4 - add new classes in the wps_config.xml on the wps web
	 * app config folder 5 - produce the Jar file of this project 6 - copy the
	 * jar file in the lib folder of the wps web app
	 * change the server parameters in the wps_config.xml file
	 */

	// inputs and outputs
	public LinkedHashMap<String, Object> inputs = new LinkedHashMap<String, Object>();
	public LinkedHashMap<String, Object> outputs = new LinkedHashMap<String, Object>();
	public LinkedHashMap<String, Long> times = new LinkedHashMap<String, Long>();
	
	public static HashMap<String, DatabaseInfo> databaseParametersMemoryCache = new HashMap<String, DatabaseInfo>();
	public static HashMap<String, String> runningcomputations = new HashMap<String, String>();
	
	public static synchronized void addComputation(String session, String user) {
		runningcomputations.put(session, user);
	}

	public static synchronized void removeComputation(String session) {
		runningcomputations.remove(session);
	}

	public static synchronized int getRuningComputations() {
		return runningcomputations.size();
	}

	public static synchronized String displayRunningComputations() {
		return runningcomputations.toString();
	}

	public void waitForResources() throws Exception {
		while (getRuningComputations() > ConfigurationManager.getMaxComputations()) {
			Thread.sleep(2000);
			AnalysisLogger.getLogger().debug("Waiting for resources to be available: " + displayRunningComputations());
		}

	}

	// inner objects
	public AlgorithmConfiguration config;
	public InfrastructureDialoguer infrastructureDialoguer;

	public static synchronized DatabaseInfo getDatabaseInfo(String scope) {
		return databaseParametersMemoryCache.get(scope);
	}

	public static synchronized void addDatabaseInfo(String scope, DatabaseInfo info) {
		databaseParametersMemoryCache.put(scope, info);
	}

	public ComputationalAgent getComputationalAgent(String algorithmName) throws Exception {
		AnalysisLogger.getLogger().debug("Searching for Agents.. " + algorithmName);
		List<ComputationalAgent> agents = new ArrayList<ComputationalAgent>();

		if (this instanceof ITransducer)
			agents = TransducerersFactory.getTransducerers(config);
		else if (this instanceof IClusterer)
			agents = ClusterersFactory.getClusterers(config);
		else if (this instanceof IEvaluator)
			agents = EvaluatorsFactory.getEvaluators(config);
		else if (this instanceof IGenerator)
			agents = GeneratorsFactory.getGenerators(config);
		else if (this instanceof IModeller)
			agents = ModelersFactory.getModelers(config);

		if (agents != null && agents.size() > 0 && agents.get(0) != null) {
			AnalysisLogger.getLogger().debug("Found " + agents.size() + " Agents for " + algorithmName);
			ComputationalAgent agent = agents.get(0);
			agent.setConfiguration(config);
			return agent;
		} else
			return null;
	}

	public List<StatisticalType> getInputParameters(String algorithmName) throws Exception {
		AnalysisLogger.getLogger().debug("Searching for Agents Inputs.. " + algorithmName);
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();

		if (this instanceof ITransducer)
			parameters = TransducerersFactory.getTransducerParameters(config, algorithmName);
		else if (this instanceof IClusterer)
			parameters = ClusterersFactory.getClustererParameters(config.getConfigPath(), algorithmName);
		else if (this instanceof IEvaluator)
			parameters = EvaluatorsFactory.getEvaluatorParameters(config.getConfigPath(), algorithmName);
		else if (this instanceof IGenerator)
			parameters = GeneratorsFactory.getAlgorithmParameters(config.getConfigPath(), algorithmName);
		else if (this instanceof IModeller)
			parameters = ModelersFactory.getModelParameters(config.getConfigPath(), algorithmName);

		if (parameters != null) {
			AnalysisLogger.getLogger().debug("Found " + parameters.size() + " Parameters for " + algorithmName);
			return parameters;
		} else
			return null;
	}

	public StatisticalType getOutput(String algorithmName) throws Exception {
		AnalysisLogger.getLogger().debug("Searching for Agents Inputs.. " + algorithmName);
		StatisticalType output = null;

		if (this instanceof ITransducer)
			output = TransducerersFactory.getTransducerOutput(config, algorithmName);
		else if (this instanceof IClusterer)
			output = ClusterersFactory.getClustererOutput(config.getConfigPath(), algorithmName);
		else if (this instanceof IEvaluator)
			output = EvaluatorsFactory.getEvaluatorOutput(config.getConfigPath(), algorithmName);
		else if (this instanceof IGenerator)
			output = GeneratorsFactory.getAlgorithmOutput(config.getConfigPath(), algorithmName);
		else if (this instanceof IModeller)
			output = ModelersFactory.getModelOutput(config.getConfigPath(), algorithmName);

		if (output != null) {
			AnalysisLogger.getLogger().debug("Found " + output + " for " + algorithmName);
			return output;
		}
		return output;
	}

	public void deleteTemporaryTables(List<String> generatedInputTables) throws Exception {

		if (generatedInputTables != null && generatedInputTables.size() > 0) {
			SessionFactory dbConnection = null;
			try {
				dbConnection = DatabaseUtils.initDBSession(config);

				for (String table : generatedInputTables) {
					if (table != null) {
						if (TableCoherenceChecker.isSystemTable(table))
							continue;
						AnalysisLogger.getLogger().debug("Dropping Temporary Table: " + table);
						try {
							DatabaseFactory.executeSQLUpdate("drop table " + table, dbConnection);
						} catch (Exception e) {
							AnalysisLogger.getLogger().debug("Could not drop Temporary Table: " + table);
						}
					}
					else
						AnalysisLogger.getLogger().debug("Could not drop Temporary Table: " + table+" table is null");
				}
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().debug(e);
			} finally {
				DatabaseUtils.closeDBConnection(dbConnection);
			}
		}
	}

	public void deleteGeneratedFiles(List<File> generatedFiles) throws Exception {
		if (generatedFiles != null) {
			for (File file : generatedFiles) {
				if (file.exists()) {
					AnalysisLogger.getLogger().debug("Deleting File " + file.getAbsolutePath());
					AnalysisLogger.getLogger().debug("Deleting File Check " + file.delete());
				}
				else
					AnalysisLogger.getLogger().debug("File does not exist " + file.getAbsolutePath());
			}
		}
	}
	
	public void manageUserToken(){
		String scope = null;
		String username = null;
		//DONE get scope and username from SmartGears
		//get scope from SmartGears
		TokenManager tokenm = new TokenManager();
		tokenm.getCredentials();
		scope = tokenm.getScope();
		username = tokenm.getUserName();
		//set parameters
		inputs.put(ConfigurationManager.scopeParameter, scope);
		inputs.put(ConfigurationManager.usernameParameter, username);
	}
	
	@Execute
	public void run() throws Exception {
		String algorithm = "";
		List<String> generatedInputTables = null;
		List<String> generatedOutputTables = null;
		List<File> generatedFiles = null;
		String computationSession = UUID.randomUUID().toString();

		try {

			// wait for server resources to be available
			time("WPS Algorithm objects Initialization: Session " + computationSession);
			ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
			root.setLevel(ch.qos.logback.classic.Level.OFF);

			// set the configuration environment for this algorithm
			ConfigurationManager configManager = new ConfigurationManager(); //initializes parameters from file
			manageUserToken();
			configManager.configAlgorithmEnvironment(inputs);
			config = configManager.getConfig();
			AnalysisLogger.getLogger().info("Configured algorithm with session " + computationSession);
			time("Configuration");
			waitForResources();
			AnalysisLogger.getLogger().info("Running algorithm with session " + computationSession);
			time("Waiting time for resources to be free");
			// add the computation to the global list of computations
			addComputation(computationSession, configManager.getUsername() + ":" + configManager.getScope());

			String scope = configManager.getScope();
			String username = configManager.getUsername();

			AnalysisLogger.getLogger().info("1 - Algorithm environment initialized in scope " + scope + " with user name " + username + " and session " + computationSession);
			AnalysisLogger.getLogger().info("Max allowed computations " + ConfigurationManager.getMaxComputations() + " using storage " + ConfigurationManager.useStorage());
			// init the infrastructure dialoguer
			AnalysisLogger.getLogger().info("2 - Initializing connection to the e-Infrastructure");
			infrastructureDialoguer = new InfrastructureDialoguer(scope);
			time("Connection to the e-Infrastructure initialized");
			// set the database parameters
			AnalysisLogger.getLogger().info("3 - Initializing connection to the e-Infrastructure central database for computations");
			DatabaseInfo supportDatabaseInfo = getDatabaseInfo(scope);
			if (supportDatabaseInfo == null) {
				supportDatabaseInfo = infrastructureDialoguer.getDatabaseInfo("StatisticalManagerDataBase");
				addDatabaseInfo(scope, supportDatabaseInfo);
			} else
				AnalysisLogger.getLogger().info("Using cached database information: " + supportDatabaseInfo);
			AnalysisLogger.getLogger().info("Retrieved Central Database: " + supportDatabaseInfo);
			InputsManager inputsManager = new InputsManager(inputs, config);
			inputsManager.configSupportDatabaseParameters(supportDatabaseInfo);
			time("Central database information retrieval");
			// retrieve the algorithm to execute
			AnalysisLogger.getLogger().info("4 - Retrieving WPS algorithm name");
			algorithm = this.getAlgorithmClass().getSimpleName();
			AnalysisLogger.getLogger().debug("Selected Algorithm: " + algorithm);
			config.setAgent(algorithm);
			config.setModel(algorithm);
			time("Ecological Engine Algorithm selection");
			// adding service parameters to the configuration
			AnalysisLogger.getLogger().info("5 - Adding Service parameters to the configuration");
			inputsManager.addInputServiceParameters(getInputParameters(algorithm), infrastructureDialoguer);
			time("Service parameters added to the algorithm");
			// merging wps with ecological engine parameters - modifies the
			// config
			AnalysisLogger.getLogger().info("6 - Translating WPS Inputs into Ecological Engine Inputs");
			inputsManager.mergeWpsAndEcologicalInputs(supportDatabaseInfo);
			generatedInputTables = inputsManager.getGeneratedTables();
			generatedFiles = inputsManager.getGeneratedInputFiles();
			time("Setup and download of input parameters with tables creation");
			// retrieve the computational agent given the configuration
			AnalysisLogger.getLogger().info("7 - Retrieving Ecological Engine algorithm");
			ComputationalAgent agent = getComputationalAgent(algorithm);
			AnalysisLogger.getLogger().debug("Found Ecological Engine Algorithm: " + agent);
			time("Algorithm initialization");
			// take the a priori declared wps output
			AnalysisLogger.getLogger().info("8 - Retrieving the a priori output of the algorithm");
			StatisticalType prioroutput = null;
			try {
				prioroutput = getOutput(algorithm);
			} catch (Exception e) {
				AnalysisLogger.getLogger().info("Warning: No a priori output for algorithm " + algorithm);
			}
			time("A priori output retrieval");
			// run the computation
			AnalysisLogger.getLogger().info("9 - Running the computation");
			agent.init();
			agent.compute();
			AnalysisLogger.getLogger().info("The computation has finished. Retrieving output");
			time("Execution time");
			// get the a posteriori output
			AnalysisLogger.getLogger().info("10 - Retrieving the a posteriori output of the algorithm");
			StatisticalType postoutput = agent.getOutput();
			AnalysisLogger.getLogger().debug("Computation Output: " + postoutput);
			time("Output retrieval");
			// merge the posterior and prior outputs
			AnalysisLogger.getLogger().info("11 - Merging the a priori and a posteriori output");
			OutputsManager outputmanager = new OutputsManager(config,computationSession);
			outputs = outputmanager.createOutput(prioroutput, postoutput);
			// in the case of storage usage, delete all local files
			generatedOutputTables = outputmanager.getGeneratedTables();
			if (ConfigurationManager.useStorage()) {
				generatedFiles.addAll(outputmanager.getGeneratedFiles());
				time("Output preparation for WPS document (using storage)");
			} else
				time("Output preparation for WPS document (no storage manager)");
			// delete all temporary tables
			AnalysisLogger.getLogger().info("12 - Deleting possible generated temporary tables");
			AnalysisLogger.getLogger().debug("Final Computation Output: " + outputs);

			AnalysisLogger.getLogger().debug("All done");
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("Error in Algorithm execution: " + algorithm);
			AnalysisLogger.getLogger().debug(e);
			e.printStackTrace();
			throw e;
		} finally {
			AnalysisLogger.getLogger().debug("Deleting Input Tables");
			deleteTemporaryTables(generatedInputTables);
			AnalysisLogger.getLogger().debug("Deleting Output Tables");
			deleteTemporaryTables(generatedOutputTables);
			AnalysisLogger.getLogger().debug("Deleting Files");
			deleteGeneratedFiles(generatedFiles);
			// remove this computation from the list
			removeComputation(computationSession);
			// cleanResources();
			time("Cleaning of resources");
			displayTimes();
		}

	}

	private void time(String label) {
		times.put(label, System.currentTimeMillis());
	}

	private void displayTimes() {
		AnalysisLogger.getLogger().debug("Times Summary:");
		AnalysisLogger.getLogger().debug("Label;Elapsed(ms);Time");
		long prevtime = 0;
		long inittime = 0;
		for (String label : times.keySet()) {
			long currentTime = times.get(label);
			if (prevtime == 0) {
				prevtime = currentTime;
				inittime = currentTime;
			}
			AnalysisLogger.getLogger().debug(label + ";" + (currentTime - prevtime) + ";" + new Date(currentTime));
			prevtime = currentTime;
		}
		AnalysisLogger.getLogger().debug("Total Elapsed;" + (prevtime - inittime) + ";" + new Date(prevtime));

	}

	private void cleanResources() {
		times = null;
		System.gc();
	}

}
