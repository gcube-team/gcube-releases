package org.gcube.portlets.user.tdwx.datasource.td.trservice;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.tdwx.datasource.td.exception.OperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OperationDefinitionMap {
	protected static Logger logger = LoggerFactory
			.getLogger(OperationDefinitionMap.class);
	
	public static OperationDefinition map(String op, TabularDataService service)
			throws OperationException {
		OperationDefinition operationDefinition = null;
		logger.debug("Retrieve Capability from Service");
		try {
			operationDefinition = service.getCapability(Long.valueOf(op));
		} catch (NumberFormatException e) {
			logger.error("No valid operation type: " + op);
			e.printStackTrace();
			throw new OperationException(
					"No valid operation type: " + op);
		} catch (NoSuchOperationException e) {
			logger.error("NoSuchOperationException: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new OperationException(
					"NoSuchOperationException: " + e.getLocalizedMessage());
		} catch(Throwable e) {
			logger.error("Error Retrieving Service Capability: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new OperationException(
					"Error Retrieving Service Capability: " + e.getLocalizedMessage());
		}
		

		return operationDefinition;

	}
}
