package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.io.Serializable;
import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.service.operation.Task;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public class TRTasksManager implements Serializable {

	
	private static final long serialVersionUID = 4517156156005181775L;
	
	private HashMap<Long, Task> trTasksMap=new HashMap<Long, Task>();
	
	public void add(Task trTask){
		trTasksMap.put(Long.getLong(trTask.getId().getValue()),trTask);
	};
	
	public Task get(Long id){
		return trTasksMap.get(id);
	};
	
	public void remove(Long id){
		trTasksMap.remove(id);
	};
	
}
