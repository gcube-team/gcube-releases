package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.utils.Util.getUserAuthorizedObject;
import static org.gcube.data.analysis.tabulardata.utils.Util.toHistoryDataList;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;
import javax.persistence.EntityManager;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.data.connection.DatabaseConnectionProvider;
import org.gcube.data.analysis.tabulardata.cube.data.connection.unprivileged.Unprivileged;
import org.gcube.data.analysis.tabulardata.exceptions.NoSuchObjectException;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;


@WebService(portName = "HistoryManagerPort",
serviceName = HistoryManager.SERVICE_NAME,
targetNamespace = Constants.HISTORY_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.HistoryManager")
@Singleton
@WeldService
public class HistoryManagerImpl implements HistoryManager{


	@Inject
	CubeManager cm;

	@Inject
	@Unprivileged
	DatabaseConnectionProvider databaseConnectionProvider;

	@Inject
	private EntityManagerHelper emHelper;

	@Inject
	Logger logger;

	@Override
	public Table getLastTable(long tabularResourceId) throws NoSuchTabularResourceException, NoSuchTableException, InternalSecurityException{
		logger.info("requesting last table for tabularResource with id {} ",tabularResourceId);

		EntityManager entityManager = emHelper.getEntityManager(); 

		StorableTabularResource tabularResource;
		try{
			tabularResource = getUserAuthorizedObject(tabularResourceId, StorableTabularResource.class, entityManager);
		}catch(NoSuchObjectException e){
			throw new NoSuchTabularResourceException(tabularResourceId);
		}catch (InternalSecurityException e) {
			logger.error("error on authorization",e);
			throw e;
		}finally{
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}

		if (tabularResource==null || 
				!(tabularResource.getScopes().contains(ScopeProvider.instance.get()))){
			logger.error("the tabular resource with id {} not exists", tabularResourceId);
			throw new NoSuchTabularResourceException(tabularResourceId);
		}

		if(tabularResource.getTableId()==null){
			logger.debug("the tabular resource with id {} has empty history", tabularResourceId);
			return null;
		}

		Table table;
		try{
			table = cm.getTable(new TableId(tabularResource.getTableId()));
		}catch (Exception e) {
			logger.error("table not found");
			throw new NoSuchTableException(new TableId(tabularResource.getTableId()));
		}

		logger.info("returning table "+table);

		return table;
	}

	@Override
	public List<HistoryData> getHistory(long tabularResourceId) throws NoSuchTabularResourceException, InternalSecurityException{
		EntityManager entityManager = emHelper.getEntityManager(); 
		StorableTabularResource tabularResource;
		try{
			tabularResource = getUserAuthorizedObject(tabularResourceId, StorableTabularResource.class, entityManager);
		}catch(NoSuchObjectException e){
			throw new NoSuchTabularResourceException(tabularResourceId);
		} finally{
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}

		if (tabularResource==null || 
				!(tabularResource.getScopes().contains(ScopeProvider.instance.get()))) {
			logger.error("the tabular resource with id %s not exists", tabularResourceId);
			throw new NoSuchTabularResourceException(tabularResourceId);
		}
		List<HistoryData> history = toHistoryDataList(tabularResource.getHistorySteps());
		return history;
	}
}
