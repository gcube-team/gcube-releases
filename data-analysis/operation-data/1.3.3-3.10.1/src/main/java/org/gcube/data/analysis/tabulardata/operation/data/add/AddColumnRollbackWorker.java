package org.gcube.data.analysis.tabulardata.operation.data.add;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.TableCreationException;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;

public class AddColumnRollbackWorker extends RollbackWorker {

	private CubeManager cubeManager;	

	public AddColumnRollbackWorker(Table diffTable, Table resultTable, OperationInvocation oldInvocation, CubeManager cm) {
		super(diffTable, resultTable, oldInvocation);
		this.cubeManager = cm;	
	}

	@SuppressWarnings("unchecked")
	@Override
	protected WorkerResult execute() throws WorkerException {
		Table tableToReturn;
		updateProgress(0.1f,"Preparing table");
		try{
			updateProgress(0.6f,"Removing the added column");
			tableToReturn = cubeManager.createTable(getResultTable().getTableType())
					.like(getResultTable(), true, getDifftablTable().getColumnsExceptTypes(IdColumnType.class)).create();
		}catch(TableCreationException tce){
			throw new WorkerException("error creating return table",tce);
		}
		return new ImmutableWorkerResult(tableToReturn);
	}


}
