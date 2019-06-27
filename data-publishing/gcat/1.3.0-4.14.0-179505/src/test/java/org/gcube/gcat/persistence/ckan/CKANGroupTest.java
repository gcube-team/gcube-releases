package org.gcube.gcat.persistence.ckan;

import org.gcube.gcat.ContextTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CKANGroupTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(CKANGroupTest.class);
	
	@Test
	public void read() throws Exception {
		CKANGroup ckanGroup = new CKANGroup();
		ckanGroup.setApiKey(CKANUtility.getSysAdminAPI());
		String name = "";
		ckanGroup.setName(name);
		String ret = ckanGroup.read();
		logger.debug("{}", ret);
	}
	
	// @Test
	public void delete() throws Exception {
		CKANGroup ckanGroup = new CKANGroup();
		ckanGroup.setApiKey(CKANUtility.getSysAdminAPI());
		String name = "";
		ckanGroup.setName(name);
		ckanGroup.delete(true);
	}
}
