package org.gcube.portlet.user.userstatisticsportlet.client;

import static org.junit.Assert.*;

import org.gcube.portlet.user.userstatisticsportlet.server.UserStatisticsServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		UserStatisticsServiceImpl serviceImpl = new UserStatisticsServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(UserStatisticsServiceImpl.defaultUserId) == 0);
		System.out.println("Test OK!");
	}

}
