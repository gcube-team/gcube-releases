package org.gcube.data.td.resources;

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
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class ResourceOperationTST implements WorkerFactory<ResourceCreatorWorker>{

	@Override
	public OperationDescriptor getOperationDescriptor() {
		return new OperationDescriptor() {
			
			@Override
			public OperationType getType() {
				return OperationType.RESOURCECREATOR;
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
				return new OperationId(Integer.MAX_VALUE);
			}
			
			@Override
			public String getName() {
				return "testresourceCreator";
			}
			
			@Override
			public String getDescription() {
				return "testresourceCreator descr";
			}
		};
	}

	@Override
	public ResourceCreatorWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		return new ResourceCreatorTest(invocation);
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
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {
		return "testresourceCreator descr";
	}

	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, WorkerFactory<ValidationWorker>> getPreconditionValidationMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getParametersForPrecondion(String identifier,
			TableId tableId, ColumnLocalId columnId,
			Map<String, Object> sourceParameterInstance)
			throws InvalidInvocationException {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
