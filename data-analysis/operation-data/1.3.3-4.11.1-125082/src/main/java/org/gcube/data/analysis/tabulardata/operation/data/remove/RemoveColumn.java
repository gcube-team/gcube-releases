package org.gcube.data.analysis.tabulardata.operation.data.remove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.type.IdColumnType;
import org.gcube.data.analysis.tabulardata.model.metadata.table.GenericMapMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

import com.google.common.collect.Lists;

public class RemoveColumn extends DataWorker {
	
	private CubeManager cubeManager;
	protected static final String METADATA_AFTER_KEY ="after";
	protected static final String METADATA_BEFORE_KEY ="before";
	private Table targetTable;
	private Table diffTable;
	
	private Column targetColumn;

	public RemoveColumn(CubeManager cubeManager, OperationInvocation invocation) {
		super(invocation);
		this.cubeManager = cubeManager;
		this.targetTable = cubeManager.getTable(invocation.getTargetTableId());
		this.targetColumn = targetTable.getColumnById(invocation.getTargetColumnId());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected WorkerResult execute() throws WorkerException {
		updateProgress(0.1f,"Removing column..");
		Table resultTable = cubeManager.createTable(targetTable.getTableType())
				.like(targetTable, true, Lists.newArrayList(targetColumn)).create();
		
		List<Column> columns = new ArrayList<Column>();
		
		//preparing diffTable
		for (Column column: targetTable.getColumnsExceptTypes(IdColumnType.class)){
			if (column!=targetColumn)
				columns.add(column);
		}
		List<Column> allColumnExceptId = targetTable.getColumnsExceptTypes(IdColumnType.class);
		int targetColumnIndex = allColumnExceptId.indexOf(targetColumn);
		Map<String, String> genericMap = new HashMap<String, String>(2);
		if (targetColumnIndex>0)
			genericMap.put(METADATA_AFTER_KEY, allColumnExceptId.get(targetColumnIndex-1).getLocalId().getValue());
		if (targetColumnIndex<allColumnExceptId.size()-1)
			genericMap.put(METADATA_BEFORE_KEY, allColumnExceptId.get(targetColumnIndex+1).getLocalId().getValue());
		GenericMapMetadata mapMetadata = new GenericMapMetadata(genericMap);
				
		diffTable = cubeManager.createTable(targetTable.getTableType())
				.like(targetTable, true, columns).create();
		diffTable = cubeManager.modifyTableMeta(diffTable.getId()).setTableMetadata(mapMetadata).create();
		updateProgress(0.9f,"Finalizing");
		return new ImmutableWorkerResult(resultTable, diffTable);
	}

}
