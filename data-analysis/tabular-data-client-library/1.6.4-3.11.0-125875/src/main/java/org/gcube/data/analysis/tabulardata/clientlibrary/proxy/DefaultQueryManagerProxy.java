package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.webservice.QueryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.time.PeriodType;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryFilter;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryOrder;
import org.gcube.data.analysis.tabulardata.query.parameters.QueryPage;
import org.gcube.data.analysis.tabulardata.query.parameters.group.QueryGroup;
import org.gcube.data.analysis.tabulardata.query.parameters.select.QuerySelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultQueryManagerProxy implements QueryManagerProxy {

	private static Logger logger = LoggerFactory.getLogger(DefaultQueryManagerProxy.class); 
	
	ProxyDelegate<QueryManager> delegate;
	
	public DefaultQueryManagerProxy(ProxyDelegate<QueryManager> config) {
		this.delegate = config;
	}

	@Override
	public Table getTable(final long tableId) throws NoSuchTableException {
		Call<QueryManager, Table> call = new Call<QueryManager, Table>() {

			@Override
			public Table call(QueryManager endpoint) throws Exception {
				return endpoint.getTable(tableId);
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("error calling getTable",e);
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public Table getTimeTable(final PeriodType period) {
		Call<QueryManager, Table> call = new Call<QueryManager, Table>() {

			@Override
			public Table call(QueryManager endpoint) throws Exception {
				return endpoint.getTimeTable(period);
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("error calling getTimeTable",e);
			throw again(e).asServiceException();
		}
	}

	@Override
	public String queryAsJson(final long tableId, final QueryPage page, final QuerySelect select,  final QueryFilter filter,
			final QueryGroup group, final QueryOrder order) throws NoSuchTableException {
		Call<QueryManager, String> call = new Call<QueryManager, String>() {

			@Override
			public String call(QueryManager endpoint) throws Exception {
				return endpoint.queryAsJson(tableId, page, select, filter, group, order);
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("error calling queryAsJson",e);
			throw again(e).asServiceException();
		}
	}

	@Override
	public int getQueryLenght(final long tableId, final QueryFilter filter)
			throws NoSuchTableException {
		Call<QueryManager, Integer> call = new Call<QueryManager, Integer>() {

			@Override
			public Integer call(QueryManager endpoint) throws Exception {
				return endpoint.getQueryLenght(tableId, filter);
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			logger.error("error calling getQueryLenght",e);
			throw again(e).asServiceException();
		}
	}


	
	
}
