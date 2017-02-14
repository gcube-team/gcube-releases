package org.gcube.portlets.widgets.openlayerbasicwidgets;

import junit.framework.TestCase;

import org.gcube.portlets.widgets.openlayerbasicwidgets.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class GitHubConnectorTest extends TestCase {

	private static Logger logger = LoggerFactory
			.getLogger(GitHubConnectorTest.class);

	public void testOLBasicWidgets() {
		if (Constants.TEST_ENABLE) {
			executeTestOnWidgets();
		} else {
			assertTrue(true);

		}
	}

	

	private void executeTestOnWidgets() {

		try {
			logger.debug("Test OpenLayer Basic Widgets");
		

		} catch (Exception e) {
			logger.debug(e.getLocalizedMessage());
			e.printStackTrace();
			fail(e.getLocalizedMessage());

		}

	}

}
