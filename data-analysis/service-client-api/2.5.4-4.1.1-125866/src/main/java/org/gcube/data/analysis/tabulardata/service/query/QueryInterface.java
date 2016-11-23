package org.gcube.data.analysis.tabulardata.service.query;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;

public interface QueryInterface extends QueryInterfaceJson {
	
//	public Table getLastTable(TabularResourceId tabularResourceId) throws NoSuchTabularResourceException;
	
	int getQueryLenght(TableId tableId, QueryFilter filter) throws NoSuchTableException;

	Table getTable(TableId tableId) throws NoSuchTableException;
	
	Table getTimeTable(PeriodType period);
}
