package org.gcube.socialnetworking.socialdataindexer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Tests {

	/**
	 * Logger
	 */
	private static Logger logger = LoggerFactory.getLogger(Tests.class);

	//	@Before
	public void beforeTest(){
	}

	//	@Test
	public void testLaunch() {
		logger.debug("Starting to test launch");
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("scope", "gcube");
		SocialDataIndexerPlugin plugin = new SocialDataIndexerPlugin(null);
		//uncomment for testing purpose
		//plugin.launch(inputs);
		logger.debug("-------------- launch test finished");
	}

	//	@After
	public void after(){

	}
}
