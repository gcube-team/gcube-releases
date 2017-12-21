package org.gcube.dataanalysis.executor.job.management;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.executor.util.InfraRetrieval;

public class WPSJobManager {

	static final int pollingTime = 5000;
	static final long maxTaskTime= 12*60000; //allowed max 12 hours per task
	
	
	int overallFailures = 0;
	int overallSuccess = 0;
	int overallTasks = 0;
	int nservices = -1;
	
	boolean stopThreads  = false;
	boolean hasResentMessages  = false;
	
	
	public static String getCallTemplate(){
		String call = null;
		try{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/WPSGWTemplate2.xml");
		AnalysisLogger.getLogger().debug("WPSJobManager->GW template Input stream is null "+(is==null));
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		String line = null;
		StringBuilder vud = new StringBuilder();

		while ((line = in.readLine()) != null) {
			vud.append(line + "\n");
		}
		
		in.close();
		
		call = vud.toString();
		
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return call;
	} 
	
	final public synchronized void incrementOverallFailures() {
		overallFailures++;
	}
	
	final public synchronized void hasResentTrue() {
		if (!hasResentMessages)
			hasResentMessages=true;
	}
	
	final public synchronized void incrementOverallSuccess() {
		overallSuccess++;
	}
	
	final public synchronized void stop() {
		stopThreads=true;
	}
	
	final public synchronized boolean isStopped() {
		return stopThreads;
	}
	
		public class TasksWatcher implements Runnable {
		AlgorithmConfiguration configuration;
		String algorithm;
		String username;
		String token;
		String wpsHost;
		int wpsPort;
		int taskNumber;
		String session;
		public String exitstatus=GenericWorker.TASK_UNDEFINED;
		int leftSetIndex;
		int rightSetIndex;
		int leftElements;
		int rightElements;
		String callTemplate;
		int maxTrialsPerThread;
		
		public TasksWatcher(String algorithm, String username, String token, String wpsHost, int wpsPort, String session, int taskNumber, AlgorithmConfiguration configuration, int leftSetIndex, int rightSetIndex, int leftElements, int rightElements,String callTemplate, int maxTrialsPerThread) {
			this.algorithm = algorithm;
			this.token = token;
			this.wpsHost = wpsHost;
			this.wpsPort = wpsPort;
			this.taskNumber = taskNumber;
			this.session = session;
			this.username = username;
			this.configuration = configuration;
			this.leftSetIndex = leftSetIndex;
			this.leftElements = leftElements;
			this.rightSetIndex = rightSetIndex;
			this.rightElements = rightElements;
			this.callTemplate=callTemplate;
			this.maxTrialsPerThread=maxTrialsPerThread;
		}

		
		public void callTask(boolean isduplicate){
			String url = "http://" + wpsHost + ":" + wpsPort + "/wps/WebProcessingService";
			
			boolean deleteTemporaryFiles = true;
			AnalysisLogger.getLogger().debug("WPSJobManager->Task Number : " + taskNumber+" GO!");
			try {
				AnalysisLogger.getLogger().debug("WPSJobManager->Invoking the GW to start");
				String algorithmCall = GenericWorkerCaller.getGenericWorkerCall(algorithm, session, configuration, leftSetIndex, rightSetIndex, leftElements, rightElements, isduplicate, deleteTemporaryFiles,callTemplate);
				String result = HttpRequest.PostXmlString(url, wpsHost, wpsPort, new LinkedHashMap<String, String>(), username, token, algorithmCall);
				AnalysisLogger.getLogger().debug("WPSJobManager->GW starting Output " + result.replace("\n", ""));

				boolean success = false;
				boolean failure = false;

				if (result.contains(GenericWorker.TASK_SUCCESS))
					success = true;
				else if (result.contains(GenericWorker.TASK_FAILURE))
					failure = true;

				String statusLocation = "";
				long taskTimeCounter = 0;
				while (!success && !isStopped() && (!failure) ) { //while !success and failure
					if (result == null || result.contains(GenericWorker.TASK_FAILURE))
						failure = true;
					else if (taskTimeCounter>maxTaskTime){
						failure = true;
					}
					else if (result.contains(GenericWorker.TASK_SUCCESS))
						success = true;
					else if (result.contains("<wps:ProcessAccepted>Process Accepted</wps:ProcessAccepted>")) {
						if (result.contains("<ows:Exception ")) {
							failure = true;	
						}else{
						statusLocation = result.substring(result.indexOf("statusLocation=") + "statusLocation=".length());
						statusLocation = statusLocation.substring(0, statusLocation.indexOf(">"));
						statusLocation = statusLocation.replace("\"", "");
						statusLocation = statusLocation + "&gcube-token=" + token;
//						AnalysisLogger.getLogger().debug("Status Location: " + statusLocation);
						result= "";
						}
					} else {
						Thread.sleep(pollingTime);
						taskTimeCounter+=pollingTime;
						result = HttpRequest.sendGetRequest(statusLocation, "");
//						AnalysisLogger.getLogger().debug("Result in location: " + result);
					}
					// request = HttpRequest.sendGetRequest(url, ""); // AnalysisLogger.getLogger().debug("Answer for task "+taskNumber+": "+request); }catch(Exception e){ AnalysisLogger.getLogger().debug("Request failure for task "+taskNumber+": "+e.getLocalizedMessage()); } if (request.contains("<wps:ProcessSucceeded>")) success = true; if (request.contains("<ows:Exception>")){ failure = true; incrementOverallFailures(); } try { Thread.sleep(pollingTime); } catch (InterruptedException e) { e.printStackTrace(); } }
				}

				if (isStopped() && statusLocation!=null && statusLocation.length()>0){
					String wpscancel = statusLocation.replace("RetrieveResultServlet", "CancelComputationServlet");
					result = HttpRequest.sendGetRequest(wpscancel, "");
				}
					
					
				exitstatus = GenericWorker.TASK_SUCCESS;
				if (failure)
				{
					exitstatus = GenericWorker.TASK_FAILURE;
					AnalysisLogger.getLogger().debug("WPSJobManager->Task Number "+taskNumber+" - Failure cause: " + URLDecoder.decode(result,"UTF-8"));
				}
//				AnalysisLogger.getLogger().debug("Process execution finished: " + exitstatus);
				
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().error("WPSJobManager->Task Number "+taskNumber+" - Process exception ", e);
				exitstatus = GenericWorker.TASK_FAILURE;
				
			}finally{
				
			}
		}
		@Override
		public void run() {
			int trials = 0;
			boolean duplicate = false;
			while (!exitstatus.equals(GenericWorker.TASK_SUCCESS) && trials<maxTrialsPerThread){
				callTask(duplicate);
				if (exitstatus.equals(GenericWorker.TASK_FAILURE)){
					trials++;
					hasResentTrue();
					duplicate = true;
					AnalysisLogger.getLogger().debug("WPSJobManager->Task Number "+taskNumber+" - Retrying n."+trials);
				}
			}
			
			if (exitstatus.equals(GenericWorker.TASK_SUCCESS))
				incrementOverallSuccess();
			else
				incrementOverallFailures();
			
			AnalysisLogger.getLogger().debug("WPSJobManager->Task Number "+taskNumber+" - Finished: " + exitstatus);
			
		}
	}
		
		public int getNumberOfNodes() {
			return 1;
		}
		
		public int getActiveNodes() {
			return 1;
		}
		
		public float getStatus() {
			return (float)(overallFailures+overallSuccess)/(float)overallTasks;
		}
		
		public boolean wasAborted() {
			return stopThreads;
		}
		
		public boolean hasResentMessages() {
			return hasResentMessages;
		}
		
		public int estimateNumberOfServices(String scope) throws Exception{
			List<String> wpsservices = InfraRetrieval.retrieveService("DataMiner", scope);
			
			if (wpsservices==null || wpsservices.size()==0){
				AnalysisLogger.getLogger().debug("WPSJobManager->Error: No DataMiner GCore Endpoints found!");
				throw new Exception ("No DataMinerWorkers GCore Endpoint found in the VRE "+scope);
			}
			List<String> differentServices = new ArrayList<String>();
			for (String service:wpsservices){
				
				service = service.substring(service.indexOf("/")+2);
				service = service.substring(0,service.indexOf(":"));
				if (!differentServices.contains(service))
					differentServices.add(service);
				
			}
			
			int numberofservices = differentServices.size();
			AnalysisLogger.getLogger().debug("WPSJobManager->Number of found services "+numberofservices);
			nservices = Math.max(1,numberofservices-1);
			return nservices;
		} 
		
		public void uploadAndExecuteChunkized(AlgorithmConfiguration configuration, String algorithmClass, List<String> arguments, String session) throws  Exception{
			ExecutorService executor = null;
			try{
			int numberofservices = 1;
			String callTemplate = getCallTemplate();
			
			AnalysisLogger.getLogger().debug("WPSJobManager->Estimating the number of services");
			if (nservices>0)
				numberofservices = nservices;
			else
				numberofservices = estimateNumberOfServices(configuration.getGcubeScope());
			
			AnalysisLogger.getLogger().debug("WPSJobManager->Number of dataminer services "+numberofservices);
			int parallelisation = numberofservices*2; 
			AnalysisLogger.getLogger().debug("WPSJobManager->Number of parallel processes (parallelisation) : "+parallelisation);
			
			//List<String> wpshosts = InfraRetrieval.retrieveAddresses("DataAnalysis",configuration.getGcubeScope(),"-----");
			List<String> wpshosts = InfraRetrieval.retrieveServiceAddress("DataAnalysis","DataMinerWorkers",configuration.getGcubeScope(),"noexclusion");
			
			if (wpshosts==null || wpshosts.size()==0){
				AnalysisLogger.getLogger().debug("WPSJobManager->Error: No DataMinerWorkers Service Endpoints found at all!");
				throw new Exception ("WPSJobManager->No Dataminer Workers Service Endpoint found in the VRE - DataMinerWorkers Resource is required in the VRE"+configuration.getGcubeScope());
			}
			
			String wpshost = wpshosts.get(0);
					
			wpshost = wpshost.substring(wpshost.indexOf("/")+2);
			//String wpshostAddress = wpshost.substring(0,wpshost.indexOf(":"));
			String wpshostAddress = wpshost.substring(0,wpshost.indexOf("/"));
			//String wpshostPort = wpshost.substring(wpshost.indexOf(":")+1,wpshost.indexOf("/"));
			//http://dataminer1-devnext.d4science.org:80/wps/gcube/resourc
			wpshost=wpshostAddress;	
			int wpsport = 80;
			overallTasks=arguments.size();
			
			AnalysisLogger.getLogger().debug("WPSJobManager->Workers WPS host "+wpshost);
			
			 executor = Executors.newFixedThreadPool(nservices);
//			executor = Executors.newFixedThreadPool(2);
			 int taskNumber = 0;
			 
			 AnalysisLogger.getLogger().debug("WPSJobManager->Executing algorithm class:"+algorithmClass);
			 	 
			 
			 for (String argument:arguments) {
				 		String[] lfnlnr = argument.split(" ");
				 		int leftOff = Integer.parseInt(lfnlnr[0]);
				 		int leftNum = Integer.parseInt(lfnlnr[1]);
				 		int rightOff = Integer.parseInt(lfnlnr[2]);
				 		int rightNum = Integer.parseInt(lfnlnr[3]);
				 		int maxTrials = parallelisation;
				 		TasksWatcher watcher = new TasksWatcher(algorithmClass, 
				 		configuration.getGcubeUserName(), 
				 		configuration.getGcubeToken(),
				 		wpshost,wpsport,session,taskNumber,configuration, leftOff, rightOff,leftNum,rightNum,callTemplate, maxTrials);
				 		
			 	        executor.execute(watcher);
			 	       AnalysisLogger.getLogger().debug("WPSJobManager->Task number "+taskNumber+" launched with arguments: "+argument);
			 	       taskNumber++;
			 	       Thread.sleep(1000);
			 }
			 
			 int njobs =  overallFailures+overallSuccess;
			 int pnjobs =njobs;  
			 
			 while (njobs<overallTasks){
				 Thread.sleep(pollingTime);
				 float percFailure = (float)(overallFailures)/(float)overallTasks;
				 //if (percFailure>0.5)
				 if (overallFailures>0)
					 stop();
				 njobs =  overallFailures+overallSuccess;
				 if (pnjobs<njobs){
					 AnalysisLogger.getLogger().debug("WPSJobManager->Number of finished jobs "+njobs+" of "+overallTasks);
					 AnalysisLogger.getLogger().debug("WPSJobManager->Number of errors "+overallFailures+" - perc failure "+percFailure);
					 pnjobs=njobs;
				 }
			 }
			 
			 AnalysisLogger.getLogger().debug("WPSJobManager->Overall computation finished");
			}catch(Exception e){
				e.printStackTrace();
				throw e;
			}
			finally{
				if (executor!=null){
					AnalysisLogger.getLogger().debug("WPSJobManager->Shutting down the executions");
					executor.shutdown();
					AnalysisLogger.getLogger().debug("WPSJobManager->Shut down completed");
				}
			}
			
		}

}
