package org.gcube.data.analysis.tabulardata.operation.validation;

import java.sql.SQLException;
import java.util.Collections;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.DataTypeFormats;
import org.gcube.data.analysis.tabulardata.model.ValueFormat;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.ValidationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GlobalDataValidationReportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.ValidationHelper;
import org.gcube.data.analysis.tabulardata.operation.datatype.TypeTransitionSQLHandler;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnTypeCastValidator extends ValidationWorker {

	private static final Logger log = LoggerFactory.getLogger(ColumnTypeCastValidator.class);

	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory evaluatorFactory;

	private Table targetTable;

	private Column targetColumn;

	private DataType targetType;

	private Column validationColumn;
	
	private ValueFormat format;
	
	public ColumnTypeCastValidator(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,SQLExpressionEvaluatorFactory sqlFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory=sqlFactory;
	}

	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException {
		retrieveParameters();
		updateProgress(0.1f,"Configuring validation");
		checkAborted();
		addValidationColumnToTable();
		updateProgress(0.4f,"Validating rows");
		checkAborted();
		fillNewTableWithData();
		updateProgress(0.8f,"Evaluating result");
		checkAborted();
		int invalidCount = createUpdatedTableMeta();
		return new ValidityResult(invalidCount==0, Collections.singletonList(createValidationDescriptor(invalidCount)));
	}

	private void retrieveParameters() {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		targetColumn = targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
		targetType = OperationHelper.getParameter(ColumnTypeCastValidatorFactory.TARGET_TYPE_PARAMETER,
				getSourceInvocation());
		
		String formatId = (String)getSourceInvocation().getParameterInstances().get(ColumnTypeCastValidatorFactory.FORMAT_ID_PARAMETER.getIdentifier());
		if (formatId!=null)
			format = DataTypeFormats.getFormatPerId(targetType.getClass(), formatId);
		else format = DataTypeFormats.getFormatsPerDataType(targetType.getClass()).get(0);
	}

	private void addValidationColumnToTable() {
		createValidationColumn();
		targetTable=cubeManager.addValidations(targetTable.getId(),validationColumn);
		ValidationReferencesMetadata referenceMeta=new ValidationReferencesMetadata(targetColumn);
		targetTable=cubeManager.modifyTableMeta(targetTable.getId()).setColumnMetadata(validationColumn.getLocalId(), referenceMeta).create();

	}

	private void createValidationColumn() {
		String columnLabel = OperationHelper.retrieveColumnLabel(targetColumn);
		DataValidationMetadata validationMeta = createDataValidationMetadata(0);
		validationColumn = new ValidationColumnFactory().useDefaultValue(new TDBoolean(true))
				.create(new ImmutableLocalizedText(String.format("Is %s a valid %s?", columnLabel, targetType.getName())),validationMeta);
	}

	private DataValidationMetadata createDataValidationMetadata(int count) {
		String columnLabel = OperationHelper.retrieveColumnLabel(targetColumn);
		String validationText = String.format("Tells whether %s can be casted to a %s value", columnLabel, targetType.getName());
		DataValidationMetadata validationMeta = new DataValidationMetadata(new Validation(validationText, count==0, 106), count);
		return validationMeta;
	}


	private void fillNewTableWithData() throws WorkerException {
		String sqlCommand = generateFillTableSQL();
		try {
			SQLHelper.executeSQLCommand(sqlCommand, connectionProvider);
		} catch (SQLException e) {
			throw new WorkerException("Unable to evaluate validation result", e);
		}
	}

	private String generateFillTableSQL() {
		TypeTransitionSQLHandler typeTransitionHandler = TypeTransitionSQLHandler.getHandler(
				targetColumn.getDataType(), targetType, evaluatorFactory);
		log.debug("Using Type transition handler: " + typeTransitionHandler.getClass().getSimpleName());
		return String.format("UPDATE %s SET %s = false WHERE %s IS NULL OR %s", 
				targetTable.getName(), validationColumn.getName(), targetColumn.getName(), typeTransitionHandler.getConditionForInvalidEntry(targetColumn, format));
	}

	private int createUpdatedTableMeta() throws WorkerException {
		try{
			int invalidCount=ValidationHelper.getErrorCount(connectionProvider, targetTable, validationColumn, evaluatorFactory);
			GlobalDataValidationReportMetadata globalMeta=ValidationHelper.createDataValidationReport(validationColumn);

			DataValidationMetadata dataValidationMetadata = createDataValidationMetadata(invalidCount);
			
			targetTable = cubeManager.modifyTableMeta(targetTable.getId())
					.setColumnMetadata(validationColumn.getLocalId(), dataValidationMetadata ).
							setTableMetadata(globalMeta).create();
			
			return invalidCount;
		}catch(Exception e){
			throw new WorkerException("Unable to evaluate global validation",e);
		}
	}

	private ValidationDescriptor createValidationDescriptor(int count){
		String columnLabel = OperationHelper.retrieveColumnLabel(targetColumn);
		String validationText = String.format("Tells whether %s can be casted to a %s value", columnLabel, targetType.getName());
		return new ValidationDescriptor(count==0, validationText, 106, validationColumn.getLocalId());
	}
}
