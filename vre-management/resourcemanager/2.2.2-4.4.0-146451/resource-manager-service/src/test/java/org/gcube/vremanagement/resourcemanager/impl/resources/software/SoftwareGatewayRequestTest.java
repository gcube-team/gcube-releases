/**
 * 
 */
package org.gcube.vremanagement.resourcemanager.impl.resources.software;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Set;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.ServiceStartupTest;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author manuele
 *
 */
public class SoftwareGatewayRequestTest extends ServiceStartupTest{

	static SoftwareGatewayRequest request;
	static GCUBEScope scope;
	static ScopedDeployedSoftware software;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 request= new SoftwareGatewayRequest();
		 scope = GCUBEScope.getScope("/gcube/devsec");
		 GCUBEPackage p = new GCUBEPackage();
		 p.setName("ResourceManager");
		 p.setClazz("VREManagement");
		 p.setVersion("1.0.0");
		 p.setPackageVersion("1.1.0");			
		 p.setPackageName("ResourceManager-service");
		 software = (ScopedDeployedSoftware) ScopedResourceFactory.newResource(scope, p.getID(), GCUBEService.TYPE);
		 software.setSourcePackage(p);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.software.SoftwareGatewayRequest#findInstances()}.
	 */
	@Test
	public final void testFindInstances() {
		try {
			Map<GCUBEScope, Set<EndpointReferenceType>> instances = request.findInstances();
			for (GCUBEScope scope : instances.keySet()) {
				for (EndpointReferenceType epr : instances.get(scope))
					System.out.println("Found instances @ " + epr.toString() +" in scope " + scope.toString());
			}
		} catch (Exception e) {
			fail("Can't find any SG instance");
			e.printStackTrace();
		}
	}
	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.software.SoftwareGatewayRequest#addSoftware(org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware)}.
	 */
	@Test
	public final void testAddSoftware() {
		request.addSoftware(software);
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.software.SoftwareGatewayRequest#send(org.gcube.common.core.scope.GCUBEScope)}.
	 */
	@Test
	public final void testSend() {
		try {
			request.send(scope);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to request the software");
		}	
	}

}
