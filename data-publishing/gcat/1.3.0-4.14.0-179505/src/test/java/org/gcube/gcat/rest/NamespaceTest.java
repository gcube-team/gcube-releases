package org.gcube.gcat.rest;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.rest.Namespace;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamespaceTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(NamespaceTest.class);
	
	@Test
	public void list() throws Exception {
		Namespace namespace = new Namespace();
		String ret = namespace.list();
		logger.debug("{}", ret);
	}
	
}
