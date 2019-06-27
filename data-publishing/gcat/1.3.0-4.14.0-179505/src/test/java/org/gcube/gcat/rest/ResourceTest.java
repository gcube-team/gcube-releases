package org.gcube.gcat.rest;

import org.gcube.gcat.ContextTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(ResourceTest.class);
	
	// @Test
	public void read() throws Exception {
		Resource resource = new Resource();
		String itemID = "";
		String resourceID = "";
		String ret = resource.read(itemID,resourceID);
		logger.debug("{}", ret);
	}
}
