/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.informationsystem.model.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.model.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.impl.relation.IsIdentifiedByImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.impl.utils.Utility;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.model.reference.relation.IsIdentifiedBy;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.model.reference.relation.Relation;
import org.gcube.informationsystem.resourceregistry.ScopedTest;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.query.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.ConsistsOfManagement;
import org.gcube.informationsystem.resourceregistry.er.relation.IsRelatedToManagement;
import org.gcube.resourcemanagement.model.impl.entity.facet.AccessPointFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.CPUFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.EventFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.LicenseFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.NetworkingFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.ServiceStateFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.SimpleFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.facet.StateFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.resource.EServiceImpl;
import org.gcube.resourcemanagement.model.impl.entity.resource.HostingNodeImpl;
import org.gcube.resourcemanagement.model.impl.relation.isrelatedto.ActivatesImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.AccessPointFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.CPUFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.EventFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.LicenseFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.NetworkingFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.ServiceStateFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.SimpleFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.SoftwareFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.StateFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.Configuration;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;
import org.gcube.resourcemanagement.model.reference.entity.resource.HostingNode;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Activates;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tinkerpop.blueprints.Direction;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class ERManagementTest extends ScopedTest {

	private static Logger logger = LoggerFactory
			.getLogger(ERManagementTest.class);
	
	@Test
	public void testCreateEService() throws Exception {
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
		
		/*
		List<Map<String, String>> list = new ArrayList<>();
		Map<String, String> map1 = new HashMap<String, String>();
		map1.put("Key1", "Value1");
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("Key2", "Value2");
		list.add(map1);
		list.add(map2);
		
		serviceStateFacet.setAdditionalProperty("MY-TEST", list);
		*/
		
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

		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eService.getHeader().getUUID());
		
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
	}
	
	//@Test
	public void testReadResource() throws Exception {
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID.fromString("26da57ee-33bd-4c4b-8aef-9206b61c329e"));

		String read= resourceManagement.read();
		logger.debug(read);

	}

	//@Test
	public void testDeleteResource() throws Exception {
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(UUID
				.fromString("64635295-7ced-4931-a55f-40fc8199b280"));

		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);

	}

	@Test(expected=ResourceRegistryException.class)
	public void testCreateAbstractEntity() throws Exception {
		StateFacet stateFacet = new StateFacetImpl();
		stateFacet.setValue("READY");
		
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setElementType(StateFacet.NAME);
		String json = ISMapper.marshal(stateFacet);
		logger.debug(json);
		facetManagement.setJSON(json);
		
		facetManagement.create();
	}
	
	@Test(expected=ResourceRegistryException.class)
	public void testCreateAnEntityDifferentFromDeclared() throws Exception {
		SimpleFacet simpleFacet = new SimpleFacetImpl();
		
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setElementType(ContactFacet.NAME);
		facetManagement.setJSON(ISMapper.marshal(simpleFacet));
		
		facetManagement.create();
	}
	
	
	@Test
	public void testCreateReadUpdateDeleteFacet() throws Exception {
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");

		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setElementType(CPUFacet.NAME);
		facetManagement.setJSON(ISMapper.marshal(cpuFacet));

		String cpuFacetJson = facetManagement.create();
		CPUFacet createdCpuFacet = ISMapper.unmarshal(CPUFacet.class,
				cpuFacetJson);
		logger.debug("Created:\nRaw Json : {}\nUnmarshalled : {}",
				cpuFacetJson, createdCpuFacet);

		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(
				createdCpuFacet.getClockSpeed()) == 0);
		Assert.assertTrue(cpuFacet.getModel().compareTo(
				createdCpuFacet.getModel()) == 0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(
				createdCpuFacet.getVendor()) == 0);

		UUID uuid = createdCpuFacet.getHeader().getUUID();

		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);

		String readJson = facetManagement.read();
		CPUFacet readCpuFacet = ISMapper.unmarshal(CPUFacet.class, readJson);
		logger.debug("Read:\nRaw Json : {}\nUnmarshalled : {}", readJson,
				readCpuFacet);
		Assert.assertTrue(cpuFacet.getClockSpeed().compareTo(
				readCpuFacet.getClockSpeed()) == 0);
		Assert.assertTrue(cpuFacet.getModel()
				.compareTo(readCpuFacet.getModel()) == 0);
		Assert.assertTrue(cpuFacet.getVendor().compareTo(
				readCpuFacet.getVendor()) == 0);
		Assert.assertTrue(uuid.compareTo(readCpuFacet.getHeader().getUUID()) == 0);

		ScopedTest.setContext(DEFAULT_TEST_SCOPE_ANOTHER_USER);
		
		String newVendor = "Intel";
		String newClockSpeed = "2 GHz";
		readCpuFacet.setVendor(newVendor);
		readCpuFacet.setClockSpeed(newClockSpeed);

		String additionPropertyKey = "My";
		String additionPropertyValue = "Test";
		readCpuFacet.setAdditionalProperty(additionPropertyKey,
				additionPropertyValue);

		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);
		facetManagement.setJSON(ISMapper.marshal(readCpuFacet));

		String updatedJson = facetManagement.update();
		CPUFacet updatedCpuFacet = ISMapper.unmarshal(CPUFacet.class,
				updatedJson);
		logger.debug("Updated:\nRaw Json : {}\nUnmarshalled : {}", updatedJson,
				updatedCpuFacet);
		Assert.assertTrue(updatedCpuFacet.getClockSpeed().compareTo(
				newClockSpeed) == 0);
		Assert.assertTrue(readCpuFacet.getModel().compareTo(
				updatedCpuFacet.getModel()) == 0);
		Assert.assertTrue(updatedCpuFacet.getVendor().compareTo(
				newVendor) == 0);
		Assert.assertTrue(((String) updatedCpuFacet
				.getAdditionalProperty(additionPropertyKey))
				.compareTo((String) readCpuFacet
						.getAdditionalProperty(additionPropertyKey)) == 0);
		Assert.assertTrue(uuid.compareTo(updatedCpuFacet.getHeader().getUUID()) == 0);
		String user = AuthorizationProvider.instance.get().getClient().getId();
		Assert.assertTrue(updatedCpuFacet.getHeader().getModifiedBy().compareTo(user)==0);
		

		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);

		String readUpdatedJson = facetManagement.read();
		CPUFacet readUpdatedCpuFacet = ISMapper.unmarshal(CPUFacet.class,
				readUpdatedJson);
		logger.debug("Read Updated:\nRaw Json : {}\nUnmarshalled : {}",
				readUpdatedJson, readUpdatedCpuFacet);
		Assert.assertTrue(updatedCpuFacet.getClockSpeed().compareTo(
				readUpdatedCpuFacet.getClockSpeed()) == 0);
		Assert.assertTrue(updatedCpuFacet.getModel().compareTo(
				readUpdatedCpuFacet.getModel()) == 0);
		Assert.assertTrue(updatedCpuFacet.getVendor().compareTo(
				readUpdatedCpuFacet.getVendor()) == 0);
		Assert.assertTrue(((String) updatedCpuFacet
				.getAdditionalProperty(additionPropertyKey))
				.compareTo((String) readUpdatedCpuFacet
						.getAdditionalProperty(additionPropertyKey)) == 0);
		Assert.assertTrue(uuid.compareTo(updatedCpuFacet.getHeader().getUUID()) == 0);
		
		facetManagement = new FacetManagement();
		facetManagement.setUUID(uuid);

		boolean deleted = facetManagement.delete();
		Assert.assertTrue(deleted);
	}

	
	public Map<String, Resource> createHostingNodeAndEService() throws Exception {
		Map<String, Resource> map = new HashMap<>();
		
		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");

		IsIdentifiedBy<Resource, Facet> isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(
				eService, softwareFacet, null);
		eService.addFacet(isIdentifiedBy);
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(EService.NAME);
		resourceManagement.setJSON(ISMapper.marshal(eService));
		
		String json = resourceManagement.create();
		logger.debug("Created : {}", json);
		eService = ISMapper.unmarshal(EService.class, json);
		logger.debug("Unmarshalled {} {}", EService.NAME, eService);
		map.put(EService.NAME, eService);
		
		
		
		
		NetworkingFacet networkingFacet = new NetworkingFacetImpl();
		networkingFacet.setIPAddress("146.48.87.183");
		networkingFacet.setHostName("pc-frosini.isti.cnr.it");
		networkingFacet.setDomainName("isti.cnr.it");
		networkingFacet.setMask("255.255.248.0");
		networkingFacet.setBroadcastAddress("146.48.87.255");

		HostingNode hostingNode = new HostingNodeImpl();
		isIdentifiedBy = new IsIdentifiedByImpl<Resource, Facet>(hostingNode,
				networkingFacet, null);
		hostingNode.addFacet(isIdentifiedBy);
		
		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		hostingNode.addFacet(cpuFacet);

		
		PropagationConstraint propagationConstraint = new PropagationConstraintImpl();
		propagationConstraint.setRemoveConstraint(RemoveConstraint.cascade);
		
		Activates<HostingNode, EService> activates = new ActivatesImpl<HostingNode, EService>(
				hostingNode, eService, propagationConstraint);
		hostingNode.attachResource(activates);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(HostingNode.NAME);
		resourceManagement.setJSON(ISMapper.marshal(hostingNode));
		
		json = resourceManagement.create();
		logger.debug("Created : {}", json);
		hostingNode = ISMapper.unmarshal(HostingNode.class, json);
		logger.debug("Unmarshalled {} {}", HostingNode.NAME, hostingNode);
		map.put(HostingNode.NAME, hostingNode);
		
		return map;
	}
	
	@Test
	public void testCreateHostingNodeAndEService() throws Exception {
		Map<String, Resource> map = createHostingNodeAndEService();
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(map.get(EService.NAME).getHeader().getUUID());
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(map.get(HostingNode.NAME).getHeader().getUUID());
		deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
	}
	
	
	@Test
	public void testCreateConsistsOfBeetweenResources() throws Exception {
		Map<String, Resource> map = createHostingNodeAndEService();
		
		UUID hostingNodeUUID = map.get(HostingNode.NAME).getHeader().getUUID();
		UUID eServiceUUID = map.get(EService.NAME).getHeader().getUUID();
		
		HostingNode hostingNode = new HostingNodeImpl();
		hostingNode.setHeader(new HeaderImpl(hostingNodeUUID));
		
		SimpleFacet fakeEServiceAsSimpleFacet = new SimpleFacetImpl();
		fakeEServiceAsSimpleFacet.setHeader(new HeaderImpl(eServiceUUID));
		
		ConsistsOf<Resource, Facet> consistsOf = new ConsistsOfImpl<Resource, Facet>(hostingNode, fakeEServiceAsSimpleFacet, null);
		
		try {
			ConsistsOfManagement consistsOfManagement = new ConsistsOfManagement();
			String json = ISMapper.marshal(consistsOf);
			json = json.replaceAll(SimpleFacet.NAME, EService.NAME);
			
			consistsOfManagement.setJSON(json);
			
			consistsOfManagement.create();
			logger.debug("The creation terminated correctly. This should not happen");
			
			
		} catch (ResourceRegistryException e) {
			logger.error("Sounds good. A {} cannot be created between two resources", ConsistsOf.NAME, e);
		} finally {
			ResourceManagement resourceManagement = new ResourceManagement();
			resourceManagement.setUUID(eServiceUUID);
			boolean deleted = resourceManagement.delete();
			Assert.assertTrue(deleted);
			
			resourceManagement = new ResourceManagement();
			resourceManagement.setUUID(hostingNodeUUID);
			deleted = resourceManagement.delete();
			Assert.assertTrue(deleted);
		}
		
	}
	
	@Test
	public void testCreateHostingNodeAndEServiceWithSharedFacet() throws Exception {
		Map<String, Resource> map = createHostingNodeAndEService();
		
		EService eService = (EService) map.get(EService.NAME);
		HostingNode hostingNode = (HostingNode) map.get(HostingNode.NAME);
		
		Facet shared = hostingNode.getConsistsOf().get(0).getTarget();
		
		
		ConsistsOfManagement consistsOfManagement = new ConsistsOfManagement();
		consistsOfManagement.setElementType(ConsistsOf.NAME);
		consistsOfManagement.setJSON("{}");
		ConsistsOf<EService, Facet> consistsOf = new ConsistsOfImpl<>(eService, shared, null);
		consistsOfManagement.setJSON(ISMapper.marshal(consistsOf));
		
		String json = consistsOfManagement.create();
		logger.debug("Created : {}", json);
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eService.getHeader().getUUID());
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(hostingNode.getHeader().getUUID());
		deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
		
		FacetManagement facetManagement = new FacetManagement();
		UUID sharedFacetUUID = shared.getHeader().getUUID();
		facetManagement.setUUID(sharedFacetUUID);
		
		try {
			String read = facetManagement.read();
			logger.debug("Read facet is : {}", read);
			throw new Exception(String.format("Shared Facet %s was not deleted", shared));
		}catch(FacetNotFoundException e){
			logger.debug("Shared Facet was not foud as expected");
		}		
	}
	
	@Test
	public void testCreateResourceAndFacet() throws Exception {
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(HostingNode.NAME);
		resourceManagement.setJSON("{}");
		
		String json = resourceManagement.create();
		HostingNode hostingNode = ISMapper.unmarshal(HostingNode.class, json);
		UUID resourceUUID = hostingNode.getHeader().getUUID();

		CPUFacet cpuFacet = new CPUFacetImpl();
		cpuFacet.setClockSpeed("1 GHz");
		cpuFacet.setModel("Opteron");
		cpuFacet.setVendor("AMD");
		
		FacetManagement facetManagement = new FacetManagement();
		facetManagement.setJSON(ISMapper.marshal(cpuFacet));
		facetManagement.setElementType(CPUFacet.NAME);
		json = facetManagement.create();
		CPUFacet createdCPUFacet = ISMapper.unmarshal(CPUFacet.class, json);

		ConsistsOfManagement consistsOfManagement = new ConsistsOfManagement();
		consistsOfManagement.setElementType(ConsistsOf.NAME);
		ConsistsOf<HostingNode, CPUFacet> consistsOf = new ConsistsOfImpl<>(hostingNode, createdCPUFacet, null);
		consistsOfManagement.setJSON(ISMapper.marshal(consistsOf));
		json = consistsOfManagement.create();
		
		logger.debug("Facet attached : {}", json);

		UUID consistOfUUID = Utility.getUUIDFromJSONString(json);

		consistsOfManagement = new ConsistsOfManagement();
		consistsOfManagement.setUUID(consistOfUUID);
		
		boolean detached = consistsOfManagement.delete();

		if (detached) {
			logger.trace("{} {} with uuid {} removed successfully",
					ConsistsOf.NAME, Relation.NAME, consistOfUUID);
		} else {
			String error = String.format("Unable to remove %s %s with uuid %s",
					ConsistsOf.NAME, Relation.NAME, consistOfUUID);
			logger.error(error);
			throw new Exception(error);
		}
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(resourceUUID);
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
	}
	
	
	@Test
	public void testGetAll() throws Exception{
		Map<String, List<Resource>> resources = new HashMap<>();
		
		final int MAX = 5;
		int typeNumber = 0;
		
		for(int i=0; i<MAX; i++){
			Map<String, Resource> map  = createHostingNodeAndEService();
			if(typeNumber==0){
				typeNumber = map.size();
			}
			for(String key : map.keySet()){
				if(!resources.containsKey(key)){
					resources.put(key, new ArrayList<Resource>());
				}
				resources.get(key).add(map.get(key));
			}
		}
		
		/* Getting all instances of created specific Resources*/
		for(String key : resources.keySet()){
			ResourceManagement resourceManagement = (ResourceManagement) ERManagementUtility.getERManagement(key);
			String json = resourceManagement.all(false);
			
			List<Resource> list = ISMapper.unmarshalList(Resource.class, json);
			logger.debug("{} are {} : {} ", key, list.size(), list);
			Assert.assertTrue(list.size()==MAX);
		}
		
		
		/* Getting all Resources polymorphic and non polymorphic */
		
		ResourceManagement resourceManagement = (ResourceManagement) ERManagementUtility.getERManagement(Resource.NAME);
		
		String json = resourceManagement.all(true);
		List<Resource> list = ISMapper.unmarshalList(Resource.class, json);
		logger.debug("{} are {} : {} ", Resource.NAME, list.size(), list);
		Assert.assertTrue(list.size()==(MAX*typeNumber));
		
		
		json = resourceManagement.all(false);
		list = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(list.size()==0);
		
		
		/* Getting all IsRelatedTo polymorphic and non polymorphic */
		
		IsRelatedToManagement isRelatedToManagement = (IsRelatedToManagement) ERManagementUtility.getERManagement(IsRelatedTo.NAME);
		
		json = isRelatedToManagement.all(true);

		List<Resource> resourcesList = ISMapper.unmarshalList(Resource.class, json);
		logger.debug("{} are {} : {} ", IsRelatedTo.NAME, resourcesList.size(), resourcesList);
		Assert.assertTrue(resourcesList.size()==MAX);

		
		json = isRelatedToManagement.all(false);
		resourcesList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourcesList.size()==0);
		
		
		
		
		/* Getting all ConsistsOf polymorphic and non polymorphic */
		
		ConsistsOfManagement consistsOfManagement = (ConsistsOfManagement) ERManagementUtility.getERManagement(ConsistsOf.NAME);
		
		json = consistsOfManagement.all(true);
		List<Resource> consistsOfPolimorphicList = ISMapper.unmarshalList(Resource.class, json);
		logger.debug("{} are {} : {} ", IsRelatedTo.NAME, consistsOfPolimorphicList.size(), consistsOfPolimorphicList);
		

		json = consistsOfManagement.all(false);
		List<Resource> consistsOfNonPolimorphicList = ISMapper.unmarshalList(Resource.class, json);
		logger.debug("{} are {} : {} ", IsRelatedTo.NAME, consistsOfNonPolimorphicList.size(), consistsOfNonPolimorphicList);
		
		Assert.assertTrue(consistsOfPolimorphicList.size()>=consistsOfNonPolimorphicList.size());
		
		
		
		
		/* Removing created Entity and Relation to have a clean DB */
		
		List<Resource> resourceList = resources.get(HostingNode.NAME);
		for(Resource r : resourceList){
			resourceManagement = new ResourceManagement();
			resourceManagement.setUUID(r.getHeader().getUUID());
			boolean deleted = resourceManagement.delete();
			Assert.assertTrue(deleted);
		}
	}
	
	@Test
	public void testGetAllFrom() throws Exception{
		
		Map<String, Resource> map  = createHostingNodeAndEService();
		
		EService eService = (EService) map.get(EService.NAME);
		UUID eServiceUUID = eService.getHeader().getUUID();
		
		HostingNode hostingNode = (HostingNode) map.get(HostingNode.NAME);
		UUID hostingNodeUUID = hostingNode.getHeader().getUUID();
		
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(Service.NAME);
		
		/* Getting Hosting Node */
		String json = resourceManagement.query(IsRelatedTo.NAME, EService.NAME, eServiceUUID, Direction.BOTH, true, null);
		List<Resource> resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==1);
		Resource resource = resourceList.get(0);
		Assert.assertTrue(resource.getHeader().getUUID().compareTo(hostingNodeUUID)==0);
		
		json = resourceManagement.query(IsRelatedTo.NAME, EService.NAME, eServiceUUID, Direction.OUT, true, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==1);
		resource = resourceList.get(0);
		Assert.assertTrue(resource.getHeader().getUUID().compareTo(hostingNodeUUID)==0);
		
		json = resourceManagement.query(IsRelatedTo.NAME, EService.NAME, eServiceUUID, Direction.IN, true, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		
		
		
		json  = resourceManagement.query(IsRelatedTo.NAME, EService.NAME, eServiceUUID, Direction.BOTH, false, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		json = resourceManagement.query(IsRelatedTo.NAME, EService.NAME, eServiceUUID, Direction.OUT, false, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		json = resourceManagement.query(IsRelatedTo.NAME, EService.NAME, eServiceUUID, Direction.IN, false, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		/* END Getting Hosting Node */
		

		
		/* Getting EService */
		json  = resourceManagement.query(IsRelatedTo.NAME, HostingNode.NAME, hostingNodeUUID, Direction.BOTH, true, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==1);
		Assert.assertTrue(resourceList.get(0).getHeader().getUUID().compareTo(eServiceUUID)==0);
		
		json = resourceManagement.query(IsRelatedTo.NAME, HostingNode.NAME, hostingNodeUUID, Direction.OUT, true, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		
		json = resourceManagement.query(IsRelatedTo.NAME, HostingNode.NAME, hostingNodeUUID, Direction.IN, true, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==1);
		Assert.assertTrue(resourceList.get(0).getHeader().getUUID().compareTo(eServiceUUID)==0);
		
		json  = resourceManagement.query(IsRelatedTo.NAME, HostingNode.NAME, hostingNodeUUID, Direction.BOTH, false, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		
		json = resourceManagement.query(IsRelatedTo.NAME, HostingNode.NAME, hostingNodeUUID, Direction.OUT, false, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		
		json = resourceManagement.query(IsRelatedTo.NAME, HostingNode.NAME, hostingNodeUUID, Direction.IN, false, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		/* END Getting HostingNode */
		
		
		Facet identificationFacet = eService.getIdentificationFacets().get(0);
		UUID identificationFacetUUID = identificationFacet.getHeader().getUUID();
		
		/* EService --ConsistsOf--> SoftwareFacet*/
		try {
			json  = resourceManagement.query(ConsistsOf.NAME, SoftwareFacet.NAME, identificationFacetUUID, Direction.BOTH, true, null);
		}catch(InvalidQueryException e) {
			// Ok expected
		}
		
		json  = resourceManagement.query(ConsistsOf.NAME, SoftwareFacet.NAME, identificationFacetUUID, Direction.OUT, true, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==1);
		resource = resourceList.get(0);
		Facet targetIdentificationFacet = resource.getIdentificationFacets().get(0);
		Assert.assertTrue(resource.getHeader().getUUID().compareTo(eServiceUUID)==0);
		Assert.assertTrue(targetIdentificationFacet.getHeader().getUUID().compareTo(identificationFacetUUID)==0);
		
		try {
			json  = resourceManagement.query(ConsistsOf.NAME, SoftwareFacet.NAME, identificationFacetUUID, Direction.IN, true, null);
		}catch(InvalidQueryException e) {
			// Ok expected
		}
		
		
		try {
			json  = resourceManagement.query(ConsistsOf.NAME, SoftwareFacet.NAME, identificationFacetUUID, Direction.BOTH, false, null);
		}catch(InvalidQueryException e) {
			// Ok expected
		}
		
		json  = resourceManagement.query(ConsistsOf.NAME, SoftwareFacet.NAME, identificationFacetUUID, Direction.OUT, false, null);
		resourceList = ISMapper.unmarshalList(Resource.class, json);
		Assert.assertTrue(resourceList.size()==0);
		
		try {
			json  = resourceManagement.query(ConsistsOf.NAME, SoftwareFacet.NAME, identificationFacetUUID, Direction.IN, false, null);
		}catch(InvalidQueryException e) {
			// Ok expected
		}
		
		/* END EService --ConsistsOf--> SoftwareFacet*/
		
		
		
		/* Removing created Entity and Relation to have a clean DB */
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(hostingNode.getHeader().getUUID());
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
		
	}
	
	public static final String TEST_RESOURCE = "test-resource.json";
	
	//@Test
	public void testUpdateResourceFromFile() throws JsonParseException, JsonMappingException, IOException, ResourceRegistryException{
		File file = new File("src/test/resources/" + TEST_RESOURCE);
		
		logger.debug("{}", file.getAbsolutePath());
		
		FileInputStream fileInputStream = new FileInputStream(file);
		EService eService = ISMapper.unmarshal(EService.class, fileInputStream);
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eService.getHeader().getUUID());
		resourceManagement.setJSON(ISMapper.marshal(eService));
		
		resourceManagement.update();
		
	}
	
	// @Test
	public void readSingleResource() throws ResourceRegistryException, JsonParseException, JsonMappingException, IOException{
		UUID uuid = UUID.fromString("");
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(uuid);
		String res = resourceManagement.read();
		logger.debug(res);
		
		Configuration configuration = ISMapper.unmarshal(Configuration.class, res);
		
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(configuration.getHeader().getUUID());
		resourceManagement.setJSON(ISMapper.marshal(configuration));
		
		resourceManagement.update();
		
	}
	
	
	
	
	@Test
	public void testCreateUpdateDeleteEService() throws Exception {
		EService eService = new EServiceImpl();

		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		softwareFacet.setGroup("InformationSystem");
		softwareFacet.setName("resource-registry");
		softwareFacet.setVersion("1.1.0");
		IsIdentifiedBy<EService, Facet> isIdentifiedBy = new IsIdentifiedByImpl<EService, Facet>(
				eService, softwareFacet, null);
		eService.addFacet(isIdentifiedBy);

		/*
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
		 */
		
		ResourceManagement resourceManagement = new ResourceManagement();
		resourceManagement.setElementType(EService.NAME);
		resourceManagement.setJSON(ISMapper.marshal(eService));
		String json = resourceManagement.create();
		
		logger.trace("Created {}", json);
		
		eService = ISMapper.unmarshal(EService.class, json);
		final String newVersion = "1.2.0";
		eService.getFacets(SoftwareFacet.class).get(0).setVersion(newVersion);
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eService.getHeader().getUUID());
		resourceManagement.setJSON(ISMapper.marshal(eService));
		
		json = resourceManagement.update();
		logger.trace("Updated {}", json);
		eService = ISMapper.unmarshal(EService.class, json);
		
		Assert.assertTrue(eService.getFacets(SoftwareFacet.class).get(0).getVersion().compareTo(newVersion)==0);
		
		
		resourceManagement = new ResourceManagement();
		resourceManagement.setUUID(eService.getHeader().getUUID());
		
		boolean deleted = resourceManagement.delete();
		Assert.assertTrue(deleted);
	}

	
}
