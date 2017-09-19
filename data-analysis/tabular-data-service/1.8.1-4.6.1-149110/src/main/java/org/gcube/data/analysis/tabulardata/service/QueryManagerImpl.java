package org.gcube.data.analysis.tabulardata.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;

import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.QueryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.gcube.data.analysis.tabulardata.model.metadata.table.CountMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.query.json.TabularJSONQuery;
import org.gcube.data.analysis.tabulardata.query.json.TabularJSONQueryFactory;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;

@WebService(portName = "QueryManagerPort",
serviceName = QueryManager.SERVICE_NAME,
targetNamespace = Constants.QUERY_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.QueryManager")
@Singleton
@WeldService
public class QueryManagerImpl implements QueryManager{

	@Inject
	private Logger logger;

	@Inject
	TabularJSONQueryFactory queryFactory;

	@Inject
	CubeManager cm;

	@Inject
	@Unprivileged
	DatabaseConnectionProvider databaseConnectionProvider;

	@Override
	public String queryAsJson(long tableId, QueryPage page, QuerySelect select,
			QueryFilter filter, QueryGroup group, QueryOrder order) throws NoSuchTableException {
		logger.trace("calling queryAsJson with filter "+filter+", page "+page+" and order "+order+" and id "+tableId);
		TabularJSONQuery jsonQuery = queryFactory.get(new TableId(tableId));
		jsonQuery.setFilter(filter);
		jsonQuery.setOrdering(order);
		jsonQuery.setSelection(select);
		jsonQuery.setGrouping(group);
		logger.debug("Executing query with filter: "+filter+", order "+order);
		if (page!=null)
			return jsonQuery.getPage(page);
		else return jsonQuery.getAll();
	}

	@Override
	public Table getTable(long tableId) throws NoSuchTableException {
		logger.trace("getting table info for tableId "+tableId);
		return cm.getTable(new TableId(tableId));
	}

	@Override
	public Table getTimeTable(PeriodType period) {
		logger.trace("getting table info for period "+period.getName());
		return cm.getTimeTable(period);
	}


	@Override
	public int getQueryLenght(long tableId, QueryFilter filter)
			throws NoSuchTableException {
		TableId id = new TableId(tableId);
		Table table;
		if (filter==null){ 
			table=cm.getTable(id);
			if (table.contains(CountMetadata.class))
				return table.getMetadata(CountMetadata.class).getCount();
			else {
				//used only for older tables (the count is set after the first request)
				TabularJSONQuery jsonQuery = queryFactory.get(id);
				int count = jsonQuery.getTotalTuples();
				cm.modifyTableMeta(id).setTableMetadata(new CountMetadata(count)).create();
				return count;
			}
		} else{
			TabularJSONQuery jsonQuery = queryFactory.get(id);
			jsonQuery.setFilter(filter);
			return jsonQuery.getTotalTuples();
		}
	}


}
