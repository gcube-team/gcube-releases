/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
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
import org.gcube.informationsystem.impl.utils.Entities;
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
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class MultiContextTest {

	private static Logger logger = LoggerFactory
			.getLogger(EntityManagementImplTest.class);

	protected EntityManagementImpl entityManagementImpl;

	public MultiContextTest() {
		entityManagementImpl = new EntityManagementImpl();
	}

	@Test
	public void testCreateEServiceHostingNode() throws Exception {
		ScopeProvider.instance.set("/gcube/devNext");

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

		String json = entityManagementImpl.createResource(EService.NAME,
				Entities.marshal(eService));
		logger.debug("Created : {}", json);
		eService = Entities.unmarshal(EService.class, json);
		logger.debug("Unmarshalled {} {}", EService.NAME, eService);

		/* ----- */

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

		String hnJson = entityManagementImpl.createResource(HostingNode.NAME,
				Entities.marshal(hostingNode));
		logger.debug("Created : {}", hnJson);
		hostingNode = Entities.unmarshal(HostingNode.class, hnJson);
		logger.debug("Unmarshalled {} {}", HostingNode.NAME, hostingNode);

		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		UUID uuid = hostingNode.getHeader().getUUID();
		boolean addedToContext = entityManagementImpl
				.addResourceToContext(uuid);
		Assert.assertTrue(addedToContext);

		String hnString = entityManagementImpl.readResource(uuid);
		HostingNode readHN = Entities.unmarshal(HostingNode.class, hnString);
		Assert.assertTrue(readHN.getHeader().getUUID().compareTo(uuid) == 0);

		UUID eServiceUUID = eService.getHeader().getUUID();
		try {
			entityManagementImpl.readResource(eServiceUUID);
		} catch (ResourceNotFoundException e) {
			logger.debug("Resource with {} Not Found as Expected",
					uuid.toString());
		}
		try {
			entityManagementImpl.deleteResource(uuid);
		} catch (ResourceNotFoundException e) {
			logger.debug("Resource with {} Not Found as Expected",
					uuid.toString());
		}

		try {
			entityManagementImpl.deleteResource(eService.getHeader().getUUID());
		} catch (ResourceNotFoundException e) {
			logger.debug("Resource with {} Not Found as Expected",
					uuid.toString());
		}

	}

	// @Test
	public void addTest() throws ResourceNotFoundException,
			ContextNotFoundException, ResourceRegistryException {
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		entityManagementImpl.addResourceToContext(UUID.fromString(""));
	}

}
