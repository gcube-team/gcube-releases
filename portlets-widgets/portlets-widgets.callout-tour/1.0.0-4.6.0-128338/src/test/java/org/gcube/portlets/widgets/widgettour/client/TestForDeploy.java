package org.gcube.portlets.widgets.widgettour.client;

import static org.junit.Assert.assertTrue;

import org.gcube.portlets.widgets.widgettour.server.TourManagerServicesImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		TourManagerServicesImpl serviceImpl = new TourManagerServicesImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo(TourManagerServicesImpl.userid) == 0);
		System.out.println("Test OK!");
	}

}
