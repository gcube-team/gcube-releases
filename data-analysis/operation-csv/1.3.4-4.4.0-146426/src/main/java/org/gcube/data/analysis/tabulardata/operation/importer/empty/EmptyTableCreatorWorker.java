package org.gcube.data.analysis.tabulardata.operation.importer.empty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.column.ColumnType;
import org.gcube.data.analysis.tabulardata.model.column.factories.BaseColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.DimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.factories.TimeDimensionColumnFactory;
import org.gcube.data.analysis.tabulardata.model.column.type.DimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.column.type.TimeDimensionColumnType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.relationship.ImmutableColumnRelationship;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.type.GenericTableType;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ImmutableWorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.DataWorker;

public class EmptyTableCreatorWorker extends DataWorker{

	private CubeManager cubeManager; 

	public EmptyTableCreatorWorker(OperationInvocation invocation, CubeManager cubeManager) {
		super(invocation);
		this.cubeManager = cubeManager;
	}

	private List<Column> columns; 
	
	@Override
	protected WorkerResult execute() throws WorkerException {
		this.updateProgress(0.1f, "Retrieving parameters");
		retriveColumns();
		this.updateProgress(0.5f, "Creating table");
		Table table = cubeManager.createTable(new GenericTableType()).addColumns(columns.toArray(new Column[columns.size()])).create();
		return new ImmutableWorkerResult(table);
	}

	@SuppressWarnings("unchecked")
	private void retriveColumns(){
		columns = new ArrayList<>();
		Map<String, Object> parameters = getSourceInvocation().getParameterInstances();
		List<Map<String, Object>> mappings = (List<Map<String, Object>>) parameters.get(EmptyTableCreatorFactory.COMPOSITE.getIdentifier());
		for (Map<String, Object> columnInstance : mappings){
			DataType dataType = (DataType)columnInstance.get(EmptyTableCreatorFactory.DATA_TYPE.getIdentifier());
			ColumnType columnType = (ColumnType) columnInstance.get(EmptyTableCreatorFactory.COLUMN_TYPE.getIdentifier());
			LocalizedText label = (LocalizedText) columnInstance.get(EmptyTableCreatorFactory.LABEL.getIdentifier());
			
			if (columnType instanceof DimensionColumnType){
				ColumnReference columnReference = (ColumnReference) columnInstance.get(EmptyTableCreatorFactory.RELATIONSHIP.getIdentifier());
				columns.add(((DimensionColumnFactory)DimensionColumnFactory.getFactory(columnType))
						.create(label, new ImmutableColumnRelationship(columnReference.getTableId(), columnReference.getColumnId())));
			}else if (columnType instanceof TimeDimensionColumnType){
				String periodType = (String) columnInstance.get(EmptyTableCreatorFactory.PERIOD_TYPE.getIdentifier());
				PeriodType period = PeriodType.valueOf(periodType);
				Column column = ((TimeDimensionColumnFactory)TimeDimensionColumnFactory.getFactory(columnType)).create(period);
				Table timeCodelist = cubeManager.getTimeTable(period);
				Column refColumn = timeCodelist.getColumnByName(period.getName()); 
				column.setRelationship(new ImmutableColumnRelationship(timeCodelist.getId(), refColumn.getLocalId()));
				columns.add(column);
			}
			else			
				columns.add(BaseColumnFactory.getFactory(columnType).create(label, dataType));
		}
	}
	
	
}
