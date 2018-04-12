package org.gcube.vremanagement.resourcemanager.impl.resources.types;


import junit.framework.Assert;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactoryTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * JUnit test for {@link MultiKeysMap}
 *
 * @author Manuele Simi (CNR)
 *
 */
public class MultiKeysMapTest {

	private MultiKeysMap<String, String, ScopedResource> map;
	
	@Before
	public void create() {
		this.map = new MultiKeysMap<String, String, ScopedResource>();
	}
	
	@Test
	public void testPut() throws Exception {
		System.out.println("Adding resources test");
		System.out.println("Map before the method");
		printMap();
		ScopedResource ghn = ScopedResourceFactoryTest.newGHN("ID1", GCUBEScope.getScope("/gcube"));
		map.put(ghn.getId(), ghn.getType(), ghn);
		Assert.assertEquals(1, map.values().size());
		ScopedResource ghn2 = ScopedResourceFactoryTest.newGHN("ID2",  GCUBEScope.getScope("/gcube"));
		map.put(ghn2.getId(), ghn2.getType(), ghn2);
		Assert.assertEquals(2, map.values().size());
		ScopedResource ri = ScopedResourceFactoryTest.newRI("RI1",  GCUBEScope.getScope("/gcube"));
		map.put(ri.getId(), ri.getType(), ri);
		Assert.assertEquals(3, map.values().size());
		System.out.println("Map after the method");
		printMap();
	}
	
	@Test
	public void testRemoveValuesByPrimaryKey() throws Exception {
		testPut();
		System.out.println("testRemoveValuesByPrimaryKey");
		map.removeValuesByPrimaryKey("ID2");
		System.out.println("Map after testRemoveValuesByPrimaryKey method");
		printMap();
	}
	
	@Test
	public void testRemoveValue() throws Exception {
		testPut();
		ScopedResource ghn = ScopedResourceFactoryTest.newGHN("ID1", GCUBEScope.getScope("/gcube"));
		map.removeValue(ghn);
		printMap();
	}

	
	@Test
	public void testRemoveValuesBySecondaryKey() throws Exception {
		ScopedResource ri = ScopedResourceFactoryTest.newRI("RI1", GCUBEScope.getScope("/gcube"));
		map.removeValuesBySecondaryKey(ri.getType());
	}


	@Test
	public void testValues() {
		for (ScopedResource resource : map.values()) {
			System.out.println("Resource Value ID = " + resource.getId());
		}
	}

	@After
	public void testClean() {
		map.clean();
	}


	
	private void printMap() {
		System.out.println("Values by KEY1:");
		for (String key1 : map.primaryKeySet()) {
			for (ScopedResource resource : map.getValuesByPrimaryKey(key1))
				System.out.println( key1 + " ->" +resource.getId());
		}
		System.out.println("Values by KEY2:");
		for (String key2 : map.secondaryKeySet()) {
			for (ScopedResource resource : map.getValuesBySecondaryKey(key2))
				System.out.println( key2 + " ->" +resource.getId());
		}		
		System.out.println("Values:");
		for (ScopedResource resource : map.values()) {
			System.out.println(resource.getId());
		}
	}

}
