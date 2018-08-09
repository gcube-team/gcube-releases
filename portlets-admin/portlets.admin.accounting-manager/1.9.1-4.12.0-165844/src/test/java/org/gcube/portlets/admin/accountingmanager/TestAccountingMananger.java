/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager;

import junit.framework.TestCase;

import org.gcube.portlets.admin.accountingmanager.server.is.EnableTabsJAXB;
import org.gcube.portlets.admin.accountingmanager.server.is.InformationSystemUtils;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TestAccountingMananger extends TestCase {

	private static Logger logger = LoggerFactory
			.getLogger(TestAccountingMananger.class);


	public void testAccountingManagerResource() {
		if (Constants.TEST_ENABLE) {
			logger.debug("Test Enabled");

			try {
				logger.debug("Scope: " + Constants.DEFAULT_SCOPE);
				EnableTabsJAXB enableTabs = InformationSystemUtils
						.retrieveEnableTab(Constants.DEFAULT_SCOPE);
				logger.debug("EnableTabs: " + enableTabs);
				assertTrue(true);

			} catch (Exception e) {
				assertTrue("Error searching the resource!", false);
				e.printStackTrace();
			}

		} else {
			logger.debug("Test Disabled");
			assertTrue(true);
		}
	}

	

}
