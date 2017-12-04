package org.gcube.socialnetworking.social_data_search_client;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.portal.databook.shared.EnhancedFeed;
import org.gcube.socialnetworking.social_data_indexing_common.utils.SearchableFields;
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
	
	ElasticSearchClientImpl el;

	//@Before
	public void beforeTest() throws Exception{
		
		el = new ElasticSearchClientImpl("gcube");
	}

	//@Test
	public void query() throws Exception{

		
		Set<String> set = new HashSet<String>();
		set.add("/gcube/devNext/NextNext");
		List<EnhancedFeed> results = el.searchInField("Costantino Perciante", set, 0, 10, SearchableFields.POST_AUTHOR);
		
		logger.debug("First result is " + results);

	}

	//@After
	public void after(){

	}
}
