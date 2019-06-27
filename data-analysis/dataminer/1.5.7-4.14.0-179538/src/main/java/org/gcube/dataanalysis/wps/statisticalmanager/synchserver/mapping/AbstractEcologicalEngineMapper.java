package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.usagerecords.JobUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
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
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace.ComputationData;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace.DataspaceManager;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace.StoredData;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.Cancellable;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.Observable;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.utils.Observer;
import org.hibernate.SessionFactory;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.commons.WPSConfig;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractEcologicalEngineMapper extends AbstractAnnotatedAlgorithm implements Observable, Cancellable{

	/**
	 * Deploying procedure: 1 - modify configuration files 2 - modify resource file: resources/templates/setup.cfg 3 - generate classes with ClassGenerator 4 - add new classes in the wps_config.xml on the wps web app config folder 5 - produce the Jar file of this project 6 - copy the jar file in the lib folder of the wps web app change the server parameters in the wps_config.xml file
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEcologicalEngineMapper.class);

	private Observer observer = null;

	private boolean cancelled = false;
	
	private TokenManager tokenm = null;
	
	private EnvironmentVariableManager env = null;
	
	// inputs and outputs
	public LinkedHashMap<String, Object> inputs = new LinkedHashMap<String, Object>();
	public LinkedHashMap<String, Object> outputs = new LinkedHashMap<String, Object>();
	public LinkedHashMap<String, Long> times = new LinkedHashMap<String, Long>();
	public String startTime;
	public String endTime;
	public static HashMap<String, DatabaseInfo> databaseParametersMemoryCache = new HashMap<String, DatabaseInfo>();
	public static HashMap<String, String> runningcomputations = new HashMap<String, String>();
	ComputationalAgent agent;
	public String wpsExternalID = null;
	ComputationData currentComputation;

	public void setWpsExternalID(String wpsExternalID) {
		this.wpsExternalID = wpsExternalID;
	}

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
			Thread.sleep(20000);
			LOGGER.debug("Waiting for resources to be available: " + displayRunningComputations());
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
		LOGGER.debug("Searching for Agents.. " + algorithmName);
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
			LOGGER.debug("Found " + agents.size() + " Agents for " + algorithmName);
			ComputationalAgent agent = agents.get(0);
			agent.setConfiguration(config);
			return agent;
		} else
			return null;
	}

	public List<StatisticalType> getInputParameters(String algorithmName) throws Exception {
		LOGGER.debug("Searching for Agents Inputs.. " + algorithmName);
		List<StatisticalType> parameters = new ArrayList<StatisticalType>();

		if (this instanceof ITransducer)
			parameters = TransducerersFactory.getTransducerParameters(config, algorithmName);
		else if (this instanceof IClusterer)
			parameters = ClusterersFactory.getClustererParameters(config.getConfigPath(), algorithmName, config);
		else if (this instanceof IEvaluator)
			parameters = EvaluatorsFactory.getEvaluatorParameters(config.getConfigPath(), algorithmName, config);
		else if (this instanceof IGenerator)
			parameters = GeneratorsFactory.getAlgorithmParameters(config.getConfigPath(), algorithmName, config);
		else if (this instanceof IModeller)
			parameters = ModelersFactory.getModelParameters(config.getConfigPath(), algorithmName, config);

		if (parameters != null) {
			LOGGER.debug("Found " + parameters.size() + " Parameters for " + algorithmName);
			return parameters;
		} else
			return null;
	}

	public StatisticalType getOutput(String algorithmName) throws Exception {
		LOGGER.debug("Searching for Agents Inputs.. " + algorithmName);
		StatisticalType output = null;

		if (this instanceof ITransducer)
			output = TransducerersFactory.getTransducerOutput(config, algorithmName);
		else if (this instanceof IClusterer)
			output = ClusterersFactory.getClustererOutput(config.getConfigPath(), algorithmName, config);
		else if (this instanceof IEvaluator)
			output = EvaluatorsFactory.getEvaluatorOutput(config.getConfigPath(), algorithmName, config);
		else if (this instanceof IGenerator)
			output = GeneratorsFactory.getAlgorithmOutput(config.getConfigPath(), algorithmName, config);
		else if (this instanceof IModeller)
			output = ModelersFactory.getModelOutput(config.getConfigPath(), algorithmName, config);

		if (output != null) {
			LOGGER.debug("Found " + output + " for " + algorithmName);
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
						LOGGER.debug("Dropping Temporary Table: " + table);
						try {
							DatabaseFactory.executeSQLUpdate("drop table " + table, dbConnection);
						} catch (Exception e) {
							LOGGER.debug("Could not drop Temporary Table: " + table);
						}
					} else
						LOGGER.debug("Could not drop Temporary Table: " + table + " table is null");
				}
			} catch (Exception e) {
				LOGGER.error("error deleting temporary table",e);
			} finally {
				DatabaseUtils.closeDBConnection(dbConnection);
			}
		}
	}

	public static void deleteGeneratedFiles(List<File> generatedFiles) throws Exception {
		System.gc();
		if (generatedFiles != null) {
			for (File file : generatedFiles) {
				if (file.exists()) {
					LOGGER.debug("Deleting File " + file.getAbsolutePath());
					try {
						LOGGER.debug("Deleting File Check " + file.delete());
					} catch (Exception e) {
					}
				} else
					LOGGER.debug("Deleting File - File does not exist " + file.getAbsolutePath());
			}
		}
		System.gc();
	}

	public void manageUserToken() {
		String scope = null;
		String username = null;
		String token = null;
		// DONE get scope and username from SmartGears
		// get scope from SmartGears
		tokenm = new TokenManager();
		tokenm.getCredentials();
		scope = tokenm.getScope();
		username = tokenm.getUserName();
		token = tokenm.getToken();
		// set parameters
		inputs.put(ConfigurationManager.scopeParameter, scope);
		inputs.put(ConfigurationManager.usernameParameter, username);
		inputs.put(ConfigurationManager.tokenParameter, token);
	}

	float previousStatus = -3;
	String host = WPSConfig.getInstance().getWPSConfig().getServer().getHostname();
	public void updateStatus(float status, boolean canWrite) {
		if (agent != null) {
			if (status != previousStatus) {
				LOGGER.debug("STATUS update to: {} ", status );
				previousStatus = status;
				super.update(new Integer((int) status));
				try {
					if (canWrite) updateComputationOnWS(status, null);
				} catch (Exception e) {
					LOGGER.warn("error updating compution on WS");
				}
			}
		}

	}

	public void setEnvironmentVariableManager(EnvironmentVariableManager env) {
		this.env = env;
	}
	
	public void updateComputationOnWS(float status, String exception) {
		updateComputationOnWS(status, exception, null, null);
	}

	class RunDataspaceManager implements Runnable{
		List<StoredData> inputData;
		List<File> generatedData;
		public RunDataspaceManager(List<StoredData> inputData,		List<File> generatedData){
			this.inputData=inputData;
			this.generatedData=generatedData;
		}

		public void run() {
			DataspaceManager manager = new DataspaceManager(config, currentComputation, inputData, null, generatedData);
			try {
				LOGGER.debug("Dataspace->Status updater->Writing computational info on the WS asyncronously");
				manager.writeRunningComputationData();
			} catch (Exception ez) {
				LOGGER.error("Dataspace->Status updater->Impossible to write computation information on the Workspace",ez);
			}
		}
	};

	public void updateComputationOnWS(float status, String exception, List<StoredData> inputData, List<File> generatedData) {
		if (currentComputation != null) {
			currentComputation.setStatus("" + status);
			if (exception != null && exception.length() > 0)
				currentComputation.setException(exception);
			LOGGER.debug("RunDataspaceManager: [inputData="+inputData+", generatedData="+generatedData+"]");
			RunDataspaceManager rundm = new RunDataspaceManager(inputData,generatedData);
			rundm.run();

			/*
			Thread t = new Thread(rundm);
			t.start();
			 */
		}
	}

	
	
	@Execute
	public void run() throws Exception {
		if (observer!=null)
			observer.isStarted(this);

		LOGGER.info("classloader context in this thread is {}",Thread.currentThread().getContextClassLoader());
			
		long startTimeLong = System.currentTimeMillis();

		OperationResult operationResult = null;

		String algorithm = "";
		List<String> generatedInputTables = null;
		List<String> generatedOutputTables = null;
		List<File> generatedFiles = null;
		//String date = new java.text.SimpleDateFormat("dd_MM_yyyy_HH:mm:ss").format(System.currentTimeMillis());
		String computationSession = this.getAlgorithmClass().getSimpleName() + "_ID_" + UUID.randomUUID().toString();
		if (wpsExternalID != null) {
			LOGGER.info("Using wps External ID " + wpsExternalID);
			computationSession = this.getAlgorithmClass().getSimpleName() + "_ID_" + wpsExternalID;
		} else
			LOGGER.info("Wps External ID not set");
		InputsManager inputsManager = null;
		ConfigurationManager configManager = new ConfigurationManager(this.env); // initializes parameters from web.xml
		manageUserToken();
		
		boolean canWriteOnShub = checkWriteAuthorization(tokenm.getUserName());
		
		Path dir = Paths.get(System.getProperty("java.io.tmpdir"), "dmlocks");
		if (!Files.exists(dir))
			dir = Files.createDirectory(dir);
		Path lockFile = Files.createTempFile(dir, "dm", ".lck");
		LOGGER.info("lock file created {}",lockFile.toUri().toURL());
		try {

			// wait for server resources to be available
			startTime = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(System.currentTimeMillis());
			time("WPS Algorithm objects Initialization: Session " + computationSession);

			// set the configuration environment for this algorithm

			configManager.configAlgorithmEnvironment(inputs);
			configManager.setComputationId(computationSession);
			config = configManager.getConfig();
			LOGGER.info("Configured algorithm with session " + computationSession);
			time("Configuration");
			waitForResources();
			LOGGER.info("Running algorithm with session " + computationSession);
			time("Waiting time for resources to be free");
			// add the computation to the global list of computations
			addComputation(computationSession, configManager.getUsername() + ":" + configManager.getScope());

			String scope = configManager.getScope();
			String username = configManager.getUsername();

			LOGGER.info("1 - Algorithm environment initialized in scope " + scope + " with user name " + username + " and session " + computationSession);
			LOGGER.info("Max allowed computations " + ConfigurationManager.getMaxComputations() + " using storage " + ConfigurationManager.useStorage());
			// init the infrastructure dialoguer
			LOGGER.info("2 - Initializing connection to the e-Infrastructure");
			infrastructureDialoguer = new InfrastructureDialoguer(scope);
			time("Connection to the e-Infrastructure initialized");
			// set the database parameters
			LOGGER.info("3 - Initializing connection to the e-Infrastructure central database for computations");
			DatabaseInfo supportDatabaseInfo = getDatabaseInfo(scope);
			if (supportDatabaseInfo == null) {
				supportDatabaseInfo = infrastructureDialoguer.getDatabaseInfo("StatisticalManagerDataBase");
				addDatabaseInfo(scope, supportDatabaseInfo);
			} else
				LOGGER.info("Using cached database information: " + supportDatabaseInfo);
			LOGGER.info("Retrieved Central Database: " + supportDatabaseInfo);
			inputsManager = new InputsManager(inputs, config, computationSession);
			inputsManager.configSupportDatabaseParameters(supportDatabaseInfo);
			time("Central database information retrieval");
			// retrieve the algorithm to execute
			LOGGER.info("4 - Retrieving WPS algorithm name");
			algorithm = this.getAlgorithmClass().getSimpleName();
			LOGGER.debug("Selected Algorithm: " + algorithm);
			config.setAgent(algorithm);
			config.setModel(algorithm);
			time("Ecological Engine Algorithm selection");
			// adding service parameters to the configuration
			LOGGER.info("5 - Adding Service parameters to the configuration");
			List<StatisticalType> dataminerInputParameters = getInputParameters(algorithm);
			LOGGER.debug("Dataminer Algo Default InputParameters: "+dataminerInputParameters);
			inputsManager.addInputServiceParameters(dataminerInputParameters, infrastructureDialoguer);
			time("Service parameters added to the algorithm");
			// merging wps with ecological engine parameters - modifies the
			// config
			LOGGER.info("6 - Translating WPS Inputs into Ecological Engine Inputs");
			LOGGER.debug("Operator class is " + this.getClass().getCanonicalName());
			// build computation Data
			currentComputation = new ComputationData(config.getTaskID(), config.getAgent(), "", "", startTime, "-", "0", config.getTaskID(), configManager.getUsername(), config.getGcubeScope(), this.getClass().getCanonicalName());
			inputsManager.mergeWpsAndEcologicalInputs(supportDatabaseInfo, dataminerInputParameters);
			generatedInputTables = inputsManager.getGeneratedTables();
			generatedFiles = inputsManager.getGeneratedInputFiles();
			time("Setup and download of input parameters with tables creation");
			// retrieve the computational agent given the configuration
			LOGGER.info("7 - Retrieving Ecological Engine algorithm");
			agent = getComputationalAgent(algorithm);
			currentComputation.setOperatorDescription(agent.getDescription());
			currentComputation.setInfrastructure(agent.getInfrastructure().name());
			LOGGER.debug("Found Ecological Engine Algorithm: " + agent);
			time("Algorithm initialization");
			// take the a priori declared wps output
			LOGGER.info("8 - Retrieving the a priori output of the algorithm");
			StatisticalType prioroutput = null;
			try {
				prioroutput = getOutput(algorithm);
			} catch (Exception e) {
				LOGGER.info("Warning: No a priori output for algorithm " + algorithm);
			}
			time("A priori output retrieval");
			// run the computation
			LOGGER.info("9 - Running the computation and updater");

			LOGGER.info("Initializing the WPS status of the computation");
			updateStatus(0, canWriteOnShub);
			LOGGER.info("Initializing the computation");
			agent.init();
			LOGGER.info("Updating status");
			runStatusUpdater(canWriteOnShub);
			LOGGER.info("Running the computation");
			agent.compute();
			LOGGER.info("The computation has finished. Retrieving output");
			time("Execution time");
			// get the a posteriori output
			LOGGER.info("10 - Retrieving the a posteriori output of the algorithm");
			StatisticalType postoutput = agent.getOutput();
			LOGGER.debug("Computation Output: " + postoutput);
			time("Output retrieval");
			// merge the posterior and prior outputs
			LOGGER.info("11 - Merging the a priori and a posteriori output");
			OutputsManager outputmanager = new OutputsManager(config, computationSession);
			outputs = outputmanager.createOutput(prioroutput, postoutput);
			// in the case of storage usage, delete all local files
			generatedOutputTables = outputmanager.getGeneratedTables();
			if (ConfigurationManager.useStorage()) {
				generatedFiles.addAll(outputmanager.getGeneratedFiles());
				time("Output preparation for WPS document (using storage)");
			} else
				time("Output preparation for WPS document (no storage manager)");

			outputmanager.shutdown();
			
			LOGGER.debug("12 - Final Computation Output");
			LOGGER.debug("Outputs: "+ outputs);

			endTime = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(System.currentTimeMillis());
			if (!isCancelled()) {
				LOGGER.debug("Save Computation Data");
				if (canWriteOnShub) saveComputationOnWS(inputsManager.getProvenanceData(), outputmanager.getProvenanceData(), agent, generatedFiles);
			} else {
				LOGGER.debug("Computation interrupted - no update");
				throw new Exception("Computation cancelled");
			}
			LOGGER.debug("All done");
			operationResult = OperationResult.SUCCESS;
		} catch (Exception e) {
			operationResult = OperationResult.FAILED;
			LOGGER.error("Error execution Algorithm {}",algorithm,e);
			int exitstatus = -2;
			if (isCancelled())
				exitstatus = -1;

			if (inputsManager != null)
				if (canWriteOnShub) updateComputationOnWS(exitstatus, e.getMessage(), inputsManager.getProvenanceData(), generatedFiles);
			else
				if (canWriteOnShub) updateComputationOnWS(exitstatus, e.getMessage());
			if (isCancelled())
				throw new Exception("Computation cancelled");
			else
				throw e;
		} finally {
			LOGGER.debug("accounting algorithm");
			accountAlgorithmExecution(startTimeLong, System.currentTimeMillis(), operationResult);
			LOGGER.debug("Deleting Input Tables");
			deleteTemporaryTables(generatedInputTables);
			LOGGER.debug("Deleting Output Tables");
			deleteTemporaryTables(generatedOutputTables);
			// LOGGER.debug("Deleting Files");
			// deleteGeneratedFiles(generatedFiles);
			// remove this computation from the list
			removeComputation(computationSession);
			// cleanResources();
			time("Cleaning of resources");
			displayTimes();
			cleanResources();
			if (observer!=null) observer.isFinished(this);
			LOGGER.debug("All done - Computation Finished");
			Files.deleteIfExists(lockFile);
		}

	}

	private boolean checkWriteAuthorization(String username) {
		if (env!=null && env.getShubUsersExcluded()!=null) {
			if (env.getShubUsersExcluded().isEmpty()) {
				return false;
			}
			if (env.getShubUsersExcluded().contains(username)) {
				return false;
			}
		}
		return true;
	}

	private void accountAlgorithmExecution(long start, long end, OperationResult result) {
		try{
			JobUsageRecord jobUsageRecord = new JobUsageRecord();
			jobUsageRecord.setJobName(this.getAlgorithmClass().getSimpleName());
			jobUsageRecord.setConsumerId(tokenm.getUserName());
			jobUsageRecord.setDuration(end-start);
			jobUsageRecord.setOperationResult(result);
			jobUsageRecord.setServiceName("DataMiner");
			jobUsageRecord.setServiceClass("WPS");
			jobUsageRecord.setHost(WPSConfig.getInstance().getWPSConfig().getServer().getHostname());
			jobUsageRecord.setCallerQualifier(tokenm.getTokenQualifier());
			
			AccountingPersistence accountingPersistence = 
					AccountingPersistenceFactory.getPersistence();
			accountingPersistence.account(jobUsageRecord);
		}catch(Throwable e){
			LOGGER.error("error accounting algorithm execution",e);
		}

	}

	public class StatusUpdater implements Runnable {
		
		private boolean canWrite = true;
		
		public StatusUpdater(boolean canWrite) {
			this.canWrite = canWrite;
		}
		
		@Override
		public void run() {
			while (agent != null && !isCancelled() && agent.getStatus() < 100) {
				try {
					updateStatus(agent.getStatus(), canWrite);
					Thread.sleep(10000);
				} catch (InterruptedException e) {}
			}
			LOGGER.info("Status updater terminated");
		}
	}

	private void runStatusUpdater(boolean canWrite) {
		StatusUpdater updater = new StatusUpdater(canWrite);

		Thread t = new Thread(updater);
		t.start();
		LOGGER.debug("Provenance manager running");
	}

	private void saveComputationOnWS(List<StoredData> inputData, List<StoredData> outputData, ComputationalAgent agent, List<File> generatedFiles) {
		LOGGER.debug("Save Computation On WS");
		LOGGER.debug("InputData: "+inputData);
		LOGGER.debug("OutputData: "+outputData);
		LOGGER.debug("Agent: "+agent);
		LOGGER.debug("Generated files: "+generatedFiles);
		LOGGER.debug("Provenance manager started for operator " + this.getClass().getCanonicalName());
		
		ComputationData computation = new ComputationData(config.getTaskID(), config.getAgent(), agent.getDescription(), agent.getInfrastructure().name(), startTime, endTime, "100", config.getTaskID(), config.getParam(ConfigurationManager.serviceUserNameParameterVariable), config.getGcubeScope(), this.getClass().getCanonicalName());
		// post on WS
		DataspaceManager manager = new DataspaceManager(config, computation, inputData, outputData, generatedFiles);

		Thread t = new Thread(manager);
		t.start();
		LOGGER.debug("Provenance manager running");
	}

	private void time(String label) {
		times.put(label, System.currentTimeMillis());
	}

	private void displayTimes() {
		LOGGER.debug("Times Summary:");
		LOGGER.debug("Label;Elapsed(ms);Time");
		long prevtime = 0;
		long inittime = 0;
		for (String label : times.keySet()) {
			long currentTime = times.get(label);
			if (prevtime == 0) {
				prevtime = currentTime;
				inittime = currentTime;
			}
			LOGGER.debug(label + ";" + (currentTime - prevtime) + ";" + new Date(currentTime));
			prevtime = currentTime;
		}
		LOGGER.debug("Total Elapsed;" + (prevtime - inittime) + ";" + new Date(prevtime));

	}

	private void cleanResources() {
		times = null;
		agent = null;
		// manage open files and garbage
		LOGGER.debug("Managing open files");
		// String checkOpenFiles = "ls -l /proc/*/fd/* 2|grep \"wps/ecocfg\"";
		try {
			String checkOpenFiles = "for i in `ls -l /proc/*/fd/* 2>/dev/null | grep delete | grep tomcat | awk '{print $9}'`; do du -hL $i | awk '{print $1}' | tr '\n' ' '; ls -l $i | awk '{print $6\" \"$7\" \"$8\" \"$9\" \"$10\" \"$11\" \"$12}'; done";
			List<String> openFiles = command(checkOpenFiles, "./");
			LOGGER.debug("Open Files " + openFiles);

			if (openFiles != null) {
				for (String openFile : openFiles) {
					if (!openFile.contains("cannot access") && openFile.contains("(deleted)")) {
						String size = openFile.substring(0, openFile.indexOf(" ")).trim();
						String pid = openFile.substring(openFile.indexOf("/proc/"), openFile.indexOf("->"));
						pid = pid.trim();
						if (!size.equals("0")) {
							LOGGER.debug("Killing " + pid + " with size " + size);
							command(":>" + pid, "./");
						}
					}
				}
			}

		} catch (Exception e) {
			LOGGER.debug("Could not kill files " + e.getLocalizedMessage());
		}

		System.gc();
	}

	public static List<String> command(final String cmdline, final String directory) {
		try {
			Process process = new ProcessBuilder(new String[] { "bash", "-c", cmdline }).redirectErrorStream(true).directory(new File(directory)).start();

			List<String> output = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null)
				output.add(line);

			// There should really be a timeout here.
			if (0 != process.waitFor())
				return null;

			return output;

		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setObserver(Observer o) {
		LOGGER.debug("setting observer in {} ",wpsExternalID);
		this.observer = o;		
	}

	@Override
	public synchronized boolean cancel() {
		if (!cancelled){
			LOGGER.debug("COMPUTATION INTERRUPTED! ({})",wpsExternalID);
			try{
				if (agent!=null){
					agent.shutdown();
					agent = null;
				}

				super.update(new Integer((int) -1));
				try {
					updateComputationOnWS(-1, null);
				} catch (Exception e) {

				}
				System.gc();
				cancelled = true;
			}catch(Exception e){
				LOGGER.warn("error cancelling computation with id {}",wpsExternalID);
				return false;
			}
		} else {
			LOGGER.debug("COMPUTATION ALREADY INTERRUPT! ({})",wpsExternalID);
			return false;
		}
		return true;

	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

}
