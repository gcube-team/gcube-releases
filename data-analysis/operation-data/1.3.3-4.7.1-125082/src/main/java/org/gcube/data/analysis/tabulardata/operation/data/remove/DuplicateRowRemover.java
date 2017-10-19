package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.Map;

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
import org.gcube.data.analysis.tabulardata.operation.validation.DuplicateRowValidatorFactory;
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

public class DuplicateRowRemover extends DataWorker {
	
	private CubeManager cubeManager;
	private DatabaseConnectionProvider connectionProvider;
	private SQLExpressionEvaluatorFactory sqlEvaluatorFactory;
	private DuplicateRowValidatorFactory validatorFactory;
	
	private Table targetTable;
	
	private Table newTable;
	
	private Table diffTable;
	
	
	public DuplicateRowRemover(OperationInvocation sourceInvocation, CubeManager cubeManager,
			DatabaseConnectionProvider connectionProvider,
			SQLExpressionEvaluatorFactory sqlEvaluatorFactory,
			DuplicateRowValidatorFactory validatorFactory) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
		this.connectionProvider = connectionProvider;
		this.sqlEvaluatorFactory=sqlEvaluatorFactory;
		this.validatorFactory=validatorFactory;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected WorkerResult execute() throws WorkerException,OperationAbortedException {
		targetTable = cubeManager.getTable(getSourceInvocation().getTargetTableId());
		updateProgress(0.1f,"Checking for duplicates");
		executeValidation();
		updateProgress(0.3f,"Creating filtered table");		
		newTable=cubeManager.createTable(targetTable.getTableType()).like(targetTable, true).create();
		diffTable = cubeManager.createTable(targetTable.getTableType()).like(targetTable, false, targetTable.getColumnsByType(ValidationColumnType.class)).create();
		updateProgress(0.5f,"Removing duplicate lines");
		filterValidatedTable();
		updateProgress(0.9f,"Finalizing");		
		cubeManager.removeValidations(targetTable.getId());
		return new ImmutableWorkerResult(cubeManager.removeValidations(newTable.getId()), diffTable);
	}

	@SuppressWarnings("unchecked")
	private void filterValidatedTable() throws WorkerException{
		Column validationColumn = newTable.getColumnsByType(ValidationColumnType.class).get(0);
		Expression deleteCondition=new Equals(newTable.getColumnReference(validationColumn),new TDBoolean(false));
		executeFilter(deleteCondition);
	}
	
	@SuppressWarnings("unchecked")
	private void executeFilter(Expression condition) throws WorkerException{
		try {
			StringBuilder columnsString = new StringBuilder("id ");
			for (Column column : newTable.getColumnsExceptTypes(ValidationColumnType.class, IdColumnType.class))
				columnsString.append(",").append(column.getName());
			
			String sqlCommand=String.format("WITH deleted AS (DELETE FROM %1$s WHERE %2$s RETURNING %3$s ) " +
					"INSERT INTO  %4$s (%3$s) SELECT %3$s FROM deleted ", 
					newTable.getName(),sqlEvaluatorFactory.getEvaluator(condition).evaluate()
					, columnsString.toString(), diffTable.getName());			
			
			SQLHelper.executeSQLBatchCommands(connectionProvider, sqlCommand);
		} catch (Exception e) {
			throw new WorkerException("Error occurred while executing SQL command", e);
		}
	}

	
	private void executeValidation() throws WorkerException, OperationAbortedException{
		WorkerWrapper<ValidationWorker, ValidityResult> wrapper=createWorkerWrapper(validatorFactory);
		Map<String,Object> map=getSourceInvocation().getParameterInstances();
		try{
			WorkerStatus status=wrapper.execute(targetTable.getId(), null, map);
			if(!status.equals(WorkerStatus.SUCCEDED))
				throw new WorkerException("Wrapped step has failed, see previous log");	
			targetTable = cubeManager.getTable(targetTable.getId());
		}catch(InvalidInvocationException e){
			throw new WorkerException("Unable to check condition",e);
		}
	}
		
}
