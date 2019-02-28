package org.gcube.gcat.utils;

import org.gcube.gcat.ContextTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIResolverTest extends ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(URIResolverTest.class);
	
	@Test
	public void getURL() {
		URIResolver uriResolver = new URIResolver();
		String catalogueItemURL = uriResolver.getCatalogueItemURL("my_first_restful_transaction_model");
		logger.debug("Item URL is {}", catalogueItemURL);
	}
	
}
