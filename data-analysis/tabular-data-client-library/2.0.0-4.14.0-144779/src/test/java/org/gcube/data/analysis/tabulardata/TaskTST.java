package org.gcube.data.analysis.tabulardata;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tasks;

import java.util.Calendar;
import java.util.List;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.junit.Before;
import org.junit.Test;

public class TaskTST {

	@Before
	public void init(){
		ScopeProvider.instance.set("/gcube/devsec");
	}
	
	@Test
	public void getTasksByTRId() throws NoSuchTabularResourceException{
		TaskManagerProxy taskManager = tasks().build();
		//System.out.println(taskManager.getTasksByTabularResource(7l).size());
		long t = Calendar.getInstance().getTimeInMillis();
		for (TaskInfo task : taskManager.getTasksByTabularResource(6l)){
			System.out.println(task.getStartTime().getTimeInMillis()-t);
			t=task.getStartTime().getTimeInMillis();
		}
	}
	
	@Test
	public void getTaskByID(){
		TaskManagerProxy taskManager = tasks().build();
		List<TaskInfo> tasks = taskManager.get("2ce7ebce-46df-4fb2-9b87-9bf43a346927");
		
		for (TaskInfo task: tasks){
			System.out.println(task);
			System.out.println(task.getStatus());
			System.out.println(task.getTaskSteps().get(0).getProgress());
		}
		
	}
	
}
