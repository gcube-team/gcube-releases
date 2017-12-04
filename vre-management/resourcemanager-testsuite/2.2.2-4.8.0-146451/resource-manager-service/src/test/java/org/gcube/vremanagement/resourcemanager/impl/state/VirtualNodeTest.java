package org.gcube.vremanagement.resourcemanager.impl.state;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.vremanagement.deployer.stubs.common.PackageInfo;
import org.gcube.vremanagement.resourcemanager.impl.ServiceStartupTest;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tester for {@link VirtualNode}s
 * @author manuele simi (CNR)
 *
 */
public class VirtualNodeTest extends ServiceStartupTest{

	static GCUBEScope scope;
	static VirtualNode nodeByID;
	static VirtualNode nodeByName;
	static String id;
	static Set<PackageInfo> packages = new HashSet<PackageInfo>();
	static String name = "node2.d.d4science.research-infrastructures.eu:8080";

	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			scope = GCUBEScope.getScope("/gcube/devsec");
			ServiceMap map = new ServiceMap();
			map.load(new InputStreamReader(VirtualNodeTest.class.getResourceAsStream("/ServiceMap_devsec.xml")));
			scope.setServiceMap(map);
			nodeByName = VirtualNode.fromName(name, scope);
			id = nodeByName.getID();
			nodeByID = VirtualNode.fromID(id, scope);
			
			PackageInfo p = new PackageInfo();
			p.setServiceName("EnvironmentLibrariesSet");
			p.setServiceClass("Execution");
			p.setServiceVersion("1.0.0");
			p.setVersion("1.0.0-SNAPSHOT");			
			p.setName("ReportingFrameworkLibrary"); //packageName
			packages.add(p);
			
		} catch (MalformedScopeExpressionException e) {
			fail("invalid scope");
		} catch (NoGHNFoundException e) {
			e.printStackTrace();
			fail("can't find the node");
		} catch (Exception e) {
			e.printStackTrace();
			fail("general exception");
		}
	
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testSetCallbackID() {
		nodeByID.setCallbackID("testCallbackID");
	}

	@Test
	public final void testGetID() {
		System.out.println("detected ID for VN1: " + nodeByID.getID());
		System.out.println("detected ID for VN2: " + nodeByName.getID());
		assertEquals(id, nodeByID.getID());
		assertEquals(id, nodeByName.getID());
	}

	@Test
	public final void testGetName() {
		System.out.println("detected name for VN1: " + nodeByID.getName());
		System.out.println("detected name for VN2: " + nodeByName.getName());
		assertEquals(name, nodeByID.getName());
		assertEquals(name, nodeByName.getName());
	}

	@Test
	public final void testSetPackagesToAdd() {
		nodeByID.setPackagesToAdd(packages);
	}
	
	@Test
	public final void testDeploy() {
		try {
			System.out.println("deploying to: " + nodeByID.getName());
			ServiceContext.getContext().setScope(scope);
			nodeByID.deploy();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public final void testSetPackagesToRemove() {
		nodeByID.setPackagesToRemove(packages);
	}

	@Test
	public final void testSetPackagesToUpgrade() {
		nodeByID.setPackagesToUpgrade(packages);
	}

	@Test
	public final void testUndeploy() {
		try {
			System.out.println("undeploying from: " + nodeByID.getName());
			ServiceContext.getContext().setScope(scope);
			//nodeByID.undeploy();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public final void testUpgrade() {
	}
	

}
