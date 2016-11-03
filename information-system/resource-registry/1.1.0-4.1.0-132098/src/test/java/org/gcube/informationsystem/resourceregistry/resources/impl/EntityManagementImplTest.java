/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.io.StringWriter;
import java.util.UUID;

import org.codehaus.jettison.json.JSONObject;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.ContactFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.impl.relation.isrelatedto.HostsImpl;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.impl.utils.Utility;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CPUFacet;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.entity.facet.NetworkingFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.model.relation.isrelatedto.Hosts;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		ScopeProvider.instance.set("/gcube/devNext");

		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
				
		
		String cpuFacetJson = entityManagementImpl.createFacet(CPUFacet.class.getSimpleName(), Entities.marshal(cpuFacet));
		CPUFacet createdCpuFacet = Entities.unmarshal(CPUFacet.class, cpuFacetJson);
		logger.debug("Created:\nRaw Json : {}\nUnmarshalled : {}", cpuFacetJson, createdCpuFacet);
		
		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(createdCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(cpuFacet.getModel().compareTo(createdCpuFacet.getModel())==0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(createdCpuFacet.getVendor())==0);
		
		UUID uuid = createdCpuFacet.getHeader().getUUID();
		
		
		String readJson = entityManagementImpl.readFacet(uuid.toString());
		CPUFacet readCpuFacet = Entities.unmarshal(CPUFacet.class, readJson);
		logger.debug("Read:\nRaw Json : {}\nUnmarshalled : {}", readJson, readCpuFacet);
		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(readCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(cpuFacet.getModel().compareTo(readCpuFacet.getModel())==0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(readCpuFacet.getVendor())==0);
		Assert.assertTrue(uuid.compareTo(readCpuFacet.getHeader().getUUID())==0);
		
		String newVendor = "Intel";
		String newClockSpeed = "2 GHz";
		readCpuFacet.setVendor(newVendor);
		readCpuFacet.setClockSpeed(newClockSpeed);
		
		String additionPropertyKey = "My";
		String additionPropertyValue = "Test";
		readCpuFacet.setAdditionalProperty(additionPropertyKey, additionPropertyValue);

		
		String updatedJson = entityManagementImpl.updateFacet(uuid.toString(),Entities.marshal(readCpuFacet));
		CPUFacet updatedCpuFacet = Entities.unmarshal(CPUFacet.class, updatedJson);
		logger.debug("Updated:\nRaw Json : {}\nUnmarshalled : {}", updatedJson, updatedCpuFacet);
		Assert.assertTrue(readCpuFacet.getClockSpeed().compareTo(updatedCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(readCpuFacet.getModel().compareTo(updatedCpuFacet.getModel())==0);
		Assert.assertTrue(readCpuFacet.getVendor().compareTo(updatedCpuFacet.getVendor())==0);
		Assert.assertTrue(((String) updatedCpuFacet.getAdditionalProperty(additionPropertyKey)).compareTo((String) readCpuFacet.getAdditionalProperty(additionPropertyKey))==0);
		Assert.assertTrue(uuid.compareTo(updatedCpuFacet.getHeader().getUUID())==0);
		
		
		String readUpdatedJson = entityManagementImpl.readFacet(uuid.toString());
		CPUFacet readUpdatedCpuFacet = Entities.unmarshal(CPUFacet.class, readUpdatedJson);
		logger.debug("Read Updated:\nRaw Json : {}\nUnmarshalled : {}", readUpdatedJson, readUpdatedCpuFacet);
		Assert.assertTrue(updatedCpuFacet.getClockSpeed().compareTo(readUpdatedCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(updatedCpuFacet.getModel().compareTo(readUpdatedCpuFacet.getModel())==0);
		Assert.assertTrue(updatedCpuFacet.getVendor().compareTo(readUpdatedCpuFacet.getVendor())==0);
		Assert.assertTrue(((String)updatedCpuFacet.getAdditionalProperty(additionPropertyKey)).compareTo((String) readUpdatedCpuFacet.getAdditionalProperty(additionPropertyKey))==0);
		Assert.assertTrue(uuid.compareTo(updatedCpuFacet.getHeader().getUUID())==0);
		
		boolean deleted = entityManagementImpl.deleteFacet(uuid.toString());
		Assert.assertTrue(deleted);
		
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
			logger.debug("You should not be able to read Facet with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to read Facet with UUID " + uuid);
		} catch (FacetNotFoundException e) {
			logger.debug("Good the facet created in /gcube/devsec is not visible in /gcube/devNext");
		}

		jsonObject = new JSONObject(stringWriter.toString());
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

	@Test
	public void testCreateContactFacet() throws Exception {
		ScopeProvider.instance.set("/gcube/devNext");

		ContactFacet contactFacet = new ContactFacetImpl();
		contactFacet.setName("Luca");
		contactFacet.setSurname("Frosini");
		contactFacet.setEMail("info@lucafrosini.com");
		
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(contactFacet, stringWriter);
		
		logger.debug("Going to create : {}", stringWriter.toString());

		String json = entityManagementImpl.createFacet(
				ContactFacet.class.getSimpleName(), stringWriter.toString());
		logger.debug("Created : {}", json);
	}
	
	
	@Test
	public void testCreateCPUFacet() throws Exception {
		ScopeProvider.instance.set("/gcube/devNext");

		CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
		cpuFacetImpl.setClockSpeed("1 GHz");
		cpuFacetImpl.setModel("Opteron");
		cpuFacetImpl.setVendor("AMD");
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(cpuFacetImpl, stringWriter);

		String json = entityManagementImpl.createFacet(
				CPUFacet.class.getSimpleName(), stringWriter.toString());
		logger.debug("Created : {}", json);
	}
	
	
	@Test
	public void testCreateHostingNode() throws Exception {
		ScopeProvider.instance.set("/gcube/devNext");

		EService eService = new EServiceImpl();
		
		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		
		IsIdentifiedBy<Resource, Facet> isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(eService, softwareFacet, null);
		eService.addFacet(isIdentifiedBy);
		
		String json = entityManagementImpl.createResource(EService.NAME, Entities.marshal(eService));
		logger.debug("Created : {}", json);
		eService = Entities.unmarshal(EService.class, json);
		logger.debug("Unmarshalled {} {}", EService.NAME, eService);
		
		
		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");
		
		json = entityManagementImpl.createFacet(NetworkingFacet.NAME, Entities.marshal(networkingFacet));
		logger.debug("Created : {}", json);
		networkingFacet = Entities.unmarshal(NetworkingFacet.class, json);
		logger.debug("Unmarshalled {} {}", NetworkingFacet.NAME, networkingFacet);
		
		
		HostingNode hostingNode = new HostingNodeImpl();
		
		CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
		cpuFacetImpl.setClockSpeed("1 GHz");
		cpuFacetImpl.setModel("Opteron");
		cpuFacetImpl.setVendor("AMD");
		
		hostingNode.addFacet(cpuFacetImpl);
		
		isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(hostingNode, networkingFacet, null);
		hostingNode.attachFacet(isIdentifiedBy);
		
		Hosts<HostingNode, EService> hosts = new HostsImpl<HostingNode, EService>(hostingNode, eService, null);
		
		hostingNode.attachResource(hosts);
		
		json = entityManagementImpl.createResource(HostingNode.NAME, Entities.marshal(hostingNode));
		logger.debug("Created : {}", json);
		
		
	}
	

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
				ConsistsOf.class.getSimpleName(), null);
		logger.debug("Facet attached : {}", json);

		String consistOfUUID = Utility.getUUIDFromJSONString(json);

		boolean detached = entityManagementImpl.detachFacet(consistOfUUID);

		if (detached) {
			logger.trace("{} {} with uuid {} removed successfully",
					ConsistsOf.NAME, Relation.NAME, consistOfUUID);
		} else {
			String error = String.format("Unable to remove %s %s with uuid %s",
					ConsistsOf.NAME, Relation.NAME, consistOfUUID);
			logger.error(error);
			throw new Exception(error);
		}
		
		entityManagementImpl.deleteResource(resourceUUID);

		entityManagementImpl.deleteFacet(facetUUID);
		
	}

	@Test
	public void testCreateResourceAndFacetTogheter() throws Exception {
		ScopeProvider.instance.set("/gcube/devsec");

		CPUFacetImpl cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("2 GHz");
		cpuFacet.setModel("Celeron");
		cpuFacet.setVendor("Intel");
		StringWriter stringWriter = new StringWriter();
		Entities.marshal(cpuFacet, stringWriter);

		String json = entityManagementImpl.createFacet(CPUFacet.class.getSimpleName(),
				stringWriter.toString());
		logger.debug("Created : {}", json);
		String createdFacetUUID = Utility.getUUIDFromJSONString(json);
		
		HostingNode hostingNode = new HostingNodeImpl();
		
		CPUFacetImpl cpuFacetImpl = new CPUFacetImpl();
		cpuFacetImpl.setClockSpeed("1 GHz");
		cpuFacetImpl.setModel("Opteron");
		cpuFacetImpl.setVendor("AMD");
		
		hostingNode.addFacet(cpuFacetImpl);
		
		hostingNode.attachFacet(UUID.fromString(createdFacetUUID));
		
		
		StringWriter resourceStringWriter = new StringWriter();
		Entities.marshal(hostingNode, resourceStringWriter);
		logger.trace(resourceStringWriter.toString());
		
		
		json = entityManagementImpl.createResource(
				HostingNode.class.getSimpleName(), resourceStringWriter.toString());
		String resourceUUID = Utility.getUUIDFromJSONString(json);

		

		//entityManagementImpl.deleteResource(resourceUUID);
		
	}
}
