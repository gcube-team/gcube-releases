package org.gcube.data.analysis.tabulardata.operation.data.replace;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.ExpressionParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
@Singleton
public class ReplaceByExpressionFactory extends ColumnTransformationWorkerFactory{

	private static final OperationId OPERATION_ID = new OperationId(3101);
	
	public static final ExpressionParameter CONDITION_PARAMETER = new ExpressionParameter("condition", "Condition",
			"Boolean condition that identifies to modify rows", Cardinality.OPTIONAL);
	
	public static final ExpressionParameter VALUE_PARAMETER = new ExpressionParameter("value", "Value",
			"Expression that returns the value to be set", Cardinality.ONE);

	
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory;
	
	
	@Inject
	public ReplaceByExpressionFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory) {
		super();
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.descriptionEvaluatorFactory=descriptionEvaluatorFactory;
	}
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		return new ReplaceByExpression(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory);
	}
	
	@Override
	public boolean isRollbackable() {
		return true;
	}

	@Override
	public RollbackWorker createRollbackWoker(Table diffTable,
			Table createdTable, OperationInvocation oldInvocation) {
		return new ReplaceRollbackWorker(diffTable, createdTable, oldInvocation, cubeManager, connectionProvider);
	}
	
	@Override
	protected String getOperationDescription() {		
		return "Updates the values of the selected column in rows matching the defined condition";
	}
	
	@Override
	protected String getOperationName() {		
		return "Replace By Expression";
	}
	
	@Override
	protected OperationId getOperationId() {		
		return OPERATION_ID;
	}
	
	@Override
	protected List<Parameter> getParameters() {
		List<Parameter> parameters=new ArrayList<Parameter>();
		parameters.add(CONDITION_PARAMETER);
		parameters.add(VALUE_PARAMETER);
		return parameters;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		Expression condition=null;
		try{
			condition=OperationHelper.getParameter(CONDITION_PARAMETER, invocation);
		}catch(Exception e){
			// condition not specified
		}
		Expression value=OperationHelper.getParameter(VALUE_PARAMETER, invocation);
		Column col=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		if(condition!=null)return String.format("Set %s = %s where %s",
				OperationHelper.retrieveColumnLabel(col),
				descriptionEvaluatorFactory.getEvaluator(value).evaluate(),
				descriptionEvaluatorFactory.getEvaluator(condition).evaluate());
		else return String.format("Set %s = %s",
				OperationHelper.retrieveColumnLabel(col),
				descriptionEvaluatorFactory.getEvaluator(value).evaluate());
	}
}
