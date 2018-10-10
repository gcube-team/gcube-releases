/**
 * 
 */
package org.gcube.portlets.admin.accountingmanager;

import junit.framework.TestCase;

import org.gcube.portlets.admin.accountingmanager.server.amservice.cache.AccountingCache;
import org.gcube.portlets.admin.accountingmanager.shared.Constants;
import org.gcube.portlets.admin.accountingmanager.shared.data.response.SeriesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class TestAccountingManangerCache extends TestCase {

	private static Logger logger = LoggerFactory
			.getLogger(TestAccountingManangerCache.class);

	public void testAccountingManagerCache() {
		if (Constants.TEST_ENABLE) {
			logger.debug("Test Enabled");

			try {
				AccountingCache accountingCache = new AccountingCache();
				SeriesResponse s = new SeriesResponse();
				for (int i = 0; i < 1000000; i++) {
					accountingCache.putSeries("key" + i, s);
				}
				Thread.sleep(180000);

				accountingCache.finalize();
				assertTrue(true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				assertTrue("Error in cache!", false);

			}

		} else {
			logger.debug("Test Disabled");
			assertTrue(true);
		}
	}
	
	
	public void testAccountingManagerCacheRemoveAll() {
		if (Constants.TEST_ENABLE) {
			logger.debug("Test Enabled");

			try {
				AccountingCache accountingCache = new AccountingCache();
				accountingCache.finalize();
				assertTrue(true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				assertTrue("Error in cache!", false);

			}

		} else {
			logger.debug("Test Disabled");
			assertTrue(true);
		}
	}

}
