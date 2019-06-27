package org.gcube.gcat.persistence.ckan;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.persistence.ckan.CKANOrganization;
import org.gcube.gcat.persistence.ckan.CKANUtility;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CKANOrganizationTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(CKANOrganizationTest.class);
	
	@Test
	public void getUserRole() throws Exception {
		CKANOrganization ckanOrganization = new CKANOrganization();
		ckanOrganization.setApiKey(CKANUtility.getSysAdminAPI());
		ckanOrganization.setName(CKANOrganization.getCKANOrganizationName());
		String ret = ckanOrganization.getUserRole("luca.frosini");
		logger.debug("{}", ret);
	}
	
	@Test
	public void listOrganization() throws Exception {
		ContextTest.setContextByName("/gcube");
		CKANOrganization ckanOrganization = new CKANOrganization();
		String ret = ckanOrganization.list(1000, 0);
		logger.debug("{}", ret);
	}
}
