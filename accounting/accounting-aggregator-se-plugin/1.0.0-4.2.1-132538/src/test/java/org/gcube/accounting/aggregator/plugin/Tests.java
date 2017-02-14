package org.gcube.accounting.aggregator.plugin;

import java.util.HashMap;
import java.util.Map;

import org.gcube.accounting.aggregator.madeaggregation.AggregationType;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Tests {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(Tests.class);

	@Before
	public void beforeTest(){
		
	}

	@Test
	public void testLaunch() throws Exception {
		
		SecurityTokenProvider.instance.set("36501a0d-a205-4bf1-87ad-4c7185faa0d6-98187548");
		//FOR DEBUG
		String scopeDebug="/gcube/devNext";
		ScopeProvider.instance.set(scopeDebug);
		// END FOR DEBUG
				
		Map<String, Object> inputs = new HashMap<String, Object>(); 	
		//type aggregation
		inputs.put("type",AggregationType.DAILY.name());
		//period to be processed
		inputs.put("interval",1 );
		/* OPTIONAL INPUT */
		//change to time
		inputs.put("startTime", 6);
		//specify bucket
		
		inputs.put("bucket","accounting_service");
		
		//current scope
		inputs.put("currentScope",false);
		//specify user for save to workspace
		
		//specify a recovery 0 default recovery and aggregate, 1 only aggregate, 2 only recovery
		inputs.put("recovery",0);
		//user 
		inputs.put("user","alessandro.pieve");
		
		AccountingAggregatorPlugin plugin = new AccountingAggregatorPlugin(null);
		plugin.launch(inputs);
		logger.debug("-------------- launch test finished");
	}
	
	@After
	public void after(){

	}
}
