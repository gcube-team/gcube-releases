package org.gcube.data.analysis.tabulardata.operation.table.metadata;

import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.metadata.column.ViewColumnMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeColumnPositionWorker extends MetadataWorker{

	private final static Logger log = LoggerFactory.getLogger(ChangeColumnPositionWorker.class);

	private CubeManager cubeManager;

	public ChangeColumnPositionWorker(CubeManager cubeManager,
			OperationInvocation invocation) {
		super(invocation);
		this.cubeManager = cubeManager; 
	}

	@Override
	protected EmptyType execute() throws WorkerException {

		List<ColumnReference> order=(List<ColumnReference>) getSourceInvocation().getParameterInstances().get(ChangeColumnPositionFactory.COLUMN_ORDER.getIdentifier());
		Table table=cubeManager.getTable(getSourceInvocation().getTargetTableId());
		Table viewTable=null;
		List<Column> viewCols=null;
		if(table.contains(DatasetViewTableMetadata.class)){
			viewTable=cubeManager.getTable(table.getMetadata(DatasetViewTableMetadata.class).getTargetDatasetViewTableId());
			viewCols=viewTable.getColumns();
		}

		int offset=1; // offset for actual position in viewtable

		for (int i=0;i<order.size();i++){
			Column toMove=table.getColumnById(order.get(i).getColumnId());
			//move column
			table=cubeManager.exchangeColumnPosition(table.getId(), toMove.getLocalId(), i+1); // first column must be code
			if(viewTable!=null){
				viewTable=cubeManager.exchangeColumnPosition(viewTable.getId(), toMove.getLocalId(), i+offset); 
				if(toMove.hasRelationship()){
					// move view columns along with dimension
					for(Column col:viewCols){
							try{			
								if(col.getMetadata(ViewColumnMetadata.class).getSourceTableDimensionColumnId().equals(toMove.getLocalId())){
									// Current col is view related to toMove column
									offset++;
									viewTable=cubeManager.exchangeColumnPosition(viewTable.getId(), col.getLocalId(), i+offset);
								}
							}catch(Exception e){
								// view metadata not found, end of moving for current toMove
							}
						}
					}

				}
			}
		
		log.debug("Result table : "+table);
		if(viewTable!=null)log.debug("Result view : "+viewTable);
		return EmptyType.instance();
	}

}
