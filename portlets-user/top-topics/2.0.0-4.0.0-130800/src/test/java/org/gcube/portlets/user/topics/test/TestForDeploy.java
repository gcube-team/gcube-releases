package org.gcube.portlets.user.topics.test;

import static org.junit.Assert.*;

import org.gcube.portlets.user.topics.server.TopicServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		TopicServiceImpl serviceImpl = new TopicServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(TopicServiceImpl.TEST_USER) == 0);
		System.out.println("Test OK!");
	}

}
