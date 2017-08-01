package org.gcube.data.simulfishgrowthdata.util;

import java.util.Map;

import junit.framework.TestCase;

public class GCubeUtilsTest extends TestCase {

	public void testGetCredentials() throws Exception {
		String dbEndpoint = "SimulFishGrowth";
		String scope = "/gcube/preprod/preECO";
		String dbUserKey = "hibernate.connection.username";
		String dbUserExpected = "sfguser";

		// Map<String, String> results = GCubeUtils.getCredentials(dbEndpoint,
		// scope);
		// assertFalse(results.isEmpty());
		// assertTrue(results.containsKey(dbUserKey));
		// assertEquals(dbUserExpected, results.get(dbUserKey));
	}

}
