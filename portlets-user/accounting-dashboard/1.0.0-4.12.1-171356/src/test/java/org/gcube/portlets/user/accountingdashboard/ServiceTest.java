package org.gcube.portlets.user.accountingdashboard;

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
public class ServiceTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(ServiceTest.class);

	@Test
	public void testService() {
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