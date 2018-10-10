package org.gcube.data.analysis.tabulardata.task.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;

import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.common.scope.impl.ScopedTasks;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.OperationTaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.RollbackTaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TemplateTaskInfo;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.metadata.task.StorableTask;
import org.gcube.data.analysis.tabulardata.task.RunnableTask;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.executor.TaskWrapper;
import org.gcube.data.analysis.tabulardata.task.executor.TaskWrapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TaskEngine {

	private Logger logger = LoggerFactory.getLogger(TaskEngine.class);

	private ExecutorService executorService;

	private TaskWrapperProvider taskExecutorFactory;

	private static Map<String, TaskWrapper> contextTaskMap = new HashMap<String, TaskWrapper>();

	@Inject
	public TaskEngine(ExecutorService executorService, TaskWrapperProvider taskExecutorFactory){
		this.executorService = executorService;
		this.taskExecutorFactory = taskExecutorFactory;
	}

	public TaskInfo createTask(String submitter, TaskContext context, StorableTabularResource tabularResource) {
		TaskInfo taskInfo = new OperationTaskInfo(submitter, tabularResource.getId());
		create(taskInfo, context, tabularResource, null);
		return taskInfo;
	}

	public TaskInfo createTask(String submitter, TaskContext context, StorableTabularResource tabularResource, RunnableTask onSuccess) {
		TaskInfo taskInfo = new OperationTaskInfo(submitter, tabularResource.getId());
		create(taskInfo, context, tabularResource, onSuccess);
		return taskInfo;
	}
	
	public TaskInfo createRollbackTask(String submitter, TaskContext context, StorableTabularResource tabularResource, List<Long> historyStepToRemove) {
		TaskInfo taskInfo = new RollbackTaskInfo(submitter, tabularResource.getId(), historyStepToRemove);
		create(taskInfo, context, tabularResource, null);
		return taskInfo;
	}

	public TaskInfo createTemplateTask(String submitter, TaskContext context, StorableTabularResource tabularResource, long templateId, RunnableTask onSuccess){
		TaskInfo taskInfo = new TemplateTaskInfo(submitter,tabularResource.getId(), templateId);
		create(taskInfo, context, tabularResource, onSuccess);
		return taskInfo;
	}

	private synchronized void create(TaskInfo taskInfo, TaskContext context, StorableTabularResource tabularResource, RunnableTask onSuccess){
		StorableTask storableTask = new StorableTask(taskInfo, tabularResource);
		storableTask.setTaskContext(context);
		taskInfo.setTaskSteps(context.getTasks());
		storableTask.setStoredTask(taskInfo);
		logger.trace("task created : "+storableTask.getIdentifier());
		startTask(taskInfo.getIdentifier(), storableTask,  tabularResource, context, onSuccess);
	}

	private void startTask(String taskId, StorableTask taskProxy, StorableTabularResource tabularResource, TaskContext context, RunnableTask onSuccess ){
		TaskWrapper task = taskExecutorFactory.get(context, tabularResource, taskProxy, false);
		if (onSuccess!=null)
			task.registerOnSuccessEvent(onSuccess);
		contextTaskMap.put(taskId, task);
		executorService.execute(ScopedTasks.bind(AuthorizedTasks.bind(task)));
	}


	public TaskInfo get(String taskId, EntityManager entityManager) throws NoSuchTaskException{
		//logger.info("requesting task "+taskId);
		TaskInfo info;
		StorableTask task;
		if (contextTaskMap.containsKey(taskId))
			task = contextTaskMap.get(taskId).getTask();
		else{
			task = entityManager.find(StorableTask.class, taskId);
			if (task==null)throw new  NoSuchTaskException(taskId);
		}

		info = task.getStoredTask();
		info.setTaskSteps(task.getTaskContext().getTasks());

		if (contextTaskMap.containsKey(taskId) && (info.getStatus()==TaskStatus.ABORTED || info.getStatus()==TaskStatus.FAILED 
				|| info.getStatus()==TaskStatus.STOPPED || info.getStatus()==TaskStatus.SUCCEDED))
			contextTaskMap.remove(info.getIdentifier());
		
		return info;
	}

	public TaskInfo continueTaskExecution(StorableTask task, Map<String, Object> instanceParametersToChange) throws NoSuchTaskException{

		TaskContext tContext = task.getTaskContext();
		tContext.resetPostOperationsForResume();
		tContext.cleanValidationsOnCurrentStep();
		tContext.movePrevious();
		if (instanceParametersToChange!=null && !instanceParametersToChange.isEmpty())
			tContext.addParametersOnNextOperation(instanceParametersToChange);


		TaskWrapper taskExecutor = taskExecutorFactory.get(tContext, task.getTabularResource(), task, true);
		contextTaskMap.put(task.getIdentifier(), taskExecutor);
		executorService.execute(ScopedTasks.bind(taskExecutor));
		return task.getStoredTask();
	}

	public void abort(TaskInfo task, EntityManager em) throws NoSuchTaskException{

		if (task.getStatus()==TaskStatus.FAILED || task.getStatus()==TaskStatus.ABORTED || task.getStatus()==TaskStatus.SUCCEDED)
			return;

		if (contextTaskMap.containsKey(task.getIdentifier())){
			TaskWrapper taskWrapper = contextTaskMap.get(task.getIdentifier());
			task.setTaskSteps(taskWrapper.getTaskContext().getTasks());
			taskWrapper.abort();
		} else {
			em.getTransaction().begin();
			try{
				StorableTask stask = em.find(StorableTask.class, task.getIdentifier());
				if (stask==null) throw new NoSuchTaskException(task.getIdentifier());
				stask.getStoredTask().setStatus(TaskStatus.ABORTED);
				em.merge(stask);
			}finally{
				em.getTransaction().commit();
			}
		}
		
	}

	public TaskInfo remove(String taskId, EntityManager em) throws NoSuchTaskException{
		final StorableTask task = em.find(StorableTask.class, taskId);
		if (task==null)throw new  NoSuchTaskException(taskId);
		TaskInfo info = task.getStoredTask();

		abort(info, em);

		em.getTransaction().begin();
		em.remove(task);
		em.getTransaction().commit();
		return info;
	}

}
