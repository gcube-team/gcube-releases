package org.gcube.socialnetworking.social_data_indexing_common;

import org.gcube.socialnetworking.social_data_indexing_common.utils.ElasticSearchRunningCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tests {

	/**
	 * Logger
	 */
	private static Logger _log = LoggerFactory.getLogger(Tests.class);

	@Before
	public void beforeTest(){
		// set security token
		//SecurityTokenProvider.instance.set("");
	}

	@Test
	public void retrieveElasticSearchInformation() throws Exception{

		ElasticSearchRunningCluster es = new ElasticSearchRunningCluster(null);
		_log.debug("Result is " + es.getClusterName() + " " + es.getHosts() + " " + es.getPorts());

	}

	@After
	public void after(){

	}

}
