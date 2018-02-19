package org.gcube.data.analysis.tabulardata.operation.worker.results;

import java.util.List;

import org.gcube.data.analysis.tabulardata.model.table.Table;

public interface WorkerResult extends Result {
	
	public Table getResultTable();
	
	public Table getDiffTable();
	
	public List<Table> getCollateralTables();

}
