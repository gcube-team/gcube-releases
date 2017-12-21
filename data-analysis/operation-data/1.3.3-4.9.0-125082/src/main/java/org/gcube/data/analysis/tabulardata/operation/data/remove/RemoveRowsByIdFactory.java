package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.IntegerParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class RemoveRowsByIdFactory extends TableTransformationWorkerFactory {

	public static final IntegerParameter ID_PARAMETER = new IntegerParameter("rowId", "RowId",
			"Selected row(s) id", new Cardinality(1, Integer.MAX_VALUE));
	
	private static List<Parameter> parameters=new ArrayList<Parameter>();
	private static final OperationId OPERATION_ID = new OperationId(3202);
	
	static{
		parameters.add(ID_PARAMETER);
	}
		
	private CubeManager cubeManager;
	
	private DatabaseConnectionProvider connectionProvider;

	@Inject
	public RemoveRowsByIdFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
	}
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new RemoveRowsById(invocation, cubeManager, connectionProvider);
	}
	
	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return new RowRecoveryRollbackWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
	}

	@Override
	public boolean isRollbackable() {
		return true;
	}
	
	@Override
	protected String getOperationDescription() {
		return "Removes selected rows";
	}
	
	
	@Override
	protected String getOperationName() {
		return "Remove rows by Id";
	}
	
	@Override
	protected List<Parameter> getParameters() {
		return parameters; 
	}
	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}

}
