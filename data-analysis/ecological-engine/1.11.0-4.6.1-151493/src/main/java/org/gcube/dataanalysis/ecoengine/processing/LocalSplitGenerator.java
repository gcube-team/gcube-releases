package org.gcube.dataanalysis.ecoengine.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.graphtools.utils.MathFunctions;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.ResourceLoad;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.Resources;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.interfaces.GenericAlgorithm;
import org.gcube.dataanalysis.ecoengine.interfaces.SpatialProbabilityDistributionTable;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class LocalSplitGenerator implements Generator {

	private AlgorithmConfiguration config;
	private ExecutorService executorService;
	private int numberOfThreadsToUse;
	private boolean threadActivity[];
	private SessionFactory dbHibConnection;
	private boolean stopInterrupt;
	private boolean flushInterrupt;
	private boolean forceflush;
	private SpatialProbabilityDistributionTable distributionModel;
	private int processedSpeciesCounter;
	private int spaceVectorsNumber;
	private List<Object> environmentVectors;
	private long lastTime;
	private int lastProcessedRecordsNumber;
	private int processedRecordsCounter;
	private float status;
	private int chunksize;
	private Timer writerScheduler;

	ConcurrentLinkedQueue<String> probabilityBuffer;

	String probabilityInsertionStatement = "insert into %1$s (speciesid,csquarecode,probability %ADDEDINFORMATION%) VALUES %2$s";

	public LocalSplitGenerator(AlgorithmConfiguration config) {
		setConfiguration(config);
		init();
	}

	public LocalSplitGenerator() {
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public String getResourceLoad() {
		long tk = System.currentTimeMillis();

		double activity = Double.valueOf(processedRecordsCounter - lastProcessedRecordsNumber) * 1000.00 / Double.valueOf(tk - lastTime);
		lastTime = tk;
		lastProcessedRecordsNumber = processedRecordsCounter;
		ResourceLoad rs = new ResourceLoad(tk, activity);
		return rs.toString();
	}

	@Override
	public String getResources() {
		Resources res = new Resources();
		try {
			for (int i = 0; i < numberOfThreadsToUse; i++) {
				try {
					double value = (threadActivity[i]) ? 100.00 : 0.00;
					res.addResource("Thread_" + (i + 1), value);
				} catch (Exception e1) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ((res != null) && (res.list != null))
			return HttpRequest.toJSon(res.list).replace("resId", "resID");
		else
			return "";
	}

	@Override
	public String getLoad() {
		long tk = System.currentTimeMillis();
		double activity = processedSpeciesCounter;
		ResourceLoad rs = new ResourceLoad(tk, activity);
		return rs.toString();
	}

	@Override
	public void init() {
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		stopInterrupt = false;
		flushInterrupt = false;
		forceflush=false;
		initDBSession();
		try {
			initModel();
		} catch (Exception e) {
			AnalysisLogger.getLogger().error("error",e);
		}
		// probabilityBuffer = new Vector<String>();
		probabilityBuffer = new ConcurrentLinkedQueue<String>();
		String addedinfo = distributionModel.getAdditionalMetaInformation();

		if (addedinfo == null)
			addedinfo = "";
		else
			addedinfo = "," + addedinfo.trim();

		probabilityInsertionStatement = probabilityInsertionStatement.replace("%ADDEDINFORMATION%", addedinfo);

		if (!distributionModel.isSynchronousProbabilityWrite()) {
			AnalysisLogger.getLogger().trace("init()->insertion scheduler initialized");
			// inizialize the scheduler for the insertions
			writerScheduler = new Timer();
			writerScheduler.schedule(new DatabaseWriter(), 0, AlgorithmConfiguration.refreshResourcesTime);
		}
	}

	private void initModel() throws Exception {
		Properties p = AlgorithmConfiguration.getProperties(config.getConfigPath() + AlgorithmConfiguration.algorithmsFile);
		String objectclass = p.getProperty(config.getModel());
		distributionModel = (SpatialProbabilityDistributionTable) Class.forName(objectclass).newInstance();
		distributionModel.init(config, dbHibConnection);
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
		if (config.getNumberOfResources() == 0)
			this.numberOfThreadsToUse = 1;
		else
			this.numberOfThreadsToUse = config.getNumberOfResources();
	}

	public void initializeThreads() {
		// initialize threads and their activity state
		executorService = Executors.newFixedThreadPool(numberOfThreadsToUse);
		threadActivity = new boolean[numberOfThreadsToUse];
		// initialize to false;
		for (int j = 0; j < threadActivity.length; j++) {
			threadActivity[j] = false;
		}
	}

	public void initDBSession() {

		try {
			if ((config != null) && (config.getConfigPath() != null)) {
				String defaultDatabaseFile = config.getConfigPath() + AlgorithmConfiguration.defaultConnectionFile;

				config.setDatabaseDriver(config.getParam("DatabaseDriver"));
				config.setDatabaseUserName(config.getParam("DatabaseUserName"));
				config.setDatabasePassword(config.getParam("DatabasePassword"));
				config.setDatabaseURL(config.getParam("DatabaseURL"));

				dbHibConnection = DatabaseFactory.initDBConnection(defaultDatabaseFile, config);
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().warn("error initializing db session",e);
		}

	}

	private void createTable() throws Exception {
		if (config.getParam("CreateTable") != null && config.getParam("CreateTable").equalsIgnoreCase("true")) {
			try {
				AnalysisLogger.getLogger().trace("recreating table: " + "drop table " + config.getParam("DistributionTable"));
				DatabaseFactory.executeSQLUpdate("drop table " + config.getParam("DistributionTable"), dbHibConnection);
				AnalysisLogger.getLogger().trace("recreating table->OK");
			} catch (Exception e) {
				AnalysisLogger.getLogger().trace("recreating table->" + e.getLocalizedMessage());
			}

			// DatabaseFactory.executeUpdateNoTransaction(distributionModel.getDistributionTableStatement(), config.getParam("DatabaseDriver"), config.getParam("DatabaseUserName"), config.getParam("DatabasePassword"), config.getParam("DatabaseURL"), true);
			DatabaseFactory.executeUpdateNoTransaction(distributionModel.getDistributionTableStatement(), config.getDatabaseDriver(), config.getDatabaseUserName(), config.getDatabasePassword(), config.getDatabaseURL(), true);

			AnalysisLogger.getLogger().trace("createTable()->OK!");
		}
	}

	public void shutdown() {
		// shutdown threads
		executorService.shutdown();
		// shutdown connection
		stopInterrupt = true;
		if (!distributionModel.isSynchronousProbabilityWrite()) {
			while (!flushInterrupt) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
		}

		if (writerScheduler != null) {
			try {
				writerScheduler.cancel();
				writerScheduler.purge();
			} catch (Exception e) {
			}
		}
		AnalysisLogger.getLogger().trace("CLOSING CONNECTIONS");
		try{
		dbHibConnection.close();
		}catch(Exception eee){}
	}

	// waits for thread to be free
	private void wait4Thread(int index) {
		// wait until thread is free
		while (threadActivity[index]) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void compute() throws Exception {
		// INITIALIZATION
		long tstart = System.currentTimeMillis();
		try {
			AnalysisLogger.getLogger().trace("generate->Using Local Computation algorithm " + distributionModel.getName());

			AnalysisLogger.getLogger().trace("generate->Check for table creation");
			createTable();
			AnalysisLogger.getLogger().trace("generate->Take area reference");
			// take the area reference vectors
			environmentVectors = DatabaseFactory.executeSQLQuery(distributionModel.getGeographicalInfoQuery(), dbHibConnection);
			AnalysisLogger.getLogger().trace("generate->Take species reference");
			List<Object> speciesVectors = DatabaseFactory.executeSQLQuery(distributionModel.getMainInfoQuery(), dbHibConnection);
			AnalysisLogger.getLogger().trace("generate->got all information");

			// calculate the number of chunks needed
			spaceVectorsNumber = environmentVectors.size();
			int speciesVectorNumber = speciesVectors.size();

			// calculate number of chunks to take into account
			chunksize = spaceVectorsNumber / numberOfThreadsToUse;
			if (chunksize == 0)
				chunksize = 1;
			int numOfChunks = spaceVectorsNumber / chunksize;
			if ((spaceVectorsNumber % chunksize) != 0)
				numOfChunks += 1;

			AnalysisLogger.getLogger().trace("generate->Calculation Started with " + numOfChunks + " chunks and " + speciesVectorNumber + " species");
			// initialize threads
			initializeThreads();

			// END INITIALIZATION
			// overall chunks counter
			int overallcounter = 0;
			processedSpeciesCounter = 0;

			// SPECIES CALCULATION
			// cycle throw the species

			for (Object species : speciesVectors) {
				// calculation on multiple threads
				// thread selection index
				int currentThread = 0;
				// take time
				long computationT0 = System.currentTimeMillis();
				// pre process for single species
				distributionModel.singleStepPreprocess(species, spaceVectorsNumber);
				AnalysisLogger.getLogger().trace("-------------------------------------------------> species " + distributionModel.getMainInfoID(species) + " - n. " + (processedSpeciesCounter + 1));
				// CALCULATION CORE
				for (int k = 0; k < numOfChunks; k++) {
					// get the starting index
					int start = k * chunksize;
					// wait for thread to be free
					wait4Thread(currentThread);
					// start species information calculation on the thread
					startNewTCalc(currentThread, species, start);
					// increment thread selection index
					currentThread++;
					// reset current thread index
					if (currentThread >= numberOfThreadsToUse) {
						currentThread = 0;
					}
					// report probability
					status = ((float) overallcounter / ((float) (speciesVectorNumber * numOfChunks))) * 100f;
					if (status == 100)
						status = 99f;
					// AnalysisLogger.getLogger().trace("STATUS->"+status+"%");
					// increment global counter index
					overallcounter++;
				}
				// END OF CALCULATION CORE

				// wait for last threads to finish
				for (int i = 0; i < numberOfThreadsToUse; i++) {
					// free previous calculation
					wait4Thread(i);
				}

				if (distributionModel.isSynchronousProbabilityWrite()) {
					probabilityBuffer = (ConcurrentLinkedQueue<String>) distributionModel.filterProbabilitySet((Queue<String>) probabilityBuffer);
					DatabaseWriter dbw = new DatabaseWriter();
					dbw.flushBuffer();
				}

				long computationT1 = System.currentTimeMillis();
				// flushBuffer();
				AnalysisLogger.getLogger().trace("generate->Species Computation Finished in " + (computationT1 - computationT0) + " ms");
				// perform overall insert
				// insertCriteria();
				// increment the count of processed species
				processedSpeciesCounter++;
				// REPORT ELAPSED TIME
				// post process for single species
				distributionModel.singleStepPostprocess(species, spaceVectorsNumber);
				// if the process was stopped then interrupt the processing

				if (stopInterrupt)
					break;
			}

			long computationT2 = System.currentTimeMillis();
			// flushInterrupt = true;
			AnalysisLogger.getLogger().trace("generate->All Species Computed in " + (computationT2 - tstart) + " ms");

		} catch (Exception e) {
			AnalysisLogger.getLogger().error("error",e);
			throw e;
		} finally {
			try {
				// REPORT OVERALL ELAPSED TIME
				distributionModel.postProcess();
				// shutdown all
				shutdown();
			} catch (Exception e) {
			}
			long tend = System.currentTimeMillis();
			long ttotal = tend - tstart;
			AnalysisLogger.getLogger().warn("generate->Distribution Generator->Algorithm finished in: " + ((double) ttotal / (double) 60000) + " min\n");
			status = 100f;
		}
	}

	// end Definition of the Thread
	// activation
	private void startNewTCalc(int index, Object speciesVector, int start) {
		threadActivity[index] = true;
		ThreadCalculator tc = new ThreadCalculator(index, speciesVector, start);
		executorService.submit(tc);
	}

	// THREAD SECTION
	// definition of the Thread
	private class ThreadCalculator implements Callable<Integer> {

		int threadIndex;
		int spaceindex;
		Object speciesVector;

		public ThreadCalculator(int threadIndex, Object speciesVector, int start) {
			this.threadIndex = threadIndex;
			this.speciesVector = speciesVector;
			this.spaceindex = start;
		}

		public Integer call() {
			// AnalysisLogger.getLogger().trace("threadCalculation->" + (threadIndex+1));
			int max = Math.min(spaceindex + chunksize, spaceVectorsNumber);
			String speciesID = distributionModel.getMainInfoID(speciesVector);

			for (int i = spaceindex; i < max; i++) {
				float prob = distributionModel.calcProb(speciesVector, environmentVectors.get(i));
				String geographicalID = distributionModel.getGeographicalID(environmentVectors.get(i));
				if (prob > 0.1) {
					String additionalInformation = distributionModel.getAdditionalInformation(speciesVector, environmentVectors.get(i));
					if (additionalInformation == null)
						additionalInformation = "";
					else if (additionalInformation.length() > 0)
						additionalInformation = "," + additionalInformation.trim();

					// probabilityBuffer.offer("'" + speciesID + "','" + geographicalID + "','" + MathFunctions.roundDecimal(prob, 2) + "'"+additionalInformation);
					probabilityBuffer.offer("'" + speciesID + "','" + geographicalID + "','" + MathFunctions.roundDecimal(prob, 2) + "'" + additionalInformation);
				}

				processedRecordsCounter++;
			}

			threadActivity[threadIndex] = false;
			return 0;
		}

	}

	// Database insertion thread

	private class DatabaseWriter extends TimerTask {
		public DatabaseWriter() {
		}

		public void run() {
			try {
				if (forceflush){
					AnalysisLogger.getLogger().trace("\t...flushing on db");
					// flush the objects
					flushBuffer();
					AnalysisLogger.getLogger().trace("\t...finished flushing on db");
					forceflush=false;
				}
				if (stopInterrupt) {
					AnalysisLogger.getLogger().trace("\t...finally flushing on db");
					// flush the objects
					flushBuffer();
					AnalysisLogger.getLogger().trace("\t...finished finally flushing on db");
					flushInterrupt = true;
					this.cancel();
				} else if ((probabilityBuffer != null) && (probabilityBuffer.size() > AlgorithmConfiguration.chunkSize)) {
					// AnalysisLogger.getLogger().trace("\t...writing on db");
					writeOnDB(AlgorithmConfiguration.chunkSize);
					// AnalysisLogger.getLogger().trace("\t...finished writing on db");
				}
			} catch (Throwable e) {
				AnalysisLogger.getLogger().error("error",e);
				flushInterrupt = true;
			}

		}

		public void flushBuffer() {

			if ((probabilityBuffer != null) && (probabilityBuffer.size() > 0)) {
				while (probabilityBuffer.size() > AlgorithmConfiguration.chunkSize)
					writeOnDB(AlgorithmConfiguration.chunkSize);
				writeOnDB(probabilityBuffer.size());
			}
			
		}

		private void writeOnDB(int endIndex) {

			if (endIndex > 0) {
				StringBuffer sb = new StringBuffer();

				for (int i = 0; i < endIndex; i++) {
					sb.append("(" + distributionModel.filterProbabiltyRow(probabilityBuffer.poll()) + ")");
					if (i < endIndex - 1) {
						sb.append(",");
					}
				}

				
				String insertionString = String.format(probabilityInsertionStatement, config.getParam("DistributionTable"), sb.toString());

				try {
					// AnalysisLogger.getLogger().debug("->"+insertionString);
					DatabaseFactory.executeSQLUpdate(insertionString, dbHibConnection);
				} catch (Exception e) {
					e.printStackTrace();
				}

				AnalysisLogger.getLogger().trace("writeOnDB()->PROBABILITIES BUFFER REMAINING:" + probabilityBuffer.size());
				sb = null;
			}

		}

	}

	@Override
	public ALG_PROPS[] getSupportedAlgorithms() {
		ALG_PROPS[] p = { ALG_PROPS.SPECIES_VS_CSQUARE_FROM_DATABASE,ALG_PROPS.PARALLEL_SPECIES_VS_CSQUARE_FROM_DATABASE };
		return p;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public List<StatisticalType> getInputParameters() {
		return new ArrayList<StatisticalType>();
//		return distributionModel.getInputParameters();
	}

	

	@Override
	public StatisticalType getOutput() {

		return distributionModel.getOutput();
	}

	@Override
	public GenericAlgorithm getAlgorithm() {
		return distributionModel;
	}

	@Override
	public String getDescription() {
		return "A generator based on tabular data production, which splits a distribution on different threads along the species dimension";
	}


}
