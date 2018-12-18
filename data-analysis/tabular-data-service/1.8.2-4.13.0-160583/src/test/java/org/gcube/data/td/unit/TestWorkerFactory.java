package org.gcube.data.td.unit;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.OperationScope;
import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class TestWorkerFactory implements WorkerFactory<DataWorker> {

	@Override
	public OperationDescriptor getOperationDescriptor() {
		return new OperationDescriptor() {
			
			@Override
			public OperationType getType() {
				return OperationType.TRANSFORMATION;
			}
			
			@Override
			public OperationScope getScope() {
				return OperationScope.TABLE;
			}
			
			@Override
			public List<Parameter> getParameters() {
				return Collections.emptyList();
			}
			
			@Override
			public OperationId getOperationId() {
				return new OperationId(-1);
			}
			
			@Override
			public String getName() {
				return "test operation";
			}
			
			@Override
			public String getDescription() {
				return "test operation";
			}
		};
	}

	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		return new TestWorker(invocation);
	}

	@Override
	public List<WorkerFactory<ValidationWorker>> getPrecoditionValidations() {
		return Collections.emptyList();
	}

	@Override
	public boolean isRollbackable() {
		return false;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return null;
	}

	@Override
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {
		return "invoked";
	}

	@Override
	public Class<DataWorker> getWorkerType() {
		return DataWorker.class;
	}

	@Override
	public Map<String, WorkerFactory<ValidationWorker>> getPreconditionValidationMap() {
		return Collections.emptyMap();
	}

	@Override
	public Map<String, Object> getParametersForPrecondion(String identifier,
			TableId tableId, ColumnLocalId columnId,
			Map<String, Object> sourceParameterInstance)
			throws InvalidInvocationException {
		return Collections.emptyMap();
	}

}
