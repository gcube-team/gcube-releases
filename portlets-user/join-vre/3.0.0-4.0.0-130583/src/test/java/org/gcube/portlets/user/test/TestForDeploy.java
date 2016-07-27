package org.gcube.portlets.user.test;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.user.joinvre.server.JoinServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		JoinServiceImpl serviceImpl = new JoinServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(JoinServiceImpl.TEST_USER) == 0);
		System.out.println("Test OK!");
	}

}
