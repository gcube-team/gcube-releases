/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.multicontext;

import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;

import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.impl.entity.facet.AccessPointFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.EventFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.LicenseFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.MemoryFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.ServiceStateFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.impl.relation.consistsof.HasPersistentMemoryImpl;
import org.gcube.informationsystem.impl.relation.consistsof.HasVolatileMemoryImpl;
import org.gcube.informationsystem.impl.relation.isrelatedto.HostsImpl;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.facet.AccessPointFacet;
import org.gcube.informationsystem.model.entity.facet.CPUFacet;
import org.gcube.informationsystem.model.entity.facet.EventFacet;
import org.gcube.informationsystem.model.entity.facet.LicenseFacet;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet;
import org.gcube.informationsystem.model.entity.facet.MemoryFacet.MemoryUnit;
import org.gcube.informationsystem.model.entity.facet.NetworkingFacet;
import org.gcube.informationsystem.model.entity.facet.ServiceStateFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.model.relation.consistsof.HasPersistentMemory;
import org.gcube.informationsystem.model.relation.consistsof.HasVolatileMemory;
import org.gcube.informationsystem.model.relation.isrelatedto.Hosts;
import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.er.SmartgearResourcesTest;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class BasicTest extends ScopedTest {

	private static Logger logger = LoggerFactory
			.getLogger(BasicTest.class);

	@Test
	public void testDifferentScopes() throws Exception {
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");

		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setJSON(ISMapper.marshal(cpuFacet));
		facetManagement.setElementType(CPUFacet.NAME);
		
		String json = facetManagement.create();
		logger.debug("Created : {}", json);

		JSONObject jsonObject = new JSONObject(json);
		JSONObject header = jsonObject.getJSONObject(Entity.HEADER_PROPERTY);
		UUID uuid = UUID.fromString(header.getString(Header.UUID_PROPERTY));

		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);
		
		String readJson = facetManagement.read();
		logger.debug("Read : {}", readJson);

		/* ------------------------------------------------------------------ */

		logger.debug("Switching to another scope");
		ScopedTest.setContext(ScopedTest.ALTERNATIVE_TEST_SCOPE);
		try {
			facetManagement = new FacetManagement();
			facetManagement.setUUID(uuid);
			readJson = facetManagement.read();
			logger.debug("You should not be able to read Facet with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to read Facet with UUID " + uuid);
		} catch (FacetAvailableInAnotherContextException e) {
			logger.debug("Good the facet created in the default context is not visible in an alternative context");
		}

		cpuFacet.setAdditionalProperty("My", "Test");

		try {
			facetManagement = new FacetManagement();
			facetManagement.setUUID(uuid);
			facetManagement.setJSON(ISMapper.marshal(cpuFacet));
			readJson = facetManagement.update();
			logger.debug("You should not be able to update Facet with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to read Facet with UUID " + uuid);
		} catch (FacetAvailableInAnotherContextException e) {
			logger.debug("Good the Facet created in the default context cannot be updated in an alternative context");
		}

		/* Commented because the behavior has been changed
		try {
			facetManagement = new FacetManagement();
			facetManagement.setUUID(uuid);
			facetManagement.delete();
			logger.debug("You should not be able to delete Facet with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to delete Facet with UUID " + uuid);
		} catch (FacetAvailableInAnotherContextException e) {
			logger.debug("Good the Facet created in the default context cannot be deleted in an alternative context");
		}
		*/

		/* ------------------------------------------------------------------ */

		logger.debug("Setting back default scope");
		ScopedTest.setContext(ScopedTest.DEFAULT_TEST_SCOPE);

		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);
		facetManagement.setJSON(ISMapper.marshal(cpuFacet));
		readJson = facetManagement.update();
		logger.debug("Updated : {}", readJson);

		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);
		readJson = facetManagement.read();
		logger.debug("Read Updated : {}", readJson);

		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);
		boolean deleted = facetManagement.delete();
		Assert.assertTrue(deleted);
	}
	
	@Test
	public void testCreateEServiceHostingNode() throws Exception {
		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(
				eService, softwareFacet, null);
		eService.addFacet(isIdentifiedBy);

		AccessPointFacet accessPointFacet = new AccessPointFacetImpl();
		accessPointFacet.setEndpoint(new URI("http://localhost"));
		accessPointFacet.setEntryName("port1");
		eService.addFacet(accessPointFacet);

		EventFacet eventFacet = new EventFacetImpl();
		eventFacet.setDate(Calendar.getInstance().getTime());
		eventFacet.setValue("Created");
		eService.addFacet(eventFacet);

		ServiceStateFacet serviceStateFacet = new ServiceStateFacetImpl();
		serviceStateFacet.setValue("ready");
		eService.addFacet(serviceStateFacet);

		LicenseFacet licenseFacet = new LicenseFacetImpl();
		licenseFacet.setName("EUPL");
		licenseFacet
				.setTextURL(new URL(
						"https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11"));
		eService.addFacet(licenseFacet);
		
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(EService.NAME);
		resourceManagement.setJSON(ISMapper.marshal(eService));

		String json = resourceManagement.create();
		logger.debug("Created : {}", json);
		eService = ISMapper.unmarshal(EService.class, json);
		logger.debug("Unmarshalled {} {}", EService.NAME, eService);


		HostingNode hostingNode = new HostingNodeImpl();

		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");

		IsIdentifiedBy<HostingNode, NetworkingFacet> isIdentifiedByHNNF = new IsIdentifiedByImpl<HostingNode, NetworkingFacet>(
				hostingNode, networkingFacet, null);
		hostingNode.addFacet(isIdentifiedByHNNF);

		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		hostingNode.addFacet(cpuFacet);

		MemoryFacet ram = new MemoryFacetImpl();
		ram.setSize(8);
		ram.setUnit(MemoryUnit.GB);
		ram.setUsed(2);

		HasVolatileMemory<HostingNode, MemoryFacet> hasVolatileMemory = new HasVolatileMemoryImpl<HostingNode, MemoryFacet>(
				hostingNode, ram, null);
		hostingNode.addFacet(hasVolatileMemory);

		MemoryFacet disk = new MemoryFacetImpl();
		disk.setSize(256);
		disk.setUnit(MemoryUnit.GB);
		disk.setUsed(120);

		HasPersistentMemory<HostingNode, MemoryFacet> hasPersistentMemory = new HasPersistentMemoryImpl<HostingNode, MemoryFacet>(
				hostingNode, disk, null);
		hostingNode.addFacet(hasPersistentMemory);

		Hosts<HostingNode, EService> hosts = new HostsImpl<HostingNode, EService>(
				hostingNode, eService, null);
		hostingNode.attachResource(hosts);

		resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(HostingNode.NAME);
		resourceManagement.setJSON(ISMapper.marshal(hostingNode));
		
		String hnJson = resourceManagement.create();
		logger.debug("Created : {}", hnJson);
		hostingNode = ISMapper.unmarshal(HostingNode.class, hnJson);
		logger.debug("Unmarshalled {} {}", HostingNode.NAME, hostingNode);
		UUID uuid = hostingNode.getHeader().getUUID();
		
		/* ------------------------------------------------------------------ */
		
		logger.debug("Switching to another scope");
		ScopedTest.setContext(ScopedTest.ALTERNATIVE_TEST_SCOPE);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(uuid);
		
		boolean addedToContext = resourceManagement.addToContext();
		Assert.assertTrue(addedToContext);

		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(uuid);
		
		String hnString = resourceManagement.read();
		HostingNode readHN = ISMapper.unmarshal(HostingNode.class, hnString);
		Assert.assertTrue(readHN.getHeader().getUUID().compareTo(uuid) == 0);

		UUID eServiceUUID = eService.getHeader().getUUID();
		try {
			resourceManagement = new ResourceManagement();
			resourceManagement.setUUID(eServiceUUID);
			resourceManagement.read();
		} catch (ResourceAvailableInAnotherContextException e) {
			logger.debug("Resource with {} Not Found as Expected",
					eServiceUUID);
		}
		
		/* Commented because the behavior has been changed
		try {
			resourceManagement = new ResourceManagement();
			resourceManagement.setUUID(eServiceUUID);
			resourceManagement.delete();
			logger.debug("You should not be able to delete EService with UUID {}",
					uuid);
			throw new Exception(
					"You should not be able to delete EService with UUID " + uuid);
		} catch (ResourceAvailableInAnotherContextException e) {
			logger.debug("Resource with {} Not Deleted as Expected",
					eServiceUUID);
		}
		*/

		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(uuid);
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
		
		/* ------------------------------------------------------------------ */

		logger.debug("Setting back default scope");
		ScopedTest.setContext(ScopedTest.DEFAULT_TEST_SCOPE);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eServiceUUID);
		deleted = resourceManagement.delete();
		Assert.assertTrue(deleted); 
				
	}

	// @Test
	public void addTest() throws ResourceNotFoundException,
			ContextNotFoundException, ResourceRegistryException {
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString(""));
		resourceManagement.addToContext();
	}
	
	
	@Test
	public void testAddAndRemoveFromContext() throws Exception {
		
		/* Creating HostingNode */
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(HostingNode.NAME);
		resourceManagement.setJSON(SmartgearResourcesTest.HOSTING_NODE);

		String hnJson = resourceManagement.create();
		logger.debug("Created : {}", hnJson);
		HostingNode hostingNode = ISMapper.unmarshal(HostingNode.class, hnJson);
		logger.debug("Unmarshalled {} {}", HostingNode.NAME, hostingNode);

		UUID hnUUID = hostingNode.getHeader().getUUID();

		
		/* Creating EService */
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(EService.NAME);
		resourceManagement.setJSON(SmartgearResourcesTest.ESERVICE);

		String eservicejson = resourceManagement.create();
		logger.debug("Created : {}", eservicejson);
		EService eService = ISMapper.unmarshal(EService.class, eservicejson);
		logger.debug("Unmarshalled {} {}", EService.NAME, eService);
		
		UUID eServiceUUID = eService.getHeader().getUUID();
		
		
		/* Creating Hosts Relation */
		
		PropagationConstraint propagationConstraint = new PropagationConstraintImpl();
		propagationConstraint
				.setRemoveConstraint(RemoveConstraint.cascade);
		propagationConstraint
				.setAddConstraint(AddConstraint.propagate);

		
		Hosts<HostingNode, EService> hosts = new HostsImpl<>(hostingNode, eService,
				propagationConstraint);
		
		IsRelatedToManagement isRelatedToManagement = new IsRelatedToManagement();
		isRelatedToManagement.setElementType(Hosts.NAME);
		String hostsJson = ISMapper.marshal(hosts);
		isRelatedToManagement.setJSON(hostsJson);
		
		String createdHostsJson = isRelatedToManagement.create(hnUUID, eServiceUUID);
		logger.debug("Created : {}", createdHostsJson);
		
		@SuppressWarnings("unchecked")
		Hosts<HostingNode, EService> createdHosts =  ISMapper.unmarshal(Hosts.class, createdHostsJson);
		UUID hostsUUID = createdHosts.getHeader().getUUID();
		
		
		/* ------------------------------------------------------------------ */
		
		logger.debug("Switching to alternative scope");
		ScopedTest.setContext(ScopedTest.ALTERNATIVE_TEST_SCOPE);

		/*
		 * resourceManagement = new ResourceManagement();
		 * resourceManagement.setUUID(hnUUID);
		 * resourceManagement.addToContext();
		
		 * resourceManagement = new ResourceManagement();
		 * resourceManagement.setUUID(eServiceUUID);
		 * resourceManagement.addToContext();
		 *
		 * This code is commented because the addToContext
		 * on relation enforce addToContext both on source
		 * and target 
		 * 
		 */
		
		isRelatedToManagement = new IsRelatedToManagement();
		isRelatedToManagement.setUUID(hostsUUID);
		isRelatedToManagement.addToContext();
		
		/* The addTocontext on the relation adds the source and target too. 
		 * So that I MUST be able to read HostinNode and EService
		 */
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(hnUUID);
		resourceManagement.read();
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eServiceUUID);
		resourceManagement.read();
		
		/* ------------------------------------------------------------------ */
		logger.debug("Setting back default scope");
		ScopedTest.setContext(ScopedTest.DEFAULT_TEST_SCOPE);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(hnUUID);
		boolean removed = resourceManagement.removeFromContext();
		Assert.assertTrue(removed);
		
		/* The cascading MUST remove the relation and the target so that
		 * I MUST not be able to read Hosts relation and EService
		 */
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eServiceUUID);
		try {
			resourceManagement.read();
			String error = String.format("{} with UUID {} should not be visible.", EService.NAME, eServiceUUID);
			logger.trace(error);
			throw new Exception(error);
		}catch (ResourceAvailableInAnotherContextException e) {
			// OK
		}
		
		isRelatedToManagement = new IsRelatedToManagement();
		isRelatedToManagement.setUUID(hostsUUID);
		try {
			isRelatedToManagement.read();
			String error = String.format("{} with UUID {} should not be visible.", Hosts.NAME, hostsUUID);
			logger.trace(error);
			throw new Exception(error);
		}catch (RelationAvailableInAnotherContextException e) {
			// OK
		}
		
		
		/* ------------------------------------------------------------------ */
		logger.debug("Switching to alternative scope again");
		ScopedTest.setContext(ScopedTest.ALTERNATIVE_TEST_SCOPE);
		// TODO checks here
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(hnUUID);
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
	}

}
