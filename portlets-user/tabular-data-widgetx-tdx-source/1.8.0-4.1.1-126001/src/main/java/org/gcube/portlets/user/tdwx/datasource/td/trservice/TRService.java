package org.gcube.portlets.user.tdwx.datasource.td.trservice;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResource;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;
import org.gcube.portlets.user.tdwx.datasource.td.exception.OperationException;
import org.gcube.portlets.user.tdwx.datasource.td.opexecution.OpExecution4ChangeSingleColumnPosition;
import org.gcube.portlets.user.tdwx.datasource.td.opexecution.OpExecutionDirector;
import org.gcube.portlets.user.tdwx.shared.ColumnsReorderingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Call operations on service
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TRService {
	// private static final String TABULAR_RESOURCE_IS_FINAL =
	// "Tabular Resource Is Final";
	private static final String TABULAR_RESOURCE_IS_LOCKED = "Tabular Resource Is Locked";
	private static final String SECURITY_EXCEPTION_RIGHTS = "Security exception, you don't have the required rights!";

	private static final Logger logger = LoggerFactory
			.getLogger(TRService.class);

	private TabularDataService service;
	private TabularResourceId tabularResourceId;

	public TRService() {

	}

	public TabularDataService getService() {
		return service;
	}

	public void setService(TabularDataService service) {
		this.service = service;
	}

	public TabularResourceId getTabularResourceId() {
		return tabularResourceId;
	}

	public void setTabularResourceId(TabularResourceId tabularResourceId) {
		this.tabularResourceId = tabularResourceId;
	}

	public void startChangeSingleColumnPosition(
			ColumnsReorderingConfig columnsReorderingConfig)
			throws OperationException {
		try {

			TabularResource tabularResource = service
					.getTabularResource(tabularResourceId);

			if (tabularResource.isLocked()) {
				logger.error(TABULAR_RESOURCE_IS_LOCKED);
				throw new OperationException(TABULAR_RESOURCE_IS_LOCKED);
			}

			OpExecution4ChangeSingleColumnPosition opEx = new OpExecution4ChangeSingleColumnPosition(
					service, columnsReorderingConfig);
			OpExecutionDirector director = new OpExecutionDirector();
			director.setOperationExecutionBuilder(opEx);
			director.constructOperationExecution();
			OperationExecution invocation = director.getOperationExecution();

			logger.debug("OperationInvocation: \n" + invocation);

			service.executeSynchMetadataOperation(invocation, tabularResourceId);

			return;

		} catch (OperationException e) {
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new OperationException(SECURITY_EXCEPTION_RIGHTS);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new OperationException("Error Changing The Column Position: "
					+ e.getLocalizedMessage());
		}

	}
}
