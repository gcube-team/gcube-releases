
package org.gcube.portlets.widgets.wsmail.test;
import static org.junit.Assert.assertTrue;

import org.gcube.portlets.widgets.wsmail.server.WsMailServiceImpl;
import org.junit.Test;

public class TestForDeploy {

	@Test
	public void testUserIsTestUser() {
		WsMailServiceImpl serviceImpl = new WsMailServiceImpl();
		String username = serviceImpl.getDevelopmentUser();
		System.out.println("username for deploy is: " + username);
		assertTrue(username.compareTo("test.user") == 0);
		System.out.println("Test OK!");
	}
	
}
