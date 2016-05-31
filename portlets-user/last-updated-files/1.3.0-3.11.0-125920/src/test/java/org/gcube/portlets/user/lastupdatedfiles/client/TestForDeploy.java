package org.gcube.portlets.user.lastupdatedfiles.client;

import static org.junit.Assert.*;

import org.gcube.portlets.user.lastupdatedfiles.server.FileServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		FileServiceImpl serviceImpl = new FileServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(FileServiceImpl.TEST_USER) == 0);
		System.out.println("Test OK!");
	}

}
