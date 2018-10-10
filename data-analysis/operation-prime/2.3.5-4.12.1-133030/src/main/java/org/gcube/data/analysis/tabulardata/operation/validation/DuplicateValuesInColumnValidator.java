package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.Collections;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.factories.ValidationColumnFactory;
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
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuplicateValuesInColumnValidator extends ValidationWorker{

	private static final Logger log = LoggerFactory.getLogger(DuplicateValuesInColumnValidator.class);

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private SQLExpressionEvaluatorFactory evaluatorFactory;

	private Table targetTable;

	private Column targetColumn;

	private Column validationColumn;

	public DuplicateValuesInColumnValidator(
			OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,SQLExpressionEvaluatorFactory factory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory=factory;
	}

	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException {
		retrieveParameters();
		updateProgress(0.1f,"Configuring validationg");
		checkAborted();
		createNewTableWithValidationColumn();
		updateProgress(0.5f,"Validating rows");
		checkAborted();
		fillNewTableWithData();
		updateProgress(0.8f,"Evaluating result");
		checkAborted();
		int invalidCount = evaluateValidityAndUpdateTableMeta();
		return new ValidityResult(invalidCount==0, Collections.singletonList(createValidationColumn(invalidCount, validationColumn)));
	}

	private void retrieveParameters() {		
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		targetColumn = targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
	}


	private void createNewTableWithValidationColumn(){
		DataValidationMetadata dataValidationMetadata = createDataValidationMetadata(0);
		validationColumn = new ValidationColumnFactory().useDefaultValue(new TDBoolean(true)).create(new ImmutableLocalizedText("Unique "+OperationHelper.retrieveColumnLabel(targetColumn)),dataValidationMetadata);
		targetTable=cubeManager.addValidations(targetTable.getId(),validationColumn);
		ValidationReferencesMetadata refreferenceMeta=new ValidationReferencesMetadata(targetColumn);
		targetTable=cubeManager.modifyTableMeta(targetTable.getId()).setColumnMetadata(validationColumn.getLocalId(), refreferenceMeta).create();
	}

	private DataValidationMetadata createDataValidationMetadata(int count) {
		String validationDescription = "True when the value in column "+OperationHelper.retrieveColumnLabel(targetColumn)+" is not a duplicate, false otherwise";
		return new DataValidationMetadata(new Validation(validationDescription, count==0, 102 ),count);
	}

	private ValidationDescriptor createValidationColumn(int count, Column validationColumn){
		String validationDescription = "No values in column "+OperationHelper.retrieveColumnLabel(targetColumn)+" are duplicates";
		return new ValidationDescriptor(count==0, validationDescription, 102, validationColumn.getLocalId());
	}
	
	private void fillNewTableWithData() throws WorkerException {
		try {
			SQLHelper.executeSQLBatchCommands(connectionProvider,createSetFalseOnDuplicatesSQL());
		} catch (Exception e) {
			String msg = "Unable to perform SQL operation";
			log.error(msg,e);
			throw new WorkerException(msg);
		}
	}
	
	private String createSetFalseOnDuplicatesSQL(){
		return String.format("UPDATE %2$s AS target SET %3$s=false WHERE  " +
				" id NOT IN (SELECT distinct min(id) from " +
				"%2$s GROUP BY %1$s) ", targetColumn.getName(), targetTable.getName(), validationColumn.getName());
	}

	private int evaluateValidityAndUpdateTableMeta() throws WorkerException {
		try{
			int invalidCount=ValidationHelper.getErrorCount(connectionProvider, targetTable, validationColumn, evaluatorFactory);
			GlobalDataValidationReportMetadata globalMeta=ValidationHelper.createDataValidationReport(validationColumn);

			targetTable = cubeManager.modifyTableMeta(targetTable.getId())
					.setColumnMetadata(validationColumn.getLocalId(), 
							createDataValidationMetadata(invalidCount)).
							setTableMetadata(globalMeta).create();
			return invalidCount;
		}catch(Exception e){
			throw new WorkerException("Unable to evaluate global validation",e);
		}
	}
}
