package org.gcube.smartgears.utils.sweeper.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.smartgears.utils.sweeper.Sweeper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SweeperTest {
	
	Sweeper sw = null;
	
	@Before
	public void setUp(){
		try {
			sw = new Sweeper();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void getGHNProfile() {
		
		HostingNode node = null;
	
		try {
			node = sw.getGHNProfile();
		} catch (FileNotFoundException | JAXBException e) {
			e.printStackTrace();
		}
		
		Assert.assertNotNull(node);
		System.out.println(node.id());
		
	}
	
	@Test
	public void getRunningInstanceProfiles() {
		ArrayList<GCoreEndpoint>  list = new ArrayList<GCoreEndpoint> ();
		try {
			list =sw.getRunningInstanceProfiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		System.out.println("Endooint size :"+ list.size());
		for (GCoreEndpoint end : list){
			Assert.assertNotNull(end);
			System.out.println(end.id());
		}	
		
	}

	@Test
	public void removeGHNProfile() {
		
		try {
			sw.cleanGHNProfile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
