package org.gcube.dataanalysis.executor.job.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.dataanalysis.executor.scripts.ScriptIOWorker;
import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.plugins.query.filter.SpecificEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import org.gcube.vremanagement.executor.plugin.PluginState;

public class RemoteJobManager {

	// TODO Chek here:
	//private static String pluginName = "ExecutorScript";
	private static String pluginName = "SmartGenericWorker";
	
	private int actualNumberOfNodes;
	private List<String> eprs;
	float status;
	boolean abort;
	boolean shutdown;
	protected int activeNodes;
	String scope;

	public int getActiveNodes() {
		return activeNodes;
	}

	public float getStatus() {
		return status;
	}

	public int getNumberOfNodes() {
		return actualNumberOfNodes;
	}

	public void setNumberOfNodes(int newNumberOfNodes) {
		actualNumberOfNodes = newNumberOfNodes;
	}

	public void init(String scope, int numberOfNodes) throws Exception {
		this.scope = scope;
		AnalysisLogger.getLogger().debug("Using the following scope for this computation: "+ scope);
		shutdown = false;
		yetuploaded = false;
		if (eprs == null)
			actualNumberOfNodes = findNodes(scope);
		else
			actualNumberOfNodes = eprs.size();

		if (numberOfNodes < actualNumberOfNodes)
			actualNumberOfNodes = numberOfNodes;

	}

	public RemoteJobManager(String scope, int numberOfNodes) throws Exception {
		init(scope, numberOfNodes);
	}

	public RemoteJobManager(String scope, int numberOfNodes, List<String> eprs) throws Exception {
		this.eprs = eprs;
		init(scope, numberOfNodes);
	}

	List<String> filenames;
	List<String> fileurls;
	boolean yetuploaded;
	String session;
	
	@SuppressWarnings("unchecked")
	public boolean uploadAndExecute(String serviceClass, String serviceName, String owner, String localDir, String remoteDir, String outputDir, String script, List<String> arguments, boolean deletefiles) throws Exception {
		boolean executeAll = false;
		long t0 = System.currentTimeMillis();
		//if not yet uploaded , upload required files
		if (!yetuploaded) {
			ScopeProvider.instance.set(scope);
			IClient client = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED).getClient();
//			IClient client = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, gscope).getClient();
			File dir = new File(localDir);
			File[] files = dir.listFiles();
			AnalysisLogger.getLogger().debug("Start uploading");
			filenames = new ArrayList<String>();
			fileurls = new ArrayList<String>();
			for (File sfile : files) {
				String localf = sfile.getAbsolutePath();
				String filename = sfile.getName();
				String remotef = remoteDir + sfile.getName();
				client.put(true).LFile(localf).RFile(remotef);
				String url = client.getUrl().RFile(remotef);
				AnalysisLogger.getLogger().debug("URL created: " + url);
				filenames.add(filename);
				fileurls.add(url);
			}
			AnalysisLogger.getLogger().debug("Upload end");
			yetuploaded = true;
			session = (""+UUID.randomUUID()).replace("-", "");
		}
		
		//if the number of available nodes is higher than zero launch the tasks
		if (actualNumberOfNodes > 0) {

			AnalysisLogger.getLogger().debug("Executing script on " + actualNumberOfNodes + " nodes");
			int len = arguments.size();
			List<WorkerWatcher> tasksProxies = new ArrayList<WorkerWatcher>();
			activeNodes = 0;
			//launch the tasks
			for (int i = 0; i < actualNumberOfNodes; i++) {
				String argum = "";
				//supply the arguments if they are available
				if (i < len)
					argum = arguments.get(i);
				//generate the input map according to the arguments
				Map<String, Object> inputs = generateInput(filenames, fileurls, outputDir, script, argum, i, scope, serviceClass, serviceName, owner, remoteDir,session,deletefiles);
				AnalysisLogger.getLogger().debug("-> Owner: " + owner + " ServiceClass: " + serviceClass + " ServiceName:" + serviceName + " remoteDir:" + remoteDir);
				
				
				//take the i-th endpoint of the executor
				String selectedEPR = eprs.get(i);
				AnalysisLogger.getLogger().debug("Launching node " + (i + 1) + " on " + selectedEPR);
				//run the executor script
				
				/*
				ExecutorCall call = new ExecutorCall(pluginName, gscope);
				call.setEndpointReference(selectedEPR);
				TaskCall task = null;
				task = call.launch(inputs);
				TaskProxy proxy =  task.getProxy();
				*/
				
				ExecutorPlugin runExecutorPlugin = new ExecutorPlugin();
				SmartExecutorPluginQuery runQuery = new SmartExecutorPluginQuery(runExecutorPlugin);
				
				/* TODO Add key_value filter here
				 * Tuple<String, String>[] tuples = new Tuple[n];
				 * 
				 * runQuery.addConditions(pluginName, tuples);
				 */
				runQuery.addConditions(pluginName);
				SpecificEndpointDiscoveryFilter sedf = new SpecificEndpointDiscoveryFilter(selectedEPR);
				runQuery.setEndpointDiscoveryFilter(sedf);
				SmartExecutorProxy proxy = new ProxyBuilderImpl<SmartExecutor, SmartExecutorProxy>(runExecutorPlugin, runQuery).build();
				
				
				LaunchParameter launchParameter = new LaunchParameter(pluginName, inputs);
				String excecutionIdentifier = proxy.launch(launchParameter);
				
				tasksProxies.add(new WorkerWatcher(proxy, excecutionIdentifier, AnalysisLogger.getLogger()));
				
				AnalysisLogger.getLogger().debug("Launching node " + (i + 1) + " OK on " + selectedEPR);
				//add the task to the list in order to reuse it
			}

			activeNodes = actualNumberOfNodes;
			AnalysisLogger.getLogger().debug("Launch Finished - Controlling Status");
			int allstatus = 0;
			abort = false;
			//control the execution: go until there are active nodes or the process must stop 
			while ((activeNodes != 0) && (!abort) && (!shutdown)) {
				//for each node get the task state
				int nworkers = tasksProxies.size();
				int i=0;
				while (i < nworkers) {
					WorkerWatcher proxy = tasksProxies.get(i);
					
					/* ---- */
					PluginState enumState = proxy.getState();
					String state = enumState.toString();
					/* ----- */
					
					
					AnalysisLogger.getLogger().debug("REMOTE JOB MANAGER-> STATE " + state );
					//control for aborted computation
					abort = ((state == null) || state.equals("FAILED") || (!state.equals("DONE") && !state.equals("RUNNING")));
					//control for finished computation
					boolean finished = false;
					if (state != null)
						finished = state.equals("DONE");
					//if finished update the active nodes 
					if (finished) {
						tasksProxies.remove(i);
						allstatus++;
						activeNodes--;
						nworkers--;
						if (activeNodes == 0)
							break;
					}
					else
						i++;
					
					status = Math.min(((float) allstatus / (float) actualNumberOfNodes) * 100f, 95f);
					if (abort)
						break;
					if (shutdown)
						break;
					// AnalysisLogger.getLogger().debug(String.format("Task " + i + "executed started at %Tc with %s state ", proxy.getStartTime(), state));
					//sleep before polling again
					Thread.sleep(2000);
				}
			}
			
			activeNodes = 0;

			AnalysisLogger.getLogger().debug("All Tasks have Finished");
			if (!abort) {
				AnalysisLogger.getLogger().debug("All Task were successful");
				/*
				 * List<StorageObject> listElements = client.showDir().RDir(remoteDir); for (StorageObject obj : listElements) { AnalysisLogger.getLogger().debug("obj stored in directory " + remoteDir + ": " + obj.getName()); }
				 */
			} else
				AnalysisLogger.getLogger().debug("Tasks were NOT successful");
		} else
			AnalysisLogger.getLogger().debug("Warning: could not execute tasks: No Nodes Available!");
		AnalysisLogger.getLogger().debug("Whole procedure done in " + (System.currentTimeMillis() - t0) + " ms");
		status = 100f;
		return executeAll;
	}
	
	public boolean wasAborted() {
		return abort;
	}

	public void stop() {
		shutdown = true;
	}

	@SuppressWarnings("unchecked")
	private List<EndpointReference> getFilteredEndpoints(String scopeString){
		ScopeProvider.instance.set(scopeString);
		
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		
		/*
		Tuple<String, String>[] tuples = new Tuple[1];
		tuples[0] = new Tuple<String, String>("Version", "1.0.0-SNAPSHOT");
		query.addConditions("SmartGenericWorker", tuples);
		*/
		
		query.addConditions(pluginName);
		
		/* Used to add extra filter to ServiceEndpoint discovery */
		query.setServiceEndpointQueryFilter(null);
		
		/* Used to add extra filter to GCore Endpoint discovery */
		query.setEndpointDiscoveryFilter(null);
		
		
		return query.fire();
	}
	
	
	private int findNodes(String scopeString) throws Exception {
		return getFilteredEndpoints(scopeString).size();
	}
	
	/*
	private int findNodes(String scopeString) throws Exception {
		GCUBEScope scope = GCUBEScope.getScope(scopeString);
		ISClient client = GHNContext.getImplementation(ISClient.class);
		WSResourceQuery wsquery = client.getQuery(WSResourceQuery.class);
		wsquery.addAtomicConditions(new AtomicCondition("//gc:ServiceName", "Executor"));
		wsquery.addAtomicConditions(new AtomicCondition("/child::*[local-name()='Task']/name[text()='"+pluginName+"']", pluginName));
		List<RPDocument> listdoc = client.execute(wsquery, scope);
		EndpointReferenceType epr = null;
		eprs = new ArrayList<EndpointReferenceType>();
		int numberOfEP = 0;
		for (RPDocument resource : listdoc) {
			epr = resource.getEndpoint();
			numberOfEP++;
			eprs.add(epr);
		}
		AnalysisLogger.getLogger().debug("Found " + numberOfEP + " endpoints");

		return numberOfEP;
	}
	*/
	
	private Map<String, Object> generateInput(Object filenames, Object fileurls, String outputDir, String script, String argum, int i, String scope, String serviceClass, String serviceName, String owner, String remoteDir,String session,boolean deletefiles) {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("FILE_NAMES", filenames);
		inputs.put("FILE_URLS", fileurls);
		inputs.put("OUTPUTDIR", ScriptIOWorker.toInputString(outputDir));
		inputs.put("SCRIPT", ScriptIOWorker.toInputString(script));
		inputs.put("ARGUMENTS", ScriptIOWorker.toInputString(argum));
		inputs.put("NODE_IDENTIFIER", "" + i);
		inputs.put("SCOPE", ScriptIOWorker.toInputString(scope));
		inputs.put("SERVICE_CLASS", ScriptIOWorker.toInputString(serviceClass));
		inputs.put("SERVICE_NAME", ScriptIOWorker.toInputString(serviceName));
		inputs.put("OWNER", ScriptIOWorker.toInputString(owner));
		inputs.put("REMOTEDIR", ScriptIOWorker.toInputString(remoteDir));
		inputs.put("CLEAN_CACHE",""+deletefiles);
//		inputs.put("SESSION", ScriptIO.toInputString(session));
		return inputs;
	}
}
