package org.gcube.data.analysis.tabulardata.service.operation;

import java.util.List;

import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

public interface TaskResult {
	
	public Table getPrimaryTable();
	
	public List<TableId> getCollateralTables();

}
