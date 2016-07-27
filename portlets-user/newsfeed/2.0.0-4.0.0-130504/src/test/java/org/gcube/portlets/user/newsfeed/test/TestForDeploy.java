package org.gcube.portlets.user.newsfeed.test;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.user.newsfeed.server.NewsServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		NewsServiceImpl serviceImpl = new NewsServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo("test.user") == 0);
		System.out.println("Test OK!");
	}
	
}
