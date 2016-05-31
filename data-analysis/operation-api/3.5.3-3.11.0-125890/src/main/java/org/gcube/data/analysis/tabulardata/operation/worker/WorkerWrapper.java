package org.gcube.data.analysis.tabulardata.operation.worker;

import java.util.HashMap;
import java.util.Map;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationScope;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerWrapper<T extends Worker<R>, R extends Result> implements AbortListener{

private static final Logger log = LoggerFactory.getLogger(WorkerWrapper.class);
	
	private WorkerFactory<T> factory;
	private T lastWorker;
	
	private boolean aborted = false;
	
	protected WorkerWrapper(WorkerFactory<T> wrapped) {		
		this.factory = wrapped;
	}


	public WorkerStatus execute(TableId tableId, ColumnLocalId columnId, Map<String,Object> parameters) throws InvalidInvocationException, OperationAbortedException{
		OperationDescriptor descriptor=factory.getOperationDescriptor();
		if(parameters==null)parameters=new HashMap<String,Object>();
		InvocationCreator creator=InvocationCreator.getCreator(descriptor).setParameters(parameters);
		if(!descriptor.getScope().equals(OperationScope.VOID))creator.setTargetTable(tableId);
		if(descriptor.getScope().equals(OperationScope.COLUMN)) creator.setTargetColumn(columnId);
		OperationInvocation invocation=creator.create();	
		lastWorker=factory.createWorker(invocation);
		log.debug("Running sub - worker : "+lastWorker+", with "+invocation);
		checkAborted();
		lastWorker.run();
		if (lastWorker.getStatus()==WorkerStatus.FAILED)
			log.warn("wrapper error", lastWorker.getException());
		return lastWorker.getStatus();
	}
	
	
	public R getResult(){
		return lastWorker.getResult();
	}
	
	public void onAbort(){
		aborted = true;
		if (lastWorker!=null)
			lastWorker.abort();
	}
	
	private void checkAborted() throws OperationAbortedException{
		if (aborted) throw new OperationAbortedException();
	}
}
