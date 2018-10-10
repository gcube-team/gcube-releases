package org.gcube.datapublishing.sdmx.datasource.tabman.querymanager.impl;

import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.datapublishing.sdmx.datasource.data.QueryFilterProvider;

public interface TabmanQuery extends QueryFilterProvider{
	
	public QueryFilter getQueryFilter();
	
	public QueryOrder getQueryOrder ();

	public QuerySelect getRequestedColumnsFilter();
	
	public TableId getTableId ();
}
