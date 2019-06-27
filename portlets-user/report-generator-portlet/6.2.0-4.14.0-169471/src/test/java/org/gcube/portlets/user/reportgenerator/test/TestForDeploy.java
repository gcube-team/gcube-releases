package org.gcube.portlets.user.reportgenerator.test;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.user.reportgenerator.server.servlet.ReportServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		ReportServiceImpl serviceImpl = new ReportServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo("test.user") == 0);
		System.out.println("Test OK!");
	}
	
}
