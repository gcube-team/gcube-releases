package org.gcube.dataanalysis.ecoengine.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.ResourceLoad;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.Resources;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.interfaces.Generator;
import org.gcube.dataanalysis.ecoengine.interfaces.GenericAlgorithm;
import org.gcube.dataanalysis.ecoengine.interfaces.SpatialProbabilityDistributionGeneric;

public class LocalSimpleSplitGenerator implements Generator {

	protected AlgorithmConfiguration config;
	protected ExecutorService executorService;
	protected int numberOfThreadsToUse;
	protected boolean threadActivity[];
	protected boolean stopInterrupt;
	protected SpatialProbabilityDistributionGeneric distributionModel;
	protected int processedSpeciesCounter;
	protected int spaceVectorsNumber;
	protected List<Object> environmentVectors;
	protected long lastTime;
	protected int lastProcessedRecordsNumber;
	protected int processedRecordsCounter;
	protected float status;
	protected int chunksize;
	protected ConcurrentLinkedQueue<String> probabilityBuffer;
	//species Objects -> (geographical Object , Probability)
	protected ConcurrentHashMap<Object,Map<Object,Float>> completeDistribution;
			
	public LocalSimpleSplitGenerator() {
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
		completeDistribution = new ConcurrentHashMap<Object, Map<Object,Float>>();
		try {
			initModel();
		} catch (Exception e) {
			AnalysisLogger.getLogger().error("error",e);
		}
		// probabilityBuffer = new Vector<String>();
		probabilityBuffer = new ConcurrentLinkedQueue<String>();

	}

	protected void initModel() throws Exception {
		Properties p = AlgorithmConfiguration.getProperties(config.getConfigPath() + AlgorithmConfiguration.algorithmsFile);
		String objectclass = p.getProperty(config.getModel());
		distributionModel = (SpatialProbabilityDistributionGeneric) Class.forName(objectclass, true, config.getAlgorithmClassLoader()).newInstance();
		distributionModel.init(config);
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

	

	public void shutdown() {
		// shutdown threads
		executorService.shutdown();
		// shutdown connection
		stopInterrupt = true;
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
			AnalysisLogger.getLogger().trace("generate->Take features reference");
			// take the area reference vectors
			environmentVectors = distributionModel.getGeographicalInfoObjects();
			if ((environmentVectors ==null) ||(environmentVectors.size()==0))
				throw new Exception("Empty Features Set");
			// calculate the number of chunks needed
			spaceVectorsNumber = environmentVectors.size();
			AnalysisLogger.getLogger().trace("generate->Features to calc: "+spaceVectorsNumber);

			AnalysisLogger.getLogger().trace("generate->Take groups references");
			List<Object> speciesVectors = distributionModel.getMainInfoObjects();
			int speciesVectorNumber = speciesVectors.size();
			AnalysisLogger.getLogger().trace("generate->Number of groups of features: "+speciesVectorNumber);
			
			// calculate number of chunks to take into account
			chunksize = spaceVectorsNumber / numberOfThreadsToUse;
			if (chunksize == 0)
				chunksize = 1;
			int numOfChunks = spaceVectorsNumber / chunksize;
			if ((spaceVectorsNumber % chunksize) != 0)
				numOfChunks += 1;

			AnalysisLogger.getLogger().trace("generate->Calculation Started with " + numOfChunks + " chunks and " + speciesVectorNumber + " groups - chunk size will be "+chunksize);
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
				AnalysisLogger.getLogger().trace("-> species " + distributionModel.getMainInfoID(species) + " - n. " + (processedSpeciesCounter + 1));
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
			// REPORT OVERALL ELAPSED TIME
			AnalysisLogger.getLogger().trace("generate-> Storing Probability Distribution");
			try{
				distributionModel.storeDistribution(completeDistribution);
			}catch(Exception ee){
				AnalysisLogger.getLogger().trace("generate-> Error Storing Probability Distribution ",ee);
			}
			try{
				distributionModel.postProcess();
			}catch(Exception eee){}
			try{
			// shutdown all
			shutdown();
			}catch(Exception eeee){}
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
			 AnalysisLogger.getLogger().trace("threadCalculation->" + (threadIndex+1));
			 int max = Math.min(spaceindex + chunksize, spaceVectorsNumber);
			String speciesID = distributionModel.getMainInfoID(speciesVector);
			 AnalysisLogger.getLogger().trace("threadCalculation-> calculating elements from "+spaceindex+" to " + max +" for species "+speciesID);
			Map<Object,Float> geoDistrib = completeDistribution.get(speciesID);
			//if the map is null then generate a new map, otherwise update it
			if (geoDistrib==null){
				geoDistrib = new ConcurrentHashMap<Object, Float>();
				completeDistribution.put(speciesVector, geoDistrib);
			}
			
			for (int i = spaceindex; i < max; i++) {
				Object enfeatures = environmentVectors.get(i);
				float prob = distributionModel.calcProb(speciesVector, enfeatures);
//				String geographicalID = distributionModel.getGeographicalID(environmentVectors.get(i));
				
				//record the overall probability distribution
				geoDistrib.put(enfeatures, prob);
		
				processedRecordsCounter++;
			}
			AnalysisLogger.getLogger().trace("FINISHED");
			threadActivity[threadIndex] = false;
			return 0;
		}

	}

	@Override
	public ALG_PROPS[] getSupportedAlgorithms() {
		ALG_PROPS[] p = {ALG_PROPS.PHENOMENON_VS_GEOINFO};
		return p;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public List<StatisticalType> getInputParameters() {
//		return distributionModel.getInputParameters();
		return new ArrayList<StatisticalType>();
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
		return "A generator which splits a distribution on different threads along the species dimension";
	}

	

}
