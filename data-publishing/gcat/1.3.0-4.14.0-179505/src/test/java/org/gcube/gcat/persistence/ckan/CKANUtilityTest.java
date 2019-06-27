package org.gcube.gcat.persistence.ckan;

import org.gcube.gcat.ContextTest;
import org.gcube.gcat.persistence.ckan.CKANUtility;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class CKANUtilityTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(CKANPackageTest.class);
	
	private static final String USERNAME = "luca_frosini";
	
	@Test
	public void testGetApiKey() throws Exception {
		String ckanAPI = CKANUtility.getApiKey(USERNAME);
		logger.debug("User {} has the following API key {}", USERNAME, ckanAPI);
	}
	
	@Test
	public void testAddUserToOrganization() throws Exception {
		CKANUtility.addUserToOrganization(USERNAME, CKANUtility.MEMBER_ROLE, true);
	}
	
	@Test
	public void testAddSpecialUserToOrganization() throws Exception {
		CKANUtility.addUserToOrganization("luca_frosini", "editor", true);
	}
	
	
	
}

