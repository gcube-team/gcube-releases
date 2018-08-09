package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class RemoveColumnFactory extends ColumnTransformationWorkerFactory {

	private static final OperationId OPERATION_ID = new OperationId(1004);

	@Inject
	private CubeManager cubeManager;
	
	@Inject
	private DatabaseConnectionProvider connectionProvider;
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new RemoveColumn(cubeManager, invocation);
	}
	
	@Override
	public boolean isRollbackable() {
		return true;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return new RemoveColumnRollbackWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
	}

	@Override
	protected String getOperationName() {
		return "Remove column";
	}

	@Override
	protected String getOperationDescription() {
		return "Remove column from table";
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

	@Override
	protected List<Parameter> getParameters() {
		return new ArrayList<Parameter>();
	}

	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		Column col=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());		
		return String.format("Remove column %s",OperationHelper.retrieveColumnLabel(col));
	}
}
