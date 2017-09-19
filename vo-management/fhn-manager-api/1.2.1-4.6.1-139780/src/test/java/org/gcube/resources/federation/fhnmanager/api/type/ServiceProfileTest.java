package org.gcube.resources.federation.fhnmanager.api.type;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class ServiceProfileTest {

	@Test
	public void testGetDescription() {
		String descr = "a description";
		ServiceProfile sp = new ServiceProfile();
		sp.setDescription(descr);
		assertEquals(descr, sp.getDescription());
	}

	@Test
	public void testGetDeployedSoftware() {

		ResourceReference<Software> s1 = new ResourceReference<Software>(
				new Software("id1"));
		ResourceReference<Software> s2 = new ResourceReference<Software>(
				new Software("id2"));

		Set<ResourceReference<Software>> ds = new HashSet<ResourceReference<Software>>();
		ds.add(s1);
		ds.add(s2);

		ServiceProfile sp = new ServiceProfile();
		sp.setDeployedSoftware(ds);

		assertEquals(ds, sp.getDeployedSoftware());
	}

}
