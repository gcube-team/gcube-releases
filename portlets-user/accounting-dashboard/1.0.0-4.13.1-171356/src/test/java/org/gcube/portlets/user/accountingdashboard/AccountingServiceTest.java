package org.gcube.portlets.user.accountingdashboard;

import org.gcube.portlets.user.accountingdashboard.server.accounting.AccountingService;
import org.gcube.portlets.user.accountingdashboard.server.accounting.AccountingServiceType;
import org.gcube.portlets.user.accountingdashboard.shared.Constants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class AccountingServiceTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(AccountingServiceTest.class);

	@Test
	public void testService() {
		if (Constants.TEST_ENABLE) {

			try {
				AuthTest.setToken();
				AccountingService accountingService = new AccountingService(AccountingServiceType.CurrentScope);
				// accountingService.getTree();

				assertTrue("Success", true);
			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}
	}

	@Test
	public void testData() {
		if (Constants.TEST_ENABLE) {

			try {
				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				fail("Error:" + e.getLocalizedMessage());

			}

		} else {
			assertTrue("Success", true);
		}
	}

}