package org.gcube.portlets.user.socialprofile.test;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.user.socialprofile.server.SocialServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		SocialServiceImpl serviceImpl = new SocialServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo("test.user") == 0);
		System.out.println("Test OK!");
	}
	
}
