package org.gcube.data.analysis.tabulardata.operation.resource;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationHelper;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class TableImport extends DataWorker {

	private CubeManager cubeManager; 
	private Table importFrom;
	private Boolean useExisting;
	
	public TableImport(OperationInvocation invocation, CubeManager cubeManager) {
		super(invocation);
		this.cubeManager = cubeManager;
		retrieveParameters(invocation);
	}

	@Override
	protected WorkerResult execute() throws WorkerException {
		Table newtable;
		if (useExisting){
			newtable = importFrom;
		}else
			newtable = cubeManager.createTable(importFrom.getTableType()).like(importFrom, true).create();
		return new ImmutableWorkerResult(newtable);
	}

	private void retrieveParameters(OperationInvocation invocation){
		Boolean useExistingParam = (Boolean)invocation.getParameterInstances().get(TableImportFactory.useExistingTableParameter.getIdentifier());
		useExisting = useExistingParam==null?false:useExistingParam;
		
		TableId tableid = OperationHelper.getParameter(TableImportFactory.targetTableImportParameter,invocation);
		importFrom = this.cubeManager.getTable(tableid);
	}
	
}
