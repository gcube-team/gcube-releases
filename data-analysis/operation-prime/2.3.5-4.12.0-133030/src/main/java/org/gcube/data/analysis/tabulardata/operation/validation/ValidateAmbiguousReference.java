package org.gcube.data.analysis.tabulardata.operation.validation;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.ValidationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GlobalDataValidationReportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
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

public class ValidateAmbiguousReference extends ValidationWorker {

	private static Logger logger = LoggerFactory.getLogger(ValidateAmbiguousReference.class);
	
	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private Table targetTable;

	private Column targetColumn;

	private Column validationColumn;

	private Table externalTable ;

	private Column externalColumn ;

	private Map<TDTypeValue, Long> mapping;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	private static Logger log = LoggerFactory.getLogger(ValidateAmbiguousReference.class);

	private static final String validationText = "Ambiguous values on external reference validation";
	
	private ValidationDescriptor validationDescriptor ;
	
	public ValidateAmbiguousReference(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider, SQLExpressionEvaluatorFactory sqlEvaluatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
	}

	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException {
		retrieveParameters();
		updateProgress(0.1f,"Creating validation structure");
		checkAborted();
		addValidationColumn();
		updateProgress(0.4f,"Filling validation column");
		checkAborted();
		fillValidationColumn();
		updateProgress(0.7f,"Finalizing validation");
		checkAborted();
		return new ValidityResult(evaluateValidityAndUpdateTableMeta()==0, Collections.singletonList(validationDescriptor));
	}

	@SuppressWarnings({ "unchecked" })
	private void retrieveParameters(){
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		targetColumn = targetTable.getColumnById(getSourceInvocation().getTargetColumnId());
		log.debug("targetColumn is "+targetColumn);
		mapping = (Map<TDTypeValue, Long>)getSourceInvocation().getParameterInstances().get(ValidateAmbiguousReferenceFactory.MAPPING_PARAMETER.getIdentifier());
		ColumnReference externalReference = (ColumnReference) getSourceInvocation().getParameterInstances().get(ValidateAmbiguousReferenceFactory.TARGET_COLUMN_PARAMETER.getIdentifier());
		externalTable = cubeManager.getTable(externalReference.getTableId());
		externalColumn = externalTable.getColumnById(externalReference.getColumnId());
	}

	private int evaluateValidityAndUpdateTableMeta() throws WorkerException {
		try{
			int invalidCount=ValidationHelper.getErrorCount(connectionProvider, targetTable, validationColumn, sqlEvaluatorFactory);
			GlobalDataValidationReportMetadata globalMeta=ValidationHelper.createDataValidationReport(validationColumn);
			
			validationDescriptor = new ValidationDescriptor(invalidCount==0, validationText, 103, validationColumn.getLocalId());
			DataValidationMetadata validationMeta = new DataValidationMetadata(new Validation(validationText, invalidCount==0, 103 ),invalidCount);
			targetTable = cubeManager.modifyTableMeta(targetTable.getId())
					.setColumnMetadata(validationColumn.getLocalId(), 
							validationMeta).
							setTableMetadata(globalMeta).create();
			return invalidCount;
		}catch(Exception e){
			throw new WorkerException("Unable to evaluate global validation",e);
		}
	}
	
	private void fillValidationColumn() throws WorkerException {
		try {
			SQLHelper.executeSQLCommand(generateValidationSqlCommand(), connectionProvider);
		} catch (SQLException e) {
			logger.error("error filling validation column",e.getNextException());
			throw new WorkerException("Error occurred while executing SQL command", e.getNextException());
		}
	}
	
	private String generateValidationSqlCommand() throws WorkerException {
		try{
			StringBuffer mappingFrom = new StringBuffer();
			String mappingWhere = "";
			if (mapping!=null && !mapping.isEmpty()){		
				mappingFrom.append("(VALUES");
				for (Entry<TDTypeValue,Long> mapEntry : mapping.entrySet())
					mappingFrom.append(" (").append(this.sqlEvaluatorFactory.getEvaluator(mapEntry.getKey()).evaluate())
					.append(",").append(mapEntry.getValue()).append("),"); 
				
				mappingFrom.deleteCharAt(mappingFrom.lastIndexOf(","));
				mappingFrom.append(") AS mapping (key, value) ");
				
				mappingWhere=String.format(" AND ( NOT EXISTS ( SELECT NULL FROM %1$s WHERE mapping.key = %2$s.%3$s AND ARRAY[mapping.value] <@ (reference.ids) ) ) ", mappingFrom, targetTable.getName(), targetColumn.getName()) ;
			}
			
			String targetColumnSQL = targetColumn.getName();
			String externalColumnSQL = externalColumn.getName();
			if (!targetColumn.getDataType().equals(externalColumn.getDataType())){
				targetColumnSQL = targetColumn.getName()+"::text";
				externalColumnSQL = externalColumn.getName()+"::text";
			}
			
			String query = String.format("UPDATE %1$s SET %2$s = false FROM (SELECT %3$s as value , array_agg(id) AS ids,  count(*) AS count FROM %4$s GROUP BY %3$s) AS reference WHERE " +
					" (reference.value = %5$s AND (count>1  %6$s )) ", targetTable.getName(), validationColumn.getName(), externalColumnSQL, 
					externalTable.getName(), targetColumnSQL, mappingWhere ) ;
			
			return query;
						
		}catch(Exception e){
			log.error("error updating validation column",e);
			throw new WorkerException("error updating validation column",e);
		}
	}

	private void addValidationColumn() throws WorkerException {	

		DataValidationMetadata validationMeta = new DataValidationMetadata(new Validation(validationText, true, 103),0);
		validationColumn = new ValidationColumnFactory().useDefaultValue(new TDBoolean(true)).create(new ImmutableLocalizedText("Ambiguous values"),validationMeta);
		targetTable=cubeManager.addValidations(targetTable.getId(),validationColumn);	
		log.debug("Added validation column:\n" + targetTable);
		ValidationReferencesMetadata meta=new ValidationReferencesMetadata(targetColumn);
		targetTable=cubeManager.modifyTableMeta(targetTable.getId()).setColumnMetadata(validationColumn.getLocalId(), meta).create();
	}

}
