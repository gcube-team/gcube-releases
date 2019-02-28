package org.gcube.common.core;

import org.apache.log4j.Logger;
//import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

//@RunWith(MyContainerTestRunner.class)
public class SmokeTest {

	static Logger logger = Logger.getLogger("test");
	
	@Test
	public void startupTest() throws Exception {
		Thread.sleep(1000);
		logger.info("startup test successful");
	}

}
