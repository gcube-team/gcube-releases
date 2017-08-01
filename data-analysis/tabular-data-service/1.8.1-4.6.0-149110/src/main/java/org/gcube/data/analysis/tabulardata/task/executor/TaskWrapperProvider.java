package org.gcube.data.analysis.tabulardata.task.executor;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cleaner.GarbageCollectorFactory;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.exceptions.TabularResourceLockedException;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.metadata.task.StorableTask;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.Factories;
import org.gcube.data.analysis.tabulardata.utils.OperationUtil;
import org.gcube.data.analysis.tabulardata.utils.ResourceCreated;

@Singleton
public class TaskWrapperProvider {

	@Inject
	CubeManager cm;
	
	@Inject
	Factories factories;
	
	@Inject
	GarbageCollectorFactory garbageCollectorFactory;
	
	@Inject
	EntityManagerHelper entityManagerHelper;
	
	@Inject
    Event<ResourceCreated> tableResourceCreationEvent;
	
	@Inject
	DatabaseConnectionProvider connectionProvider;
	
	@Inject
	OperationUtil operationUtil;
	
	public TaskWrapper get(TaskContext context, StorableTabularResource tabularResource, StorableTask task, boolean isResumedExecution) throws TabularResourceLockedException{
		return new TaskWrapper(entityManagerHelper, cm, context, tabularResource, task, 
				garbageCollectorFactory.getGarbageCollector(),tableResourceCreationEvent, connectionProvider, operationUtil, isResumedExecution);
	}
	
}
