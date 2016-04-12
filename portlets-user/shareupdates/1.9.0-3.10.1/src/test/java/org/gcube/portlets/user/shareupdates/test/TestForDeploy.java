package org.gcube.portlets.user.shareupdates.test;

import static org.junit.Assert.*;

import org.gcube.portlets.user.shareupdates.server.ShareUpdateServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		ShareUpdateServiceImpl serviceImpl = new ShareUpdateServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(ShareUpdateServiceImpl.TEST_USER) == 0);
		System.out.println("Test OK!");
	}

}
