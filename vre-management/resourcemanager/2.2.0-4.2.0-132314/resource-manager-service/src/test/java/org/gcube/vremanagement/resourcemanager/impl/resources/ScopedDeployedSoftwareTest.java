/**
 * 
 */
package org.gcube.vremanagement.resourcemanager.impl.resources;

import static org.junit.Assert.*;

import java.io.InputStreamReader;

import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.vremanagement.resourcemanager.impl.ServiceStartupTest;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource.STATUS;
import org.gcube.vremanagement.resourcemanager.impl.resources.software.GCUBEPackage;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNodeList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Manuele Simi (CNR)
 *
 */
public class ScopedDeployedSoftwareTest extends ServiceStartupTest {

	static GCUBEScope scope;
	static ScopedDeployedSoftware sw;
	static GCUBEPackage sourcePackage;
	static VirtualNode targetNode;
	static final String targetNodeName= "node2.d.d4science.research-infrastructures.eu:8080";

	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//static initialisations
		scope = GCUBEScope.getScope("/gcube/devsec");
		ServiceMap map = new ServiceMap();
		map.load(new InputStreamReader(ScopedDeployedSoftwareTest.class.getResourceAsStream("/ServiceMap_devsec.xml")));
		scope.setServiceMap(map);
		sourcePackage = new GCUBEPackage();
		sourcePackage.setName("UserProfileAccess");
		sourcePackage.setClazz("Personalisation");
		sourcePackage.setVersion("1.0.0");
		sourcePackage.setPackageVersion("2.1.0-SNAPSHOT");	
		sourcePackage.setPackageName("UserProfileAccess-service");
		sw = (ScopedDeployedSoftware) ScopedResourceFactory.newResource(scope, sourcePackage.getID(), GCUBEService.TYPE);
		targetNode = new VirtualNodeList(scope).getNode(targetNodeName);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}


	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#setSourcePackage(org.gcube.vremanagement.resourcemanager.impl.resources.software.GCUBEPackage)}.
	 */
	@Test
	public final void testSetSourcePackage() {
		sw.setSourcePackage(sourcePackage);
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getSourcePackage()}.
	 */
	@Test
	public final void testGetSourcePackage() {
		assertNotNull(sw.getSourcePackage());
	}
	
	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#find()}.
	 */
	@Test
	public final void testFind() {
		try {
			sw.find();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#addToScope()}.
	 */
	@Test
	public final void testAddToScope() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#removeFromScope()}.
	 */
	@Test
	public final void testRemoveFromScope() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getMaxFindAttempts()}.
	 */
	@Test
	public final void testGetMaxFindAttempts() {
		System.out.println("Max attempts: " + sw.getMaxFindAttempts());
	}


	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getResolvedDependencies(java.lang.String)}.
	 */
	@Test
	public final void testGetResolvedDependencies() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getLastResolvedDependencies()}.
	 */
	@Test
	public final void testGetLastResolvedDependencies() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#setResolvedDependencies(java.util.List)}.
	 */
	@Test
	public final void testSetResolvedDependencies() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getMissingDependencies(java.lang.String)}.
	 */
	@Test
	public final void testGetMissingDependencies() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getLastMissingDependencies()}.
	 */
	@Test
	public final void testGetLastMissingDependencies() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#setMissingDependencies(java.util.List)}.
	 */
	@Test
	public final void testSetMissingDependencies() {
		fail("Not yet implemented");
	}
	

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#scheduleDeploy(org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode)}.
	 */
	@Test
	public final void testScheduleDeploy() {
		sw.setStatus(STATUS.ADDREQUESTED);
		sw.scheduleDeploy(targetNode);
		try {
			//targetNode.deploy();
		} catch (Exception e) {
			e.printStackTrace();
			fail("fails to depoy");
		}
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#scheduleUndeploy(org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode)}.
	 */
	@Test
	public final void testScheduleUndeploy() {
		try {
			sw.setStatus(STATUS.REMOVEREQUESTED);
			sw.scheduleUndeploy(targetNode);
			//targetNode.undeploy();
		} catch (NoGHNFoundException e) {
			e.printStackTrace();
			fail("no ghn found");
		} catch (Exception e) {
			e.printStackTrace();
			fail("fails to undeploy");
		}
	}


	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getTargetNodeID()}.
	 */
	@Test
	public final void testGetTargetNodeID() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#setRI(java.lang.String)}.
	 */
	@Test
	public final void testSetRI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getRI()}.
	 */
	@Test
	public final void testGetRI() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#setCallbackID(java.lang.String)}.
	 */
	@Test
	public final void testSetCallbackID() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedDeployedSoftware#getTargetNodeName()}.
	 */
	@Test
	public final void testGetTargetNodeName() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource#doAction(org.gcube.vremanagement.resourcemanager.impl.operators.Operator.ACTION)}.
	 */
	@Test
	public final void testDoAction() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource#isSuccess()}.
	 */
	@Test
	public final void testIsSuccess() {
		fail("Not yet implemented");
	}

}
