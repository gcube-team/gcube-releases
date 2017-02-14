package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.TableTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDataWithExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class FilterByExpressionFactory extends TableTransformationWorkerFactory{

	private static final OperationId OPERATION_ID = new OperationId(3201);
	
	public static final ExpressionParameter EXPRESSION_PARAMETER = new ExpressionParameter("expression", "Expression",
			"Filter condition", Cardinality.ONE);

	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private ValidateDataWithExpressionFactory validateDataWithExpressionFactory;
	private DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory;
	
	@Inject
	public FilterByExpressionFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory,
			ValidateDataWithExpressionFactory validateDataWithExpressionFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.validateDataWithExpressionFactory = validateDataWithExpressionFactory;
		this.descriptionEvaluatorFactory=descriptionEvaluatorFactory;
	}


	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {		
		performBaseChecks(invocation,cubeManager);
		return new FilterByExpression(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory,  validateDataWithExpressionFactory);
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
		return "Delete rows that doesn't match the given expression from the target table";
	}
	
	@Override
	protected String getOperationName() {		
		return "Filter by Expression";
	}
	
	@Override
	protected List<Parameter> getParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(EXPRESSION_PARAMETER);
		return parameters;
	}
	
	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public String describeInvocation(OperationInvocation toDescribeInvocation)
			throws InvalidInvocationException {
		performBaseChecks(toDescribeInvocation,cubeManager);
		Expression expr=OperationHelper.getParameter(EXPRESSION_PARAMETER, toDescribeInvocation);
		return String.format("Remove rows not satisfying condition %s.",descriptionEvaluatorFactory.getEvaluator(expr).evaluate());
	}
}
