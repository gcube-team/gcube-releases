package org.gcube.common.core;

import static junit.framework.Assert.*;

import org.gcube.common.core.contexts.GHNContext;
//import org.gcube.common.mycontainer.MyContainer;
import org.junit.BeforeClass;
import org.junit.Test;

public class SmokeClientTest {

	@BeforeClass 
	public static void setup() {
	
		/*unused, but configures environment for client
		MyContainer container = new MyContainer();
		container.cleanState();
		*/
	}
	
	@Test
	public void smokeTest() {
		/*
		GHNContext context = GHNContext.getContext();
		assertTrue(context.isClientMode());
		*/
	}
}
