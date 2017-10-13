package org.gcube.data.analysis.tabulardata.operation.validation;

import java.sql.SQLException;
import java.util.Collections;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.ValidationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GlobalDataValidationReportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.ValidationHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.time.PeriodTypeHelper;
import org.gcube.data.analysis.tabulardata.operation.time.PeriodTypeHelperProvider;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;

public class TimeDimensionColumnValidator extends ValidationWorker {

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private Table targetTable;

	private Column targetColumn;

	private Column validationColumn;

	private PeriodType periodType;
	
	private ValueFormat timeFormat;

	private PeriodTypeHelperProvider periodTypeHelperProvider;

	private SQLExpressionEvaluatorFactory evaluatorFactory;

	private ValidationDescriptor validationDescriptor;
	
	public TimeDimensionColumnValidator(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,PeriodTypeHelperProvider periodTypeHelperProvider,SQLExpressionEvaluatorFactory factory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.periodTypeHelperProvider = periodTypeHelperProvider;
		this.evaluatorFactory=factory;
	}

	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException {
		retrieveParameters();
		updateProgress(0.1f,"Configuring validation");
		checkAborted();
		createValidationTable();
		updateProgress(0.4f,"Validating rows");
		checkAborted();
		fillValidationTableWithValidationData();
		updateProgress(0.8f,"Evaluating result");
		checkAborted();
		return new ValidityResult(evaluateValidationResultAndUpdateTableMeta()==0, Collections.singletonList(validationDescriptor));
	}

	private int evaluateValidationResultAndUpdateTableMeta() throws WorkerException {
		try{
			int invalidCount=ValidationHelper.getErrorCount(connectionProvider, targetTable, validationColumn, evaluatorFactory);
			GlobalDataValidationReportMetadata globalMeta=ValidationHelper.createDataValidationReport(validationColumn);
			ValidationReferencesMetadata referencesMeta=new ValidationReferencesMetadata(targetColumn);

			targetTable = cubeManager.modifyTableMeta(targetTable.getId())
					.setColumnMetadata(validationColumn.getLocalId(), 
							createDataValidationMetadata(invalidCount)).
							setColumnMetadata(validationColumn.getLocalId(),referencesMeta).
							setTableMetadata(globalMeta).create();
			return invalidCount;	
		}catch(Exception e){
			throw new WorkerException("Unable to evaluate global validation",e);
		}

	}

	private void fillValidationTableWithValidationData() throws WorkerException {
		PeriodTypeHelper helper = periodTypeHelperProvider.getHelper(periodType);
		try {
			SQLHelper.executeSQLCommand(helper.getFillValidationColumnSQL(targetTable,
					validationColumn.getName(), targetColumn, timeFormat, evaluatorFactory), connectionProvider);
		}catch(MalformedExpressionException mee){
			throw new WorkerException("error converting type",mee);
		} catch (SQLException e) {
			throw new WorkerException("Unable to evaluate validation condition on DB", e);
		}
	}

	private void createValidationTable() throws OperationAbortedException {
		validationColumn = createValidationColumn(0);
		checkAborted();
		targetTable=cubeManager.addValidations(targetTable.getId(),validationColumn);
	}

	private Column createValidationColumn(int count) {
		LocalizedText name = new ImmutableLocalizedText(String.format("Is valid %s format?", periodType.getName()));
		DataValidationMetadata dataValidationMetadata = createDataValidationMetadata(count);
		return new ValidationColumnFactory().useDefaultValue(new TDBoolean(false)).create(name, dataValidationMetadata);
	}

	private DataValidationMetadata createDataValidationMetadata(int count) {
		String descriptionText = String.format("Tells if %s is of valid %s format",
				OperationHelper.retrieveColumnLabel(targetColumn), periodType.getName());
		validationDescriptor = new ValidationDescriptor(count==0, descriptionText, 107, targetColumn.getLocalId());
		return new DataValidationMetadata(new Validation(descriptionText, count==0, 107), count);
	}

	private void retrieveParameters() {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		targetColumn = targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
		periodType = PeriodType.fromName(OperationHelper.getParameter(TimeDimensionColumnValidatorFactory.PERIOD_FORMAT_PARAMETER,
				getSourceInvocation()));
		String timeFormatId = (String)getSourceInvocation().getParameterInstances().get(TimeDimensionColumnValidatorFactory.FORMAT_ID_PARAMETER.getIdentifier());
		if (timeFormatId!=null)
			timeFormat = periodType.getTimeFormatById(timeFormatId);
		else timeFormat = periodType.getAcceptedFormats().get(0);
	}

}
