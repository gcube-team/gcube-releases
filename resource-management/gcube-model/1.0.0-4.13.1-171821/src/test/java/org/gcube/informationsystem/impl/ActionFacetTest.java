package org.gcube.informationsystem.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.resourcemanagement.model.impl.entity.facet.ActionFacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet.TYPE;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Test cases for {@link ActionFacet}
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class ActionFacetTest {

	@Test
	public void serialize() {
		ActionFacet facet = new ActionFacetImpl();
		facet.setName("FirstAction");
		facet.setType(TYPE.ANSIBLE);
		facet.setSource("git@myrepo:playbook.yml");
		facet.setCommand("ansible-pull");
		facet.setOptions("playbook.yml");
		String marshalled = "";
		try {
			marshalled = ISMapper.marshal(facet);
		} catch (JsonProcessingException e) {
			assertFalse("Failed to marshal the action.", false);
		}
		assertTrue("Unexpected content", marshalled.contains("ansible-pull"));
	}

	@Test
	public void deserialize() {
		String marshalled = "{\"@class\":\"ActionFacet\",\"header\":null,\"name\":\"FirstAction\","
				+ "\"type\":\"ANSIBLE\",\"source\":\"git@myrepo:playbook.yml\","
				+ "\"options\":\"playbook.yml\",\"command\":\"ansible-pull\"}";
		ActionFacet facet = null;
		try {
			facet = ISMapper.unmarshal(ActionFacetImpl.class, marshalled);
		} catch (Exception e) {
			assertFalse("Failed to unmarshal the context.", false);
		}
		assertTrue("Unexpected content for command", facet.getCommand().compareTo("ansible-pull") == 0);
		assertTrue("Unexpected content for option", facet.getOptions().compareTo("playbook.yml") == 0);
		assertTrue("Unexpected content for TYPE", facet.getType() == TYPE.ANSIBLE);
		assertTrue("Unexpected content for TYPE", facet.getSource().compareTo("git@myrepo:playbook.yml") == 0);

	}
}
