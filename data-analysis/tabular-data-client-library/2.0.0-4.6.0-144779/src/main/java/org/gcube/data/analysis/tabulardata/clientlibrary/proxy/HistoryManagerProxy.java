package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public interface HistoryManagerProxy{
	
	Table getLastTable(long tabularResourceId) throws NoSuchTabularResourceException, NoSuchTableException;

	List<HistoryData> getHistory(long tabularResourceId)
			throws NoSuchTabularResourceException;

}
