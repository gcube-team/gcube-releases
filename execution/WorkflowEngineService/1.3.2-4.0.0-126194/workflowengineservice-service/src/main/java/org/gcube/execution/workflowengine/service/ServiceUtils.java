package org.gcube.execution.workflowengine.service;

import gr.uoa.di.madgik.execution.event.ExecutionExternalProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionPerformanceReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionProgressReportStateEvent;
import gr.uoa.di.madgik.execution.event.ExecutionStateEvent;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.exception.ExecutionRunTimeException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import gr.uoa.di.madgik.execution.utils.DataTypeUtils;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.AdaptorCondorResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.AttachedCondorResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.condor.OutputCondorResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AdaptorGridResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.AttachedGridResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.grid.OutputSandboxGridResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AdaptorHadoopResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource.AccessInfo;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.AttachedHadoopResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.hadoop.OutputHadoopResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AdaptorJDLResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.ResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.OutputSandboxJDLResource;
import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
import gr.uoa.di.madgik.workflow.exception.WorkflowEnvironmentException;
import gr.uoa.di.madgik.workflow.exception.WorkflowException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.execution.workflowengine.service.WorkflowEngineService.ResourceAccessType;
import org.gcube.execution.workflowengine.service.stubs.CONDORParams;
import org.gcube.execution.workflowengine.service.stubs.CONDORResource;
import org.gcube.execution.workflowengine.service.stubs.ExecutionEvent;
import org.gcube.execution.workflowengine.service.stubs.ExecutionExternalProgressEvent;
import org.gcube.execution.workflowengine.service.stubs.ExecutionPerformanceEvent;
import org.gcube.execution.workflowengine.service.stubs.ExecutionProgressEvent;
import org.gcube.execution.workflowengine.service.stubs.GRIDParams;
import org.gcube.execution.workflowengine.service.stubs.GRIDResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPArchiveResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPArgumentResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPFileResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPInputResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPLibResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPOutputResource;
import org.gcube.execution.workflowengine.service.stubs.HADOOPParams;
import org.gcube.execution.workflowengine.service.stubs.HADOOPPropertyResource;
import org.gcube.execution.workflowengine.service.stubs.JDLParams;
import org.gcube.execution.workflowengine.service.stubs.JDLResource;
import org.gcube.execution.workflowengine.service.stubs.JobOutput;

public class ServiceUtils
{
	private static final Logger logger=LoggerFactory.getLogger(ServiceUtils.class);
	
	public static JobOutput GetJobOutput(ExecutionObserver obs, IOutputResource res) throws ExecutionException, WorkflowException
	{
		JobOutput jo=new JobOutput();
		switch(obs.GetWorkflowType())
		{
			case JDL:
			{
				if(!(res instanceof OutputSandboxJDLResource)) throw new WorkflowValidationException("Different type of output found");
				String OutputSSID=ServiceUtils.GetStoredFileID(((OutputSandboxJDLResource)res).VariableID,obs);
				logger.info("Execution with id "+obs.GetExecutionID()+" Output file of node "+((OutputSandboxJDLResource)res).NodeName+" with jdl name : "+((OutputSandboxJDLResource)res).SandboxName+" is stored at StorageSystem id "+OutputSSID);
				jo.setKey(((OutputSandboxJDLResource)res).SandboxName);
				jo.setSubKey(((OutputSandboxJDLResource)res).NodeName);
				jo.setStorageSystemID(OutputSSID);
				break;
			}
			case Grid:
			{
				if(!(res instanceof OutputSandboxGridResource)) throw new WorkflowValidationException("Different type of output found");
				String OutputSSID=ServiceUtils.GetStoredFileID(((OutputSandboxGridResource)res).VariableID,obs);
				logger.info("Execution with id "+obs.GetExecutionID()+" Output file "+((OutputSandboxGridResource)res).Key+" is stored at StorageSystem id "+OutputSSID);
				jo.setKey(((OutputSandboxGridResource)res).Key);
				jo.setStorageSystemID(OutputSSID);
				break;
			}
			case Hadoop:
			{
				if(!(res instanceof OutputHadoopResource)) throw new WorkflowValidationException("Different type of output found");
				String OutputSSID=ServiceUtils.GetStoredFileID(((OutputHadoopResource)res).VariableID,obs);
				logger.info("Execution with id "+obs.GetExecutionID()+" Output file "+((OutputHadoopResource)res).Key+" of type "+((OutputHadoopResource)res).TypeOfOutput+" is stored at StorageSystem id "+OutputSSID);
				jo.setKey(((OutputHadoopResource)res).Key);
				jo.setSubKey(((OutputHadoopResource)res).TypeOfOutput.toString());
				jo.setStorageSystemID(OutputSSID);
				break;
			}
			case Condor:
			{
				if(!(res instanceof OutputCondorResource)) throw new WorkflowValidationException("Different type of output found");
				String OutputSSID=ServiceUtils.GetStoredFileID(((OutputCondorResource)res).VariableID,obs);
				logger.info("Execution with id "+obs.GetExecutionID()+" Output file "+((OutputCondorResource)res).Key+" of type "+((OutputCondorResource)res).TypeOfOutput+" is stored at StorageSystem id "+OutputSSID);
				jo.setKey(((OutputCondorResource)res).Key);
				jo.setSubKey(((OutputCondorResource)res).TypeOfOutput.toString());
				jo.setStorageSystemID(OutputSSID);
				break;
			}
			default: throw new WorkflowValidationException("Unrecognized workflow type "+obs.GetWorkflowType().toString());
		}
		return jo;
	}
	
	public static String GetExecutionErrorMessage(ExecutionObserver obs) throws IOException
	{
		StringBuilder buf=new StringBuilder();
		if(obs.GetExecutionHandle().GetCompletionError() instanceof ExecutionRunTimeException) buf.append("Error Cause was "+((ExecutionRunTimeException)obs.GetExecutionHandle().GetCompletionError()).GetCauseFullName());
		StringWriter sw=new StringWriter();
		PrintWriter pw= new PrintWriter(sw);
		obs.GetExecutionHandle().GetCompletionError().printStackTrace(pw);
		pw.flush();
		pw.close();
		sw.flush();
		sw.close();
		sw.toString();
		buf.append(sw.toString());
		return buf.toString();
	}
	
	public static List<ExecutionEvent> GetExecutionEvents(ExecutionObserver obs)
	{
		List<ExecutionStateEvent> events= obs.GetEvents();
		List<ExecutionEvent> reportEvents=new ArrayList<ExecutionEvent>();
		for(ExecutionStateEvent event : events)
		{
			ExecutionEvent ev=new ExecutionEvent();
			ev.setEventTimestamp(event.GetEmitTimestamp());
			ev.setEventType(event.GetEventName().toString());
			if(event instanceof ExecutionProgressReportStateEvent)
			{
				ExecutionProgressEvent nfo=new ExecutionProgressEvent();
				nfo.setCurrentStep(((ExecutionProgressReportStateEvent)event).GetCurrentStep());
				nfo.setEmiterID(((ExecutionProgressReportStateEvent)event).GetID());
				nfo.setMessage(((ExecutionProgressReportStateEvent)event).GetMessage());
				nfo.setNodeName(((ExecutionProgressReportStateEvent)event).GetNodeName());
				nfo.setNodeHostName(((ExecutionProgressReportStateEvent)event).GetNodeHostName());
				nfo.setNodePort(((ExecutionProgressReportStateEvent)event).GetNodePort());
				nfo.setReportProgress(((ExecutionProgressReportStateEvent)event).DoesReportProgress());
				nfo.setReportNodeProgress(((ExecutionProgressReportStateEvent)event).DoesReportNodeProgress());
				nfo.setReportNodeStatus(((ExecutionProgressReportStateEvent)event).DoesReportNodeStatus());
				nfo.setTotalStep(((ExecutionProgressReportStateEvent)event).GetTotalSteps());
				ev.setProgressEventInfo(nfo);
			}
			else if(event instanceof ExecutionExternalProgressReportStateEvent)
			{
				ExecutionExternalProgressEvent nfo=new ExecutionExternalProgressEvent();
				nfo.setCurrentStep(((ExecutionExternalProgressReportStateEvent)event).GetCurrentStep());
				nfo.setEmiterID(((ExecutionExternalProgressReportStateEvent)event).GetID());
				nfo.setMessage(((ExecutionExternalProgressReportStateEvent)event).GetMessage());
				nfo.setReportProgress(((ExecutionExternalProgressReportStateEvent)event).DoesReportProgress());
				nfo.setTotalStep(((ExecutionExternalProgressReportStateEvent)event).GetTotalSteps());
				nfo.setExternalEmiterName(((ExecutionExternalProgressReportStateEvent)event).GetExternalSender());
				ev.setProgressExternalEventInfo(nfo);
			}
			else if(event instanceof ExecutionPerformanceReportStateEvent)
			{
				ExecutionPerformanceEvent nfo=new ExecutionPerformanceEvent();
				nfo.setEmiterID(((ExecutionPerformanceReportStateEvent)event).GetID());
				nfo.setTotalTime(((ExecutionPerformanceReportStateEvent)event).GetTotalTime());
				nfo.setInitializationTime(((ExecutionPerformanceReportStateEvent)event).GetInitializationTime());
				nfo.setFinalizationTime(((ExecutionPerformanceReportStateEvent)event).GetFinilizationTime());
				nfo.setChildrenTotalTime(((ExecutionPerformanceReportStateEvent)event).GetChildrenTotalTime());
				nfo.setNumberOfSubcalls(((ExecutionPerformanceReportStateEvent)event).GetSubCalls());
				nfo.setSubcallsTotalTime(((ExecutionPerformanceReportStateEvent)event).GetSubCallTotalTime());
				ev.setPerformanceEventInfo(nfo);
			}
			reportEvents.add(ev);
		}
		return reportEvents;
	}
	
	public static String GetStoredFileID(String varID,ExecutionObserver obs) throws ExecutionValidationException
	{
		if(obs.GetExecutionHandle().GetPlan().Variables.Get(varID)==null) 
		{
			logger.info("Variable " + varID + " is null");
			return "not available";
		}
		if(!obs.GetExecutionHandle().GetPlan().Variables.Get(varID).IsAvailable)
		{
			logger.info("Variable " + varID + " is not available");
			return "not available";	
		}
		return DataTypeUtils.GetValueAsString(obs.GetExecutionHandle().GetPlan().Variables.Get(varID).Value.GetValue());
	}

	public static boolean EvaluateResult(ExecutionObserver obs)
	{
		if(!obs.GetExecutionHandle().IsCompleted())
		{
			logger.warn("Execution "+obs.GetExecutionID()+" Not completed! Why am I here?");
			return false;
		}
		else if(obs.GetExecutionHandle().IsCompletedWithSuccess())
		{
			logger.info("Execution "+obs.GetExecutionID()+" successfully completed");
			return false;
		}
		else if(obs.GetExecutionHandle().IsCompletedWithError()) 
		{
			String errorString="unsuccessfully completed with error";
			if(obs.GetExecutionHandle().GetCompletionError() instanceof ExecutionRunTimeException) errorString+=" of cause "+((ExecutionRunTimeException)obs.GetExecutionHandle().GetCompletionError()).GetCauseFullName();
			logger.info("Execution "+obs.GetExecutionID()+" "+errorString,obs.GetExecutionHandle().GetCompletionError());
			return true;
		}
		else
		{
			logger.warn("Execution "+obs.GetExecutionID()+" Completed but neither with success or failure!");
			return false;
		}
	}

	public static void EnrichHadoopResource(AttachedHadoopResource att, ResourceAccessType rat, AccessInfo ai, String reference, byte[] bytePayload,String stringPayload) throws WorkflowEnvironmentException, WorkflowValidationException
	{
		switch(rat)
		{
			case Reference:
			{
				att.Value=reference;
				att.ResourceLocationType=AttachedHadoopResource.AttachedResourceType.Reference;
				if(ai!=null)
				{
					if(ai.userId != null)
					{
						att.accessInfo.userId = ai.userId;
						att.accessInfo.password = ai.password;
					}
					att.accessInfo.port = ai.port;
				}
				break;
			}
			case CMSReference:
			{
				att.Value=reference;
				att.ResourceLocationType=AttachedHadoopResource.AttachedResourceType.CMSReference;
				break;
			}
			case InMessageBytes:
			{
				try
				{
					File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
					BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(tmp));
					bout.write(bytePayload);
					bout.flush();
					bout.close();
					att.Value=tmp.toString();
					att.ResourceLocationType=AttachedHadoopResource.AttachedResourceType.LocalFile;
				}catch(Exception ex)
				{
					throw new WorkflowEnvironmentException("Could not store attached payload");
				}
				break;
			}
			case InMessageString:
			{
				try
				{
					File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
					BufferedWriter bout=new BufferedWriter(new FileWriter(tmp));
					bout.write(stringPayload);
					bout.flush();
					bout.close();
					att.Value=tmp.toString();
					att.ResourceLocationType=AttachedHadoopResource.AttachedResourceType.LocalFile;
				}catch(Exception ex)
				{
					throw new WorkflowEnvironmentException("Could not store attached payload");
				}
				break;
			}
			default:
			{
				throw new WorkflowValidationException("Value of resource access invalid");
			}
		}
	}
	
	public static AdaptorHadoopResources GetAdaptorHADOOPResources(HADOOPParams params) throws WorkflowValidationException, WorkflowEnvironmentException
	{
		AdaptorHadoopResources resources=new AdaptorHadoopResources();
		resources.Resources.add(new AttachedHadoopResource(ServiceContext.GetServiceContext().getScope().toString(),ServiceContext.GetServiceContext().getScope().toString(),AttachedHadoopResource.ResourceType.Scope));
		if(params.getHadoopResources().getArchives()!=null)
		{
			for(HADOOPArchiveResource res : params.getHadoopResources().getArchives())
			{
				AttachedHadoopResource att=new AttachedHadoopResource();
				att.TypeOfResource=AttachedHadoopResource.ResourceType.Archive;
				att.Key=res.getResourceKey();
				att.IsHDFSPresent=res.isHdfsPresent();
				ResourceAccessType rat=ResourceAccessType.InMessageBytes;
				try
				{
					rat=ResourceAccessType.valueOf(res.getResourceAccess());
				} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
				ServiceUtils.EnrichHadoopResource(att, rat, null, res.getResourceReference(), res.getInMessageBytePayload(), res.getInMessageStringPayload());
				resources.Resources.add(att);
			}
		}
		if(params.getHadoopResources().getArguments()!=null)
		{
			for(HADOOPArgumentResource res : params.getHadoopResources().getArguments())
			{
				AttachedHadoopResource att=new AttachedHadoopResource();
				att.TypeOfResource=AttachedHadoopResource.ResourceType.Argument;
				att.Key=res.getResourceValue();
				att.Order=res.getOrder();
				att.Value=res.getResourceValue();
				resources.Resources.add(att);
			}
		}
		if(params.getHadoopResources().getConfiguration()!=null)
		{
			AttachedHadoopResource att=new AttachedHadoopResource();
			att.TypeOfResource=AttachedHadoopResource.ResourceType.Configuration;
			att.Key=params.getHadoopResources().getConfiguration().getResourceKey();
			att.IsHDFSPresent=params.getHadoopResources().getConfiguration().isHdfsPresent();
			ResourceAccessType rat=ResourceAccessType.InMessageBytes;
			try
			{
				rat=ResourceAccessType.valueOf(params.getHadoopResources().getConfiguration().getResourceAccess());
			} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
			ServiceUtils.EnrichHadoopResource(att, rat, null, params.getHadoopResources().getConfiguration().getResourceReference(), params.getHadoopResources().getConfiguration().getInMessageBytePayload(), params.getHadoopResources().getConfiguration().getInMessageStringPayload());
			resources.Resources.add(att);
		}
		if(params.getHadoopResources().getFiles()!=null)
		{
			for(HADOOPFileResource res : params.getHadoopResources().getFiles())
			{
				AttachedHadoopResource att=new AttachedHadoopResource();
				att.TypeOfResource=AttachedHadoopResource.ResourceType.File;
				att.Key=res.getResourceKey();
				att.IsHDFSPresent=res.isHdfsPresent();
				ResourceAccessType rat=ResourceAccessType.InMessageBytes;
				try
				{
					rat=ResourceAccessType.valueOf(res.getResourceAccess());
				} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
				ServiceUtils.EnrichHadoopResource(att, rat, null, res.getResourceReference(), res.getInMessageBytePayload(), res.getInMessageStringPayload());
				resources.Resources.add(att);
			}
		}
		if(params.getHadoopResources().getInputs()!=null)
		{
			for(HADOOPInputResource res : params.getHadoopResources().getInputs())
			{
				AttachedHadoopResource att=new AttachedHadoopResource();
				att.TypeOfResource=AttachedHadoopResource.ResourceType.Input;
				att.Key=res.getResourceKey();
				att.CleanUp=res.isCleanup();
				ResourceAccessType rat=ResourceAccessType.InMessageBytes;
				try
				{
					rat=ResourceAccessType.valueOf(res.getResourceAccess());
				} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
				ServiceUtils.EnrichHadoopResource(att, rat, null, res.getResourceReference(), res.getInMessageBytePayload(), res.getInMessageStringPayload());
				resources.Resources.add(att);
			}
		}
		if(params.getHadoopResources().getJar()!=null)
		{
			AttachedHadoopResource att=new AttachedHadoopResource();
			att.TypeOfResource=AttachedHadoopResource.ResourceType.Jar;
			att.Key=params.getHadoopResources().getJar().getResourceKey();
			att.IsHDFSPresent=params.getHadoopResources().getJar().isHdfsPresent();
			ResourceAccessType rat=ResourceAccessType.InMessageBytes;
			try
			{
				rat=ResourceAccessType.valueOf(params.getHadoopResources().getJar().getResourceAccess());
			} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
			ServiceUtils.EnrichHadoopResource(att, rat, null, params.getHadoopResources().getJar().getResourceReference(), params.getHadoopResources().getJar().getInMessageBytePayload(), params.getHadoopResources().getJar().getInMessageStringPayload());
			resources.Resources.add(att);
		}
		if(params.getHadoopResources().getLibs()!=null)
		{
			for(HADOOPLibResource res : params.getHadoopResources().getLibs())
			{
				AttachedHadoopResource att=new AttachedHadoopResource();
				att.TypeOfResource=AttachedHadoopResource.ResourceType.Lib;
				att.Key=res.getResourceKey();
				att.IsHDFSPresent=res.isHdfsPresent();
				ResourceAccessType rat=ResourceAccessType.InMessageBytes;
				try
				{
					rat=ResourceAccessType.valueOf(res.getResourceAccess());
				} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
				ServiceUtils.EnrichHadoopResource(att, rat, null, res.getResourceReference(), res.getInMessageBytePayload(), res.getInMessageStringPayload());
				resources.Resources.add(att);
			}
		}
		if(params.getHadoopResources().getMain()!=null)
		{
			AttachedHadoopResource att=new AttachedHadoopResource();
			att.TypeOfResource=AttachedHadoopResource.ResourceType.MainClass;
			att.Key=params.getHadoopResources().getMain().getResourceValue();
			att.Value=params.getHadoopResources().getMain().getResourceValue();
			resources.Resources.add(att);
		}
		if(params.getHadoopResources().getOutputs()!=null)
		{
			for(HADOOPOutputResource res : params.getHadoopResources().getOutputs())
			{
				AttachedHadoopResource att=new AttachedHadoopResource();
				att.TypeOfResource=AttachedHadoopResource.ResourceType.Output;
				att.Key=res.getResourceKey();
				att.CleanUp=res.isCleanup();
				ResourceAccessType rat=ResourceAccessType.CMSReference;
				if(res.getResourceAccess()!=null)
				{
					try
					{
						if(res.getResourceAccess() != null) rat=ResourceAccessType.valueOf(res.getResourceAccess());
					} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
				}
				AccessInfo ai = new AccessInfo();
				if(res.getResourceAccessInfo()!=null)
				{
					if(res.getResourceAccessInfo().getUserId() != null)
					{
						ai.userId = res.getResourceAccessInfo().getUserId();
						ai.password = res.getResourceAccessInfo().getPassword();
					}
					if(res.getResourceAccessInfo().getPort()!=null) ai.port = Integer.parseInt(res.getResourceAccessInfo().getPort());
				}
				ServiceUtils.EnrichHadoopResource(att, rat, ai, res.getResourceReference(), null, null);
				resources.Resources.add(att);
			}
		}
		if(params.getHadoopResources().getProperties()!=null)
		{
			for(HADOOPPropertyResource res : params.getHadoopResources().getProperties())
			{
				AttachedHadoopResource att=new AttachedHadoopResource();
				att.TypeOfResource=AttachedHadoopResource.ResourceType.Property;
				att.Key=res.getResourceValue();
				att.Value=res.getResourceValue();
				resources.Resources.add(att);
			}
		}
		return resources;
	}

	public static AdaptorGridResources GetAdaptorGRIDResources(GRIDParams params) throws WorkflowValidationException, WorkflowEnvironmentException
	{
		AdaptorGridResources resources=new AdaptorGridResources();
		resources.Resources.add(new AttachedGridResource(ServiceContext.GetServiceContext().getScope().toString(),ServiceContext.GetServiceContext().getScope().toString(),AttachedGridResource.ResourceType.Scope));
		for(GRIDResource res : params.getGridResources())
		{
			AttachedGridResource.ResourceType rt=AttachedGridResource.ResourceType.InData;
			try
			{
				rt=AttachedGridResource.ResourceType.valueOf(res.getResourceType());
			} catch(Exception ex) { throw new WorkflowValidationException("Value of resource type invalid"); }
			ResourceAccessType rat=ResourceAccessType.InMessageBytes;
			try
			{
				rat=ResourceAccessType.valueOf(res.getResourceAccess());
			} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
			AttachedGridResource att=null;
			switch(rat)
			{
				case Reference:
				{
					att=new AttachedGridResource(res.getResourceKey(), res.getResourceReference(), rt, AttachedGridResource.AttachedResourceType.Reference);
					if(res.getResourceAccessInfo()!=null)
					{
						if(res.getResourceAccessInfo().getUserId() != null)
						{
							att.accessInfo.userId = res.getResourceAccessInfo().getUserId();
							att.accessInfo.password = res.getResourceAccessInfo().getPassword();
						}
						if(res.getResourceAccessInfo().getPort()!=null) att.accessInfo.port = Integer.parseInt(res.getResourceAccessInfo().getPort());
					}
					break;
				}
				case CMSReference:
				{
					att=new AttachedGridResource(res.getResourceKey(), res.getResourceReference(), rt, AttachedGridResource.AttachedResourceType.CMSReference);
					break;
				}
				case InMessageBytes:
				{
					try
					{
						File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
						BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(tmp));
						bout.write(res.getInMessageBytePayload());
						bout.flush();
						bout.close();
						att=new AttachedGridResource(res.getResourceKey(), tmp.toString(),rt,AttachedGridResource.AttachedResourceType.LocalFile);
					}catch(Exception ex)
					{
						throw new WorkflowEnvironmentException("Could not store attached payload");
					}
					break;
				}
				case InMessageString:
				{
					try
					{
						File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
						BufferedWriter bout=new BufferedWriter(new FileWriter(tmp));
						bout.write(res.getInMessageStringPayload());
						bout.flush();
						bout.close();
						att=new AttachedGridResource(res.getResourceKey(), tmp.toString(),rt,AttachedGridResource.AttachedResourceType.LocalFile);
					}catch(Exception ex)
					{
						throw new WorkflowEnvironmentException("Could not store attached payload");
					}
					break;
				}
				default:
				{
					throw new WorkflowValidationException("Value of resource access invalid");
				}
			}
			resources.Resources.add(att);
		}
		return resources;
	}

	public static AdaptorCondorResources GetAdaptorCONDORResources(CONDORParams params) throws WorkflowValidationException, WorkflowEnvironmentException
	{
		AdaptorCondorResources resources=new AdaptorCondorResources();
		resources.Resources.add(new AttachedCondorResource(ServiceContext.GetServiceContext().getScope().toString(),ServiceContext.GetServiceContext().getScope().toString(),AttachedCondorResource.ResourceType.Scope));
		for(CONDORResource res : params.getCondorResources())
		{
			AttachedCondorResource.ResourceType rt=AttachedCondorResource.ResourceType.InData;
			try
			{
				rt=AttachedCondorResource.ResourceType.valueOf(res.getResourceType());
			} catch(Exception ex) { throw new WorkflowValidationException("Value of resource type invalid"); }
			ResourceAccessType rat=ResourceAccessType.InMessageBytes;
			try
			{
				rat=ResourceAccessType.valueOf(res.getResourceAccess());
			} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
			AttachedCondorResource att=null;
			switch(rat)
			{
				case Reference:
				{
					att=new AttachedCondorResource(res.getResourceKey(), res.getResourceReference(), rt, AttachedCondorResource.AttachedResourceType.Reference);
					break;
				}
				case CMSReference:
				{
					att=new AttachedCondorResource(res.getResourceKey(), res.getResourceReference(), rt, AttachedCondorResource.AttachedResourceType.CMSReference);
					break;
				}
				case InMessageBytes:
				{
					try
					{
						File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
						BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(tmp));
						bout.write(res.getInMessageBytePayload());
						bout.flush();
						bout.close();
						att=new AttachedCondorResource(res.getResourceKey(), tmp.toString(),rt,AttachedCondorResource.AttachedResourceType.LocalFile);
					}catch(Exception ex)
					{
						throw new WorkflowEnvironmentException("Could not store attached payload");
					}
					break;
				}
				case InMessageString:
				{
					try
					{
						File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
						BufferedWriter bout=new BufferedWriter(new FileWriter(tmp));
						bout.write(res.getInMessageStringPayload());
						bout.flush();
						bout.close();
						att=new AttachedCondorResource(res.getResourceKey(), tmp.toString(),rt,AttachedCondorResource.AttachedResourceType.LocalFile);
					}catch(Exception ex)
					{
						throw new WorkflowEnvironmentException("Could not store attached payload");
					}
					break;
				}
				default:
				{
					throw new WorkflowValidationException("Value of resource access invalid");
				}
			}
			resources.Resources.add(att);
		}
		return resources;
	}

	public static AdaptorJDLResources GetAdaptorJDLResources(JDLParams params) throws WorkflowValidationException, WorkflowEnvironmentException
	{
		AdaptorJDLResources resources=new AdaptorJDLResources();
		for(JDLResource res : params.getJdlResources())
		{
			ResourceAccessType rat=ResourceAccessType.InMessageBytes;
			if(res.getResourceType() != null && ResourceType.valueOf(res.getResourceType()).equals(ResourceType.OutData)) rat=ResourceAccessType.CMSReference;
			try
			{
				rat=ResourceAccessType.valueOf(res.getResourceAccess());
			} catch(Exception ex) { throw new WorkflowValidationException("Value of resource access invalid"); }
			AttachedJDLResource att=null;
			switch(rat)
			{
				case Reference:
				{
					ResourceType rType = null;
					if(res.getResourceType()!=null) rType = ResourceType.valueOf(res.getResourceType());
					att=new AttachedJDLResource(res.getResourceKey(), rType, res.getResourceReference(),AttachedJDLResource.AttachedResourceType.Reference);
					logger.info("Created attached jdl resource: key=" + res.getResourceKey() + " type=" + res.getResourceType() + " ref=" + res.getResourceReference() + " access=Reference");
					if(res.getResourceAccessInfo()!=null)
					{
						if(res.getResourceAccessInfo().getUserId() != null)
						{
							att.accessInfo.userId = res.getResourceAccessInfo().getUserId();
							att.accessInfo.password = res.getResourceAccessInfo().getPassword();
						}
						if(res.getResourceAccessInfo().getPort()!=null) att.accessInfo.port = Integer.parseInt(res.getResourceAccessInfo().getPort());
					}
					break;
				}
				case CMSReference:
				{
					ResourceType rType = null;
					if(res.getResourceType()!=null) rType = ResourceType.valueOf(res.getResourceType());
					att=new AttachedJDLResource(res.getResourceKey(), rType, res.getResourceReference(),AttachedJDLResource.AttachedResourceType.CMSReference);
					logger.info("Create attached jdl resource: key=" + res.getResourceKey() + " type=" + res.getResourceType() + " ref=" + res.getResourceReference() + " access=CMSReference");
					break;
				}
				case InMessageBytes:
				{
					try
					{
						//TODO temp file removal
						File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
						BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(tmp));
						bout.write(res.getInMessageBytePayload());
						bout.flush();
						bout.close();
						ResourceType rType = null;
						if(res.getResourceType()!=null) rType = ResourceType.valueOf(res.getResourceType());
						att=new AttachedJDLResource(res.getResourceKey(), rType, tmp.toString(),AttachedJDLResource.AttachedResourceType.LocalFile);
						logger.info("Create attached jdl resource: key=" + res.getResourceKey() + " type=" + res.getResourceType() + " ref=" + res.getResourceReference() + " access=InMessageBytes(Local)");
					}catch(Exception ex)
					{
						throw new WorkflowEnvironmentException("Could not store attached payload", ex);
					}
					break;
				}
				case InMessageString:
				{
					try
					{
						//TODO temp file removal
						File tmp=File.createTempFile(UUID.randomUUID().toString(), "attached.tmp");
						BufferedWriter bout=new BufferedWriter(new FileWriter(tmp));
						bout.write(res.getInMessageStringPayload());
						bout.flush();
						bout.close();
						ResourceType rType = null;
						if(res.getResourceType()!=null) rType = ResourceType.valueOf(res.getResourceType());
						att=new AttachedJDLResource(res.getResourceKey(), rType, tmp.toString(),AttachedJDLResource.AttachedResourceType.LocalFile);
						logger.info("Create attached jdl resource: key=" + res.getResourceKey() + " type=" + res.getResourceType() + " ref=" + res.getResourceReference() + " access=InMessageString(Local)");
					}catch(Exception ex)
					{
						throw new WorkflowEnvironmentException("Could not store attached payload", ex);
					}
					break;
				}
				default:
				{
					throw new WorkflowValidationException("Value of resource access invalid");
				}
			}
			resources.Resources.add(att);
		}
		return resources;
	}
}
