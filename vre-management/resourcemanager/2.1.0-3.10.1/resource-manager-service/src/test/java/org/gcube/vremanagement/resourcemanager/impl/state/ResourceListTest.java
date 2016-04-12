package org.gcube.vremanagement.resourcemanager.impl.state;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactoryTest;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;

import com.thoughtworks.xstream.XStream;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;



/**
 * 
 * JUnit test class for {@link ScopeState}
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ResourceListTest {

	private static final String SCOPE = "/gcube/devsec";
	
	private static final String NAME = "test-list";
	
	private static final String DESCRIPTION = "This is a test for scoped resources";
	
	private ScopeState list;
	
	@Before
	public void initializeList() {
		list = new ScopeState();
		list.initialize(GCUBEScope.getScope(SCOPE), NAME, false, DESCRIPTION);
	}
	

	@Test
	public void testAddResource()throws Exception  {
		Set<ScopedResource> resourceToAdd =  new HashSet<ScopedResource>();
		ScopedResource ghn = ScopedResourceFactoryTest.newGHN("ID1",GCUBEScope.getScope("/gcube"));
		resourceToAdd.add(ghn);
		list.addResources(resourceToAdd);
		Assert.assertEquals(1, list.getResourcesByType(ghn.getType()).size());
		ScopedResource ghn2 = ScopedResourceFactoryTest.newGHN("ID2", GCUBEScope.getScope("/gcube"));
		resourceToAdd.add(ghn2);
		list.addResources(resourceToAdd);
		Assert.assertEquals(2, list.getResourcesByType(ghn2.getType()).size());
		ScopedResource ri = ScopedResourceFactoryTest.newRI("RI1",  GCUBEScope.getScope("/gcube"));
		ri.setHostedON("node1.p:8080");
		resourceToAdd.add(ri);
		list.addResources(resourceToAdd);
		Assert.assertEquals(1, list.getResourcesByType(ri.getType()).size());
		Assert.assertEquals(2, list.getResourcesByType(ghn.getType()).size());

	}

	public void testGetResourcesByType() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testChangeDescription() {
		list.changeDescription("This is a new description");
	}

	public void testRemoveAllResourcesByType() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveResource() throws Exception {
		Set<ScopedResource> resourceToRemove =  new HashSet<ScopedResource>();
		ScopedResource ghn = ScopedResourceFactoryTest.newGHN("ID1",GCUBEScope.getScope("/gcube"));
		resourceToRemove.add(ghn);					
		list.removeResources(resourceToRemove);
	}
	
	@Test
	public void testCleanList() {
		
	}
	
	
	public void testAddObserver() {
		fail("Not yet implemented");
	}

	public void testDeleteObserver() {
		fail("Not yet implemented");
	
	}
	@Test
	public void print() throws Exception {
		XStream stream = new XStream();
		stream.processAnnotations(ScopeState.class);		
		System.out.println(stream.toXML(list));

	}
}
