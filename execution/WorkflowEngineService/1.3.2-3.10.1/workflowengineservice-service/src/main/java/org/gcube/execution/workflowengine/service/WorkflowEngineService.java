package org.gcube.execution.workflowengine.service;

import gr.uoa.di.madgik.execution.engine.ExecutionEngine;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.engine.QueueableExecutionEngine;
import gr.uoa.di.madgik.execution.exception.ExecutionException;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.ShellPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement.PlanElementType;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowCondorAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowGridAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowHadoopAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowJDLAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.IOutputResource;
import gr.uoa.di.madgik.workflow.directory.ExecutionDirectory;
import gr.uoa.di.madgik.workflow.directory.ExecutionObserver;
import gr.uoa.di.madgik.workflow.exception.WorkflowException;
import gr.uoa.di.madgik.workflow.exception.WorkflowValidationException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.execution.workflowengine.service.stubs.CONDORParams;
import org.gcube.execution.workflowengine.service.stubs.ExecutionEvent;
import org.gcube.execution.workflowengine.service.stubs.GRIDParams;
import org.gcube.execution.workflowengine.service.stubs.HADOOPParams;
import org.gcube.execution.workflowengine.service.stubs.JDLParams;
import org.gcube.execution.workflowengine.service.stubs.JobOutput;
import org.gcube.execution.workflowengine.service.stubs.StatusReport;
import org.gcube.execution.workflowengine.service.stubs.StatusRequest;

public class WorkflowEngineService extends GCUBEPortType
{
	private static final Logger logger=LoggerFactory.getLogger(WorkflowEngineService.class);
	
	public enum ResourceAccessType
	{
		InMessageBytes,
		InMessageString,
		Reference,
		CMSReference
	}

	@Override
	protected GCUBEServiceContext getServiceContext()
	{
		return ServiceContext.GetServiceContext();
	}

	public String about(String name) 
	{		
		return "Hello WS world";
	}
	
	public String adaptJDL(JDLParams params) throws RemoteException
	{
		if(params.getJdlDescription()==null || params.getJdlDescription().trim().length()==0) throw new RemoteException("No jdl specified",new WorkflowValidationException("No jdl specified"));
		if(params.getJdlResources()==null || params.getJdlResources().length==0) throw new RemoteException("No resources specified",new WorkflowValidationException("No resources specified"));
		WorkflowJDLAdaptor adaptor=new WorkflowJDLAdaptor();
		adaptor.ConstructEnvironmentHints(ServiceContext.GetServiceContext().getScope().toString());
		adaptor.SetJDL(params.getJdlDescription());
		String ExecutionID=ExecutionDirectory.ReserveKey();
		adaptor.SetExecutionId(ExecutionID);
		try
		{
			adaptor.SetAdaptorResources(ServiceUtils.GetAdaptorJDLResources(params));
		}catch(WorkflowException ex)
		{
			logger.warn("Could not retrieve adaptor resources", ex);
			throw new RemoteException("Could not retrieve adaptor resources", ex);
		}
		try
		{
			adaptor.CreatePlan();
		}catch(WorkflowException ex)
		{
			logger.warn("Could not create plan", ex);
			throw new RemoteException("Could not create plan", ex);
		}
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting=params.getConfig().isChokePerformanceEvents();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting=params.getConfig().isChokeProgressEvents();
		boolean isQueueable=params.getConfig().isQueueSupport();
		if (isQueueable) {
			adaptor.GetCreatedPlan().Config.Utiliaztion=params.getConfig().getUtilization();
			adaptor.GetCreatedPlan().Config.PassedBy=params.getConfig().getPassedBy();
		}
		
		ExecutionHandle Handle= null;
		try
		{
			if (isQueueable)
				Handle= QueueableExecutionEngine.Submit(adaptor.GetCreatedPlan());
			else
				Handle= ExecutionEngine.Submit(adaptor.GetCreatedPlan());
		} catch (ExecutionException ex)
		{
			logger.warn("Could not submit plan", ex);
			throw new RemoteException("Could not submit plan", ex);
		}
		logger.info("Starting execution with id "+ExecutionID);
		ExecutionObserver obs=new ExecutionObserver(ExecutionID,ExecutionDirectory.DirectoryEntryType.JDL,params.getExecutionLease(),Handle,adaptor,true);
		ExecutionDirectory.Register(obs);
		Handle.RegisterObserver(obs);
		try
		{
			if (isQueueable)
				QueueableExecutionEngine.Execute(Handle);
			else
				ExecutionEngine.Execute(Handle);
		} catch (ExecutionException ex)
		{
			logger.warn("Could not execute plan with id "+ExecutionID, ex);
			throw new RemoteException("Could not execute plan", ex);
		}
		return ExecutionID;
	}
	
	public String adaptGRID(GRIDParams params) throws RemoteException
	{
		if(params.getGridResources()==null || params.getGridResources().length==0) throw new RemoteException("No resources specified",new WorkflowValidationException("No resources specified"));
		WorkflowGridAdaptor adaptor=new WorkflowGridAdaptor();
		try
		{
			adaptor.SetAdaptorResources(ServiceUtils.GetAdaptorGRIDResources(params));
		}catch(WorkflowException ex)
		{
			logger.warn("Could not retrieve adaptor resources", ex);
			throw new RemoteException("Could not retrieve adaptor resources", ex);
		}
		if(params.getConfig().getRetryOnErrorPeriod()>0)adaptor.RetryOnErrorPeriod=params.getConfig().getRetryOnErrorPeriod();
		if(params.getConfig().getRetryOnErrorTimes()>0)adaptor.RetryOnErrorTimes=params.getConfig().getRetryOnErrorTimes();
		if(params.getConfig().getTimeout()>0)adaptor.Timeout=params.getConfig().getTimeout();
		if(params.getConfig().getWaitPeriod()>0)adaptor.WaitPeriod=params.getConfig().getWaitPeriod();
		String ExecutionID=ExecutionDirectory.ReserveKey();
		adaptor.SetExecutionId(ExecutionID);
		try
		{
			adaptor.CreatePlan();
		}catch(WorkflowException ex)
		{
			logger.warn("Could not create plan", ex);
			throw new RemoteException("Could not create plan", ex);
		}
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting=params.getConfig().isChokePerformanceEvents();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting=params.getConfig().isChokeProgressEvents();

		ExecutionHandle Handle= null;
		try
		{
			Handle= ExecutionEngine.Submit(adaptor.GetCreatedPlan());
		} catch (ExecutionException ex)
		{
			logger.warn("Could not submit plan", ex);
			throw new RemoteException("Could not submit plan", ex);
		}
		logger.info("Starting execution with id "+ExecutionID);
		ExecutionObserver obs=new ExecutionObserver(ExecutionID,ExecutionDirectory.DirectoryEntryType.Grid,params.getExecutionLease(),Handle,adaptor,true);
		ExecutionDirectory.Register(obs);
		Handle.RegisterObserver(obs);
		try
		{
			ExecutionEngine.Execute(Handle);
		} catch (ExecutionException ex)
		{
			logger.warn("Could not execute plan with id "+ExecutionID, ex);
			throw new RemoteException("Could not execute plan", ex);
		}
		return ExecutionID;
	}
	
	public String adaptCONDOR(CONDORParams params) throws RemoteException
	{
		if(params.getCondorResources()==null || params.getCondorResources().length==0) throw new RemoteException("No resources specified",new WorkflowValidationException("No resources specified"));
		WorkflowCondorAdaptor adaptor=new WorkflowCondorAdaptor();
		try
		{
			adaptor.SetAdaptorResources(ServiceUtils.GetAdaptorCONDORResources(params));
		}catch(WorkflowException ex)
		{
			logger.warn("Could not retrieve adaptor resources", ex);
			throw new RemoteException("Could not retrieve adaptor resources", ex);
		}
		adaptor.RetrieveJobClassAd=params.getConfig().isRetrieveJobClassAd();
		if(params.getConfig().getWaitPeriod()>0) adaptor.WaitPeriod=params.getConfig().getWaitPeriod();
		if(params.getConfig().getTimeout()>0)adaptor.Timeout=params.getConfig().getTimeout();
		adaptor.IsDag=params.getConfig().isIsDag();
		String ExecutionID=ExecutionDirectory.ReserveKey();
		adaptor.SetExecutionId(ExecutionID);
		try
		{
			adaptor.CreatePlan();
		}catch(WorkflowException ex)
		{
			logger.warn("Could not create plan", ex);
			throw new RemoteException("Could not create plan", ex);
		}
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting=params.getConfig().isChokePerformanceEvents();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting=params.getConfig().isChokeProgressEvents();

		ExecutionHandle Handle= null;
		try
		{
			Handle= ExecutionEngine.Submit(adaptor.GetCreatedPlan());
		} catch (ExecutionException ex)
		{
			logger.warn("Could not submit plan", ex);
			throw new RemoteException("Could not submit plan", ex);
		}
		logger.info("Starting execution with id "+ExecutionID);
		ExecutionObserver obs=new ExecutionObserver(ExecutionID,ExecutionDirectory.DirectoryEntryType.Condor,params.getExecutionLease(),Handle,adaptor,true);
		ExecutionDirectory.Register(obs);
		Handle.RegisterObserver(obs);
		try
		{
			ExecutionEngine.Execute(Handle);
		} catch (ExecutionException ex)
		{
			logger.warn("Could not execute plan with id "+ExecutionID, ex);
			throw new RemoteException("Could not execute plan", ex);
		}
		return ExecutionID;
	}
	
	public String adaptHADOOP(HADOOPParams params) throws RemoteException
	{
		if(params.getHadoopResources()==null) throw new RemoteException("No resources specified",new WorkflowValidationException("No resources specified"));
		WorkflowHadoopAdaptor adaptor=new WorkflowHadoopAdaptor();
		try
		{
			adaptor.SetAdaptorResources(ServiceUtils.GetAdaptorHADOOPResources(params));
		}catch(WorkflowException ex)
		{
			logger.warn("Could not retrieve adaptor resources", ex);
			throw new RemoteException("Could not retrieve adaptor resources", ex);
		}
		String ExecutionID=ExecutionDirectory.ReserveKey();
		adaptor.SetExecutionId(ExecutionID);
		try
		{
			adaptor.CreatePlan();
		}catch(WorkflowException ex)
		{
			logger.warn("Could not create plan", ex);
			throw new RemoteException("Could not create plan", ex);
		}
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting=params.getConfig().isChokePerformanceEvents();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting=params.getConfig().isChokeProgressEvents();

		ExecutionHandle Handle= null;
		try
		{
			Handle= ExecutionEngine.Submit(adaptor.GetCreatedPlan());
		} catch (ExecutionException ex)
		{
			logger.warn("Could not submit plan", ex);
			throw new RemoteException("Could not submit plan", ex);
		}
		logger.info("Starting execution with id "+ExecutionID);
		ExecutionObserver obs=new ExecutionObserver(ExecutionID,ExecutionDirectory.DirectoryEntryType.Hadoop,params.getExecutionLease(),Handle,adaptor,true);
		ExecutionDirectory.Register(obs);
		Handle.RegisterObserver(obs);
		try
		{
			ExecutionEngine.Execute(Handle);
		} catch (ExecutionException ex)
		{
			logger.warn("Could not execute plan with id "+ExecutionID, ex);
			throw new RemoteException("Could not execute plan", ex);
		}
		return ExecutionID;
	}
	
	public StatusReport executionStatus(StatusRequest request) throws RemoteException
	{
		if(request.getExecutionID()==null || request.getExecutionID().trim().length()==0) throw new RemoteException("No execution id provided", new WorkflowValidationException("No execution id provided"));
		ExecutionObserver obs = ExecutionDirectory.Retrieve(request.getExecutionID());
		if(obs==null) throw new RemoteException("No execution observer found for provided id", new WorkflowValidationException("No execution observer found for provided id"));
		StatusReport report=new StatusReport();
		report.setIsCompleted(obs.IsCompleted());
		if(obs.IsCompleted() || request.getIncludePlan())
		{
			try
			{
				report.setPlan(obs.GetExecutionHandle().GetPlan().Serialize());
			} catch (ExecutionException ex)
			{
				throw new RemoteException("Could not retrieve plan serialization", ex);
			}
		}
		report.setEvents(ServiceUtils.GetExecutionEvents(obs).toArray(new ExecutionEvent[0]));
		List<JobOutput> reportOutput =new ArrayList<JobOutput>();
		if(report.isIsCompleted())
		{
			if(ServiceUtils.EvaluateResult(obs))
			{
				try
				{
					report.setError(ServiceUtils.GetExecutionErrorMessage(obs));
					StringBuilder buf=new StringBuilder();
					for(IPlanElement elem : obs.GetExecutionHandle().GetPlan().LocateActionElements())
					{
						try
						{
							if(elem.GetPlanElementType()!=PlanElementType.Shell) continue;
							if(((ShellPlanElement)elem).StdErrParameter==null) continue;
							Object obj=((ShellPlanElement)elem).StdErrParameter.GetParameterValue(obs.GetExecutionHandle());
							if(obj==null) continue;
							if(!((ShellPlanElement)elem).StdErrIsFile) buf.append(obj.toString());
							else buf.append("Error details located in file stored in Storage System with ID : " + obj.toString());
							buf.append("\n");
						}catch(Exception ex)
						{
							logger.debug("Error trying to retrieve action element error parameter. Continuing");
						}
					}
					report.setErrorDetails(buf.toString());
				} catch (IOException ex)
				{
					throw new RemoteException("Could not retrieve error message of unsuccessful execution",ex);
				}
			}
			for(IOutputResource res : obs.GetAdaptor().GetOutput())
			{
				try
				{
					reportOutput.add(ServiceUtils.GetJobOutput(obs, res));
				} catch (ExecutionException ex)
				{
					throw new RemoteException("Could not retrieve job output", ex);
				} catch (WorkflowException ex)
				{
					throw new RemoteException("Could not retrieve job output", ex);
				}
			}
			report.setOutput(reportOutput.toArray(new JobOutput[0]));
		}
		return report;
	}
}
