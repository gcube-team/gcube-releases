package org.gcube.vremanagement.resourcemanager.impl;

import static org.junit.Assert.*;



import javax.inject.Inject;
import org.gcube.common.core.contexts.GCUBEServiceContext.Status;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainer;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MyContainerTestRunner.class)
public class ServiceStartupTest {

	@Deployment
	static Gar serviceGar = SERVICE_GAR();
	
	public static Logger log = LoggerFactory.getLogger("test");


	@Inject
	static MyContainer container;

	@Test
	public void serviceStartsUp() {

		serviceIsReady();

		binderIsReadyInRIScopes();

		stateIsCreated();
	}
	

	// helper
	static void binderIsReadyInRIScopes() {
		//InstanceState binderResource = InstanceState..getContext().binder();
		//assertNotNull(binderResource);
		//Set<String> ghnScopes = ServiceContext.getContext().getInstance()
		//		.getScopes().keySet();
		//Set<String> binderScopes = new HashSet<String>(binderResource.getResourcePropertySet().getScope());
		//assertEquals(ghnScopes, binderScopes);
		//log.info("T-Binder is instantiated and in RI scopes");
	}

	public static void serviceIsReady() {
		assertTrue(ServiceContext.getContext().getStatus() == Status.READIED);
		log.info("service is ready");
	}
	
	public static void stateIsCreated() {
		try {
			//assertNotNull(ServiceContext.getContext().getResource());
		} catch (Exception e) {
			fail("state has not been successfully created");
		} 
		log.info("state is created");
	}

	public static Gar SERVICE_GAR() {
		return new Gar("resourcemanager").addInterfaces("../wsdl").addConfigurations("../config");
	}
}

