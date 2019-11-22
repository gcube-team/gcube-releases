package org.gcube.data.analysis.dataminermanagercl.test;

import org.gcube.data.analysis.dataminermanagercl.server.uriresolver.UriResolverUtils;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class URIResolverQueryTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(URIResolverQueryTest.class);

	public void testUriResolver() {

		if (Constants.TEST_ENABLE) {
			logger.info("Test URI Resolver");
			try {

				String publicLink = "https://data-dev.d4science.net/CyUd";

				logger.debug("PublicLink: " + publicLink);

				UriResolverUtils uriResolverUtils = new UriResolverUtils();
				uriResolverUtils.getFileName(publicLink);
				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage(), e);
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success", true);
		}
	}

}
