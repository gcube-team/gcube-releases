package org.gcube.data.analysis.tabulardata.service.impl.operation.tasks;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskFactory {

	private static Logger logger = LoggerFactory.getLogger(TaskFactory.class);

	private static Map<String, TaskFactory> factoryPerScope = new HashMap<String, TaskFactory>();

	public static TaskFactory getFactory(){
		String scope = ScopeProvider.instance.get();
		if(!factoryPerScope.containsKey(scope))
			factoryPerScope.put(scope, new TaskFactory(scope));
		return factoryPerScope.get(scope);

	}

	private Map<String, Task> tasks = new HashMap<String, Task>();

	private TaskUpdater updater;

	private String scope;

	public TaskFactory(String scope) {
		super();
		this.scope = scope;
		updater = new TaskUpdater(this.scope);
	}

	public Task createTask(TaskInfo taskInfo){
		if (!ScopeProvider.instance.get().equals(this.scope)) throw new RuntimeException("Invalid Scope Exception (actual scope is different to factory scope)");
		TaskObject task = new TaskObject(taskInfo);
		logger.debug("======== "+task);
		if (!task.getStatus().isFinal())
			task.setUpdater(updater);
		tasks.put(task.getId().getValue(), task);
		return task;
	}

	public Task getTask(TaskId taskIdentifier) throws NoSuchTaskException{
		if (!ScopeProvider.instance.get().equals(this.scope)) throw new RuntimeException("Invalid Scope Exception (actual scope is different to factory scope)");
		if (!tasks.containsKey(taskIdentifier)){

			List<TaskInfo> tasks= tasks().build().get(taskIdentifier.getValue());
			if (tasks.size()==0) throw new NoSuchTaskException(taskIdentifier.getValue());
			TaskObject task = new TaskObject(tasks.get(0));
			if (!task.getStatus().isFinal())
				task.setUpdater(updater);
			return task;
		} else return tasks.get(taskIdentifier);
	}

	public List<Task> getTasks(TabularResourceId tabularResourceId, TaskStatus status) throws NoSuchTabularResourceException{
		if (!ScopeProvider.instance.get().equals(this.scope)) throw new RuntimeException("Invalid Scope Exception (actual scope is different to factory scope)");
		List<Task> taskInfos = new ArrayList<Task>();

		List<TaskInfo> infos = null;
		if (status ==null) infos = tasks().build().getTasksByTabularResource(tabularResourceId.getValue());
		else infos = tasks().build().getTasksByTabularResource(tabularResourceId.getValue(), status);

		for (TaskInfo info : infos){
			if (tasks.containsKey(info.getIdentifier()))
				taskInfos.add(tasks.get(info.getIdentifier()));
			else{
				TaskObject task = new TaskObject(info);
				if (!task.getStatus().isFinal())
					task.setUpdater(updater);
				taskInfos.add(task);
			}
		}
		return taskInfos;

	}



	/*
	public removeTask(String user,TaskId taskIdentifier, TaskManagerProxy proxy) throws NoSuchTaskException {
		if (!ScopeProvider.instance.get().equals(this.scope)) throw new RuntimeException("Invalid Scope Exception (actual scope is different to factory scope)");
		if (tasks.containsKey(taskIdentifier)) tasks.remove(taskIdentifier);
		TaskObject.remove(user, taskIdentifier);
		try {
			proxy.remove(taskIdentifier.getValue());
		} catch (org.gcube.data.td.commons.webservice.exception.NoSuchTaskException e) {
			throw new NoSuchTaskException(taskIdentifier);
		}
		updater.unregisterObserver(taskIdentifier.getValue());
	}*/

}

