package org.gcube.gcat.utils;

import org.gcube.gcat.ContextTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantsTest extends ContextTest {
	
	private static final Logger logger = LoggerFactory.getLogger(ConstantsTest.class);
	
	
	
	@Test
	public void testGetApplicationToken() {
		logger.debug("Application token for Context {} is {}", ContextUtility.getCurrentContext(), Constants.getCatalogueApplicationToken());
	}
	
	
}
