package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.HashMap;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.expression.composite.comparable.Equals;
import org.gcube.data.analysis.tabulardata.expression.evaluator.sql.SQLExpressionEvaluatorFactory;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.ValidationColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.SQLHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDataWithExpression;
import org.gcube.data.analysis.tabulardata.operation.validation.ValidateDataWithExpressionFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerWrapper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterByExpression extends DataWorker {

	private static final Logger log = LoggerFactory.getLogger(ValidateDataWithExpression.class);

	private CubeManager cubeManager;

	private DatabaseConnectionProvider connectionProvider;

	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;

	private ValidateDataWithExpressionFactory validateDataWithExpressionFactory;

	public FilterByExpression(OperationInvocation sourceInvocation,
			CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			ValidateDataWithExpressionFactory validateDataWithExpressionFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory = sqlEvaluatorFactory;
		this.validateDataWithExpressionFactory = validateDataWithExpressionFactory;
	}



	private Table targetTable;
	private Table evaluatedTable;
	private Table filteredTable;
	private Table diffTable;
	private Column validationColumn;
	private Expression filterCondition;
	private Expression deleteCondition;

	@SuppressWarnings("unchecked")
	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		retrieveParameters();
		updateProgress(0.1f,"Validating to remove rows");
		checkAborted();
		executeValidation();
		checkAborted();
		updateProgress(0.3f,"Gathering to remove rows");
		filteredTable=cubeManager.createTable(evaluatedTable.getTableType()).like(evaluatedTable, true).create();
		diffTable = cubeManager.createTable(evaluatedTable.getTableType()).like(evaluatedTable, false, evaluatedTable.getColumnsByType(ValidationColumnType.class)).create();
		updateProgress(0.5f,"Filtering rows");
		checkAborted();
		filterValidatedTable();
		updateProgress(0.9f,"Finalizing");
		cubeManager.removeValidations(targetTable.getId());
		return new ImmutableWorkerResult(cubeManager.removeValidations(filteredTable.getId()), diffTable);
	}

	private void retrieveParameters(){		
		filterCondition=(Expression) getSourceInvocation().getParameterInstances().get(FilterByExpressionFactory.EXPRESSION_PARAMETER.getIdentifier());
		targetTable=cubeManager.getTable(getSourceInvocation().getTargetTableId());		
	}

	private void executeValidation() throws WorkerException, OperationAbortedException{
		WorkerWrapper<ValidationWorker, ValidityResult> wrapper=createWorkerWrapper(validateDataWithExpressionFactory);
		HashMap<String,Object> map=new HashMap<String,Object>();		
		map.put(ValidateDataWithExpressionFactory.EXPRESSION_PARAMETER.getIdentifier(), filterCondition);
		try{
			WorkerStatus status=wrapper.execute(targetTable.getId(), null, map);
			if(!status.equals(WorkerStatus.SUCCEDED))
				throw new WorkerException("Wrapped step has failed, see previous log");	
			evaluatedTable = cubeManager.getTable(targetTable.getId());
			validationColumn = evaluatedTable.getColumnById(wrapper.getResult().getValidationDescriptors().get(0).getValidationColumn());
			log.debug("Evaluated table : "+evaluatedTable);
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to check condition",e);
		}
	}

	@SuppressWarnings("unchecked")
	private void filterValidatedTable() throws WorkerException{
		log.debug("Creating condition on validation column for table "+filteredTable);
		//validationColumn = filteredTable.getColumnsByType(ValidationColumnType.class).get(0);
		deleteCondition=new Equals(filteredTable.getColumnReference(validationColumn),new TDBoolean(false));
		executeFilter(deleteCondition);
	}

	@SuppressWarnings("unchecked")
	private void executeFilter(Expression deleteCondition) throws WorkerException{
		try {
			StringBuilder columnsString = new StringBuilder("id ");
			for (Column column : filteredTable.getColumnsExceptTypes(ValidationColumnType.class, IdColumnType.class))
				columnsString.append(",").append(column.getName());

			String sqlCommand=String.format("WITH deleted AS (DELETE FROM %1$s WHERE %2$s RETURNING %3$s ) " +
					"INSERT INTO  %4$s (%3$s) SELECT %3$s FROM deleted ", 
					filteredTable.getName(),sqlEvaluatorFactory.getEvaluator(deleteCondition).evaluate()
					, columnsString.toString(), diffTable.getName());			

			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlCommand);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}


}
