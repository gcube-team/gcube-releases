package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ColumnValidatorFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.parameters.Cardinality;
import org.gcube.data.analysis.tabulardata.operation.parameters.Parameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.MultivaluedStringParameter;
import org.gcube.data.analysis.tabulardata.operation.parameters.leaves.SimpleStringParameter;
import org.gcube.data.analysis.tabulardata.operation.time.PeriodTypeHelperProvider;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

@Singleton
public class TimeDimensionColumnValidatorFactory extends ColumnValidatorFactory {

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;
	private PeriodTypeHelperProvider periodTypeHelperProvider;

	private SQLExpressionEvaluatorFactory evaluatorFactory;
	
	public final static MultivaluedStringParameter PERIOD_FORMAT_PARAMETER;
	
	public final static SimpleStringParameter FORMAT_ID_PARAMETER;

	private static final OperationId OPERATION_ID = new OperationId(5005);
	
	
	private final static List<Parameter> parameters;

	static {
		List<String> admittedValues = new ArrayList<String>();
		for (PeriodType periodType : PeriodType.values()) {
			admittedValues.add(periodType.getName());
		}
		PERIOD_FORMAT_PARAMETER = new MultivaluedStringParameter("periodFormat", "Period Format",
				"Period representation format", Cardinality.ONE, admittedValues);
		parameters = new ArrayList<Parameter>();
		parameters.add(PERIOD_FORMAT_PARAMETER);
		
		FORMAT_ID_PARAMETER = new SimpleStringParameter("inputFormatId", "Input Format Id", 
				"The id of one of the TimeFormat compatibles with the selected Period type", Cardinality.OPTIONAL);
		
		parameters.add(FORMAT_ID_PARAMETER);
	}

	@Inject
	public TimeDimensionColumnValidatorFactory(CubeManager cubeManager, DatabaseConnectionProvider connectionProvider,
			PeriodTypeHelperProvider periodTypeHelperProvider,SQLExpressionEvaluatorFactory factory) {
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.periodTypeHelperProvider = periodTypeHelperProvider;
		this.evaluatorFactory=factory;
	}

	@Override
	public ValidationWorker createWorker(OperationInvocation invocation) throws InvalidInvocationException {
		performBaseChecks(invocation,cubeManager);
		checkFormat(invocation);
		checkColumnEligibility(invocation);
		return new TimeDimensionColumnValidator(invocation, cubeManager, connectionProvider, periodTypeHelperProvider,evaluatorFactory);
	}

	private void checkColumnEligibility(OperationInvocation invocation) throws InvalidInvocationException {
		Table targetTable = cubeManager.getTable(invocation.getTargetTableId());
		Column targetColumn = targetTable.getColumnById(invocation.getTargetColumnId());
		if (!targetColumn.getDataType().getClass().equals(TextType.class))
			throw new InvalidInvocationException(invocation, "Target column is not a text column");
	}
	
	private void checkFormat(OperationInvocation invocation) throws InvalidInvocationException {
		String periodFormat=OperationHelper.getParameter(PERIOD_FORMAT_PARAMETER, invocation);
		String timeFormatId = (String)invocation.getParameterInstances().get(FORMAT_ID_PARAMETER.getIdentifier());
		if (timeFormatId!=null && PeriodType.fromName(periodFormat).getTimeFormatById(timeFormatId) ==null)
			throw new InvalidInvocationException(invocation, "the provided format id "+timeFormatId+" is invalid");

	}

	@Override
	protected String getOperationName() {
		return "Period format check";
	}

	@Override
	protected String getOperationDescription() {
		return "Check if a column data represents a valid period format";
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
		checkColumnEligibility(invocation);
		Column targetColumn=cubeManager.getTable(invocation.getTargetTableId()).getColumnById(invocation.getTargetColumnId());
		String periodFormat=OperationHelper.getParameter(PERIOD_FORMAT_PARAMETER, invocation);
		String timeFormatId = (String)invocation.getParameterInstances().get(FORMAT_ID_PARAMETER.getIdentifier());
		ValueFormat tf;
		if (timeFormatId!=null){
			tf = PeriodType.fromName(periodFormat).getTimeFormatById(timeFormatId);
			if (tf ==null) throw new InvalidInvocationException(invocation, "the provided format id "+timeFormatId+" is invalid");
		} else tf = PeriodType.fromName(periodFormat).getAcceptedFormats().get(0);
		return String.format("Check if %s is a valid time dimension [format %s (eg %s) ]", OperationHelper.retrieveColumnLabel(targetColumn),periodFormat, tf.getExample());
	}
}
