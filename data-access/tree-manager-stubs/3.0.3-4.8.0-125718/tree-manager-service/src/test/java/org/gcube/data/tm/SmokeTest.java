package org.gcube.data.tm;

import static junit.framework.Assert.*;
import static org.gcube.data.tm.Constants.*;
import static org.gcube.data.tm.TestUtils.*;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainer;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.data.tm.context.ServiceContext;
import org.gcube.data.tm.context.TBinderContext;
import org.gcube.data.tm.state.TBinderResource;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class)
public class SmokeTest {

	@Deployment
	static Gar serviceGar = TestUtils.gar();

	@Inject
	static MyContainer container;
	

	@Test
	public void serviceStartsUp() {

		serviceIsReady();

		gRSIsInitialized();

		binderIsReadyInRIScopes();
		
		binderIsAccessible();
	}

	// helper
	static void gRSIsInitialized() {
		assertTrue(TCPConnectionManager.IsInitialized());
	}

	// helper
	static void binderIsReadyInRIScopes() {
		TBinderResource binderResource = TBinderContext.getContext().binder();
		assertNotNull(binderResource);
		Set<String> ghnScopes = ServiceContext.getContext().getInstance().getScopes().keySet();
		Set<String> binderScopes = new HashSet<String>(binderResource.getResourcePropertySet().getScope());
		assertEquals(ghnScopes, binderScopes);
	}
	
	void binderIsAccessible() {
		
		EndpointReferenceType address= container.reference(TBINDER_NAME, NS, SINGLETON_BINDER_ID);
		String properties = queryProperties(address);
		log.info(properties);
	}

}
