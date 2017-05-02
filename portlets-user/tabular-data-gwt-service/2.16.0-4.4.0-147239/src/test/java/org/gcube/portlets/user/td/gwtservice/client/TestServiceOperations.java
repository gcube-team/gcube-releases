package org.gcube.portlets.user.td.gwtservice.client;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.service.TabularDataService;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TestServiceOperations {

	private static Logger logger = LoggerFactory
			.getLogger(TestServiceOperations.class);

	@Test
	public void TestTROperation() {
		try {

			TDService tdService = new TDService();
			TabularDataService service = tdService.getService();

			List<OperationDefinition> trOperations = service.getCapabilities();
			Assert.assertTrue("No operations exists", trOperations.size() > 0);
			logger.debug("------------Tabular Resource Operation--------------");
			for (OperationDefinition operation : trOperations) {
				logger.debug("Name: " + operation.getName());
				// logger.debug("Scope: "+operation.getScope());
				logger.debug("Desc: " + operation.toString());

				logger.debug("-----------------------------------");
			}

		} catch (Throwable e) {
			logger.error(
					"Error retrieving operations: " + e.getLocalizedMessage(),
					e);
		}
	}

}
