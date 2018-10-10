package org.gcube.data.analysis.tabulardata.operation.data.replace;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnTransformationWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.IntegerParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.TDTypeValueParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

@Singleton
public class ReplaceByIdFactory extends ColumnTransformationWorkerFactory{

	public static final OperationId OPERATION_ID = new OperationId(3102);
	public static final TDTypeValueParameter VALUE=new TDTypeValueParameter("value", "Value", "To set value", Cardinality.ONE);
	public static final IntegerParameter ID=new IntegerParameter("id","Id","Row id",Cardinality.ONE);
	
	private CubeManager cubeManager;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private DatabaseConnectionProvider connectionProvider;
	
	@Inject
	public ReplaceByIdFactory(CubeManager cubeManager,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			DatabaseConnectionProvider connectionProvider) {
		super();
		this.cubeManager = cubeManager;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.connectionProvider = connectionProvider;
	}
	
	@Override
	public DataWorker createWorker(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		TDTypeValue value=OperationHelper.getParameter(VALUE, invocation);
		DataType colType=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId()).getDataType();
		if(!value.getReturnedDataType().getClass().equals(colType.getClass())) throw new InvalidInvocationException(invocation,"Invalid value for specified column");
		return new ReplaceById(invocation, cubeManager, connectionProvider, sqlEvaluatorFactory);
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
		return "Replaces the value of the target column in the declared row.";
	}
	
	@Override
	protected String getOperationName() {		
		return "Replace By Id";
	}
	
	@Override
	protected List<Parameter> getParameters() {
		List<Parameter> parameters=new ArrayList<Parameter>();
		parameters.add(VALUE);
		parameters.add(ID);
		return parameters;
	}
	
	@Override
	protected OperationId getOperationId() {		
		return OPERATION_ID;
	}
}
