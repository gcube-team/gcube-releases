package org.gcube.data.analysis.tabulardata.query;

import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;

public interface TabularBaseQuery<QueryType extends TabularBaseQuery<QueryType,QueryResult>,QueryResult> {

	public QueryType setFilter(QueryFilter filter);

	public QueryType setOrdering(QueryOrder ordering);
	
	public QueryType setSelection(QuerySelect selection);
	
	public QueryType setGrouping(QueryGroup grouping);

	public int getTotalTuples();
	
	public QueryResult getAll();
	
	public QueryResult getPage(QueryPage page);

}
