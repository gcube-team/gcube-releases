package org.gcube.portlets.user.td.gwtservice.server.trservice;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class OperationDefinitionMap {
	private static Logger logger = LoggerFactory
			.getLogger(OperationDefinitionMap.class);
	
	public static OperationDefinition map(String op, TabularDataService service)
			throws TDGWTServiceException {
		OperationDefinition operationDefinition = null;
		logger.debug("Retrieve Capability from Service: "+op);
		try {
			operationDefinition = service.getCapability(Long.valueOf(op));
		} catch (NumberFormatException e) {
			logger.error("No valid operation type: " + op);
			e.printStackTrace();
			throw new TDGWTServiceException(
					"No valid operation type: " + op);
		} catch (NoSuchOperationException e) {
			logger.error("NoSuchOperationException: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"NoSuchOperationException: " + e.getLocalizedMessage());
		} catch(Throwable e) {
			logger.error("Error Retrieving Service Capability: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException(
					"Error Retrieving Service Capability: " + e.getLocalizedMessage());
		}
		

		return operationDefinition;

	}
}
