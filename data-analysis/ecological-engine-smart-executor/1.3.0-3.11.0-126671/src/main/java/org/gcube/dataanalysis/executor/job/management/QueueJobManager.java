package org.gcube.dataanalysis.executor.job.management;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.ActiveMQConnection;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.resource.StorageObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.dataanalysis.ecoengine.utils.Operations;
import org.gcube.dataanalysis.executor.messagequeue.ATTRIBUTE;
import org.gcube.dataanalysis.executor.messagequeue.Consumer;
import org.gcube.dataanalysis.executor.messagequeue.Producer;
import org.gcube.dataanalysis.executor.messagequeue.QCONSTANTS;
import org.gcube.dataanalysis.executor.messagequeue.QueueManager;
import org.gcube.dataanalysis.executor.scripts.ScriptIOWorker;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vremanagement.executor.api.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.client.plugins.ExecutorPlugin;
import org.gcube.vremanagement.executor.client.plugins.query.SmartExecutorPluginQuery;
import org.gcube.vremanagement.executor.client.plugins.query.filter.ListEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.plugins.query.filter.SpecificEndpointDiscoveryFilter;
import org.gcube.vremanagement.executor.client.proxies.SmartExecutorProxy;
import static org.gcube.resources.discovery.icclient.ICFactory.*;

public class QueueJobManager {

	
	// broadcast message period
	public static int broadcastTimePeriod = 120000;
	// max silence before computation stops
	public static int maxSilenceTimeBeforeComputationStop = 10800000;
	// max number of retries per computation step
	public static int maxNumberOfComputationRetries = 1;
	// period for controlling a node activity
	public static int computationWatcherTimerPeriod = 120000;
	// max number of message to put in a queue
//	protected static int maxNumberOfMessages = 20;
	public static int maxNumberOfStages = Integer.MAX_VALUE;//10;
	// timeout for resending a message
	public static int queueWatcherMaxwaitingTime = QCONSTANTS.refreshStatusTime;// * 5;

	protected int maxFailureTries;
	private static String pluginName = "SmartGenericWorker";//"GenericWorker";

	protected String scope;
	protected String session;

	protected boolean yetstopped;
	protected boolean messagesresent;
	protected float status;
	protected boolean abort;
	protected boolean shutdown;

	protected List<String> eprs;
	protected int activeNodes;
	protected int computingNodes;
	protected int numberOfMessages;
	protected int totalNumberOfMessages;
	protected int actualNumberOfNodes;
	protected int totalNumberOfStages;
	public int currentNumberOfStages;

	// files management
	protected List<String> filenames;
	protected List<String> fileurls;

	// queue parameters
	protected String queueName;
	protected String queueResponse;
	protected String queueURL;
	protected String queueUSER;
	protected String queuePWD;
	protected org.gcube.dataanalysis.executor.messagequeue.Consumer consumer;
	protected Producer producer;

	Timer broadcastTimer;
	Timer computationWatcherTimer;
	ComputationTimerWatcher computationWatcher;
	String serviceClass;
	String serviceName;
	String owner;
	String localDir;
	String remoteDir;
	String outputDir;
	String script;
	List<String> arguments;
	String configuration;
	boolean deletefiles;
	StatusListener statuslistener;

	private void resetAllVars() {
		scope = null;

		yetstopped = false;
		messagesresent = false;
		status = 0;
		abort = false;
		shutdown = false;

		eprs = null;
		activeNodes = 0;
		computingNodes = 0;
		numberOfMessages = 0;

		actualNumberOfNodes = 0;
		filenames = null;
		fileurls = null;

		queueName = null;
		queueResponse = null;
		queueURL = null;
		queueUSER = null;
		queuePWD = null;
		consumer = null;
		producer = null;
		broadcastTimer = null;
		computationWatcherTimer = null;
		computationWatcher = null;
		serviceClass = null;
		serviceName = null;
		owner = null;
		localDir = null;
		remoteDir = null;
		outputDir = null;
		script = null;
		arguments = null;
		configuration = null;
		deletefiles = false;
		statuslistener = null;
	}

	public int getActiveNodes() {
		return computingNodes;
	}

	public float getStatus() {
		float innerStatus = 0;
		if (totalNumberOfMessages != 0)
			innerStatus = (1f - ((float) numberOfMessages / (float) totalNumberOfMessages));
		if (totalNumberOfStages == 0)
			return innerStatus;
		else {
			float offset = ((float) Math.max(currentNumberOfStages - 1, 0)) / (float) totalNumberOfStages;
			float status = offset + (innerStatus / (float) totalNumberOfStages);
			// AnalysisLogger.getLogger().info("stages: "+totalNumberOfStages+" inner status: "+innerStatus+" currentStage: "+currentNumberOfStages+" status: "+status);
			return status;
		}
	}

	// there is only one node from the client point of view
	public int getNumberOfNodes() {
		if (eprs.size() > 0)
			return 1;
		else
			return 0;
	}

	public void setNumberOfNodes(int newNumberOfNodes) {
		// ignore this setting in this case
	}

	private void init(String scope, int numberOfNodes) throws Exception {
		resetAllVars();
		// init scope variables
		this.scope = scope;
		// introduce a session
		// initialize flags
		shutdown = false;
		yetstopped = false;
		messagesresent = false;
		abort = false;
		// find all the nodes - initialize the eprs
		findNodes(scope);
	}

	public QueueJobManager(String scope, int numberOfNodes, String session) throws Exception {
		init(scope, numberOfNodes);
		this.session = session;
	}

	public QueueJobManager(String scope, int numberOfNodes, List<String> eprs, String session) throws Exception {
		init(scope, numberOfNodes);
		this.eprs = eprs;
		this.session = session;
	}

	private void setGlobalVars(String serviceClass, String serviceName, String owner, String localDir, String remoteDir, String outputDir, String script, List<String> arguments, String configuration, boolean deletefiles) {
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.owner = owner;
		this.localDir = localDir;
		this.remoteDir = remoteDir;
		this.outputDir = outputDir;
		this.script = script;
		this.arguments = arguments;
		this.configuration = configuration;
		this.deletefiles = deletefiles;
	}

	private int totalmessages = 0;

	public boolean uploadAndExecuteChunkized(String serviceClass, String serviceName, String owner, String localDir, String remoteDir, String outputDir, String script, List<String> arguments, String configuration, boolean deletefiles, boolean forceUpload) throws Exception {
		long t0 = System.currentTimeMillis();

		int elements = arguments.size();
		/*generic-worker
		 * int div = elements / (maxNumberOfMessages); int rest = elements % (maxNumberOfMessages); if (rest > 0) div++; if (div == 0) { div = 1; }
		 */
		if (session == null || session.length()==0)
			session = (("" + UUID.randomUUID()).replace("-", "") + Math.random()).replace(".", "");
		int[] chunkSizes = null;
		//up to 1120 species we don't make stages
		if (elements>maxNumberOfStages)
			chunkSizes = Operations.takeChunks(elements, maxNumberOfStages);
		else {
			chunkSizes = new int[1];
			chunkSizes[0]=elements;		
		}
		int allchunks = chunkSizes.length;
		totalNumberOfStages = allchunks;
		currentNumberOfStages = 0;
		int start = 0;
		totalmessages = 0;
		AnalysisLogger.getLogger().info("Starting the computation in  "+allchunks+" stages");
		for (int i = 0; i < allchunks; i++) {
			numberOfMessages = totalNumberOfMessages = 0;
			currentNumberOfStages++;
			int end = Math.min(elements, start + chunkSizes[i]);
			AnalysisLogger.getLogger().info("Computing the chunk number " + (i + 1) + " of " + allchunks + " between " + start + " and " + (end - 1));
			List<String> sublist = new ArrayList<String>();
			for (int j = start; j < end; j++)
				sublist.add(arguments.get(j));

			AnalysisLogger.getLogger().info("size sub:" + sublist.size());
			// totalmessages=totalmessages+sublist.size();
			uploadAndExecute(serviceClass, serviceName, owner, localDir, remoteDir, outputDir, script, sublist, configuration, deletefiles, forceUpload);
			if (abort)
				break;
			start = end;
			AnalysisLogger.getLogger().info("Processed chunk number " + (i + 1));

		}

		currentNumberOfStages = totalNumberOfStages;
		AnalysisLogger.getLogger().info("Finished computation on all chunks and messages " + totalmessages);
		AnalysisLogger.getLogger().info("Whole Procedure done in " + (System.currentTimeMillis() - t0) + " ms");
		return (!abort);
	}

	private boolean uploadAndExecute(String serviceClass, String serviceName, String owner, String localDir, String remoteDir, String outputDir, String script, List<String> arguments, String configuration, boolean deletefiles, boolean forceUpload) throws Exception {
		int numberOfRetries = maxNumberOfComputationRetries;
		boolean recompute = true;

		while ((numberOfRetries > 0) && (recompute)) {
			long t0 = System.currentTimeMillis();
			// if (numberOfRetries<maxNumberOfComputationRetries)
			init(scope, 1);

			AnalysisLogger.getLogger().info("Computation Try number " + (maxNumberOfComputationRetries + 1 - numberOfRetries));

			AnalysisLogger.getLogger().info("Contacting " + actualNumberOfNodes + " Nodes");
			// set globals
			setGlobalVars(serviceClass, serviceName, owner, localDir, remoteDir, outputDir, script, arguments, configuration, deletefiles);
			// initializing queue
			setQueueVariables();
			// if not yet uploaded , upload required files
			uploadFilesOnStorage(forceUpload);
			// broadcast a message to all executors for purging previous queues
			// purgeQueues();
			createClientProducer();
			broadcastListenCommandToExecutorNodes();

			maxFailureTries = activeNodes * 1;

			broadcastTimer = new Timer();
			broadcastTimer.schedule(new Broadcaster(), broadcastTimePeriod, broadcastTimePeriod);

			computationWatcherTimer = new Timer();
			computationWatcher = new ComputationTimerWatcher(maxSilenceTimeBeforeComputationStop);
			computationWatcherTimer.schedule(computationWatcher, computationWatcherTimerPeriod, computationWatcherTimerPeriod);

			// send all messages
			sendMessages();
			createClientConsumer();
			// wait for messages
			waitForMessages();

			AnalysisLogger.getLogger().info("Wait for message finished - checking result");
			if (numberOfMessages == 0) {
				AnalysisLogger.getLogger().info("All tasks have correctly finished!");
			}

			/*
			 * else{ AnalysisLogger.getLogger().info("Timeout - Warning Some Task is missing!"); for (int k=0;k<finishedChunks.length;k++){ if (finishedChunks[k]==0){ AnalysisLogger.getLogger().info("Sending Again message number " + k); Map<String, Object> inputs = generateInputMessage(filenames, fileurls, outputDir, script, arguments.get(k), k, scope, serviceClass, serviceName, owner, remoteDir, session, configuration, deletefiles); producer.sendMessage(inputs, 0); AnalysisLogger.getLogger().info("Sent Message " + k); } } waitForMessages(); if (numberOfMessages>0){ abort = true; } }
			 */

			// deleteRemoteFolder();
			// summary
			AnalysisLogger.getLogger().info("-SUMMARY-");
			for (int i = 0; i < totalNumberOfMessages; i++) {
				if (activeMessages[i])
					AnalysisLogger.getLogger().info("Error : the Message Number " + i + " Was Never Processed!");
				if (resentMessages[i] > 0) {
					messagesresent = true;
					AnalysisLogger.getLogger().info("Warning : the Message Number " + i + " Was resent " + resentMessages[i] + " Times");
				}
			}
			AnalysisLogger.getLogger().info("-SUMMARY END-");

			stop();
			AnalysisLogger.getLogger().info("Stopped");
			AnalysisLogger.getLogger().info("Single Step Procedure done in " + (System.currentTimeMillis() - t0) + " ms");
			activeNodes = 0;
			numberOfRetries--;
			if (abort) {
				recompute = true;
				if (numberOfRetries > 0)
					Thread.sleep(10000);
			} else
				recompute = false;
		}

		return (!abort);
	}

	public boolean hasResentMessages() {
		return messagesresent;
	}

	public void waitForMessages() throws Exception {
		AnalysisLogger.getLogger().info("Waiting...");
		while ((numberOfMessages > 0) && (!abort)) {
			Thread.sleep(2000);

			// long tcurrent = System.currentTimeMillis();
			// if ((tcurrent - waitTime) > maxwaitingTime) {
			// break;
			// }
		}
		AnalysisLogger.getLogger().info("...Stop - Abort?" + abort);
	}

	public boolean wasAborted() {
		return abort;
	}

	public void purgeQueues() throws Exception {
		AnalysisLogger.getLogger().info("Purging Queue");
		List<WorkerWatcher> tasksProxies = new ArrayList<WorkerWatcher>();
		for (int j = 0; j < actualNumberOfNodes; j++) {
			try {
				contactNodes(tasksProxies, j, queueName, queueUSER, queuePWD, queueURL, queueResponse, session, "true");
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().info("Error in purgin queue on node " + j);
			}
		}
		AnalysisLogger.getLogger().info("Queue Purged");
	}

	public void stop() {
		try {
			if (!yetstopped) {
				if (broadcastTimer != null) {
					AnalysisLogger.getLogger().info("Stopping Broadcaster");
					broadcastTimer.cancel();
					broadcastTimer.purge();
				}

				if (computationWatcherTimer != null) {
					AnalysisLogger.getLogger().info("Stopping Watcher");
					computationWatcherTimer.cancel();
					computationWatcherTimer.purge();
				}

				AnalysisLogger.getLogger().info("Purging Status Listener");

				if (statuslistener != null)
					statuslistener.destroyAllWatchers();

				AnalysisLogger.getLogger().info("Stopping Producer and Consumer");
				
				try{
				producer.stop();
				producer.closeSession();
				}catch(Exception e1){}
				try{
				consumer.stop();
				consumer.closeSession();
				}catch(Exception e2){}
				
				AnalysisLogger.getLogger().info("Purging Remote Queues");
				purgeQueues();

				yetstopped = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().info("Not completely stopped");
		}
	}

	@SuppressWarnings("unchecked")
	private void contactNodes(List<WorkerWatcher> tasksProxies, int order, String queueName, String queueUSER, String queuePWD, String queueURL, String queueResponse, String session, String purgeQueue) throws Exception {
		// generate the input map according to the arguments
		Map<String, Object> inputs = generateWorkerInput(queueName, queueUSER, queuePWD, queueURL, queueResponse, session, purgeQueue);
		AnalysisLogger.getLogger().info("Inputs " + inputs);
		// take the i-th endpoint of the executor
		String selectedEPR = eprs.get(order);
		AnalysisLogger.getLogger().info("Broadcasting to node " + (order + 1) + " on " + selectedEPR);
		
		
		/*OLD EXECUTOR CALL
		// run the executor script
		ExecutorCall call = new ExecutorCall(pluginName, gscope);
		call.setEndpointReference(selectedEPR);
		TaskCall task = null;
		AnalysisLogger.getLogger().info("EPR:" + selectedEPR);
		task = call.launch(inputs);
//		AnalysisLogger.getLogger().info("Task EPR:" + task.getEndpointReference());
		TaskProxy proxy = task.getProxy();
		tasksProxies.add(new WorkerWatcher(proxy, AnalysisLogger.getLogger()));
		// AnalysisLogger.getLogger().info("Contacting node " + (order + 1) + " OK on " + selectedEPR);
		 */
		
		ScopeProvider.instance.set(scope);
		
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
		AnalysisLogger.getLogger().debug("Launching Smart Executor in namely Scope: "+scope+" real scope "+ScopeProvider.instance.get());
		LaunchParameter launchParameter = new LaunchParameter(pluginName, inputs);
		String excecutionIdentifier = proxy.launch(launchParameter);
		tasksProxies.add(new WorkerWatcher(proxy, excecutionIdentifier, AnalysisLogger.getLogger()));
		
		AnalysisLogger.getLogger().info("Contacting node " + (order + 1) + " OK on " + selectedEPR);
		

	}

	
	@SuppressWarnings("unchecked")
	private List<String> getFilteredEndpoints(String scopeString){
		ScopeProvider.instance.set(scopeString);
		
		ExecutorPlugin executorPlugin = new ExecutorPlugin();
		SmartExecutorPluginQuery query = new SmartExecutorPluginQuery(executorPlugin);
		
		/*
		 add key_value filter here
		 * Tuple<String, String>[] tuples = new Tuple[n];
		 * 
		 * runQuery.addConditions(pluginName, tuples);
		*/
		
		query.addConditions(pluginName);
		
		/* Used to add extra filter to ServiceEndpoint discovery */
		query.setServiceEndpointQueryFilter(null);
		List<String> nodes = query.discoverEndpoints(new ListEndpointDiscoveryFilter());
		AnalysisLogger.getLogger().debug("Found the following nodes: "+nodes+" in scope "+scopeString);
		return nodes;
	}
	
	
	private int findNodes(String scopeString) throws Exception {
		eprs = getFilteredEndpoints(scopeString);
		actualNumberOfNodes = eprs.size();
		return actualNumberOfNodes;
	}
	
	/*
	private int findNodes(String scopeString) throws Exception {
		AnalysisLogger.getLogger().debug("SCOPE:"+scopeString);
		GCUBEScope scope = GCUBEScope.getScope(scopeString);
		ISClient client = GHNContext.getImplementation(ISClient.class);
		WSResourceQuery wsquery = client.getQuery(WSResourceQuery.class);
		wsquery.addAtomicConditions(new AtomicCondition("//gc:ServiceName", "Executor"));
		wsquery.addAtomicConditions(new AtomicCondition("/child::*[local-name()='Task']/name[text()='" + pluginName + "']", pluginName));
		List<RPDocument> listdoc = client.execute(wsquery, scope);
		EndpointReferenceType epr = null;
		eprs = new ArrayList<EndpointReferenceType>();
		int numberOfEP = 0;
		for (RPDocument resource : listdoc) {
			epr = resource.getEndpoint();
			numberOfEP++;
			eprs.add(epr);
		}
		AnalysisLogger.getLogger().info("Found " + numberOfEP + " endpoints");
		// get current number of available nodes
		actualNumberOfNodes = eprs.size();
		return numberOfEP;
	}
	*/
	public String getQueueURL(String scope) throws Exception{
		//set the scope provider first!
		//<Category>Service</Category>
        //<Name>MessageBroker</Name>

		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		 query.addCondition("$resource/Profile/Category/text() eq 'Service' and $resource/Profile/Name eq 'MessageBroker' ");
		 DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		 List<ServiceEndpoint> resources = client.submit(query);
		 if (resources==null || resources.size()==0){
			 throw new Exception("No Message-Queue available in scope "+scope);
		 }
		 else{
			 AccessPoint ap = resources.get(0).profile().accessPoints().iterator().next();
			 String queue = ap.address();
			 AnalysisLogger.getLogger().debug("Found AMQ Url : "+queue);
			 return queue;
		 }
	} 
	
	private void setQueueVariables() throws Exception {
		queueName = "D4ScienceJob"; // + session;
		queueResponse = queueName + "Response"+session;
		//general scope
		
		
		// TODO Check THIS
//		queueURL = gscope.getServiceMap().getEndpoints(GHNContext.MSGBROKER).iterator().next().getAddress().toString();
		queueURL = getQueueURL(scope);
		
		
		//tests on ecosystem
		//TODO: delete this!
//		queueURL = "tcp://ui.grid.research-infrastructures.eu:6166";
//		queueURL = "tcp://message-broker.d4science.research-infrastructures.eu:6166";
		AnalysisLogger.getLogger().info("Queue for the scope: " + queueURL);
		if (queueURL==null){
			if (scope.startsWith("/gcube"))
				queueURL = "tcp://ui.grid.research-infrastructures.eu:6166";
			else
				queueURL = "tcp://message-broker.d4science.research-infrastructures.eu:6166";
		}
		queueUSER = ActiveMQConnection.DEFAULT_USER;
		queuePWD = ActiveMQConnection.DEFAULT_PASSWORD;
	}

	public void deleteRemoteFolder() throws Exception {
		ScopeProvider.instance.set(scope);
		IClient client = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED,MemoryType.VOLATILE).getClient();
//		IClient client = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, gscope).getClient();
		AnalysisLogger.getLogger().info("Removing Remote Dir " + remoteDir);
		client.removeDir().RDir(remoteDir);
		AnalysisLogger.getLogger().info("Removed");
	}

	private void uploadFilesOnStorage(boolean forceupload) throws Exception {
		ScopeProvider.instance.set(scope);
		IClient client = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, MemoryType.VOLATILE).getClient();
//		IClient client = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, gscope).getClient();
		File dir = new File(localDir);
		File[] files = dir.listFiles();
		AnalysisLogger.getLogger().info("Start uploading");
		filenames = new ArrayList<String>();
		fileurls = new ArrayList<String>();
		boolean uploadFiles = forceupload;
		// if we do not force upload then check if the folder is yet there
		if (!uploadFiles) {
			List<StorageObject> remoteObjects = client.showDir().RDir(remoteDir);
			// only upload files if they are not yet uploaded
			if (remoteObjects.size() == 0)
				uploadFiles = true;
		}
		if (!uploadFiles)
			AnalysisLogger.getLogger().info("Unnecessary to Uploading Files");

		AnalysisLogger.getLogger().info("Loading files");
		//patch for concurrent uploads
		String tempdir = ""+UUID.randomUUID()+"/";
		for (File sfile : files) {
			if (sfile.getName().startsWith("."))
				continue;
			
			String localf = sfile.getAbsolutePath();
			String filename = sfile.getName();
			
			String remotef = remoteDir + tempdir+sfile.getName();
			if (uploadFiles) {
				client.put(true).LFile(localf).RFile(remotef);
				AnalysisLogger.getLogger().info("Uploading File "+localf+" as remote file "+remotef);
			}
			String url = client.getUrl().RFile(remotef);
//			 AnalysisLogger.getLogger().info("URL obtained: " + url);
			filenames.add(filename);
			fileurls.add(url);
		}
		AnalysisLogger.getLogger().info("Loading finished");

	}

	private void broadcastListenCommandToExecutorNodes() throws Exception {
		AnalysisLogger.getLogger().info("Submitting script to Remote Queue " + queueName);
		List<WorkerWatcher> tasksProxies = new ArrayList<WorkerWatcher>();
		try{
		findNodes(scope);
		}catch(Exception e){
			AnalysisLogger.getLogger().info("Error in Finding nodes - using previous value");
		}
		activeNodes = actualNumberOfNodes;
		// launch the tasks
		for (int i = 0; i < actualNumberOfNodes; i++) {
			try {
				contactNodes(tasksProxies, i, queueName, queueUSER, queuePWD, queueURL, queueResponse, session, "false");
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().info("Error in Contacting nodes");
			}
		}
	}

	private void createClientProducer() throws Exception {
		AnalysisLogger.getLogger().info("Creating Message Queue and Producer");
		// create the Producer
		QueueManager qm = new QueueManager();
		qm.createAndConnect(queueUSER, queuePWD, queueURL, queueName);
		producer = new Producer(qm, queueName);
		AnalysisLogger.getLogger().info("Producer OK");
	}

	private void createClientConsumer() throws Exception {
		AnalysisLogger.getLogger().info("Creating Response Message Queue and Consumer");
		// create the listener
		statuslistener = new StatusListener();
		QueueManager qm1 = new QueueManager();
		qm1.createAndConnect(queueUSER, queuePWD, queueURL, queueResponse);
		consumer = new Consumer(qm1, statuslistener, statuslistener, queueResponse);
		AnalysisLogger.getLogger().info("Consumers OK");
	}
	
	boolean activeMessages[];
	public int resentMessages[];

	private void sendMessages() throws Exception {
		int i = 0;
		numberOfMessages = arguments.size();
		totalNumberOfMessages = numberOfMessages;
		AnalysisLogger.getLogger().info("Messages To Send " + numberOfMessages);
		activeMessages = new boolean[numberOfMessages];
		resentMessages = new int[numberOfMessages];
		for (String argum : arguments) {
			Map<String, Object> inputs = generateInputMessage(filenames, fileurls, outputDir, script, argum, i, scope, serviceClass, serviceName, owner, remoteDir, session, configuration, deletefiles, false);
			producer.sendMessage(inputs, 0);
			AnalysisLogger.getLogger().info("Send " + i);
			activeMessages[i] = true;
			i++;
		}
		AnalysisLogger.getLogger().info("Messages Sent " + numberOfMessages);
	}

	private Map<String, Object> generateInputMessage(Object filenames, Object fileurls, String outputDir, String script, String argum, int i, String scope, String serviceClass, String serviceName, String owner, String remoteDir, String session, String configuration, boolean deletefiles, boolean duplicateMessage) {
		Map<String, Object> inputs = new HashMap<String, Object>();

		inputs.put(ATTRIBUTE.FILE_NAMES.name(), filenames);
		inputs.put(ATTRIBUTE.FILE_URLS.name(), fileurls);
		inputs.put(ATTRIBUTE.OUTPUTDIR.name(), outputDir);
		inputs.put(ATTRIBUTE.SCRIPT.name(), script);
		inputs.put(ATTRIBUTE.ARGUMENTS.name(), argum + " " + duplicateMessage);
		inputs.put(ATTRIBUTE.ORDER.name(), "" + i);
		inputs.put(ATTRIBUTE.SCOPE.name(), scope);
		inputs.put(ATTRIBUTE.SERVICE_CLASS.name(), serviceClass);
		inputs.put(ATTRIBUTE.SERVICE_NAME.name(), serviceName);
		inputs.put(ATTRIBUTE.OWNER.name(), owner);
		inputs.put(ATTRIBUTE.REMOTEDIR.name(), remoteDir);
		inputs.put(ATTRIBUTE.CLEAN_CACHE.name(), "" + deletefiles);
		inputs.put(ATTRIBUTE.QSESSION.name(), session);
		inputs.put(ATTRIBUTE.CONFIGURATION.name(), configuration);
		inputs.put(ATTRIBUTE.TOPIC_RESPONSE_NAME.name(), queueResponse);
		inputs.put(ATTRIBUTE.QUEUE_USER.name(), queueUSER);
		inputs.put(ATTRIBUTE.QUEUE_PASSWORD.name(), queuePWD);
		inputs.put(ATTRIBUTE.QUEUE_URL.name(), queueURL);
		return inputs;
	}

	private Map<String, Object> generateWorkerInput(String queueName, String queueUser, String queuePassword, String queueURL, String queueResponse, String session, String purge) {

		Map<String, Object> inputs = new HashMap<String, Object>();

		inputs.put(ATTRIBUTE.TOPIC_NAME.name(), ScriptIOWorker.toInputString(queueName));
		inputs.put(ATTRIBUTE.QUEUE_USER.name(), ScriptIOWorker.toInputString(queueUser));
		inputs.put(ATTRIBUTE.QUEUE_PASSWORD.name(), ScriptIOWorker.toInputString(queuePassword));
		inputs.put(ATTRIBUTE.QUEUE_URL.name(), ScriptIOWorker.toInputString(queueURL));
		inputs.put(ATTRIBUTE.TOPIC_RESPONSE_NAME.name(), ScriptIOWorker.toInputString(queueResponse));
		inputs.put(ATTRIBUTE.QSESSION.name(), session);
		inputs.put(ATTRIBUTE.ERASE.name(), purge);
		return inputs;
	}

	public class Broadcaster extends TimerTask {

		@Override
		public void run() {
			try {
				AnalysisLogger.getLogger().info("(((((((((((((((((((((((((((------Broadcasting Information To Watchers------)))))))))))))))))))))))))))");
				broadcastListenCommandToExecutorNodes();
				AnalysisLogger.getLogger().info("(((((((((((((((((((((((((((------END Broadcasting Information To Watchers------)))))))))))))))))))))))))))");
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().info("--------------------------------Broadcaster: Error Sending Listen Message to Executors------)))))))))))))))))))))))))))");
			}
		}

	}

	public class ComputationTimerWatcher extends TimerTask {

		long maxTime;
		long lastTimeClock;

		public ComputationTimerWatcher(long maxtime) {
			this.maxTime = maxtime;
			this.lastTimeClock = System.currentTimeMillis();
		}

		public void reset() {
			lastTimeClock = System.currentTimeMillis();
		}

		public void setmaxTime(long maxTime) {
			this.maxTime = maxTime;
		}

		@Override
		public void run() {
			try {
				long t0 = System.currentTimeMillis();
				AnalysisLogger.getLogger().info("Computation Watcher Timing Is " + (t0 - lastTimeClock)+" max computation time is "+maxTime);
				if ((t0 - lastTimeClock) > maxTime) {
					AnalysisLogger.getLogger().info("Computation Watcher - Computation Timeout:  Closing Queue Job Manager!!!");
					abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
				AnalysisLogger.getLogger().info("Error Taking clock");
			}
		}

	}

	public synchronized void abort() {
		AnalysisLogger.getLogger().info("Computation Aborted");
		this.abort = true;
	}

	public class StatusListener implements MessageListener, ExceptionListener {

		private QueueWorkerWatcher[] watchers;

		synchronized public void onException(JMSException ex) {
			abort();
			AnalysisLogger.getLogger().info("JMS Exception occured.  Shutting down client.");
		}

		private synchronized void addWatcher(int order) {
			if (watchers == null)
				watchers = new QueueWorkerWatcher[totalNumberOfMessages];

			QueueWorkerWatcher watcher = watchers[order];
			if (watcher != null) {
				destroyWatcher(order);
			}

			Map<String, Object> message = generateInputMessage(filenames, fileurls, outputDir, script, arguments.get(order), order, scope, serviceClass, serviceName, owner, remoteDir, session, configuration, deletefiles, true);
			watchers[order] = new QueueWorkerWatcher(producer, message, order);
		}

		private synchronized void resetWatcher(int order) {
			if (watchers == null)
				watchers = new QueueWorkerWatcher[totalNumberOfMessages];
			else if (watchers[order] != null)
				watchers[order].resetTime();
		}

		private synchronized void destroyWatcher(int order) {
			if (watchers != null && watchers[order] != null) {
				if (watchers[order].hasResent())
					resentMessages[order] = resentMessages[order] + 1;

				watchers[order].destroy();
				watchers[order] = null;
				AnalysisLogger.getLogger().info("Destroyed Watcher number " + order);
			}
		}

		public synchronized void destroyAllWatchers() {
			if (watchers != null) {
				for (int i = 0; i < watchers.length; i++) {
					destroyWatcher(i);
				}
			}
		}

		public void onMessage(Message message) {

			// get message
			try {

				HashMap<String, Object> details = (HashMap<String, Object>) (HashMap<String, Object>) message.getObjectProperty(ATTRIBUTE.CONTENT.name());
				String status = (String) details.get(ATTRIBUTE.STATUS.name());
				String order = "" + details.get(ATTRIBUTE.ORDER.name());
				String nodeaddress = (String) details.get(ATTRIBUTE.NODE.name());
				String msession = (String) details.get(ATTRIBUTE.QSESSION.name());
				Object error = details.get(ATTRIBUTE.ERROR.name());
				
				AnalysisLogger.getLogger().info("Current session " + session);
				if ((msession != null) && (msession.equals(session))) {
					AnalysisLogger.getLogger().info("Session " + session + " is right - acknowledge");
					message.acknowledge();
					AnalysisLogger.getLogger().info("Session " + session + " acknowledged");
					int orderInt = -1;
					try {
						orderInt = Integer.parseInt(order);
					} catch (Exception e3) {
						e3.printStackTrace();
					}
					if (orderInt > -1) {

						// reset the watcher
						if (computationWatcher!=null)
							computationWatcher.reset();

						AnalysisLogger.getLogger().info("Task number " + order + " is " + status + " on node " + nodeaddress + " and session " + session);

						if (status.equals(ATTRIBUTE.STARTED.name())) {
							computingNodes++;
							addWatcher(orderInt);
						}
						if (status.equals(ATTRIBUTE.PROCESSING.name())) {

							resetWatcher(orderInt);
						} else if (status.equals(ATTRIBUTE.FINISHED.name())) {

							totalmessages++;
							computingNodes--;
							destroyWatcher(orderInt);
							if (numberOfMessages > 0)
								numberOfMessages--;

							AnalysisLogger.getLogger().info("Remaining " + numberOfMessages + " messages to manage");
							activeMessages[orderInt] = false;

						} else if (status.equals(ATTRIBUTE.FATAL_ERROR.name())) {
							if (error!=null)
								AnalysisLogger.getLogger().info("REPORTED FATAL_ERROR on " +nodeaddress+" : ");
								AnalysisLogger.getLogger().info(error);
							
							computingNodes--;
							if (maxFailureTries <= 0) {
								AnalysisLogger.getLogger().info("Too much Failures - Aborting");
								destroyAllWatchers();
								abort();
							} else {
								AnalysisLogger.getLogger().info("Failure Occurred - Now Resending Message " + orderInt);
								resentMessages[orderInt] = resentMessages[orderInt] + 1;
								maxFailureTries--;
								// resend message
								Map<String, Object> retrymessage = generateInputMessage(filenames, fileurls, outputDir, script, arguments.get(orderInt), orderInt, scope, serviceClass, serviceName, owner, remoteDir, session, configuration, deletefiles, true);
								producer.sendMessage(retrymessage, QCONSTANTS.timeToLive);
								AnalysisLogger.getLogger().info("Failure Occurred - Resent Message " + orderInt);
							}

						}

					} else
						AnalysisLogger.getLogger().info("Ignoring message " + order + " with status " + status);
				} else {
					AnalysisLogger.getLogger().info("wrong session " + msession + " ignoring message");
//					consumer.manager.session.recover();
				}
			} catch (Exception e) {

				AnalysisLogger.getLogger().info("Error reading details ", e);
				AnalysisLogger.getLogger().info("...Aborting Job...");
				abort();

			}
		}
	}

}
