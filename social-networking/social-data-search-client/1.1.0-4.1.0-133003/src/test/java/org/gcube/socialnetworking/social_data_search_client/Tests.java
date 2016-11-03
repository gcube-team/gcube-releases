package org.gcube.socialnetworking.social_data_search_client;


import java.util.HashSet;
import java.util.Set;

import org.gcube.socialnetworking.social_data_indexing_common.utils.ElasticSearchRunningCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Unit test for simple App.
 */
public class Tests 

{
	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(Tests.class);

	//@Before
	public void beforeTest(){
	}

	//@Test
	public void retrieveElasticSearchInformation() throws Exception{

		ElasticSearchRunningCluster es = new ElasticSearchRunningCluster("gcube");
		logger.debug("Result is " + es.getClusterName() + " " + es.getHosts() + " " + es.getPorts());

	}

	//@Test
	public void query() throws Exception{

		ElasticSearchClientImpl el = new ElasticSearchClientImpl("gcube");
		Set<String> set = new HashSet<String>();
		set.add("/gcube/devsec/devVRE");
		set.add("/gcube/devsec/OpenAireDevVRE");
		el.searchInEnhancedFeeds("#pippo test", set, 0, 10);

	}

	//@After
	public void after(){

	}
}
