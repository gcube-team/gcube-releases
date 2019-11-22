package org.gcube.dataanalysis.executor.job.management;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.ResourceLoad;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.Resources;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.apache.log4j.Logger;

public class DistributedProcessingAgentWPS {

	
	protected WPSJobManager jobManager;
	protected boolean deletefiles = true;
	protected String mainclass;
	public int maxElementsAllowedPerJob = 20;
	protected boolean forceUpload = true;
	protected boolean stop;
	protected String gscope;
	protected String userName;
	protected String pathToLib;
	protected String modelName;
	protected String containerFolder;
	protected AlgorithmConfiguration configuration;
	protected int rightSetNumberOfElements;
	protected int leftSetNumberOfElements;
	protected List<String> endpoints;
	protected int subdivisiondiv;
	protected String sessionID;
	
	protected static String defaultJobOutput = "execution.output";
	protected static String defaultScriptFile = "script";
	protected Logger logger;
	
	/**
	 * A distributed processing agent. Performs a distributed computation doing the MAP of the product of two sets: A and B
	 * Splits over B : A x B1 , A x B2, ... , A x Bn
	 * Prepares a script to be executed on remote nodes
	 * The computation is then sent to remote processors.
	 */
	public DistributedProcessingAgentWPS(AlgorithmConfiguration configuration, 
																String gCubeScope, 
																String computationOwner,
																String mainClass, 
																String pathToLibFolder,
																String modelName,
																String containerFolder,
																int maxElementsPerJob, 
																boolean forceReUploadofLibs,
																int leftSetNumberOfElements,
																int rightSetNumberOfElements,
																String sessionID
																) {
		this.stop = false;
		this.deletefiles = true;
		this.gscope=gCubeScope;
		this.mainclass=mainClass;
		this.maxElementsAllowedPerJob=maxElementsPerJob;
		this.forceUpload=forceReUploadofLibs;
		this.configuration=configuration;
		this.rightSetNumberOfElements=rightSetNumberOfElements;
		this.leftSetNumberOfElements=leftSetNumberOfElements;
		this.userName=computationOwner;
		this.pathToLib=pathToLibFolder;
		this.modelName=modelName;
		this.containerFolder=containerFolder;
		this.sessionID = sessionID;
	}

	public void setLogger(Logger logger){
		this.logger=logger;
	}
	
	
	public boolean hasResentMessages(){
		return jobManager.hasResentMessages();
	}
	
	public void compute() throws Exception {
		try {
			if (logger == null){
				logger = AnalysisLogger.getLogger();
			}
			if (gscope == null)
				throw new Exception("Null Scope");
			AnalysisLogger.getLogger().debug("SCOPE: "+gscope);

			jobManager = new WPSJobManager();
			// we split along right dimension so if elements are less than nodes, we should reduce the number of nodes
			// chunkize the number of species in order to lower the computational effort of the workers
			int nservices = jobManager.estimateNumberOfServices(configuration.getGcubeScope()); 
			//subdivisiondiv = rightSetNumberOfElements / (maxElementsAllowedPerJob);
			subdivisiondiv = nservices; //rightSetNumberOfElements / (nservices); //RIGHT
//			subdivisiondiv =  rightSetNumberOfElements / (maxElementsAllowedPerJob); //rightSetNumberOfElements / (nservices);
			AnalysisLogger.getLogger().debug("Subdivision for the job "+subdivisiondiv);
			/*
			int rest = rightSetNumberOfElements % (nservices);
			if (rest > 0)
					subdivisiondiv++;
					*/
				if (subdivisiondiv == 0)
					subdivisiondiv = 1;
			
			executeWork(leftSetNumberOfElements, rightSetNumberOfElements, 0, subdivisiondiv, deletefiles, forceUpload);
			AnalysisLogger.getLogger().debug("The WPS job has been completely executed");
				if (jobManager.wasAborted()) {
					logger.debug("Warning: Job was aborted");
//					distributionModel.postProcess(false,true);
					throw new Exception("Job System Error");
				}
				else{
					//postprocess
//					distributionModel.postProcess(jobManager.hasResentMessages(),false);
				}
				
			
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("The WPS job got an error "+e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} finally {
			shutdown();
		}
	}

	private void executeWork(int leftNum, int rightNum, int offset, int numberOfResources, boolean deletefiles, boolean forceUpload) throws Exception {
		
		int[] chunkSizes = Operations.takeChunks(rightNum, numberOfResources);
		List<String> arguments = new ArrayList<String>();
		
		// chunkize respect to the cells: take a chunk of cells vs all species at each node!
		for (int i = 0; i < chunkSizes.length; i++) {
			String argumentString = "0 " + leftNum + " " + offset + " " + chunkSizes[i];
			arguments.add(argumentString);
			offset  += chunkSizes[i];
			logger.debug("Generator-> Argument " + i + ": " + argumentString);
		}

		jobManager.uploadAndExecuteChunkized(configuration,mainclass,arguments,sessionID);

	}

	public String getResources() {
		Resources res = new Resources();
		try {
			int activeNodes = jobManager.getActiveNodes();
			for (int i = 0; i < activeNodes; i++) {
				try {
					res.addResource("Worker_" + (i + 1), 100);
				} catch (Exception e1) {
				}
			}
		} catch (Exception e) {
			AnalysisLogger.getLogger().debug("D4ScienceGenerator->active nodes not ready");
		}
		if ((res != null) && (res.list != null))
			return HttpRequest.toJSon(res.list).replace("resId", "resID");
		else
			return "";
	}

	public float getStatus() {
		try {
			if (stop)
				return 100f;
			else
				if (jobManager!=null)
					return Math.max(0.5f, jobManager.getStatus() * 100f);
				else
					return 0;
		} catch (Exception e) {
			return 0f;
		}
	}

	public ALG_PROPS[] getSupportedAlgorithms() {
		ALG_PROPS[] p = { ALG_PROPS.PHENOMENON_VS_PARALLEL_PHENOMENON};
		return p;
	}

	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.D4SCIENCE;
	}

	public void shutdown() {
		
		try {
			jobManager.stop();
		} catch (Exception e) {
		}
		stop = true;
	}

	public String getLoad() {
		long tk = System.currentTimeMillis();
		ResourceLoad rs = null;
		if (jobManager!=null)
			rs = new ResourceLoad(tk, 1*subdivisiondiv);
		else
			rs = new ResourceLoad(tk, 0);
		return rs.toString();
	}

	private long lastTime;
	private int lastProcessed;
	public String getResourceLoad() {
		long thisTime = System.currentTimeMillis();
		int processedRecords = 0;
		if ((jobManager!=null) && (subdivisiondiv>0))
			processedRecords = 1*subdivisiondiv;
		
		int estimatedProcessedRecords = 0;
		if (processedRecords == lastProcessed) {
			estimatedProcessedRecords = Math.round(((float) thisTime * (float) lastProcessed) / (float) lastTime);
		} else {
			lastProcessed = processedRecords;
			estimatedProcessedRecords = lastProcessed;
		}
		lastTime = thisTime;
		ResourceLoad rs = new ResourceLoad(thisTime, estimatedProcessedRecords);
		return rs.toString();
	}

}
