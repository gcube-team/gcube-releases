/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.io.StringWriter;

import org.codehaus.jettison.json.JSONObject;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.impl.utils.Utility;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.facet.CPUFacet;
import org.gcube.informationsystem.model.relation.ConsistOf;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.model.resource.HostingNode;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class EntityManagementImplTest {

	private static Logger logger = LoggerFactory
			.getLogger(EntityManagementImplTest.class);

	protected EntityManagementImpl entityManagementImpl;

	public EntityManagementImplTest() {
		entityManagementImpl = new EntityManagementImpl();
	}

	@Test
	public void testCreateReadDeleteFacet() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");

		CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
		cpuFacetImpl.setClockSpeed("1 GHz");
		cpuFacetImpl.setModel("Opteron");
		cpuFacetImpl.setVendor("AMD");
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(cpuFacetImpl, stringWriter);

		String json = entityManagementImpl.createFacet(
				CPUFacet.class.getSimpleName(), stringWriter.toString());
		logger.debug("Created : {}", json);

		String uuid = Utility.getUUIDFromJSONString(json);

		String readJson = entityManagementImpl.readFacet(uuid);
		logger.debug("Read : {}", readJson);

		stringWriter = new StringWriter();
		Entities.marshal(cpuFacetImpl, stringWriter);
		cpuFacetImpl.setVendor("Luca");

		JsonNode jsonNode = Utility.getJSONNode(stringWriter.toString());
		((ObjectNode) jsonNode).remove("clockSpeed");
		((ObjectNode) jsonNode).put("My", "Test");

		stringWriter = new StringWriter();
		Entities.marshal(jsonNode, stringWriter);

		readJson = entityManagementImpl.updateFacet(uuid,
				stringWriter.toString());
		logger.debug("Updated : {}", readJson);

		readJson = entityManagementImpl.readFacet(uuid);
		logger.debug("Read Updated : {}", readJson);

		boolean deleted = entityManagementImpl.deleteFacet(uuid);
		if (!deleted) {
			throw new Exception("Facet Not Deleted");
		}

	}

	@Test
	public void testDifferentScopes() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");

		CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
		cpuFacetImpl.setClockSpeed("1 GHz");
		cpuFacetImpl.setModel("Opteron");
		cpuFacetImpl.setVendor("AMD");
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(cpuFacetImpl, stringWriter);

		String json = entityManagementImpl.createFacet(
				CPUFacet.class.getSimpleName(), stringWriter.toString());
		logger.debug("Created : {}", json);

		JSONObject jsonObject = new JSONObject(json);
		JSONObject header = jsonObject.getJSONObject(Entity.HEADER_PROPERTY);
		String uuid = header.getString(Header.UUID_PROPERTY);

		String readJson = entityManagementImpl.readFacet(uuid);
		logger.debug("Read : {}", readJson);

		/*----------*/

		logger.debug("Setting /gcube/devNext scope");
		ScopeProvider.instance.set("/gcube/devNext");
		try {
			readJson = entityManagementImpl.readFacet(uuid);
			logger.debug("You should not be able to read Feact with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to read Feact with UUID " + uuid);
		} catch (FacetNotFoundException e) {
			logger.debug("Good the facet created in /gcube/devsec is not visible in /gcube/devNext");
		}

		jsonObject = new JSONObject(stringWriter.toString());
		jsonObject.remove("clockSpeed");
		jsonObject.put("My", "Test");

		try {
			readJson = entityManagementImpl.updateFacet(uuid,
					jsonObject.toString());
			logger.debug("You should not be able to update Facet with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to read Facet with UUID " + uuid);
		} catch (FacetNotFoundException e) {
			logger.debug("Good the Facet created in /gcube/devsec cannot be updated in /gcube/devNext");
		}

		try {
			entityManagementImpl.deleteFacet(uuid);
			logger.debug("You should not be able to delete Facet with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to delete Facet with UUID " + uuid);
		} catch (FacetNotFoundException e) {
			logger.debug("Good the Facet created in /gcube/devsec cannot be deleted in /gcube/devNext");
		}

		/*----------*/

		logger.debug("Setting back /gcube/devsec scope");
		ScopeProvider.instance.set("/gcube/devsec");

		readJson = entityManagementImpl
				.updateFacet(uuid, jsonObject.toString());
		logger.debug("Updated : {}", readJson);

		readJson = entityManagementImpl.readFacet(uuid);
		logger.debug("Read Updated : {}", readJson);

		boolean deleted = entityManagementImpl.deleteFacet(uuid);
		if (!deleted) {
			throw new Exception("Facet Not Deleted");
		}

	}

	/*
	 * @Test public void testCreateFacet() throws Exception{
	 * ScopeProvider.instance.set("/gcube/devsec");
	 * 
	 * CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
	 * cpuFacetImpl.setClockSpeed("1 GHz");
	 * cpuFacetImpl.setModelName("Opteron"); cpuFacetImpl.setVendor("AMD");
	 * StringWriter stringWriter = new StringWriter();
	 * Entities.marshal(cpuFacetImpl, stringWriter);
	 * 
	 * String json =
	 * entityManagementImpl.createFacet(CPUFacet.class.getSimpleName(),
	 * stringWriter.toString()); logger.debug("Created : {}", json); }
	 */

	/*
	 * @Test public void testReadFacet() throws Exception{
	 * ScopeProvider.instance.set("/gcube/devsec"); String readJson =
	 * entityManagementImpl.readFacet(""); logger.debug("Read : {}", readJson);
	 * }
	 */

	/*
	 * @Test public void testDeleteFacet() throws Exception{
	 * ScopeProvider.instance.set("/gcube/devsec"); boolean deleted =
	 * entityManagementImpl.deleteFacet(""); if(!deleted){ throw new
	 * Exception("Facet Not Deleted"); } }
	 */

	@Test
	public void testCreateResourceAndFacet() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");

		String json = entityManagementImpl.createResource(
				HostingNode.class.getSimpleName(), "{}");
		String resourceUUID = Utility.getUUIDFromJSONString(json);

		CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
		cpuFacetImpl.setClockSpeed("1 GHz");
		cpuFacetImpl.setModel("Opteron");
		cpuFacetImpl.setVendor("AMD");
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(cpuFacetImpl, stringWriter);

		json = entityManagementImpl.createFacet(CPUFacet.class.getSimpleName(),
				stringWriter.toString());
		logger.debug("Created : {}", json);
		String facetUUID = Utility.getUUIDFromJSONString(json);

		json = entityManagementImpl.attachFacet(resourceUUID, facetUUID,
				ConsistOf.class.getSimpleName(), null);
		logger.debug("Facet attached : {}", json);

		String consistOfUUID = Utility.getUUIDFromJSONString(json);

		boolean detached = entityManagementImpl.detachFacet(consistOfUUID);

		if (detached) {
			logger.trace("{} {} with uuid {} removed successfully",
					ConsistOf.NAME, Relation.NAME, consistOfUUID);
		} else {
			String error = String.format("Unable to remove %s %s with uuid %s",
					ConsistOf.NAME, Relation.NAME, consistOfUUID);
			logger.error(error);
			throw new Exception(error);
		}
		
		entityManagementImpl.deleteResource(resourceUUID);

		entityManagementImpl.deleteFacet(facetUUID);
		
	}

}
