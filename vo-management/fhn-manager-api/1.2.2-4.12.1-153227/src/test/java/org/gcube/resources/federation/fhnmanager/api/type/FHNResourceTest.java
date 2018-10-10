package org.gcube.resources.federation.fhnmanager.api.type;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FHNResourceTest {

	@Test
	public void testId() {
		FHNResource r = new FHNResource("someid");
		assertEquals("message", r.id, "someid");
	}

	@Test
	public void testGetId() {
		FHNResource r = new FHNResource();
		String id = "someid";
		r.setId(id);
		assertEquals("message", r.getId(), id);
	}

}
