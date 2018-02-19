/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.publisher;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.impl.entity.facet.AccessPointFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.CPUFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.EventFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.LicenseFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.ServiceStateFacetImpl;
import org.gcube.informationsystem.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.informationsystem.impl.entity.resource.EServiceImpl;
import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;
import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.impl.relation.isrelatedto.HostsImpl;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.AccessPointFacet;
import org.gcube.informationsystem.model.entity.facet.CPUFacet;
import org.gcube.informationsystem.model.entity.facet.EventFacet;
import org.gcube.informationsystem.model.entity.facet.LicenseFacet;
import org.gcube.informationsystem.model.entity.facet.NetworkingFacet;
import org.gcube.informationsystem.model.entity.facet.ServiceStateFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.model.relation.isrelatedto.Hosts;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.client.Direction;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClient;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClientFactory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ERManagementTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(ERManagementTest.class);

	protected ResourceRegistryPublisher resourceRegistryPublisher;
	protected ResourceRegistryClient resourceRegistryClient;

	public ERManagementTest() {
		resourceRegistryPublisher = ResourceRegistryPublisherFactory.create();
		resourceRegistryClient = ResourceRegistryClientFactory.create();
	}

	@Test
	public void testCreateDeleteEService() throws Exception {
		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(eService,
				softwareFacet, null);
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
		licenseFacet.setTextURL(
				new URL("https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11"));
		eService.addFacet(licenseFacet);

		eService = resourceRegistryPublisher.createResource(eService);

		boolean deleted = resourceRegistryPublisher.deleteResource(eService);
		Assert.assertTrue(deleted);
	}

	@Test
	public void testCreateUpdateDeleteEService() throws Exception {
		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(eService,
				softwareFacet, null);
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
		licenseFacet.setTextURL(
				new URL("https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11"));
		eService.addFacet(licenseFacet);

		eService = resourceRegistryPublisher.createResource(eService);

		eService.getFacets(SoftwareFacet.class).get(0).setVersion("1.2.0");

		eService = resourceRegistryPublisher.updateResource(eService);

		Assert.assertTrue(eService.getFacets(SoftwareFacet.class).get(0).getVersion().compareTo("1.2.0") == 0);

		boolean deleted = resourceRegistryPublisher.deleteResource(eService);
		Assert.assertTrue(deleted);
	}

	@Test
	public void testCreateEServiceAsJsonString() throws Exception {
		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(eService,
				softwareFacet, null);
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
		licenseFacet.setTextURL(
				new URL("https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11"));
		eService.addFacet(licenseFacet);

		String json = ISMapper.marshal(eService);

		String createdString = resourceRegistryPublisher.createResource(json);

		EService created = ISMapper.unmarshal(EService.class, createdString);

		boolean deleted = resourceRegistryPublisher.deleteResource(created);
		Assert.assertTrue(deleted);
	}

	@Test
	public void testCreateUpdateDeleteEServiceAsJsonString() throws Exception {
		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(eService,
				softwareFacet, null);
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
		licenseFacet.setTextURL(
				new URL("https://joinup.ec.europa.eu/community/eupl/og_page/european-union-public-licence-eupl-v11"));
		eService.addFacet(licenseFacet);

		String eServiceString = resourceRegistryPublisher.createResource(EService.NAME, ISMapper.marshal(eService));
		eService = ISMapper.unmarshal(EService.class, eServiceString);

		eService.getFacets(SoftwareFacet.class).get(0).setVersion("1.2.0");

		eServiceString = resourceRegistryPublisher.updateResource(eService.getHeader().getUUID(),
				ISMapper.marshal(eService));
		eService = ISMapper.unmarshal(EService.class, eServiceString);

		Assert.assertTrue(eService.getFacets(SoftwareFacet.class).get(0).getVersion().compareTo("1.2.0") == 0);

		boolean deleted = resourceRegistryPublisher.deleteResource(eService);
		Assert.assertTrue(deleted);
	}

	@Test
	public void testCreateReadDeleteFacet() throws Exception {
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");

		CPUFacet createdCpuFacet = resourceRegistryPublisher.createFacet(cpuFacet);

		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(createdCpuFacet.getClockSpeed()) == 0);
		Assert.assertTrue(cpuFacet.getModel().compareTo(createdCpuFacet.getModel()) == 0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(createdCpuFacet.getVendor()) == 0);

		UUID uuid = createdCpuFacet.getHeader().getUUID();

		CPUFacet readCpuFacet = resourceRegistryClient.getInstance(CPUFacet.class, uuid);
		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(readCpuFacet.getClockSpeed()) == 0);
		Assert.assertTrue(cpuFacet.getModel().compareTo(readCpuFacet.getModel()) == 0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(readCpuFacet.getVendor()) == 0);
		Assert.assertTrue(uuid.compareTo(readCpuFacet.getHeader().getUUID()) == 0);

		String newVendor = "Intel";
		String newClockSpeed = "2 GHz";
		readCpuFacet.setVendor(newVendor);
		readCpuFacet.setClockSpeed(newClockSpeed);

		String additionPropertyKey = "My";
		String additionPropertyValue = "Test";
		readCpuFacet.setAdditionalProperty(additionPropertyKey, additionPropertyValue);

		CPUFacet updatedCpuFacet = resourceRegistryPublisher.updateFacet(readCpuFacet);
		Assert.assertTrue(readCpuFacet.getClockSpeed().compareTo(updatedCpuFacet.getClockSpeed()) == 0);
		Assert.assertTrue(readCpuFacet.getModel().compareTo(updatedCpuFacet.getModel()) == 0);
		Assert.assertTrue(readCpuFacet.getVendor().compareTo(updatedCpuFacet.getVendor()) == 0);
		Assert.assertTrue(((String) updatedCpuFacet.getAdditionalProperty(additionPropertyKey))
				.compareTo((String) readCpuFacet.getAdditionalProperty(additionPropertyKey)) == 0);
		Assert.assertTrue(uuid.compareTo(updatedCpuFacet.getHeader().getUUID()) == 0);

		CPUFacet readUpdatedCpuFacet = resourceRegistryClient.getInstance(CPUFacet.class, uuid);
		Assert.assertTrue(updatedCpuFacet.getClockSpeed().compareTo(readUpdatedCpuFacet.getClockSpeed()) == 0);
		Assert.assertTrue(updatedCpuFacet.getModel().compareTo(readUpdatedCpuFacet.getModel()) == 0);
		Assert.assertTrue(updatedCpuFacet.getVendor().compareTo(readUpdatedCpuFacet.getVendor()) == 0);
		Assert.assertTrue(((String) updatedCpuFacet.getAdditionalProperty(additionPropertyKey))
				.compareTo((String) readUpdatedCpuFacet.getAdditionalProperty(additionPropertyKey)) == 0);
		Assert.assertTrue(uuid.compareTo(updatedCpuFacet.getHeader().getUUID()) == 0);

		boolean deleted = resourceRegistryPublisher.deleteFacet(readUpdatedCpuFacet);
		Assert.assertTrue(deleted);
	}

	public Map<String, Resource> createHostingNodeAndEService() throws Exception {
		Map<String, Resource> map = new HashMap<>();

		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");

		IsIdentifiedBy<Resource, Facet> isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(eService,
				softwareFacet, null);
		eService.addFacet(isIdentifiedBy);

		eService = resourceRegistryPublisher.createResource(eService);
		map.put(EService.NAME, eService);

		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");

		networkingFacet = resourceRegistryPublisher.createFacet(networkingFacet);
		logger.debug("Unmarshalled {} {}", NetworkingFacet.NAME, networkingFacet);

		HostingNode hostingNode = new HostingNodeImpl();

		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		hostingNode.addFacet(cpuFacet);

		isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(hostingNode, networkingFacet, null);
		hostingNode.addFacet(isIdentifiedBy);

		PropagationConstraint propagationConstraint = new PropagationConstraintImpl();
		propagationConstraint.setRemoveConstraint(RemoveConstraint.cascade);
		propagationConstraint.setAddConstraint(AddConstraint.unpropagate);

		Hosts<HostingNode, EService> hosts = new HostsImpl<HostingNode, EService>(hostingNode, eService,
				propagationConstraint);
		hostingNode.attachResource(hosts);

		hostingNode = resourceRegistryPublisher.createResource(hostingNode);
		logger.debug("Unmarshalled {} {}", HostingNode.NAME, hostingNode);
		map.put(HostingNode.NAME, hostingNode);

		return map;
	}

	@Test
	public void testCreateHostingNodeAndEService() throws Exception {
		Map<String, Resource> map = createHostingNodeAndEService();

		boolean deleted = resourceRegistryPublisher.deleteResource(map.get(EService.NAME));
		Assert.assertTrue(deleted);

		deleted = resourceRegistryPublisher.deleteResource(map.get(HostingNode.NAME));
		Assert.assertTrue(deleted);
	}

	@Test
	public void testCreateHostingNodeAndEServiceWithSharedFacet() throws Exception {
		Map<String, Resource> map = createHostingNodeAndEService();

		EService eService = (EService) map.get(EService.NAME);
		HostingNode hostingNode = (HostingNode) map.get(HostingNode.NAME);

		Facet shared = hostingNode.getConsistsOf().get(0).getTarget();
		UUID sharedFacetUUID = shared.getHeader().getUUID();

		ConsistsOf<EService, Facet> consistsOf = new ConsistsOfImpl<EService, Facet>(eService, shared, null);
		consistsOf = resourceRegistryPublisher.createConsistsOf(consistsOf);

		boolean deleted = resourceRegistryPublisher.deleteResource(map.get(EService.NAME));
		Assert.assertTrue(deleted);

		deleted = resourceRegistryPublisher.deleteResource(map.get(HostingNode.NAME));
		Assert.assertTrue(deleted);

		try {
			resourceRegistryClient.getInstance(Facet.class, sharedFacetUUID);
			throw new Exception(String.format("Shared Facet %s was not deleted", shared));
		} catch (ResourceRegistryException e) {
			logger.debug("Shared Facet was not foud as expected");
		}
	}

	@Test
	public void testCreateResourceAndFacet() throws Exception {

		HostingNode hostingNode = new HostingNodeImpl();
		hostingNode = resourceRegistryPublisher.createResource(hostingNode);

		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");

		CPUFacet createdCpuFacet = resourceRegistryPublisher.createFacet(cpuFacet);

		ConsistsOf<HostingNode, Facet> consistsOf = new ConsistsOfImpl<HostingNode, Facet>(hostingNode, createdCpuFacet,
				null);
		consistsOf = resourceRegistryPublisher.createConsistsOf(consistsOf);

		UUID consistOfUUID = consistsOf.getHeader().getUUID();

		boolean detached = resourceRegistryPublisher.deleteConsistsOf(consistsOf);

		if (detached) {
			logger.trace("{} {} with uuid {} removed successfully", ConsistsOf.NAME, Relation.NAME, consistOfUUID);
		} else {
			String error = String.format("Unable to remove %s %s with uuid %s", ConsistsOf.NAME, Relation.NAME,
					consistOfUUID);
			logger.error(error);
			throw new Exception(error);
		}

		boolean deleted = resourceRegistryPublisher.deleteResource(hostingNode);
		Assert.assertTrue(deleted);

	}

	@Test
	public void testGetAll() throws Exception {
		Map<String, List<Resource>> resources = new HashMap<>();

		final int MAX = 5;
		int typeNumber = 0;

		for (int i = 0; i < MAX; i++) {
			Map<String, Resource> map = createHostingNodeAndEService();
			if (typeNumber == 0) {
				typeNumber = map.size();
			}
			for (String key : map.keySet()) {
				if (!resources.containsKey(key)) {
					resources.put(key, new ArrayList<Resource>());
				}
				resources.get(key).add(map.get(key));
			}
		}

		/* Getting all instances of created specific Resources */
		for (String key : resources.keySet()) {
			String instances = resourceRegistryClient.getInstances(key, false);
			List<Resource> list = ISMapper.unmarshalList(Resource.class, instances);
			logger.debug("{} are {} : {} ", key, list.size(), list);
			Assert.assertTrue(list.size() == MAX);
		}

		/* Getting all Resources polymorphic and non polymorphic */
		List<Resource> list = resourceRegistryClient.getInstances(Resource.class, true);
		logger.debug("{} are {} : {} ", Resource.NAME, list.size(), list);
		Assert.assertTrue(list.size() == (MAX * typeNumber));

		list = resourceRegistryClient.getInstances(Resource.class, false);
		Assert.assertTrue(list.size() == 0);

		/* Getting all IsRelatedTo polymorphic and non polymorphic */

		List<Resource> resourcesList = resourceRegistryClient.getInstances(IsRelatedTo.class, true);
		logger.debug("{} are {} : {} ", IsRelatedTo.NAME, resourcesList.size(), resourcesList);
		Assert.assertTrue(resourcesList.size() == MAX);

		resourcesList = resourceRegistryClient.getInstances(IsRelatedTo.class, false);
		Assert.assertTrue(resourcesList.size() == 0);

		/* Getting all ConsistsOf polymorphic and non polymorphic */

		List<Resource> consistsOfPolimorphicList = resourceRegistryClient.getInstances(ConsistsOf.class, true);
		logger.debug("{} are {} : {} ", IsRelatedTo.NAME, consistsOfPolimorphicList.size(), consistsOfPolimorphicList);

		List<Resource> consistsOfNonPolimorphicList = resourceRegistryClient.getInstances(ConsistsOf.class, false);
		logger.debug("{} are {} : {} ", IsRelatedTo.NAME, consistsOfNonPolimorphicList.size(),
				consistsOfNonPolimorphicList);

		Assert.assertTrue(consistsOfPolimorphicList.size() >= consistsOfNonPolimorphicList.size());

		/* Removing created Entity and Relation to have a clean DB */

		List<Resource> resourceList = resources.get(HostingNode.NAME);
		for (Resource r : resourceList) {
			boolean deleted = resourceRegistryPublisher.deleteResource(r);
			Assert.assertTrue(deleted);
		}
	}

	@Test
	public void testGetAllFrom() throws Exception {
		Map<String, Resource> map = createHostingNodeAndEService();

		EService eService = (EService) map.get(EService.NAME);
		UUID eServiceUUID = eService.getHeader().getUUID();

		HostingNode hostingNode = (HostingNode) map.get(HostingNode.NAME);
		UUID hostingNodeUUID = hostingNode.getHeader().getUUID();

		/* EService */
		List<Resource> resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, true,
				eServiceUUID, Direction.both);
		Assert.assertTrue(resourceList.size() == 1);
		Resource sourceResource = resourceList.get(0);
		Resource targetResource = sourceResource.getIsRelatedTo().get(0).getTarget();
		Assert.assertTrue(sourceResource.getHeader().getUUID().compareTo(hostingNodeUUID) == 0);
		Assert.assertTrue(targetResource.getHeader().getUUID().compareTo(eServiceUUID) == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, true, eServiceUUID,
				Direction.in);
		Assert.assertTrue(resourceList.size() == 1);
		sourceResource = resourceList.get(0);
		targetResource = sourceResource.getIsRelatedTo().get(0).getTarget();
		Assert.assertTrue(sourceResource.getHeader().getUUID().compareTo(hostingNodeUUID) == 0);
		Assert.assertTrue(targetResource.getHeader().getUUID().compareTo(eServiceUUID) == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, true, eServiceUUID,
				Direction.out);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, false, eServiceUUID,
				Direction.both);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, false, eServiceUUID,
				Direction.in);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, false, eServiceUUID,
				Direction.out);
		Assert.assertTrue(resourceList.size() == 0);
		/* END EService */

		/* Hosting Node */
		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, true, hostingNodeUUID,
				Direction.both);
		Assert.assertTrue(resourceList.size() == 1);
		sourceResource = resourceList.get(0);
		targetResource = sourceResource.getIsRelatedTo().get(0).getTarget();
		Assert.assertTrue(sourceResource.getHeader().getUUID().compareTo(hostingNodeUUID) == 0);
		Assert.assertTrue(targetResource.getHeader().getUUID().compareTo(eServiceUUID) == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, true, hostingNodeUUID,
				Direction.in);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, true, hostingNodeUUID,
				Direction.out);
		Assert.assertTrue(resourceList.size() == 1);
		sourceResource = resourceList.get(0);
		targetResource = sourceResource.getIsRelatedTo().get(0).getTarget();
		Assert.assertTrue(sourceResource.getHeader().getUUID().compareTo(hostingNodeUUID) == 0);
		Assert.assertTrue(targetResource.getHeader().getUUID().compareTo(eServiceUUID) == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, false, hostingNodeUUID,
				Direction.both);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, false, hostingNodeUUID,
				Direction.in);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(IsRelatedTo.class, false, hostingNodeUUID,
				Direction.out);
		Assert.assertTrue(resourceList.size() == 0);
		/* END HostingNode */

		Facet identificationFacet = eService.getIdentificationFacets().get(0);
		UUID identificationFacetUUID = identificationFacet.getHeader().getUUID();

		/* SoftwareFacet of Eservice */
		resourceList = resourceRegistryClient.getInstancesFromEntity(ConsistsOf.class, true, identificationFacetUUID,
				Direction.both);
		Assert.assertTrue(resourceList.size() == 1);
		sourceResource = resourceList.get(0);
		Facet targetIdentificationFacet = sourceResource.getIdentificationFacets().get(0);
		Assert.assertTrue(sourceResource.getHeader().getUUID().compareTo(eServiceUUID) == 0);
		Assert.assertTrue(targetIdentificationFacet.getHeader().getUUID().compareTo(identificationFacetUUID) == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(ConsistsOf.class, true, identificationFacetUUID,
				Direction.in);
		Assert.assertTrue(resourceList.size() == 1);
		sourceResource = resourceList.get(0);
		targetIdentificationFacet = sourceResource.getIdentificationFacets().get(0);
		Assert.assertTrue(sourceResource.getHeader().getUUID().compareTo(eServiceUUID) == 0);
		Assert.assertTrue(targetIdentificationFacet.getHeader().getUUID().compareTo(identificationFacetUUID) == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(ConsistsOf.class, true, identificationFacetUUID,
				Direction.out);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(ConsistsOf.class, false, identificationFacetUUID,
				Direction.both);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(ConsistsOf.class, false, identificationFacetUUID,
				Direction.in);
		Assert.assertTrue(resourceList.size() == 0);

		resourceList = resourceRegistryClient.getInstancesFromEntity(ConsistsOf.class, false, identificationFacetUUID,
				Direction.out);
		Assert.assertTrue(resourceList.size() == 0);
		/* END SoftwareFacet of Eservice */

		/* Removing created Entity and Relation to have a clean DB */
		boolean deleted = resourceRegistryPublisher.deleteResource(hostingNode);
		Assert.assertTrue(deleted);
	}

	@Test
	public void testCreateHostingNodeThenHostsWithEService() throws Exception {

		HostingNode hostingNode = new HostingNodeImpl();

		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		hostingNode.addFacet(cpuFacet);

		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");

		IsIdentifiedBy<HostingNode, NetworkingFacet> hnIsIdentifiedBy = new IsIdentifiedByImpl<HostingNode, NetworkingFacet>(
				hostingNode, networkingFacet, null);
		hostingNode.addFacet(hnIsIdentifiedBy);

		hostingNode = resourceRegistryPublisher.createResource(hostingNode);

		/* -------- */

		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");

		IsIdentifiedBy<Resource, Facet> isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(eService,
				softwareFacet, null);
		eService.addFacet(isIdentifiedBy);

		PropagationConstraint propagationConstraint = new PropagationConstraintImpl();
		propagationConstraint.setRemoveConstraint(RemoveConstraint.cascade);
		propagationConstraint.setAddConstraint(AddConstraint.unpropagate);

		Hosts<HostingNode, EService> hosts = new HostsImpl<HostingNode, EService>(hostingNode, eService,
				propagationConstraint);

		hosts = resourceRegistryPublisher.createIsRelatedTo(hosts);
		hostingNode.attachResource(hosts);

		logger.debug("Created Hosts is {}", ISMapper.marshal(hosts));

		boolean deleted = resourceRegistryPublisher.deleteResource(hostingNode);
		Assert.assertTrue(deleted);

	}

}
