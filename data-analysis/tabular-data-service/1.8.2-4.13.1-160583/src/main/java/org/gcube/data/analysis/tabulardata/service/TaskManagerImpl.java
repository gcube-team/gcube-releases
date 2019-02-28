package org.gcube.data.analysis.tabulardata.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;
import javax.persistence.EntityManager;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchOption;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.ExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ResumeOperationRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.metadata.task.StorableTask;
import org.gcube.data.analysis.tabulardata.task.engine.TaskEngine;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(portName = "TaskeManagerPort",
serviceName = TaskManager.SERVICE_NAME,
targetNamespace = Constants.TASK_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager")
@Singleton
@WeldService
public class TaskManagerImpl implements TaskManager{

	private Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);

	@Inject
	EntityManagerHelper emHelper;

	@Inject
	TaskEngine taskEngine;

	@Inject
	OperationManager operationManager;

	@PreDestroy
	public void destroyTasks(){
		logger.trace("checking tasks before shutdown");
		abortUnfinishedTask();
	}

	@Override
	public TaskInfo remove(String identifier) throws NoSuchTaskException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		if (taskEngine.get(identifier, em).getSubmitter().equals(AuthorizationProvider.instance.get()))
			throw new InternalSecurityException("trying to abort a task without authorization");
		try{
			return taskEngine.remove(identifier, em);
		}finally{
			em.close();
		}
	}

	@Override //TODO: no check authentication for batch updates
	public List<TaskInfo> get(String[] identifiers) {
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		EntityManager em = emHelper.getEntityManager();
		for(String id : identifiers)
			try {
				tasks.add(taskEngine.get(id, em));
			} catch (NoSuchTaskException e) {
				logger.warn("task with id {} not found",id,e);
			}
		em.close();
		return tasks;
	}

	@Override
	public List<TaskInfo> getTasksByTabularResource(Long tabularResourceId) throws NoSuchTabularResourceException, InternalSecurityException {
		logger.info("requesting tasks for tabularResources with id {} ",tabularResourceId);
		Map<String, Object> parameters = new HashMap<String, Object>(4);
		parameters.put("trid", tabularResourceId);
		parameters.put("user", AuthorizationProvider.instance.get().getClient().getId());
		parameters.put("group", ScopeProvider.instance.get());
		parameters.put("scope", ScopeProvider.instance.get());
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		EntityManager em = emHelper.getEntityManager();
		try{
			for (StorableTask task : emHelper.getResults("TASK.getByTr", StorableTask.class, parameters))
				try{
					tasks.add(taskEngine.get(task.getIdentifier(), em));
				}catch (NoSuchTaskException e) {
					logger.warn("unexpected exception", e);
				}
		}finally{
			em.close();
		}
		return tasks;
	}

	@Override
	public List<TaskInfo> getTasksByStatusAndTabularResource(Long tabularResourceId, TaskStatus status) throws NoSuchTabularResourceException, InternalSecurityException {
		logger.info("requesting tasks for tabularResources with id {} and status {} ",tabularResourceId, status);
		Map<String, Object> parameters = new HashMap<String, Object>(4);
		parameters.put("trid", tabularResourceId);
		parameters.put("user", AuthorizationProvider.instance.get().getClient().getId());
		parameters.put("group", ScopeProvider.instance.get());
		parameters.put("scope", ScopeProvider.instance.get());
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		EntityManager em = emHelper.getEntityManager();
		try{
			for (StorableTask task : emHelper.getResults("TASK.getByTr", StorableTask.class, parameters)){
				logger.trace("task found "+task.getStoredTask());
				try{
					TaskInfo updatedTask = taskEngine.get(task.getIdentifier(), em);
					if (updatedTask.getStatus()== status)
						tasks.add(updatedTask);
				}catch (NoSuchTaskException e) {
					logger.warn("unexpected exception", e);
				}
			}	
		}finally{
			em.close();
		}
		return tasks;

	}

	private void abortUnfinishedTask() {
		EntityManager em = emHelper.getEntityManager();

		try{
			for (StorableTask task : emHelper.getResults("TASK.getAll", StorableTask.class)){
				logger.trace("checking task "+task.getIdentifier()+" fro shutdown");
				if (!task.getStoredTask().getStatus().isFinal())
					try{
						taskEngine.abort(task.getStoredTask(), em);
						logger.info("task "+task.getIdentifier()+" aborted");
					}catch (NoSuchTaskException e) {
						logger.trace("task not found for abort");
					}
			}	
		}finally{
			em.close();
		}
	}


	@Override
	public TaskInfo abort(String identifier) throws NoSuchTaskException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			TaskInfo info = taskEngine.get(identifier, em);
			if (info.getSubmitter().equals(AuthorizationProvider.instance.get()))
				throw new InternalSecurityException("trying to abort a task without ownership");
			taskEngine.abort(info, em);
			return info;
		}finally{
			em.close();
		}
	}

	@Override
	public TaskInfo resubmit(String identifier) throws NoSuchTaskException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		StorableTask oldTask = em.find(StorableTask.class, identifier);
		em.close();
		if (oldTask==null)throw new  NoSuchTaskException(identifier);;
		if ((oldTask.getStoredTask().getSubmitter().equals(AuthorizationProvider.instance.get())))
			throw new InternalSecurityException("trying to resubmit a task without ownership");
		if (!oldTask.getStoredTask().isResubmittable())
			throw new InternalSecurityException("the task "+identifier+" cannot be resubmitted");

		List<OperationExecution> oldInvocation = new ArrayList<OperationExecution>(oldTask.getStoredTask().getTaskSteps().size());
		for (TaskStep step : oldTask.getStoredTask().getTaskSteps())	
			oldInvocation.add(step.getSourceInvocation());

		if (oldInvocation.size()==1){
			try {
				return operationManager.execute(new ExecuteRequest(oldTask.getTabularResource().getId(), oldInvocation.get(0)));
			} catch (Exception e) {
				logger.error("unexpected exception",e);
				throw new RuntimeException("unexpected exception",e);
			}	
		}else{
			try {
				return operationManager.batchExecute(new BatchExecuteRequest(oldTask.getTabularResource().getId(), oldInvocation, BatchOption.ROLLBACK));
			} catch (Exception e) {
				logger.error("unexpected exception",e);
				throw new RuntimeException("unexpected exception",e);
			}	
		}
	}


	@Override
	public TaskInfo resume(ResumeOperationRequest request) throws NoSuchTaskException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableTask oldTask = em.find(StorableTask.class, request.getIdentifier());
			if (oldTask==null) throw new  NoSuchTaskException(request.getIdentifier());

			if ((oldTask.getStoredTask().getSubmitter().equals(AuthorizationProvider.instance.get())))
				throw new InternalSecurityException("trying to resubmit a task without ownership");

			if (!(oldTask.getStoredTask().getStatus()==TaskStatus.STOPPED))
				throw new RuntimeException("the task "+request.getIdentifier()+" cannot be resubmitted (the status was not stopped)");
			return taskEngine.continueTaskExecution(oldTask, request.getCurrentOperationParameter());
		}finally{
			em.close();
		}

	}


}
