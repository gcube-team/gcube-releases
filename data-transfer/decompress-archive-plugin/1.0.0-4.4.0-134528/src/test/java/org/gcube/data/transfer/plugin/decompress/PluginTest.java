package org.gcube.data.transfer.plugin.decompress;

import java.util.HashMap;
import java.util.Map;

import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.plugin.fails.ParameterException;
import org.gcube.data.transfer.plugins.decompress.DecompressPluginFactory;
import org.junit.Assert;
import org.junit.Test;

public class PluginTest {

	@Test
	public void factoryTest() throws ParameterException{
		
		
		
		DecompressPluginFactory factory=new DecompressPluginFactory();
		Assert.assertNotNull(factory.getDescription());
		Assert.assertNotNull(factory.getID());
		Assert.assertNotNull(factory.getParameters());
		
		Map<String,String> params=new HashMap<>();
		params.put(DecompressPluginFactory.DESTINATION_PARAMETER, "here");
		params.put(DecompressPluginFactory.SOURCE_PARAMETER, "/home/fabio/workspaces/home-library-jcr-PRE-PROD.jar");
		
		PluginInvocation invocation=new PluginInvocation(factory.getID(), params);
		
		factory.checkInvocation(invocation);
		Assert.assertNotNull(factory.createWorker(invocation));
	}
	
	
}
