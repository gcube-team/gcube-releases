/**
 * 
 */
package org.acme;

import static org.acme.TestUtils.*;
import static org.acme.sample.ServiceContext.*;
import static org.acme.sample.Utils.*;
import static org.gcube.common.core.contexts.GCUBEServiceContext.Status.*;
import static org.junit.Assert.*;

//import javax.inject.Named;

import org.acme.sample.Stateless;
import org.acme.sample.stubs.StatelessPortType;
import org.acme.sample.stubs.service.StatelessServiceAddressingLocator;
import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.gcube.common.mycontainer.Deployment;
//import org.gcube.common.mycontainer.Gar;
//import org.gcube.common.mycontainer.MyContainerTestRunner;
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
	@Deployment
	static Gar serviceGar=SAMPLE_GAR();

	@Named(STATELESS_NAME)
	static Stateless stateless;

	@Named(STATELESS_NAME)
	static EndpointReferenceType statelesEpr;
	
	@BeforeClass
	public static void serviceHasStarted() {
		assertTrue(getContext().getStatus()==READIED);
	}
	
	@Test
	public void smokeTest() throws Exception {
		
		assertNotNull(stateless.about("joe"));
		
	}
	
	@Test
	public void smokeClientTest() throws Exception {
		
		StatelessPortType statelessPT = 
				new StatelessServiceAddressingLocator().getStatelessPortTypePort(statelesEpr);
		
		assertNotNull(statelessPT.about("joe"));
		
	}
*/
}
