package gr.uoa.di.madgik.workflow.test;

import gr.uoa.di.madgik.commons.channel.proxy.tcp.ChannelTCPConnManagerEntry;
import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.commons.utils.FileUtils;
import gr.uoa.di.madgik.environment.exception.EnvironmentValidationException;
import gr.uoa.di.madgik.environment.hint.EnvHint;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.environment.hint.NamedEnvHint;
import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionEngineConfig;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngCallbackTCPConnManagerEntry;
import gr.uoa.di.madgik.execution.plan.element.invocable.tcpserver.ExecEngTCPConnManagerEntry;
import gr.uoa.di.madgik.is.InformationSystem;
import gr.uoa.di.madgik.ss.StorageSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestAdaptorBase implements Observer
{
	protected static ExecutionHandle Handle=null;
	protected static final Object synchCompletion=new Object();
	protected static Logger logger;
	private static EnvHintCollection Hints=new EnvHintCollection();
	
	protected static void Init(String HostName,int Port,String EnvProvider) throws EnvironmentValidationException
	{
		TestAdaptorBase.logger=LoggerFactory.getLogger(TestAdaptorBase.class);
		logger.info("Initializing Connection Manager");
		ArrayList<PortRange> ports=new ArrayList<PortRange>();
		boolean useRandom=true;
		if(Port<=0) useRandom=true;
		else
		{
			ports.add(new PortRange(Port, Port));
			useRandom=false;
		}
		TCPConnectionManager.Init(new TCPConnectionManagerConfig(HostName,ports,useRandom));
		TCPConnectionManager.RegisterEntry(new ExecEngTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ExecEngCallbackTCPConnManagerEntry());
		TCPConnectionManager.RegisterEntry(new ChannelTCPConnManagerEntry());
		logger.info("Initializing Execution Engine");
		ExecutionEngine.Init(new ExecutionEngineConfig(ExecutionEngineConfig.InfinitePlans));
		String providerInformationName=null;
		String providerStorageName=null;
		if(EnvProvider.equalsIgnoreCase("gcube"))
		{
			Hints.AddHint(new NamedEnvHint("StorageSystemDeleteOnExit",new EnvHint(Boolean.TRUE.toString())));
			Hints.AddHint(new NamedEnvHint("StorageSystemLocalFileSystemBufferPath",new EnvHint("/tmp/")));
			providerInformationName="gr.uoa.di.madgik.environment.gcube.GCubeInformationSystemProvider";
			providerStorageName="gr.uoa.di.madgik.environment.gcube.GCubeStorageSystemProvider";
		}
		else
		{
			Hints.AddHint(new NamedEnvHint("InformationSystemFTPURL",new EnvHint("ftp://ftpuser:za73ba97ra@dl13.di.uoa.gr/d5s/is/")));
			Hints.AddHint(new NamedEnvHint("StorageSystemFTPURL",new EnvHint("ftp://ftpuser:za73ba97ra@dl13.di.uoa.gr/d5s/ss/")));
			Hints.AddHint(new NamedEnvHint("StorageSystemDeleteOnExit",new EnvHint(Boolean.TRUE.toString())));
			Hints.AddHint(new NamedEnvHint("StorageSystemLocalFileSystemBufferPath",new EnvHint("/tmp/")));
			providerInformationName="gr.uoa.di.madgik.environment.ftp.FTPInformationSystemProvider";
			providerStorageName="gr.uoa.di.madgik.environment.ftp.FTPStorageSystemProvider";
		}
		logger.info("Initializing Information System");
		InformationSystem.Init(providerInformationName, Hints);
		logger.info("Initializing Storage System");
		StorageSystem.Init(providerStorageName, Hints);
		// do not register node so that ID does not need cleanup on restart in case of random ports
	}
	
	protected static String GetStoredFilePayload(String varID,String adaptorType) throws Exception
	{
		if(Handle.GetPlan().Variables.Get(varID)==null) return "variable not defined";
		if(!Handle.GetPlan().Variables.Get(varID).IsAvailable) return "variable payload not available";
		File file=StorageSystem.Retrieve(Handle.GetPlan().Variables.Get(varID).Value.GetValue(),Handle.GetPlan().EnvHints);
		File outFile=File.createTempFile(varID, ".test."+adaptorType+".adaptor.out");
		FileUtils.Copy(file, outFile);
		return outFile.toString();
	}

	protected static boolean EvaluateResult() throws ExecutionSerializationException
	{
		if(!Handle.IsCompleted())
		{
			logger.warn("Not completed! Why am I here?");
			return false;
		}
		else if(Handle.IsCompletedWithSuccess())
		{
			logger.info("Plan successfully completed");
			return true;
		}
		else if(Handle.IsCompletedWithError()) 
		{
			String errorString="Plan unsuccessfully completed with error";
			if(Handle.GetCompletionError() instanceof ExecutionRunTimeException) errorString+=" of cause "+((ExecutionRunTimeException)Handle.GetCompletionError()).GetCauseFullName();
			logger.info(errorString,Handle.GetCompletionError());
			return false;
		}
		else
		{
			logger.warn("Completed but neither with success or failure!");
			return false;
		}
	}
	
	public void update(Observable o, Object arg)
	{
		if (!o.getClass().getName().equals(arg.getClass().getName())) return;
		if (!(arg instanceof ExecutionStateEvent)) return;
		switch (((ExecutionStateEvent) arg).GetEventName())
		{
			case ExecutionCompleted:
			{
				logger.info("Received event " + arg.getClass().getSimpleName());
				synchronized (synchCompletion)
				{
					synchCompletion.notify();
				}
				break;
			}
			case ExecutionCancel:
			case ExecutionPause:
			case ExecutionResume:
			case ExecutionStarted:
			{
				logger.info("Received event " + arg.getClass().getSimpleName());
				break;
			}
			case ExecutionProgress:
			{
				ExecutionProgressReportStateEvent ev = (ExecutionProgressReportStateEvent) arg;
				String report = "";
				if (ev.DoesReportProgress()) report = ev.GetCurrentStep() + "/" + ev.GetTotalSteps();
				String sender = "";
				if (ev.GetID() != null)
				{
					if(Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String msg = "";
				if (ev.GetMessage() != null) msg = ev.GetMessage();
				logger.info("sender (" + sender + ") progress(" + report + ") message (" + msg + ")");
				break;
			}
			case ExecutionExternalProgress:
			{
				ExecutionExternalProgressReportStateEvent ev = (ExecutionExternalProgressReportStateEvent) arg;
				String report = "";
				if (ev.DoesReportProgress()) report = ev.GetCurrentStep() + "/" + ev.GetTotalSteps();
				String sender = "";
				if (ev.GetID() != null)
				{
					if(Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String msg = "";
				if (ev.GetMessage() != null) msg = ev.GetMessage();
				String extSender = "";
				if (ev.GetExternalSender() != null) extSender = ev.GetExternalSender();
				logger.info("sender (" + sender + ") external sender (" + extSender + ") progress(" + report + ") message (" + msg + ")");
				break;
			}
			case ExecutionPerformance:
			{
				ExecutionPerformanceReportStateEvent ev = (ExecutionPerformanceReportStateEvent) arg;
				String sender = "";
				if (ev.GetID() != null)
				{
					if(Handle.GetPlan().Locate(ev.GetID())==null) logger.warn("No element with id "+ev.GetID()+" is located");
					sender = Handle.GetPlan().Locate(ev.GetID()).GetName();
				}
				String subCalls="";
				if(ev.GetSubCalls() !=0)
				{
					subCalls="including "+ev.GetSubCalls()+" sub calls of total time "+ev.GetSubCallTotalTime();
				}
				logger.info("sender (" + sender + ") reports performance ( Total "+ev.GetTotalTime()+", Initialization "+ev.GetInitializationTime()+", Finilization "+ev.GetFinilizationTime()+" Children "+ev.GetChildrenTotalTime()+") miliseconds "+subCalls);
				break;
			}
			default:
			{
				logger.warn("Received unrecognized event type " + ((ExecutionStateEvent) arg).GetEventName().toString());
			}
		}
	}

}
