/**
 *
 */
package org.gcube.portlets.user.td.taskswidget.server.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.impl.TabularDataServiceFactory;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Dec 4, 2013
 *
 */
public class TaskTabularDataService implements Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = -5748702207846640585L;

	public static Logger logger = LoggerFactory.getLogger(TaskTabularDataService.class);

	private String scope;
	private TabularDataService tabularDataService;
	private String username;

	public TaskTabularDataService(String scope, String username) throws Exception {
		this.scope = scope;

		try {
			logger.trace("Instancing tabular data service factory with scope: "+ scope + " and user: " + username);
			//ScopeProvider.instance.set(scope);
			this.username = username;
			this.tabularDataService = TabularDataServiceFactory.getService();
//			this.tabularDataService = TabularDataServiceFactory.getService(username);
		} catch (Exception e) {
			logger.error("Error on instancing Tabular Data Service Client", e);
			this.tabularDataService = null;
			throw new Exception("Tabular data service is null");
		}
	}

	public TabularDataService getTabularDataService() {
		return tabularDataService;
	}

	public String getScope() {
		return scope;
	}

	/**
	 * @param currentTabularResource
	 * @return
	 * @throws NoSuchTabularResourceException
	 */
	public List<Task> getTasks(TabularResourceId currentTabularResource) throws NoSuchTabularResourceException {
		return tabularDataService.getTasks(currentTabularResource);
	}

	/**
	 * @param operationId
	 * @return
	 * @throws NoSuchTaskException
	 */
	public Task getTask(TaskId operationId) throws NoSuchTaskException {
		return tabularDataService.getTask(operationId);
	}

	/**
	 * @return
	 */
	public List<OperationDefinition> getCapabilities() {
		return tabularDataService.getCapabilities();
	}

	/**
	 * @return
	 */
	public List<TabularResource> getTabularResources() {
		return tabularDataService.getTabularResources();
	}


	/**
	 *
	 * @param operationId
	 * @return
	 * @throws NoSuchOperationException
	 */
	public OperationDefinition getOperationDescriptionById(long operationId) throws NoSuchOperationException {
		return tabularDataService.getCapability(operationId);

	}

	public Map<Long, OperationDefinition> getOperationDescriptionMap(){
		List<OperationDefinition> listOpd = tabularDataService.getCapabilities();

		Map<Long, OperationDefinition> mapOperDefinition = new HashMap<Long, OperationDefinition>(listOpd.size());
		for (OperationDefinition operationDefinition : listOpd) {
			mapOperDefinition.put(operationDefinition.getOperationId(), operationDefinition);
		}

		logger.info("Map of  OperationDefinition returning "+mapOperDefinition.size()+" elements");
		return mapOperDefinition;
	}


}
