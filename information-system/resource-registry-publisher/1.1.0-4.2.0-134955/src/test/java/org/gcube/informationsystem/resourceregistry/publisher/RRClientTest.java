/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.publisher;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.CPUFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.ResourceNotFoundException;
import org.gcube.informationsystem.resourceregistry.client.proxy.ResourceRegistryClient;
import org.gcube.informationsystem.resourceregistry.client.proxy.ResourceRegistryClientFactory;
import org.gcube.informationsystem.resourceregistry.publisher.proxy.ResourceRegistryPublisher;
import org.gcube.informationsystem.resourceregistry.publisher.proxy.ResourceRegistryPublisherFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class RRClientTest {

	private static final Logger logger = LoggerFactory.getLogger(EntityManagementTest.class);
	
	protected ResourceRegistryPublisher resourceRegistryPublisher;
	protected ResourceRegistryClient resourceRegistryClient;
	
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		ScopedTest.beforeClass();
	}
	
	public RRClientTest(){
		resourceRegistryPublisher = ResourceRegistryPublisherFactory.create();
		resourceRegistryClient = ResourceRegistryClientFactory.create();
		logger.trace("{} and {} created",
				ResourceRegistryPublisher.class.getSimpleName(),
				ResourceRegistryClient.class.getSimpleName());
	}
	
	@Test
	public void testGetFacet() throws FacetNotFoundException, ResourceRegistryException{
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		CPUFacet createdCpuFacet = resourceRegistryPublisher.createFacet(CPUFacet.class, cpuFacet);
		
		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(createdCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(cpuFacet.getModel().compareTo(createdCpuFacet.getModel())==0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(createdCpuFacet.getVendor())==0);
		
		UUID uuid = createdCpuFacet.getHeader().getUUID();
		
		Facet f = resourceRegistryClient.getFacet(uuid);
		Assert.assertTrue(f instanceof CPUFacet);
		
		CPUFacet readCpuFacet = (CPUFacet) f;
		Assert.assertTrue(readCpuFacet.getClockSpeed().compareTo(createdCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(readCpuFacet.getModel().compareTo(createdCpuFacet.getModel())==0);
		Assert.assertTrue(readCpuFacet.getVendor().compareTo(createdCpuFacet.getVendor())==0);
		
		
		readCpuFacet = resourceRegistryClient.getFacet(CPUFacet.class, uuid);
		Assert.assertTrue(readCpuFacet.getClockSpeed().compareTo(createdCpuFacet.getClockSpeed())==0);
		Assert.assertTrue(readCpuFacet.getModel().compareTo(createdCpuFacet.getModel())==0);
		Assert.assertTrue(readCpuFacet.getVendor().compareTo(createdCpuFacet.getVendor())==0);
		
	}
	
	@Test
	public void testGetResource() throws ResourceNotFoundException, ResourceRegistryException{
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
		Assert.assertTrue(f instanceof SoftwareFacet);
		SoftwareFacet createdSoftwareFacet = (SoftwareFacet) f;
		Assert.assertTrue(createdSoftwareFacet.getGroup().compareTo(softwareFacet.getGroup())==0);
		Assert.assertTrue(createdSoftwareFacet.getName().compareTo(softwareFacet.getName())==0);
		Assert.assertTrue(createdSoftwareFacet.getVersion().compareTo(softwareFacet.getVersion())==0);
		
		
		Resource resource = resourceRegistryClient.getResource(createdEService.getHeader().getUUID());
		Assert.assertTrue(resource.getHeader().getUUID().compareTo(createdEService.getHeader().getUUID())==0);
		Assert.assertTrue(resource instanceof EService);
		
		EService readEService = resourceRegistryClient.getResource(EService.class, createdEService.getHeader().getUUID());
		Assert.assertTrue(readEService.getHeader().getUUID().compareTo(createdEService.getHeader().getUUID())==0);
		List<? extends Facet> idFacets = readEService.getIdentificationFacets();
		Assert.assertTrue(idFacets!=null);
		Assert.assertTrue(idFacets.size()==1);
		f = idFacets.get(0);
		Assert.assertTrue(f!=null);
		Assert.assertTrue(f instanceof SoftwareFacet);
		SoftwareFacet readSoftwareFacet = (SoftwareFacet) f;
		Assert.assertTrue(readSoftwareFacet.getGroup().compareTo(softwareFacet.getGroup())==0);
		Assert.assertTrue(readSoftwareFacet.getName().compareTo(softwareFacet.getName())==0);
		Assert.assertTrue(readSoftwareFacet.getVersion().compareTo(softwareFacet.getVersion())==0);
		Assert.assertTrue(readSoftwareFacet.getHeader().getUUID().compareTo(createdSoftwareFacet.getHeader().getUUID())==0);
		
	}
	
}
