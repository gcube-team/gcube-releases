package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.expression.TableReferenceReplacer;
import org.gcube.data.analysis.tabulardata.expression.evaluator.EvaluatorException;
import org.gcube.data.analysis.tabulardata.expression.evaluator.description.DescriptionExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.factories.ValidationColumnFactory;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.metadata.column.DataValidationMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ValidationReferencesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.common.ImmutableLocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.Validation;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GlobalDataValidationReportMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
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

public class ValidateDataWithExpression extends ValidationWorker {

	private static final Logger log = LoggerFactory.getLogger(ValidateDataWithExpression.class);

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private Table targetTable;

	private Expression validationExpression;

	private String expressionDescription = null;

	private String validationTitle= null;

	private Column validationColumn;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	private DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory;

	private int validationCode = 100;

	private List<Column> evaluatedColumns=new ArrayList<>();

	private ValidationDescriptor validationDescriptors;
	
	public ValidateDataWithExpression(OperationInvocation invocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider, SQLExpressionEvaluatorFactory sqlEvaluatorFactory, DescriptionExpressionEvaluatorFactory descriptionEvaluatorFactory) {
		super(invocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;

		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.descriptionEvaluatorFactory = descriptionEvaluatorFactory;
	}

	@Override
	protected ValidityResult execute() throws WorkerException, OperationAbortedException{
		initiate();
		updateProgress(0.1f,"Creating validation structure");
		checkAborted();
		addValidationColumn();
		updateProgress(0.4f,"Filling validation column");
		checkAborted();
		fillValidationColumn();
		updateProgress(0.7f,"Finalizing validation");
		checkAborted();
		return new ValidityResult(evaluateValidityAndUpdateTableMeta()==0, Collections.singletonList(validationDescriptors));
	}


	private void initiate() throws WorkerException{
		try{
			OperationInvocation invocation=getSourceInvocation();
			targetTable = cubeManager.getTable(invocation.getTargetTableId());
			validationExpression = (Expression) invocation.getParameterInstances().get(
					ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier());

			// Getting info from columns in expression
			TableReferenceReplacer replacer=new TableReferenceReplacer(validationExpression);
			for(TableId tableId:replacer.getTableIds()){			
				if(tableId.equals(targetTable.getId())){
					//internal
					for(ColumnReference ref:replacer.getReferences(tableId)){
						Column col=targetTable.getColumnById(ref.getColumnId());
						evaluatedColumns.add(col);			
					}
				}
			}


			if (invocation.getParameterInstances().containsKey(ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier()))
				expressionDescription = (String) invocation.getParameterInstances().get(
						ValidateDataWithExpressionFactory.DESCRIPTION_PARAMETER.getIdentifier());
			else expressionDescription= descriptionEvaluatorFactory.getEvaluator(validationExpression).evaluate();

			if (invocation.getParameterInstances().containsKey(ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier()))
				try{
					validationCode = Integer.parseInt((String) invocation.getParameterInstances().get(
							ValidateDataWithExpressionFactory.VALIDATION_CODE_PARAMETER.getIdentifier()));
				}catch (NumberFormatException e) {
					log.warn("validation code not valid on ValidateExpresion parameter");
				}
			if (invocation.getParameterInstances().containsKey(ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier()))
				validationTitle = (String) invocation.getParameterInstances().get(
						ValidateDataWithExpressionFactory.VALIDATION_TITLE_PARAMETER.getIdentifier());
			else validationTitle= "Expression on "+OperationHelper.getColumnLabelsSnippet(evaluatedColumns);

		}catch(Exception e){
			throw new WorkerException("Unable to initiate, wrong parameters configuration",e);
		}
	}


	private int evaluateValidityAndUpdateTableMeta() throws WorkerException {

		try{
			int invalidCount=ValidationHelper.getErrorCount(connectionProvider, targetTable, validationColumn, sqlEvaluatorFactory);
			GlobalDataValidationReportMetadata globalMeta=ValidationHelper.createDataValidationReport(validationColumn);
			log.trace("adding column to validation "+validationColumn.getLocalId());
			validationDescriptors = new ValidationDescriptor(invalidCount==0, validationTitle,  expressionDescription, validationCode, validationColumn.getLocalId());
			DataValidationMetadata validationMeta = new DataValidationMetadata(new Validation(expressionDescription, invalidCount==0, validationCode),invalidCount);
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
			SQLHelper.executeSQLCommand(generateValidationSqlCommand(),connectionProvider);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}


	private String generateValidationSqlCommand() throws WorkerException {
		try {
			//Change table references in given expression
			//			log.debug("Passed expression is "+sqlEvaluatorFactory.getEvaluator(validationExpression).evaluate());
			//			Expression toUseExpression =new TableReferenceReplacer(validationExpression).replaceTableId(targetTable.getId(), tempTable.getId()).getExpression();
			validationExpression.validate();
			return String.format("UPDATE %s SET %s = true WHERE %s;", targetTable.getName(), validationColumn.getName(),
					sqlEvaluatorFactory.getEvaluator(validationExpression).evaluate());
		} catch (EvaluatorException e) {
			throw new WorkerException("Unable to evaluate epression", e);
		} catch (MalformedExpressionException e) {
			throw new WorkerException("Unable to evaluate epression", e);
		}
	}

	private void addValidationColumn() throws WorkerException {	

		DataValidationMetadata validationMeta = new DataValidationMetadata(new Validation(expressionDescription, true, validationCode),0);
		validationColumn = new ValidationColumnFactory().useDefaultValue(new TDBoolean(false)).create(new ImmutableLocalizedText(validationTitle),validationMeta);
		targetTable=cubeManager.addValidations(targetTable.getId(),validationColumn);	
		log.debug("Added validation column:\n" + targetTable);

		ValidationReferencesMetadata meta=new ValidationReferencesMetadata(evaluatedColumns.toArray(new Column[evaluatedColumns.size()]));

		targetTable=cubeManager.modifyTableMeta(targetTable.getId()).setColumnMetadata(validationColumn.getLocalId(), meta).create();
	}

}
