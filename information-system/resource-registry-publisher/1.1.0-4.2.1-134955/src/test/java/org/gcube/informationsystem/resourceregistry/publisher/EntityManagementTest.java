/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.publisher;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.impl.relation.isrelatedto.HostsImpl;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class EntityManagementTest {

	private static final Logger logger = LoggerFactory.getLogger(EntityManagementTest.class);
	
	protected ResourceRegistryPublisher resourceRegistryPublisher;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		ScopedTest.beforeClass();
	}
	
	public EntityManagementTest(){
		resourceRegistryPublisher = ResourceRegistryPublisherFactory.create();
	}
	
	@Test
	public void testCreateUpdateDeleteFacet() throws Exception {
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		CPUFacet createdCpuFacet = resourceRegistryPublisher.createFacet(CPUFacet.class, cpuFacet);
		
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

		CPUFacet updatedCpuFacet = resourceRegistryPublisher.updateFacet(CPUFacet.class, createdCpuFacet);
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
		
		EService createdEService = resourceRegistryPublisher.createResource(EService.class, eService);
		
		List<? extends Facet> idenficationFacets = createdEService.getIdentificationFacets();
		Assert.assertTrue(idenficationFacets!=null);
		Assert.assertTrue(idenficationFacets.size()==1);
		Facet f = idenficationFacets.get(0);
		Assert.assertTrue(f!=null);
		Assert.assertTrue(SoftwareFacet.class.isAssignableFrom(f.getClass()));
		SoftwareFacet createdSoftwareFacet = (SoftwareFacet) f;
		logger.debug("Created : {}", softwareFacet);
		Assert.assertTrue(createdSoftwareFacet.getGroup().compareTo(softwareFacet.getGroup())==0);
		Assert.assertTrue(createdSoftwareFacet.getName().compareTo(softwareFacet.getName())==0);
		Assert.assertTrue(createdSoftwareFacet.getVersion().compareTo(softwareFacet.getVersion())==0);
		
		
		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");

		NetworkingFacet createdNetworkingFacet = resourceRegistryPublisher.createFacet(NetworkingFacet.class, networkingFacet);
		
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
		
		HostingNode createdHostingNode = resourceRegistryPublisher.createResource(HostingNode.class, hostingNode);
		
		List<ConsistsOf<? extends Resource, ? extends Facet>> consistsOfList = createdHostingNode.getConsistsOf();
		CPUFacet createdCPUFacet = null;
		for(ConsistsOf<? extends Resource, ? extends Facet> consistsOf : consistsOfList){
			if (CPUFacet.class.isAssignableFrom(consistsOf.getTarget().getClass())) {
				createdCPUFacet = (CPUFacet) consistsOf.getTarget();
			}
			
		}
		Assert.assertTrue(createdCPUFacet!=null);
		logger.info("Created : {}", createdCPUFacet);
		
		
		boolean deleted = resourceRegistryPublisher.deleteResource(createdHostingNode);
		Assert.assertTrue(deleted);
		
		deleted = resourceRegistryPublisher.deleteFacet(createdCPUFacet);
		Assert.assertTrue(deleted);
		
		deleted = resourceRegistryPublisher.deleteFacet(createdNetworkingFacet);
		Assert.assertTrue(deleted);
		
		
		deleted = resourceRegistryPublisher.deleteFacet(createdSoftwareFacet);
		Assert.assertTrue(deleted);
		
		
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
		
		EService createdEService = resourceRegistryPublisher.createResource(EService.class, eService);
		
		HostingNode hostingNode = new HostingNodeImpl();
		HostingNode createdHostingNode = resourceRegistryPublisher.createResource(HostingNode.class, hostingNode);
		
		
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		CPUFacet createdCpuFacet = resourceRegistryPublisher.createFacet(CPUFacet.class, cpuFacet);
		
		
		IsIdentifiedBy<HostingNode, CPUFacet> isIdentifiedByCPUFacet = new IsIdentifiedByImpl<>(createdHostingNode, createdCpuFacet, null);
		@SuppressWarnings("unchecked")
		IsIdentifiedBy<HostingNode, CPUFacet> createdIsIdentifiedByCPUFacet = resourceRegistryPublisher.createConsistsOf(IsIdentifiedBy.class, isIdentifiedByCPUFacet);
		
		Hosts<HostingNode, EService> hosts = new HostsImpl<>(createdHostingNode, createdEService, null);
		@SuppressWarnings("unchecked")
		Hosts<HostingNode, EService> createdHosts = resourceRegistryPublisher.createIsRelatedTo(Hosts.class, hosts);
		
		
		boolean deleted = resourceRegistryPublisher.deleteConsistsOf(createdIsIdentifiedByCPUFacet);
		Assert.assertTrue(deleted);
		
		
		deleted = resourceRegistryPublisher.deleteFacet(createdCpuFacet);
		Assert.assertTrue(deleted);
		
		
		deleted = resourceRegistryPublisher.deleteIsRelatedTo(createdHosts);
		Assert.assertTrue(deleted);
		
		
		deleted = resourceRegistryPublisher.deleteResource(createdHostingNode);
		Assert.assertTrue(deleted);
	}

	
}
