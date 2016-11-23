/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.publisher;

import java.util.List;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.impl.relation.isrelatedto.HostsImpl;
import org.gcube.informationsystem.impl.utils.Entities;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CPUFacet;
import org.gcube.informationsystem.model.entity.facet.NetworkingFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.model.relation.isrelatedto.Hosts;
import org.gcube.informationsystem.resourceregistry.publisher.proxy.ResourceRegistryPublisher;
import org.gcube.informationsystem.resourceregistry.publisher.proxy.ResourceRegistryPublisherFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class ResourceRegistryPublisherTest {

	private static final Logger logger = LoggerFactory.getLogger(ResourceRegistryPublisherTest.class);
	
	
	protected ResourceRegistryPublisher resourceRegistryPublisher;
	
	@Before
	public void before(){
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		resourceRegistryPublisher = ResourceRegistryPublisherFactory.create();
	}
	
	@Test
	public void testCreateUpdateDeleteFacet() throws Exception {
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		logger.debug("Going to create: {}", cpuFacet);
		CPUFacet createdCpuFacet = resourceRegistryPublisher.createFacet(CPUFacet.class, cpuFacet);
		logger.debug("Created: {}", createdCpuFacet);
		
		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(createdCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(cpuFacet.getModel().compareTo(createdCpuFacet.getModel())==0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(createdCpuFacet.getVendor())==0);
		
		UUID uuid = createdCpuFacet.getHeader().getUUID();
		
		String newVendor = "Intel";
		String newClockSpeed = "2 GHz";
		createdCpuFacet.setVendor(newVendor);
		createdCpuFacet.setClockSpeed(newClockSpeed);
		
		String additionPropertyKey = "My";
		String additionPropertyValue = "Test";
		createdCpuFacet.setAdditionalProperty(additionPropertyKey, additionPropertyValue);

		logger.debug("Going to update: {}", cpuFacet);
		CPUFacet updatedCpuFacet = resourceRegistryPublisher.updateFacet(CPUFacet.class, createdCpuFacet);
		logger.debug("Updated: {}", updatedCpuFacet);
		Assert.assertTrue(createdCpuFacet.getClockSpeed().compareTo(updatedCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(createdCpuFacet.getModel().compareTo(updatedCpuFacet.getModel())==0);
		Assert.assertTrue(createdCpuFacet.getVendor().compareTo(updatedCpuFacet.getVendor())==0);
		Assert.assertTrue(((String) updatedCpuFacet.getAdditionalProperty(additionPropertyKey)).compareTo((String) createdCpuFacet.getAdditionalProperty(additionPropertyKey))==0);
		Assert.assertTrue(uuid.compareTo(updatedCpuFacet.getHeader().getUUID())==0);
		
		boolean deleted = resourceRegistryPublisher.deleteFacet(updatedCpuFacet);
		Assert.assertTrue(deleted);
		
	}

	@Test
	public void testCreateDeleteResources() throws Exception {
		
		EService eService = new EServiceImpl();
		
		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		
		IsIdentifiedBy<EService, SoftwareFacet> isIdentifiedByESSF = new IsIdentifiedByImpl<>(eService, softwareFacet, null);
		eService.addFacet(isIdentifiedByESSF);
		
		Entities.registerSubtypes(IsIdentifiedBy.class, SoftwareFacet.class, EService.class, HostingNode.class, NetworkingFacet.class, CPUFacet.class);
		
		logger.debug("Going to create : {}", eService);
		EService createdEService = resourceRegistryPublisher.createResource(EService.class, eService);
		logger.debug("Created : {}", createdEService);
		List<? extends Facet> idenficationFacets = createdEService.getIdentificationFacets();
		Assert.assertTrue(idenficationFacets!=null);
		Assert.assertTrue(idenficationFacets.size()==1);
		Facet f = idenficationFacets.get(0);
		Assert.assertTrue(f!=null);
		Assert.assertTrue(SoftwareFacet.class.isAssignableFrom(f.getClass()));
		SoftwareFacet createdSoftwareFacet = (SoftwareFacet) f;
		logger.debug("Created : {}", softwareFacet);
		
		
		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");

		logger.debug("Going to create : {}", networkingFacet);
		NetworkingFacet createdNetworkingFacet = resourceRegistryPublisher.createFacet(NetworkingFacet.class, networkingFacet);
		logger.debug("Created : {}", createdNetworkingFacet);
		
		
		HostingNode hostingNode = new HostingNodeImpl();
		
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		hostingNode.addFacet(cpuFacet);
		
		IsIdentifiedBy<HostingNode, NetworkingFacet> isIdentifiedByHNNF = new IsIdentifiedByImpl<>(hostingNode, createdNetworkingFacet, null);
		hostingNode.attachFacet(isIdentifiedByHNNF);
		
		Hosts<HostingNode, EService> hosts = new HostsImpl<HostingNode, EService>(hostingNode, createdEService, null);
		
		hostingNode.attachResource(hosts);
		
		
		logger.debug("Going to create : {}", hostingNode);
		HostingNode createdHostingNode = resourceRegistryPublisher.createResource(HostingNode.class, hostingNode);
		logger.debug("Created : {}", createdHostingNode);
		
		List<ConsistsOf<? extends Resource, ? extends Facet>> consistsOfList = createdHostingNode.getConsistsOf();
		CPUFacet createdCPUFacet = null;
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfList){
			if (CPUFacet.class.isAssignableFrom(consistsOf.getTarget().getClass())) {
				createdCPUFacet = (CPUFacet) consistsOf.getTarget();
			}
			
		}
		Assert.assertTrue(createdCPUFacet!=null);
		logger.debug("Created : {}", createdCPUFacet);
		
		
		logger.debug("Going to delete : {}", createdHostingNode);
		boolean deleted = resourceRegistryPublisher.deleteResource(createdHostingNode);
		Assert.assertTrue(deleted);
		
		logger.debug("Going to delete : {}", createdCPUFacet);
		deleted = resourceRegistryPublisher.deleteFacet(createdCPUFacet);
		Assert.assertTrue(deleted);
		
		logger.debug("Going to delete : {}", createdNetworkingFacet);
		deleted = resourceRegistryPublisher.deleteFacet(createdNetworkingFacet);
		Assert.assertTrue(deleted);
		
		
		logger.debug("Going to delete : {}", createdSoftwareFacet);
		deleted = resourceRegistryPublisher.deleteFacet(createdSoftwareFacet);
		Assert.assertTrue(deleted);
		
		
		logger.debug("Going to delete : {}", createdEService);
		deleted = resourceRegistryPublisher.deleteResource(createdEService);
		Assert.assertTrue(deleted);
		
	}
	
	@Test
	public void testCreateAndDeleteEntitiesAndRelations() throws Exception {
		EService eService = new EServiceImpl();
		
		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		
		IsIdentifiedBy<Resource, Facet> isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(eService, softwareFacet, null);
		eService.addFacet(isIdentifiedBy);
		
		Entities.registerSubtypes(IsIdentifiedBy.class, SoftwareFacet.class, EService.class);
		
		
		logger.debug("Going to create : {}", eService);
		EService createdEService = resourceRegistryPublisher.createResource(EService.class, eService);
		logger.debug("Created : {}", createdEService);
		
		
		HostingNode hostingNode = new HostingNodeImpl();
		
		logger.debug("Going to create : {}", hostingNode);
		HostingNode createdHostingNode = resourceRegistryPublisher.createResource(HostingNode.class, hostingNode);
		logger.debug("Created : {}", createdHostingNode);
		
		
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		
		Entities.registerSubtypes(HostingNode.class, CPUFacet.class);
		
		
		logger.debug("Going to create: {}", cpuFacet);
		CPUFacet createdCpuFacet = resourceRegistryPublisher.createFacet(CPUFacet.class, cpuFacet);
		logger.debug("Created: {}", createdCpuFacet);
		
		
		IsIdentifiedBy<HostingNode, CPUFacet> isIdentifiedByCPUFacet = new IsIdentifiedByImpl<>(createdHostingNode, createdCpuFacet, null);
		logger.debug("Going to create : {}", isIdentifiedByCPUFacet);
		@SuppressWarnings("unchecked")
		IsIdentifiedBy<HostingNode, CPUFacet> createdIsIdentifiedByCPUFacet = resourceRegistryPublisher.createConsistsOf(IsIdentifiedBy.class, isIdentifiedByCPUFacet);
		logger.debug("Created : {}", createdIsIdentifiedByCPUFacet);
		
		Hosts<HostingNode, EService> hosts = new HostsImpl<>(createdHostingNode, createdEService, null);
		logger.debug("Going to create : {}", hosts);
		@SuppressWarnings("unchecked")
		Hosts<HostingNode, EService> createdHosts = resourceRegistryPublisher.createIsRelatedTo(Hosts.class, hosts);
		logger.debug("Created : {}", createdHosts);
		
		
		logger.debug("Going to delete : {}", createdIsIdentifiedByCPUFacet);
		boolean deleted = resourceRegistryPublisher.deleteConsistsOf(createdIsIdentifiedByCPUFacet);
		Assert.assertTrue(deleted);
		
		
		logger.debug("Going to delete : {}", createdCpuFacet);
		deleted = resourceRegistryPublisher.deleteFacet(createdCpuFacet);
		Assert.assertTrue(deleted);
		
		
		logger.debug("Going to delete : {}", createdHosts);
		deleted = resourceRegistryPublisher.deleteIsRelatedTo(createdHosts);
		Assert.assertTrue(deleted);
		
		
		logger.debug("Going to delete : {}", createdHostingNode);
		deleted = resourceRegistryPublisher.deleteResource(createdHostingNode);
		Assert.assertTrue(deleted);
	}

	
}
