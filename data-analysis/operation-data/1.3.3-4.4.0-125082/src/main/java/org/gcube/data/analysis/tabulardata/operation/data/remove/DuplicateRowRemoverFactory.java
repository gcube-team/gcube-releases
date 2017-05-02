package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.validation.DuplicateRowValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class DuplicateRowRemoverFactory extends TableTransformationWorkerFactory {
	
	private static final OperationId OPERATION_ID = new OperationId(3007);
	
	private CubeManager cubeManager;
	
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private DuplicateRowValidatorFactory validatorFactory;
	
	private static List<Parameter> params=new ArrayList<Parameter>();
	static{
		params.add(DuplicateRowValidatorFactory.KEY);
		params.add(DuplicateRowValidatorFactory.INVALIDATE_MODE);
	}
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new DuplicateRowRemover(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory, validatorFactory);
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



	@Inject
	public DuplicateRowRemoverFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			DuplicateRowValidatorFactory validatorFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.validatorFactory = validatorFactory;
	}

	@Override
	protected String getOperationName() {
		return "Remove duplicate tuples";
	}

	@Override
	protected String getOperationDescription() {
		return "Remove tuples that presents the same data";
	}

	@Override
	protected List<Parameter> getParameters() {
		return params;
	}

	@Override
	protected OperationId getOperationId() {		
		return OPERATION_ID;
	}
	
}
