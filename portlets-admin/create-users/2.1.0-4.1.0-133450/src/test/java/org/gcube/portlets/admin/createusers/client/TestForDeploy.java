package org.gcube.portlets.admin.createusers.client;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.admin.createusers.server.CreateUsersImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		CreateUsersImpl serviceImpl = new CreateUsersImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo("test.user") == 0);
		System.out.println("Test OK!");
	}
	
}
