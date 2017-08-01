package org.gcube.data.simulfishgrowthdata.api.base;

import org.apache.commons.lang.StringUtils;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gr.i2s.fishgrowth.model.Modeler;
import junit.framework.TestCase;

public class GlobalModelConsumptionExecutorTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		String dbEndpointName = "SimulFishGrowth";
		String scope = "/gcube/preprod/preECO";

		HibernateUtil.configGently(dbEndpointName, scope);
	}

}
