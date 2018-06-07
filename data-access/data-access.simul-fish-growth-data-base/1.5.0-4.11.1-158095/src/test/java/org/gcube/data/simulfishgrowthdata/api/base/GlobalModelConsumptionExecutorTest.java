package org.gcube.data.simulfishgrowthdata.api.base;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;

import junit.framework.TestCase;

public class GlobalModelConsumptionExecutorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		String dbEndpointName = "SimulFishGrowth";
		String scope = "/gcube/preprod/preECO";

		HibernateUtil.configGently(dbEndpointName, scope);
	}

}
