package org.gcube.vremanagement.resourcemanager.impl.state;

import static org.junit.Assert.*;

import java.io.InputStreamReader;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.ServiceMap;
import org.gcube.common.core.scope.GCUBEScope.MalformedScopeExpressionException;
import org.gcube.vremanagement.resourcemanager.impl.state.VirtualNode.NoGHNFoundException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tester for {@link VirtualNodeList}
 * @author manuele simi (CNR)
 *
 */
public class VirtualNodeListTest {
	
	static VirtualNodeList list;
	
	static GCUBEScope scope;
	
	static String name = "node2.d.d4science.research-infrastructures.eu:8080";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		scope = GCUBEScope.getScope("/gcube/devsec");
		list = new VirtualNodeList(scope);
		RawScopeState rawState = new RawScopeState();
		rawState.initialize(scope);
		list.loadFromState(rawState);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testVirtualNodeList() {
	}

	@Test
	public final void testLoadFromState() {
	}

	@Test
	public final void testStoreToState() {
	}

	@Test
	public final void testAddNode() {
		try {
			GCUBEScope scope = GCUBEScope.getScope("/gcube/devsec");
			ServiceMap map = new ServiceMap();
			map.load(new InputStreamReader(VirtualNodeListTest.class.getResourceAsStream("/ServiceMap_devsec.xml")));
			scope.setServiceMap(map);
			VirtualNode node = VirtualNode.fromName("node2.d.d4science.research-infrastructures.eu:8080", scope);
			list.addNode(node);
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

	@Test
	public final void testGetNode() {
		try {
			assertNotNull(list.getNode(name));
			System.out.println("Node in the list: " + list.getNode("node2.d.d4science.research-infrastructures.eu:8080").getID());
		} catch (NoGHNFoundException e) {
			e.printStackTrace();
			fail("can't find the node");
		}
	}

}
