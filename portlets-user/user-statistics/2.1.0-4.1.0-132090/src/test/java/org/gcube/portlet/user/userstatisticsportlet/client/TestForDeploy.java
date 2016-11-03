package org.gcube.portlet.user.userstatisticsportlet.client;

import static org.junit.Assert.assertTrue;

import org.gcube.portlet.user.userstatisticsportlet.server.ServerUtils;
import org.gcube.portlet.user.userstatisticsportlet.server.UserStatisticsServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		String username = ServerUtils.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(UserStatisticsServiceImpl.defaultUserId) == 0);
		System.out.println("Test OK!");
	}

}
