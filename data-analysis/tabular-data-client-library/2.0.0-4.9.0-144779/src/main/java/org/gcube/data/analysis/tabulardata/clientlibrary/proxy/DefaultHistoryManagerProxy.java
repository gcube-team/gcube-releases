package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.util.List;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultHistoryManagerProxy implements HistoryManagerProxy {

	ProxyDelegate<HistoryManager> delegate;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultHistoryManagerProxy.class); 
	
	public DefaultHistoryManagerProxy(ProxyDelegate<HistoryManager> config) {
		this.delegate = config;
	}

	@Override
	public Table getLastTable(final long tabularResourceId)
			throws NoSuchTabularResourceException, NoSuchTableException {
		Call<HistoryManager, Table> call = new Call<HistoryManager, Table>() {

			@Override
			public Table call(HistoryManager endpoint) throws Exception {
				return endpoint.getLastTable(tabularResourceId);
			}
		};
		try{
			return delegate.make(call);
		}catch (NoSuchTabularResourceException e) {
			logger.error("no tabular resource found with id {}",tabularResourceId);
			throw e;
		}catch (NoSuchTableException e1) {
			logger.error("no tabular resource found with id {}",tabularResourceId);
			throw e1;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public List<HistoryData> getHistory(final long tabularResourceId)
			throws NoSuchTabularResourceException {
		Call<HistoryManager, List<HistoryData>> call = new Call<HistoryManager, List<HistoryData>>() {

			@Override
			public List<HistoryData> call(HistoryManager endpoint) throws Exception {
				return endpoint.getHistory(tabularResourceId);
			}
		};
		try{
			return delegate.make(call);
		}catch (NoSuchTabularResourceException e) {
			logger.error("no tabular resource found with id {}",tabularResourceId);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	

	
	
}
