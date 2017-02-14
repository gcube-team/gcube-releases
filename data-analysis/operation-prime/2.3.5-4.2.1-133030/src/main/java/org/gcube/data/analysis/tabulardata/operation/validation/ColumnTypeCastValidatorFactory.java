package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.DataTypeParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class ColumnTypeCastValidatorFactory extends ColumnValidatorFactory {
	
	private final static List<Parameter> parameters = new ArrayList<Parameter>();
	
	private static final OperationId OPERATION_ID = new OperationId(5002);
	
	public static final DataTypeParameter TARGET_TYPE_PARAMETER = new DataTypeParameter("targetDataType", "Target type", "The data type on which the cast should be checked", Cardinality.ONE);
	public static final SimpleStringParameter FORMAT_ID_PARAMETER = new SimpleStringParameter("inputFormatId", "Input Format Id", 
			"The id of a ValueFormat representing the format of the Strign in the column to verify (only for numeric types)", Cardinality.OPTIONAL);
	
	static{
		parameters.add(TARGET_TYPE_PARAMETER);
		parameters.add(FORMAT_ID_PARAMETER);
	}

	CubeManager cubeManager;
	
	DatabaseConnectionProvider connectionProvider;

	SQLExpressionEvaluatorFactory evalutorFactory;
	
	@Inject
	public ColumnTypeCastValidatorFactory(CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory evalutorFactory) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evalutorFactory=evalutorFactory;
	}

	@Override
	public ValidationWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkFormat(invocation);
		return new ColumnTypeCastValidator(invocation, cubeManager, connectionProvider,evalutorFactory);
		
	}
	
	private void checkFormat(OperationInvocation invocation) throws InvalidInvocationException {
		DataType dataType=OperationHelper.getParameter(TARGET_TYPE_PARAMETER, invocation);
		String formatId = (String)invocation.getParameterInstances().get(FORMAT_ID_PARAMETER.getIdentifier());
		if (formatId!=null && DataTypeFormats.getFormatPerId(dataType.getClass(), formatId) ==null)
			throw new InvalidInvocationException(invocation, "the provided format id "+formatId+" is invalid for dataType "+dataType.getClass());
	}

	@Override
	protected String getOperationName() {
		return "Column type cast check";
	}

	@Override
	protected String getOperationDescription() {
		return "Check if a column type can be casted to another one";
	}

	@Override
	protected List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	protected OperationId getOperationId() {
		return OPERATION_ID;
	}
	
	@Override
	public String describeInvocation(OperationInvocation invocation)
			throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		Column targetCol=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		DataType dataType=OperationHelper.getParameter(TARGET_TYPE_PARAMETER, invocation);
		String formatId = (String)invocation.getParameterInstances().get(FORMAT_ID_PARAMETER.getIdentifier());
		ValueFormat format = null;
		if (formatId!=null)
			format = DataTypeFormats.getFormatPerId(dataType.getClass(), formatId);
		else format = DataTypeFormats.getFormatsPerDataType(dataType.getClass()).get(0);
		return String.format("Check if %s can be converted to %s using specific format (eg %s)",OperationHelper.retrieveColumnLabel(targetCol),dataType.getName(), format.getExample());
	}
}
