package org.gcube.data.integration;

import static org.gcube.data.TestUtils.*;
import static org.junit.Assert.*;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.data.tm.plugin.PluginManager;
import org.gcube.data.tr.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class)
public class StartupTest {

	@Deployment
	static Gar gar = serviceGar();
	
	@Test
	public void serviceStartsWithPlugin() {
		
		assertTrue(new PluginManager().plugins().containsKey(Constants.TR_NAME));
		
	}
}
