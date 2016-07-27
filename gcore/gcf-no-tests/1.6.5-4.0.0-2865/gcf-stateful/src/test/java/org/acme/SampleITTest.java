/**
 * 
 */
package org.acme;

import static org.acme.TestUtils.*;
import static org.acme.sample.ServiceContext.*;
import static org.acme.sample.Utils.*;
import static org.gcube.common.core.contexts.GCUBERemotePortTypeContext.*;
import static org.gcube.common.core.contexts.GCUBEServiceContext.Status.*;
import static org.junit.Assert.*;

//import javax.inject.Named;

import org.acme.sample.Factory;
import org.acme.sample.stubs.FactoryPortType;
import org.acme.sample.stubs.StatefulPortType;
import org.acme.sample.stubs.service.FactoryServiceAddressingLocator;
import org.acme.sample.stubs.service.StatefulServiceAddressingLocator;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.types.VOID;
/*
import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainerTestRunner;
*/
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fabio Simeoni
 *
 */
//@RunWith(MyContainerTestRunner.class)
public class SampleITTest {

	/*
	Logger logger = Logger.getLogger("test");
	
	final static GCUBEScope SCOPE = GCUBEScope.getScope("/gcube/devsec");
	
	@Deployment
	static Gar serviceGar=SAMPLE_GAR();

	@Named(FACTORY_NAME)
	static Factory factory;
	
	@Named(FACTORY_NAME)
	static EndpointReferenceType factoryEpr;
	
	@BeforeClass
	public static void serviceHasStarted() {
		assertTrue(getContext().getStatus()==READIED);
	}
	
	@Test
	public void createResourceTest() throws Exception {

		ScopeProvider.instance.set(SCOPE.toString());
		
		EndpointReferenceType resourceEpr = factory.create("joe");
		logger.info("resource epr:"+resourceEpr);
		assertNotNull(resourceEpr);
		
	}
	
	@Test
	public void smokeClientTest() throws Exception {
		
		FactoryPortType factoryPT = new FactoryServiceAddressingLocator().getFactoryPortTypePort(factoryEpr);
		factoryPT = getProxy(factoryPT,SCOPE);
		
		EndpointReferenceType resourceEpr = factoryPT.create("joe");
		
		StatefulPortType statefulPT = 
				new StatefulServiceAddressingLocator().getStatefulPortTypePort(resourceEpr);
		statefulPT = getProxy(statefulPT,SCOPE);
		
		assertNotNull(statefulPT.visit(new VOID()));
		
	}
	*/
}
