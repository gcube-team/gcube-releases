package org.gcube.execution;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.environment.accounting.AccountingSystem;
import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.environment.is.elements.ExtensionPair;
import gr.uoa.di.madgik.environment.is.elements.NodeInfo;
import gr.uoa.di.madgik.environment.jms.JMSProvider;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.engine.QueueableExecutionEngine;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.notificationhandling.NotificationHandling;
import gr.uoa.di.madgik.ss.StorageSystem;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.rest.commons.helpers.XMLConverter;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.resourcemanager.discovery.InformationCollector;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ServiceContext 
{
	private static Logger logger=LoggerFactory.getLogger(ServiceContext.class);
	
	public static final String PROPERTIES_FILE = "deploy.properties";
	public static final String ServiceClass = "Execution";
	public static final String ServiceName = "ExecutionEngineService";
	public static final String ENDPOINT_KEY = "resteasy-servlet";
	
	private static InformationCollector ic;
	private static ResourcePublisher<RunInstance> rp;
	
	@Inject
	public ServiceContext(InformationCollector ic, ResourcePublisher<RunInstance> rp) throws Exception {
		ServiceContext.ic = ic;
		ServiceContext.rp = rp;
		this.initialize();
	}
	
	private String scope = null;
	
	public String getScope(){
		return scope;
	}

	void initialize() throws Exception {
		final Map<String, String> map = readProperties();
		this.scope = map.get("scope");

		EnvHintCollection Hints = new EnvHintCollection();
		Hints.AddHint(new NamedEnvHint("StorageSystemDeleteOnExit", new EnvHint(Boolean.TRUE.toString())));
		Hints.AddHint(new NamedEnvHint("StorageSystemLocalFileSystemBufferPath", new EnvHint(this.GetLocalFSBuffer(map))));
		Hints.AddHint(new NamedEnvHint("RetryOnErrorCount", new EnvHint(Integer.toString(this.GetRetryOnErrorTimes(map)))));
		Hints.AddHint(new NamedEnvHint("RetryOnErrorInterval", new EnvHint(Integer.toString(this.GetRetryOnErrorInterval(map)))));
		Hints.AddHint(new NamedEnvHint("InformationSystemRIContainerServiceClass", new EnvHint(ServiceClass)));
		Hints.AddHint(new NamedEnvHint("InformationSystemRIContainerServiceName", new EnvHint(ServiceName)));
		Hints.AddHint(new NamedEnvHint("GCubeActionScope", new EnvHint(scope)));
		this.InitExecutionEngineEnvironment(Hints, map);

		// this.RegisterInfo(Hints);

		if (!Boolean.valueOf(map.get("test"))) {
			new Thread() {
				public void run() {
					try {
						Set<RunInstance> instances;
						Set<RunInstance> endpoints = new HashSet<RunInstance>();
						for (int i = 0;; i++) {
							instances = ic.discoverRunningInstancesFilteredByEndopointKey(ServiceName, ServiceClass, ENDPOINT_KEY, scope);
							for (RunInstance inst : instances) {
								if (inst == null || inst.getProfile() == null || inst.getProfile().accessPoint.runningInstanceInterfaces == null)
									continue;

								URI epr = inst.getProfile().accessPoint.runningInstanceInterfaces.get(ENDPOINT_KEY);
								if (epr == null) {
									continue;
								}

								logger.info("scope: " + scope + " found epr at : " + epr.toASCIIString() + " comparing to : " + getHostName(map));
								if (epr.toASCIIString().contains(getHostName(map))) {
									endpoints.add(inst);
									logger.info("scope: " + scope + " added: " + epr.toASCIIString());
								}
							}
							if (endpoints.size() > 0) {
								logger.trace("Found endpoint resource at scope: " + scope);
								break;
							}
							Thread.sleep(1000);
							if (i > 60) {
								throw new Exception("Could not retrieve endpoint resource");
							}
							logger.warn("Could not find endpoint resource. trying again at scope: " + scope);
						}
						String serialization = createSerialization(getPe2ngPort(), getHostName(map));

						logger.info("endpoints num: " + endpoints.size());
						for (RunInstance endpoint : endpoints) {
							Element newBody = DocumentBuilderFactory.newInstance().newDocumentBuilder()
									.parse(new ByteArrayInputStream(serialization.getBytes())).getDocumentElement();

							if (endpoint.getProfile().specificData.root != null)
								removeAllChildren(endpoint.getProfile().specificData.root);

							endpoint.getProfile().specificData.root = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
									.adoptNode(newBody);

							rp.updateResource(endpoint, scope);
							logger.info("update resource: " + endpoint.getId());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	public static void removeAllChildren(Node node) {
		logger.trace("node: " + node + " :" + XMLConverter.nodeToString(node));
		for (Node child; (child = node.getFirstChild()) != null; node.removeChild(child));
	}
	
	static String createSerialization(String pe2ngPort, String hostname){
		StringBuffer strBuf = new StringBuffer();
		
		strBuf.append("<doc>");
		strBuf.append("<element id=\"" + UUID.randomUUID() + "\">");
		strBuf.append("<dynamic>");
		strBuf.append("<entry key=\"pe2ng.port\">" + pe2ngPort + "</entry>");
		strBuf.append("<entry key=\"hostname\">" + hostname + "</entry>");
		strBuf.append("</dynamic>");
		strBuf.append("</element>");
		strBuf.append("</doc>");
		return strBuf.toString();
	}
	
	private void InitExecutionEngineEnvironment(EnvHintCollection Hints, Map<String, String> map) throws Exception
	{
		logger.info("Initalizing Engine Environment");
		logger.info("Initalizing TCPManager");
		List<PortRange> range=this.GetPortRanges(map);
		for(PortRange r : range) logger.info("port range : "+r.GetStart()+"-"+r.GetEnd());
		TCPConnectionManager.Init(new TCPConnectionManagerConfig(this.getHostName(map),range,this.GetUseRandomPort(map)));
		if (range.get(0).GetStart() == Integer.valueOf(getPe2ngPort()).intValue())
			logger.info("Registered port: " + getPe2ngPort());
		else
			logger.warn("Registered port: " + getPe2ngPort() + ". Start port was in use: " + range.get(0).GetStart());
		logger.info("Registering entries");
		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
		TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
		logger.info("Initializing Execution Engine");
		ExecutionEngineConfig conf = new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans, getHostName(map), Integer.valueOf(map.get("port")));
		ExecutionEngine.Init(conf);
		logger.info("Collecting Environment Hints");
		logger.info("Initializing Information System Provider");
		
		//Hints.AddHint(new NamedEnvHint("InformationSystemFTPURL", new EnvHint(map.get("ftpUrl"))));
		//InformationSystem.Init(this.GetInformationSystemProvider(map), Hints);
		logger.info("Initializing Storage System Provider");
		StorageSystem.Init(this.GetStorageSystemProvider(map), Hints);
		logger.info("Initializing Accoutning System Provider");
		AccountingSystem.init(this.GetAccountingSystemProvider(map));
		JMSProvider.Init(this.GetJMSProvider(map), Hints, this.GetJMSFallBack(map));
		String jmshost = GetJMSHost();
		if (jmshost != null) {
			Hints.AddHint(new NamedEnvHint("JMSHost", new EnvHint(GetJMSHost())));
			
			String notificationHandlingProvider = GetNotificationHandlingProvider(map);
			try {
				NotificationHandling.Init(notificationHandlingProvider, Hints);
			} catch (Exception e) {
				logger.warn("Notification Handler initialization failed", e);
			}
		}
		try {
			String id = this.getHostName(map);
			//TODO: multiple instances in same host???
			
			QueueableExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans, this.getHostName(map), Integer.valueOf(this.getPe2ngPort())), id);
		}catch (NoClassDefFoundError e) {
			logger.warn("Queuing mechanism is not supported cause class not found " + e.getMessage());
		}
	}
	
	
	void getTomcatPort() {
		
	}
	
	private void RegisterInfo(EnvHintCollection Hints) throws Exception
	{
		logger.info("Registering Node");
		NodeInfo nfo=new NodeInfo();
		nfo.DynamicExtensions.put("pe2ng.port", new ExtensionPair("pe2ng.port", Integer.toString(TCPConnectionManager.GetConnectionManagerPort())));
		InformationSystem.RegisterNode(nfo, Hints);
		logger.info("Registering boundary listener");
	}

	private String GetLocalFSBuffer(Map<String, String> map)
	{
		Object tmpObj = map.get("localFSBuffer");
		if(tmpObj==null) return "";
		return tmpObj.toString();
	}

	private int GetRetryOnErrorInterval(Map<String, String> map)
	{
		Object tmpObj = map.get("retryOnErrorInterval");
		if(tmpObj==null) return 0;
		try{return Integer.parseInt(tmpObj.toString().trim());}catch(Exception ex){return 0;}
	}

	private int GetRetryOnErrorTimes(Map<String, String> map)
	{
		Object tmpObj = map.get("retryOnErrorTimes");
		if(tmpObj==null) return 0;
		try{return Integer.parseInt(tmpObj.toString().trim());}catch(Exception ex){return 0;}
	}

	private String GetInformationSystemProvider(Map<String, String> map)
	{
		Object informationProviderObj = map.get("providerInformationName");
		if(informationProviderObj==null) return null;
		return informationProviderObj.toString();
	}

	private String GetStorageSystemProvider(Map<String, String> map)
	{
		Object storageProviderObj = map.get("providerStorageName");
		if(storageProviderObj==null) return null;
		return storageProviderObj.toString();
	}
	
	private String GetAccountingSystemProvider(Map<String, String> map)
	{
		Object accountingProviderObj = map.get("providerAccountingName");
		if(accountingProviderObj==null) return null;
		return accountingProviderObj.toString();
	}
	
	private String GetNotificationHandlingProvider(Map<String, String> map)
	{
		Object JMSProviderObj = map.get("NotificationHandlingProviderName");
		if(JMSProviderObj==null) return null;
		return JMSProviderObj.toString();
	}
	
	private String GetJMSProvider(Map<String, String> map)
	{
		Object JMSProviderObj = map.get("providerJMSName");
		if(JMSProviderObj==null) return null;
		return JMSProviderObj.toString();
	}

	private String GetJMSFallBack(Map<String, String> map)
	{
		Object JMSProviderObj = map.get("fallbackJMS");
		if(JMSProviderObj==null) return null;
		return JMSProviderObj.toString();
	}

	private String GetJMSHost() {
		try {
			return JMSProvider.getJMSPRovider();
		} catch (EnvironmentInformationSystemException e) {
			return null;
		}
	}
	
	private List<PortRange> GetPortRanges(Map<String, String> map)
	{
		Object portRangesObj = map.get("portRanges");
		logger.info("port ranges retrieved is "+portRangesObj);
		if(portRangesObj==null) return new ArrayList<PortRange>();
		String portRangesStr=portRangesObj.toString();
		String []rangePairs=portRangesStr.trim().split("-");
		List<PortRange> ranges=new ArrayList<PortRange>();
		for(String pair : rangePairs)
		{
			String []pairSplit=pair.trim().split(",");
			if(pairSplit.length!=2) return new ArrayList<PortRange>();
			String pairStart=pairSplit[0].trim();
			if(!pairStart.startsWith("{")) return new ArrayList<PortRange>();
			pairStart=pairStart.substring(1).trim();
			String pairEnd=pairSplit[1].trim();
			if(!pairEnd.endsWith("}")) return new ArrayList<PortRange>();
			pairEnd=pairEnd.substring(0,pairEnd.length()-1).trim();
			int beginPair=0;
			int endPair=0;
			try{beginPair=Integer.parseInt(pairStart.trim());} catch(Exception ex){return new ArrayList<PortRange>();}
			try{endPair=Integer.parseInt(pairEnd.trim());} catch(Exception ex){return new ArrayList<PortRange>();}
			ranges.add(new PortRange(beginPair, endPair));
		}
		return ranges;
	}
	
	private Boolean GetUseRandomPort(Map<String, String> map)
	{
		Object portRandomObj = map.get("useRandomPort");
		if(portRandomObj==null) return false;
		try{return Boolean.parseBoolean(portRandomObj.toString().trim());} catch(Exception ex){return false;}
	}
	
	String getHostName(Map<String, String> map) {
		return map.get("hostname");

	}
	
	String getPe2ngPort() {
		return String.valueOf(TCPConnectionManager.GetConnectionManagerPort());

	}
	
	private Map<String, String> readProperties() throws FileNotFoundException, IOException {
		Map<String, String> map = new HashMap<String, String>();

		Properties prop = new Properties();
		//prop.load(new FileInputStream(PROPERTIES_FILE));
		InputStream is = ServiceContext.class.getResourceAsStream("/" + PROPERTIES_FILE);
		prop.load(is);
		for (String key : prop.stringPropertyNames()) {
			String value = prop.getProperty(key);
			if (value != null)
				map.put(key, value.trim());
		}

		logger.info("properties read : " + map);
		return map;
	}
}
