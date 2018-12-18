package org.gcube.data.analysis.tabulardata.utils;

import java.util.Collection;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;

public class Factories {

	private Map<OperationId, WorkerFactory<?>> factories;
	
	protected Factories(Map<OperationId, WorkerFactory<?>> factories){
		this.factories = factories;
	}
		
	public WorkerFactory<?> get(OperationId id){
		return factories.get(id);
	}
	
	public boolean isEmpty(){
		return factories.isEmpty();
	}
	
	public Collection<WorkerFactory<?>> values(){
		return factories.values();
	}

	public boolean containsKey(OperationId operationId) {
		return factories.containsKey(operationId);
	}
	
}
