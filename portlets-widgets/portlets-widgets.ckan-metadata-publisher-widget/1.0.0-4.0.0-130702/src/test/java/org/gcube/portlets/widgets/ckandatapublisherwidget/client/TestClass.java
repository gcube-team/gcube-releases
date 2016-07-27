package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import org.gcube.portlets.widgets.ckandatapublisherwidget.server.CKANPublisherServicesImpl;
import org.junit.Test;

public class TestClass {

	@Test
	public void testUser() {
		
		assert(new CKANPublisherServicesImpl().getDevelopmentUser().equals(CKANPublisherServicesImpl.TEST_USER));
		
	}

}
