package org.gcube.execution.workflowengine.service;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.engine.QueueableExecutionEngine;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.notificationhandling.NotificationHandling;
import gr.uoa.di.madgik.reporting.ReportingFramework;
import gr.uoa.di.madgik.ss.StorageSystem;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.messaging.endpoints.BrokerEndpoints;
import org.gcube.common.messaging.endpoints.ScheduledRetriever;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceContext extends GCUBEServiceContext
{	
	private static Logger logger=LoggerFactory.getLogger(ServiceContext.class);
	private static ServiceContext Context=null;
	private static final Object lockMe=new Object();
	
	private ServiceContext() {}
	
	public static ServiceContext GetServiceContext()
	{
		if(ServiceContext.Context==null)
		{
			synchronized (ServiceContext.lockMe)
			{
				if(ServiceContext.Context==null) ServiceContext.Context=new ServiceContext();
			}
		}
		return ServiceContext.Context;
	}

	@Override
	protected String getJNDIName()
	{
		return "execution/workflowengine";
	}
	
	@Override
	protected void onReady() throws Exception
	{
		EnvHintCollection Hints=new EnvHintCollection();
		Hints.AddHint(new NamedEnvHint("StorageSystemDeleteOnExit",new EnvHint(Boolean.TRUE.toString())));
		Hints.AddHint(new NamedEnvHint("StorageSystemLocalFileSystemBufferPath",new EnvHint(this.GetLocalFSBuffer())));
		Hints.AddHint(new NamedEnvHint("RetryOnErrorCount",new EnvHint(Integer.toString(this.GetRetryOnErrorTimes()))));
		Hints.AddHint(new NamedEnvHint("RetryOnErrorInterval",new EnvHint(Integer.toString(this.GetRetryOnErrorInterval()))));
		Hints.AddHint(new NamedEnvHint("InformationSystemRIContainerServiceClass",new EnvHint(this.GetInformationSystemRIContainerServiceClass())));
		Hints.AddHint(new NamedEnvHint("InformationSystemRIContainerServiceName",new EnvHint(this.GetInformationSystemRIContainerServiceName())));
		String nodeSelector = this.GetNodeSelector();
		if(nodeSelector!=null) Hints.AddHint(new NamedEnvHint("NodeSelector", new EnvHint(nodeSelector)));
		Hints.AddHint(new NamedEnvHint("ReportingFrameworkRIContainerServiceClass",new EnvHint(this.GetReportingFrameworkRIContainerServiceClass())));
		Hints.AddHint(new NamedEnvHint("ReportingFrameworkRIContainerServiceName",new EnvHint(this.GetReportingFrameworkRIContainerServiceName())));
		Hints.AddHint(new NamedEnvHint("ReportingFrameworkRIContainerServiceJNDIName",new EnvHint(this.GetReportingFrameworkRIContainerServiceJNDIName())));
		Hints.AddHint(new NamedEnvHint("GCubeGHN", new EnvHint(this.GetHostName())));
		Hints.AddHint(new NamedEnvHint("JMSHost", new EnvHint(GetJMSHost())));
//		this.InitLogging();
		String JMSPROVIDER = GetJMSProvider();
		try {
			NotificationHandling.Init(JMSPROVIDER, Hints);
		} catch (Exception e) {
			logger.warn("Notification Handler initialization failed", e);
		}
		this.InitWorkflowEngineEnvironment(Hints);
		super.onReady();
	}
	
	private void InitWorkflowEngineEnvironment(EnvHintCollection Hints) throws Exception
	{
		logger.info("Initalizing Engine Environment");
		logger.info("Initalizing TCPManager");
		List<PortRange> range=this.GetPortRanges();
		for(PortRange r : range) logger.info("port range : "+r.GetStart()+"-"+r.GetEnd());
		TCPConnectionManager.Init(new TCPConnectionManagerConfig(this.GetHostName(),range,this.GetUseRandomPort()));
		logger.info("Registering entries");
		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());
		logger.info("Initializing Execution Engine");
		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
		try {
			QueueableExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans), GHNContext.getContext().getGHNID());
		}catch (NoClassDefFoundError e) {
			logger.warn("Queuing mechanism is not supported cause class not found " + e.getMessage());
		}
		logger.info("Collecting Environment Hints");
		logger.info("Initializing Information System Provider");
		InformationSystem.Init(this.GetInformationSystemProvider(), Hints);
		logger.info("Initializing Storage System Provider");
		StorageSystem.Init(this.GetStorageSystemProvider(), Hints);
		logger.info("Initializing Reporting Framework Provider");
		ReportingFramework.Init(this.GetReportingProvider(), Hints);
		logger.info("Initializing Execution Directory CleanUp");
		ExecutionDirectory.SetCleanupPeriod(this.GetExecutionDirectoryCleanup());
	}
	
//	private void InitLogging() throws SecurityException, IOException
//	{
//		logger.info("Initalizing Logging Environment");
//		InputStream is = ServiceContext.class.getResourceAsStream(this.GetLoggingConfigLocation());
//		if(is==null) throw new IllegalStateException("Could not find logging config location "+this.GetLoggingConfigLocation());
//		LogManager.getLogManager().readConfiguration(is);
//		is.close();
//	}

	private String GetLocalFSBuffer()
	{
		Object tmpObj = ServiceContext.GetServiceContext().getProperty("localFSBuffer", false);
		if(tmpObj==null) return "";
		return tmpObj.toString();
	}

	private long GetExecutionDirectoryCleanup()
	{
		Object tmpObj = ServiceContext.GetServiceContext().getProperty("executionDirectoryCleanup", false);
		if(tmpObj==null) return 0;
		try{return Long.parseLong(tmpObj.toString().trim());}catch(Exception ex){return 0;}
	}

	private int GetRetryOnErrorInterval()
	{
		Object tmpObj = ServiceContext.GetServiceContext().getProperty("retryOnErrorInterval", false);
		if(tmpObj==null) return 0;
		try{return Integer.parseInt(tmpObj.toString().trim());}catch(Exception ex){return 0;}
	}

	private int GetRetryOnErrorTimes()
	{
		Object tmpObj = ServiceContext.GetServiceContext().getProperty("retryOnErrorTimes", false);
		if(tmpObj==null) return 0;
		try{return Integer.parseInt(tmpObj.toString().trim());}catch(Exception ex){return 0;}
	}

	private String GetInformationSystemProvider()
	{
		Object informationProviderObj = ServiceContext.GetServiceContext().getProperty("providerInformationName", false);
		if(informationProviderObj==null) return null;
		return informationProviderObj.toString();
	}

	private String GetStorageSystemProvider()
	{
		Object storageProviderObj = ServiceContext.GetServiceContext().getProperty("providerStorageName", false);
		if(storageProviderObj==null) return null;
		return storageProviderObj.toString();
	}
	
	private String GetJMSProvider()
	{
		Object JMSProviderObj = ServiceContext.GetServiceContext().getProperty("JMSProviderName", false);
		if(JMSProviderObj==null) return null;
		return JMSProviderObj.toString();
	}
	
	private String GetJMSHost() {
		String JMSHost = null;
		for (GCUBEScope scope : GHNContext.getContext().getGHN().getScopes().values()) {
			if (scope.isInfrastructure()) {

				ScopeProvider.instance.set(scope.toString());

				ScheduledRetriever retriever = null;
				try {
					retriever = BrokerEndpoints.getRetriever(60, 60);
				} catch (Exception e) {
					logger.warn("Could not find JMSHost for scope " + scope, e);
					continue;
				}

				JMSHost = retriever.getFailoverEndpoint();
				break;
			}
		}

		return JMSHost;
	}
	
	private String GetReportingProvider()
	{
		Object reportingProviderObj = ServiceContext.GetServiceContext().getProperty("providerReportingName", false);
		if(reportingProviderObj==null) return null;
		return reportingProviderObj.toString();
	}

	private String GetInformationSystemRIContainerServiceClass()
	{
		Object informationProviderObj = ServiceContext.GetServiceContext().getProperty("informationSystemRIContainerServiceClass", false);
		if(informationProviderObj==null) return null;
		return informationProviderObj.toString();
	}

	private String GetInformationSystemRIContainerServiceName()
	{
		Object informationProviderObj = ServiceContext.GetServiceContext().getProperty("informationSystemRIContainerServiceName", false);
		if(informationProviderObj==null) return null;
		return informationProviderObj.toString();
	}
	
	private String GetNodeSelector()
	{
		Object nodeSelectorObj = ServiceContext.GetServiceContext().getProperty("nodeSelector, false)");
		if(nodeSelectorObj==null) return null;
		return nodeSelectorObj.toString();
	}
	
	private String GetReportingFrameworkRIContainerServiceClass()
	{
		Object informationProviderObj = ServiceContext.GetServiceContext().getProperty("reportingFrameworkRIContainerServiceClass", false);
		if(informationProviderObj==null) return null;
		return informationProviderObj.toString();
	}

	private String GetReportingFrameworkRIContainerServiceName()
	{
		Object informationProviderObj = ServiceContext.GetServiceContext().getProperty("reportingFrameworkRIContainerServiceName", false);
		if(informationProviderObj==null) return null;
		return informationProviderObj.toString();
	}
	
	private String GetReportingFrameworkRIContainerServiceJNDIName()
	{
		Object informationProviderObj = ServiceContext.GetServiceContext().getProperty("reportingFrameworkRIContainerServiceJNDIName", false);
		if(informationProviderObj==null) return null;
		return informationProviderObj.toString();
	}
	
	private List<PortRange> GetPortRanges()
	{
		Object portRangesObj = ServiceContext.GetServiceContext().getProperty("portRanges", false);
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
	
	private Boolean GetUseRandomPort()
	{
		Object portRandomObj = ServiceContext.GetServiceContext().getProperty("useRandomPort", false);
		if(portRandomObj==null) return false;
		try{return Boolean.parseBoolean(portRandomObj.toString().trim());} catch(Exception ex){return false;}
	}
	
	private String GetHostName()
	{
		return GHNContext.getContext().getHostname();
	}
	
	private String GetLoggingConfigLocation()
	{
		Object loggingObj = ServiceContext.GetServiceContext().getProperty("loggingConfigLocation", false);
		if(loggingObj==null) return null;
		return loggingObj.toString();
	}
}