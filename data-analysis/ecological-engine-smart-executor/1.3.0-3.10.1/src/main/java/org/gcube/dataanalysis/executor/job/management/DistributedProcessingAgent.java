package org.gcube.dataanalysis.executor.job.management;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.ecoengine.configuration.ALG_PROPS;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.ResourceLoad;
import org.gcube.dataanalysis.ecoengine.connectors.livemonitor.Resources;
import org.gcube.dataanalysis.ecoengine.utils.Operations;

import com.thoughtworks.xstream.XStream;

public class DistributedProcessingAgent {

	
	protected QueueJobManager jobManager;
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
	protected Serializable configurationFile;
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
	public DistributedProcessingAgent(Serializable configurationFile, 
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
		this.configurationFile=configurationFile;
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
	
	public void setEndPoints(List<String> endpoints){
		this.endpoints=endpoints;
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
			if (endpoints != null) {
				
				/*
				List<EndpointReferenceType> eprtList = new ArrayList<EndpointReferenceType>();
				for (String ep : endpoints) {
					eprtList.add(new EndpointReferenceType(new Address(ep)));
				}
				
				jobManager = new QueueJobManager(gscope, endpoints.size(), eprtList);
				*/
				jobManager = new QueueJobManager(gscope, endpoints.size(), endpoints,sessionID);
			} else
				jobManager = new QueueJobManager(gscope, 1,sessionID);

			int numberOfResources = jobManager.getNumberOfNodes();
			// we split along right dimension so if elements are less than nodes, we should reduce the number of nodes
			if (numberOfResources > 0) {
				// chunkize the number of species in order to lower the computational effort of the workers
				subdivisiondiv = rightSetNumberOfElements / (numberOfResources * maxElementsAllowedPerJob);
				int rest = rightSetNumberOfElements % (numberOfResources * maxElementsAllowedPerJob);
				if (rest > 0)
					subdivisiondiv++;
				if (subdivisiondiv == 0)
					subdivisiondiv = 1;
				
				executeWork(leftSetNumberOfElements, rightSetNumberOfElements, 0, subdivisiondiv, deletefiles, forceUpload);

				if (jobManager.wasAborted()) {
					logger.debug("Warning: Job was aborted");
//					distributionModel.postProcess(false,true);
					throw new Exception("Job System Error");
				}
				else{
					//postprocess
//					distributionModel.postProcess(jobManager.hasResentMessages(),false);
				}
				
			} else {
				logger.debug("Warning: No Workers available");
				throw new Exception("No Workers available");
			}
			
		} catch (Exception e) {
			logger.error("ERROR: An Error occurred ", e);
			e.printStackTrace();
			throw e;
		} finally {
			shutdown();
		}
	}

	private void executeWork(int leftNum, int rightNum, int offset, int numberOfResources, boolean deletefiles, boolean forceUpload) throws Exception {
		
		String owner = userName;

		int[] chunkSizes = Operations.takeChunks(rightNum, numberOfResources);
		List<String> arguments = new ArrayList<String>();
		// chunkize respect to the cells: take a chunk of cells vs all species at each node!
		
		for (int i = 0; i < chunkSizes.length; i++) {
			String argumentString = "0 " + leftNum + " " + offset + " " + chunkSizes[i] + " ./ "+mainclass;
			arguments.add(argumentString);
			offset  += chunkSizes[i];
			logger.debug("Generator-> Argument " + i + ": " + argumentString);
		}
		
		if (owner == null)
			throw new Exception("Null Owner");
		
		String pathToDir = new File (pathToLib, containerFolder).getAbsolutePath();
		
		if (!(new File(pathToDir).exists()))
			throw new Exception("No Implementation of node-model found for algorithm " + pathToDir);

		if (mainclass == null)
			throw new Exception("No mainClass found for algorithm " + pathToDir);

		buildScriptFile(modelName, defaultJobOutput, pathToDir, mainclass);

		jobManager.uploadAndExecuteChunkized(AlgorithmConfiguration.StatisticalManagerClass, AlgorithmConfiguration.StatisticalManagerService, owner, pathToDir, "/" + modelName + "/", "./", getScriptName(mainclass), arguments, new XStream().toXML(configurationFile), deletefiles, forceUpload);

	}

	private String getScriptName(String fullMainClass){
		String scriptName = defaultScriptFile+"_"+fullMainClass.substring(fullMainClass.lastIndexOf(".")+1)+".sh";
		return scriptName;
	}
	// builds a job.sh
	public void buildScriptFile(String jobName, String jobOutput, String jarsPath, String fullMainClass) throws Exception {
		File expectedscript = new File(jarsPath,getScriptName(fullMainClass));
		if (!expectedscript.exists()) {
			StringBuffer sb = new StringBuffer();
			sb.append("#!/bin/sh\n");
			sb.append("# " + jobName + "\n");
			sb.append("cd $1\n");
			sb.append("\n");
			sb.append("java -Xmx1024M -classpath ./:");
			File jarsPathF = new File(jarsPath);
			File[] files = jarsPathF.listFiles();

			for (File jar : files) {

				if (jar.getName().endsWith(".jar")) {
					sb.append("./" + jar.getName());
					sb.append(":");
				}
			}

			sb.deleteCharAt(sb.length() - 1);
			sb.append(" " + fullMainClass + " $2 " + jobOutput);
			sb.append("\n");

			AnalysisLogger.getLogger().trace("D4ScienceGenerator->Generating script in " + expectedscript.getAbsolutePath());
			FileTools.saveString(expectedscript.getAbsolutePath(), sb.toString(), true, "UTF-8");
		}
		AnalysisLogger.getLogger().trace("D4ScienceGenerator->Script " + expectedscript.getAbsolutePath()+" yet exists!");
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
			rs = new ResourceLoad(tk, jobManager.currentNumberOfStages*subdivisiondiv);
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
			processedRecords = jobManager.currentNumberOfStages*subdivisiondiv;
		
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
