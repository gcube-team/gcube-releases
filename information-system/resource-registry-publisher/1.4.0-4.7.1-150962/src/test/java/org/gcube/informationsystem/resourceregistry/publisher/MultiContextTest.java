/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.publisher;

import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;

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
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.resource.ResourceAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClient;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClientFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class MultiContextTest extends ScopedTest {

	private static Logger logger = LoggerFactory
			.getLogger(MultiContextTest.class);

	protected ResourceRegistryPublisher resourceRegistryPublisher;
	protected ResourceRegistryClient resourceRegistryClient;
	
	public MultiContextTest(){
		resourceRegistryPublisher = ResourceRegistryPublisherFactory.create();
		resourceRegistryClient = ResourceRegistryClientFactory.create();
	}
	
	@Test
	public void testCreateEServiceHostingNode() throws Exception {
		EService eService = new EServiceImpl();
		
		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(eService, softwareFacet, null);
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
		licenseFacet.setTextURL(new URL("https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11"));
		eService.addFacet(licenseFacet);
		
		EService createdEService = resourceRegistryPublisher.createResource(EService.class, eService);
		logger.debug("Created : {}", createdEService);
		
		HostingNode hostingNode = new HostingNodeImpl();
				
		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");
		
		IsIdentifiedBy<HostingNode, NetworkingFacet> isIdentifiedByHNNF = new IsIdentifiedByImpl<HostingNode, NetworkingFacet>(hostingNode, networkingFacet, null);
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
		
		HasVolatileMemory<HostingNode, MemoryFacet> hasVolatileMemory = 
				new HasVolatileMemoryImpl<HostingNode, MemoryFacet>(hostingNode, ram, null);
		hostingNode.addFacet(hasVolatileMemory);
		
		
		MemoryFacet disk = new MemoryFacetImpl();
		disk.setSize(256);
		disk.setUnit(MemoryUnit.GB);
		disk.setUsed(120);
		
		HasPersistentMemory<HostingNode, MemoryFacet> hasPersistentMemory = 
				new HasPersistentMemoryImpl<HostingNode, MemoryFacet>(hostingNode, disk, null);
		hostingNode.addFacet(hasPersistentMemory);
		
		Hosts<HostingNode, EService> hosts = new HostsImpl<HostingNode, EService>(hostingNode, createdEService, null);
		hostingNode.attachResource(hosts);
		
		HostingNode createdHN = resourceRegistryPublisher.createResource(HostingNode.class, hostingNode);
		logger.debug("Created : {}", createdHN);
		

		
		
		
		logger.debug("Changing token to test add to scope");
		ScopedTest.setContext(ScopedTest.ALTERNATIVE_TEST_SCOPE);
		
		
		UUID hostingNodeUUID = createdHN.getHeader().getUUID();
		UUID eServiceUUID = createdEService.getHeader().getUUID();
		
		logger.debug("Changing token to test add to scope");
		ScopedTest.setContext(ScopedTest.ALTERNATIVE_TEST_SCOPE);
		
		try {
			resourceRegistryClient.exists(EService.class, eServiceUUID);
		} catch (ERNotFoundException e) {
			throw e;
		} catch (ERAvailableInAnotherContextException e) {
			// Good
		} catch (ResourceRegistryException e) {
			throw e;
		}
		
		
		try {
			resourceRegistryClient.exists(HostingNode.class, hostingNodeUUID);
		} catch (ERNotFoundException e) {
			throw e;
		} catch (ERAvailableInAnotherContextException e) {
			// Good
		} catch (ResourceRegistryException e) {
			throw e;
		}
		
		
		boolean addedToContext = resourceRegistryPublisher.addResourceToContext(hostingNodeUUID);
		Assert.assertTrue(addedToContext);
		
		try {
			resourceRegistryClient.exists(EService.class, eServiceUUID);
		}catch(ResourceAvailableInAnotherContextException e){
			logger.debug("Resource with {} Not Found as Expected", eServiceUUID.toString());
		}
		
		boolean deleted = resourceRegistryPublisher.deleteResource(createdHN);
		Assert.assertTrue(deleted);
		
		
		logger.debug("Restoring original scope");
		ScopedTest.setContext(ScopedTest.DEFAULT_TEST_SCOPE);
		
		deleted = resourceRegistryPublisher.deleteResource(createdEService);
		Assert.assertTrue(deleted);
		
	}
	
}
