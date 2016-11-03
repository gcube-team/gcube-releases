package org.gcube.vremanagement.resourcemanager.impl.resources.types;

import java.util.Observable;
import java.util.Observer;


import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactoryTest;
import org.gcube.vremanagement.resourcemanager.impl.resources.types.MultiKeysMap;


import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for {@link MultiKeysMap}
 * @author manuele simi (CNR)
 *
 */
public class MultiKeysTester {

	MultiKeysMap<String, String, ScopedResource> map;
	
	@Before	
	public void createMap() {
		this.map = new MultiKeysMap<String, String, ScopedResource>();				
	}
	
	@Test
	public void addObserver() {
		this.createMap();
		//map.addObserver(new CollectionWatcher());
	}
	
	@Test
	public void populateMap() throws Exception {
		ScopedResource ghn = ScopedResourceFactoryTest.newGHN("ID1",GCUBEScope.getScope("/gcube"));
		map.put(ghn.getId(), ghn.getType(), ghn);
		ScopedResource ghn2 = ScopedResourceFactoryTest.newGHN("ID2", GCUBEScope.getScope("/gcube"));
		map.put(ghn2.getId(), ghn2.getType(), ghn2);
		ScopedResource ri = ScopedResourceFactoryTest.newRI("RI1",  GCUBEScope.getScope("/gcube"));
		map.put(ri.getId(), ri.getType(), ri);
		printMap(map);		
		map.removeValuesBySecondaryKey(ri.getType());
		printMap(map);
		map.removeValue(ghn);
		printMap(map);
	}
	
	private void printMap(MultiKeysMap<String, String, ScopedResource> map) {
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
	
	 class CollectionWatcher implements Observer {

		public void update(Observable o, Object arg) {
			System.out.println("Notified");
			
		}
		
	}
}
