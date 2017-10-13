package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.ValidationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;
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

public class DuplicateRowValidator extends ValidationWorker {

	private static final Logger log = LoggerFactory.getLogger(DuplicateRowValidator.class);

	CubeManager cubeManager;

	DatabaseConnectionProvider connectionProvider;

	SQLExpressionEvaluatorFactory evaluatorFactory;

	Table targetTable;

	Column validationColumn;

	List<Column> toCheckColumns=null;

	private ValidationDescriptor validationdescriptor;
	
	public DuplicateRowValidator(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,SQLExpressionEvaluatorFactory evaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.evaluatorFactory=evaluatorFactory;
	}

	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException {
		retrieveTargetTable();
		updateProgress(0.2f,"Configuring validation");
		checkAborted();
		createNewTableWithValidationColumn();
		updateProgress(0.4f,"Validating rows");
		checkAborted();
		fillNewTableWithData();
		updateProgress(0.8f,"Evaluating result");
		checkAborted();
		return new ValidityResult(evaluateValidityAndUpdateTableMeta()==0, Collections.singletonList(validationdescriptor));
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

	private void fillNewTableWithData() throws WorkerException {
		try {
			SQLHelper.executeSQLBatchCommands(connectionProvider, createSetDuplicateASFalse());
		} catch (Exception e) {
			String msg = "Unable to perform SQL operation";
			log.error(msg,e);
			throw new WorkerException(msg);
		}
	}
	
	private String createSetDuplicateASFalse() {

		String columnNamesSnippet = getConcatenationSnippet(toCheckColumns);
		
		
		
		String invalidateModeValue = (String) getSourceInvocation().getParameterInstances().get(DuplicateRowValidatorFactory.INVALIDATE_MODE.getIdentifier());
		
		String valueToMaintain = "min(id)";
		if (invalidateModeValue !=null && invalidateModeValue.equalsIgnoreCase("Newer"))
			valueToMaintain = "max(id)";
		
		return String.format("WITH selid AS (SELECT array_agg(id) FROM (SELECT %4$s AS id FROM (SELECT DISTINCT id , %1$s AS md FROM %2$s ORDER BY id ) AS ext GROUP BY md ) as agg) " +
				"UPDATE %2$s as target set %3$s=true " +
				" WHERE target.id =ANY ((SELECT * FROM selid)::Integer[]);", columnNamesSnippet, targetTable.getName(), validationColumn.getName(), valueToMaintain);

	}



	private void createNewTableWithValidationColumn() {
		DataValidationMetadata dataValidationMetadata = createDataValidationMetadata(0);
		validationColumn = new ValidationColumnFactory().useDefaultValue(new TDBoolean(false)).create(new ImmutableLocalizedText("Unique Column Set"),dataValidationMetadata);
		targetTable=cubeManager.addValidations(targetTable.getId(),validationColumn);

		ValidationReferencesMetadata referencesMetadata=new ValidationReferencesMetadata(toCheckColumns.toArray(new Column[toCheckColumns.size()]));

		targetTable=cubeManager.modifyTableMeta(targetTable.getId()).setColumnMetadata(validationColumn.getLocalId(), referencesMetadata).create();

	}

	private DataValidationMetadata createDataValidationMetadata(int count) {
		String validationText = String.format("%s is unique",OperationHelper.getColumnLabelsSnippet(toCheckColumns));
		validationdescriptor = new ValidationDescriptor(count==0, validationText, 101);
		return new DataValidationMetadata(new Validation(validationText, count==0, 101),count);
	}



	@SuppressWarnings("unchecked")
	private void retrieveTargetTable() {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		if(getSourceInvocation().getParameterInstances().containsKey(DuplicateRowValidatorFactory.KEY.getIdentifier())){
			this.toCheckColumns=new ArrayList<Column>();
			Object colParam=getSourceInvocation().getParameterInstances().get(DuplicateRowValidatorFactory.KEY.getIdentifier());
			if(colParam instanceof Iterable<?>){
				for(ColumnReference col:(Iterable<ColumnReference>)colParam)
					this.toCheckColumns.add(targetTable.getColumnById(col.getColumnId()));
			}else{
				this.toCheckColumns.add(targetTable.getColumnById(((ColumnReference)colParam).getColumnId()));
			}			
		}else this.toCheckColumns=targetTable.getColumnsExceptTypes(IdColumnType.class, ValidationColumnType.class);	
	}
	
	private final static String getConcatenationSnippet(Collection<Column> columns){
		StringBuilder columnNamesSnippet = new StringBuilder();
		for (Column col : columns) {
			if (col.getDataType() instanceof GeometryType)
				columnNamesSnippet.append(" ST_AsText(").append(col.getName()).append(") || ");
			else
				columnNamesSnippet.append(col.getName()).append("|| '|' || ");
		}
		columnNamesSnippet.delete(columnNamesSnippet.length() - 3, columnNamesSnippet.length() - 1);
		return columnNamesSnippet.toString();
	}

}
