package org.gcube.data.analysis.tabulardata.service.impl.operation.tasks;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tasks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;

public class TaskUpdater implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final long MILLIS_FOR_UPDATE = 3000;

	private Long timestamp = 0l;  

	private Map<String, TaskObserver> observers = new HashMap<String, TaskObserver>();

	private String scope;

	public TaskUpdater(String scope) {
		super();
		this.scope = scope;
	}

	void registerObserver(TaskObserver observer){
		this.observers.put(observer.getObserverIdentifier(), observer);
	}

	void unregisterObserver(String observerIdentifier){
		this.observers.remove(observerIdentifier);
	}

	boolean checkUpdate(){
		long actualTimestamp =System.currentTimeMillis();
		if (!observers.isEmpty() && (actualTimestamp-timestamp)>MILLIS_FOR_UPDATE){
			timestamp = System.currentTimeMillis();
			notifiesObservers(callUpdate());
			return true;
		}else return false;
	}

	private List<TaskInfo> callUpdate() {
		String actualScope = ScopeProvider.instance.get();
		try{
			ScopeProvider.instance.set(scope);
			TaskManagerProxy proxy = tasks().build();
			List<TaskInfo> tasks = proxy.get(observers.keySet().toArray(new String[observers.size()]));
			if (tasks.size()<observers.size())
				observers = new HashMap<String, TaskObserver>();
			return tasks;
		}finally{
			if (actualScope!=null)
				ScopeProvider.instance.set(actualScope);
		}
	}

	private void notifiesObservers(List<TaskInfo> tasks){
		for (TaskInfo task: tasks)
			observers.get(task.getIdentifier()).notify(task);

	}

}
