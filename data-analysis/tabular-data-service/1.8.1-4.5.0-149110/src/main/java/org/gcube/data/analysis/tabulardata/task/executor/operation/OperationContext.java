package org.gcube.data.analysis.tabulardata.task.executor.operation;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskStep;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;

public class OperationContext {
			
	private TableId currentTable; 
		
	private TableId referredTable;
	
	private TaskStep taskStep;
	
	private CubeManager cubeManager;
	
	private PreconditionResult preconditionResult = new PreconditionResult(true);
	
	private WorkerFactory<?> workerFactory;
	
	//private List<ColumnLocalId> createdValidationColumns;
	
	public OperationContext(TableId currentTable, TableId referredTable,
			WorkerFactory<?> workerFactory, TaskStep taskStep, CubeManager cubeManager) {
		super();
		this.currentTable = currentTable;
		this.referredTable = referredTable;
		this.workerFactory = workerFactory;
		this.taskStep = taskStep;
		this.cubeManager = cubeManager;
	}
	
	
	public TableId getCurrentTableId() {
		return currentTable;
	}

	public TableId getReferredTableId() {
		return referredTable;
	}
/*
	public void setCreatedValidationColumns(List<ColumnLocalId> validationColumns){
		this.createdValidationColumns = validationColumns;
	}
		
	public List<ColumnLocalId> getCreatedValidationColumns() {
		return createdValidationColumns;
	}
*/

	public void setPreconditionResult(PreconditionResult result) {
		this.preconditionResult = result; 		
	}

	public PreconditionResult getPreconditionResult() {
		return preconditionResult;
	}

	public WorkerFactory<?> getWorkerFactory() {
		return workerFactory;
	}


	public TaskStep getTaskStep() {
		return taskStep;
	}
	
	public Table getCurrentTable(){
		if (this.currentTable==null) return null;
		return this.cubeManager.getTable(this.currentTable);
	}
}
