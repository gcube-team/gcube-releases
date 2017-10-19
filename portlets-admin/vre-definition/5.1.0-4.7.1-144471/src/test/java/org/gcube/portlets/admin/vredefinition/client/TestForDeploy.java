package org.gcube.portlets.admin.vredefinition.client;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.admin.vredefinition.server.VREDefinitionServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		VREDefinitionServiceImpl serviceImpl = new VREDefinitionServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(VREDefinitionServiceImpl.defaultUserId) == 0);
		System.out.println("Test OK!");
	}

}
