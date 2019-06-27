package org.gcube.data.analysis.tabulardata.operation.worker;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;


public interface WorkerFactory<T extends Worker<?>> {
	
	OperationDescriptor getOperationDescriptor();

	T createWorker(OperationInvocation invocation) throws InvalidInvocationException;
	
	@Deprecated
	List<WorkerFactory<ValidationWorker>> getPrecoditionValidations();
	
	boolean isRollbackable();
	
	RollbackWorker createRollbackWoker(Table diffTable, Table createdTable, OperationInvocation oldInvocation);
	
	String describeInvocation(OperationInvocation toDescribeInvocation)throws InvalidInvocationException;
	
	Class<T> getWorkerType();

	Map<String, WorkerFactory<ValidationWorker>> getPreconditionValidationMap();

	Map<String, Object> getParametersForPrecondion(String identifier, TableId tableId, ColumnLocalId columnId,
			Map<String, Object> sourceParameterInstance) throws InvalidInvocationException;
}
