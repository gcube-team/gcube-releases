package org.gcube.data.simulfishgrowthdata.util;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.encryption.StringEncrypter;

import junit.framework.TestCase;

public class HibernateUtilTest extends TestCase {

	private Map<String, String> connectionIno;
	private String dbEndpointName;
	private String scope;

	protected void setUp() throws Exception {
		super.setUp();
		 dbEndpointName = "SimulFishGrowth";
		 scope = "/gcube/devNext/NextNext";
		
		 // HibernateUtil.configGently(dbEndpointName, scope);
	}

	public void testOpenSession() {
		// assertNotNull(HibernateUtil.openSession());
	}

}
