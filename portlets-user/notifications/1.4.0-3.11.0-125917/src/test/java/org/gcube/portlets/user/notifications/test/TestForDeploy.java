package org.gcube.portlets.user.notifications.test;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.user.notifications.server.NotificationsServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		NotificationsServiceImpl serviceImpl = new NotificationsServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo("test.user") == 0);
		System.out.println("Test OK!");
	}
	
}
