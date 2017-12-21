package org.gcube.data.analysis.tabulardata.service.impl.query;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.query;

import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.QueryManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.data.analysis.tabulardata.service.query.QueryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryInterfaceImpl implements QueryInterface {

	private static Logger logger = LoggerFactory.getLogger(QueryInterfaceImpl.class);

	private static QueryManagerProxy queryManager = query().build();

	public Table getTable(TableId tableId) throws NoSuchTableException {
		return queryManager.getTable(tableId.getValue());

	}

	public Table getTimeTable(PeriodType period) {
		return queryManager.getTimeTable(period);
	}

	public int getQueryLenght(TableId tableId, QueryFilter filter)
			throws NoSuchTableException {
		return queryManager.getQueryLenght(tableId.getValue(), filter);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryOrder order) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, null, filter, null, order);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, null, filter, null, null);

	}

	public String queryAsJson(TableId tableId, QueryPage page, QueryOrder order)
			throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, null, null, null, order);

	}

	public String queryAsJson(TableId tableId, QueryPage page)
			throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, null, null, null, null);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QuerySelect select) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select, null, null, null);

	}

	public String queryAsJson(TableId tableId, QueryPage page, QueryGroup group)
			throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page,null, null, group, null);
	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryOrder order, QuerySelect select)
					throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select, filter, null, order);
	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryOrder order, QuerySelect select,
			QueryGroup group) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select, filter, group, order);
	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QuerySelect select) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select, filter, null, null);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QuerySelect select, QueryGroup group)
					throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select, filter, group, null);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryFilter filter, QueryGroup group) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, null, filter, group, null);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryOrder order, QuerySelect select, QueryGroup group)
					throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select, null, group, order);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryOrder order, QueryGroup group) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, null, null, group, order);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QueryOrder order, QuerySelect select) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select,  null, null, order);

	}

	public String queryAsJson(TableId tableId, QueryPage page,
			QuerySelect select, QueryGroup group) throws NoSuchTableException {
		return queryManager.queryAsJson(tableId.getValue(), page, select, null, group, null);

	}


}
