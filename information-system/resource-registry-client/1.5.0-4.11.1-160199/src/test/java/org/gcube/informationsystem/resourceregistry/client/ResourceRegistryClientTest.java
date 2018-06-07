/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.impl.entity.resource.HostingNodeImpl;
import org.gcube.informationsystem.impl.utils.ISMapper;
import org.gcube.informationsystem.model.embedded.Header;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.entity.facet.SoftwareFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.IsIdentifiedBy;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistryClientTest extends ScopedTest {

	private static Logger logger = LoggerFactory
			.getLogger(ResourceRegistryClientTest.class);
	
	protected ResourceRegistryClient resourceRegistryClient;
	
	public ResourceRegistryClientTest(){
		resourceRegistryClient = ResourceRegistryClientFactory.create();
	}
	
	@Test
	public void testQuery() throws ResourceRegistryException{
		String res = resourceRegistryClient.query("SELECT FROM V", 0, null);
		logger.trace(res);
	}
	
	@Test
	public void testGetFacetSchema() throws SchemaNotFoundException, ResourceRegistryException {
		List<TypeDefinition> typeDefinitions = resourceRegistryClient.getSchema(ContactFacet.class, true);
		logger.trace("{}", typeDefinitions);
	}
	
	@Test
	public void testGetResourceSchema() throws SchemaNotFoundException, ResourceRegistryException {
		List<TypeDefinition> typeDefinitions = resourceRegistryClient.getSchema(HostingNode.class, true);
		logger.trace("{}", typeDefinitions);
	}
	
	interface Aux extends Service {
		
		
	}
	
	@Test(expected=SchemaNotFoundException.class)
	public void testException() throws SchemaNotFoundException, ResourceRegistryException {
		resourceRegistryClient.getSchema(Aux.class, true);
	}
	
	@Test
	public void testExists() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		resourceRegistryClient.exists(EService.NAME, uuid);
	}
	
	@Test
	public void testExistsByClass() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		resourceRegistryClient.exists(EService.class, uuid);
	}
	
	@Test
	public void testGetInstance() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		String eService = resourceRegistryClient.getInstance(EService.NAME, uuid);
		logger.trace("{}", eService);
	}
	
	@Test
	public void testGetInstanceByClass() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		EService eService = resourceRegistryClient.getInstance(EService.class, uuid);
		logger.trace("{}", eService);
	}
	
	@Test
	public void testGetInstances() throws ResourceRegistryException {
		String eServices = resourceRegistryClient.getInstances(EService.NAME, true);
		logger.trace("{}", eServices);
	}
	
	@Test
	public void testGetInstancesByClass() throws ResourceRegistryException {
		List<EService> eServices = resourceRegistryClient.getInstances(EService.class, true);
		logger.trace("{}", eServices);
	}
	
	@Test
	public void testGetInstancesFromEntity() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("b0d15e45-62af-4221-b785-7d014f10e631");
		String eServices = resourceRegistryClient.getInstancesFromEntity(EService.NAME, true, uuid, Direction.out);
		logger.trace("{}", eServices);
	}
	
	@Test
	public void testGetInstancesFromEntityByClass() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("b0d15e45-62af-4221-b785-7d014f10e631");
		List<EService> eServices = resourceRegistryClient.getInstancesFromEntity(EService.class, true, uuid, Direction.out);
		logger.trace("{}", eServices);
	}
	
	@Test
	public void testGetInstancesFromEntityByClasses() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("b0d15e45-62af-4221-b785-7d014f10e631");
		HostingNode hostingNode = new HostingNodeImpl();
		Header header = new HeaderImpl(uuid);
		hostingNode.setHeader(header);
		List<EService> eServices = resourceRegistryClient.getInstancesFromEntity(EService.class, true, hostingNode, Direction.out);
		logger.trace("{}", eServices);
	}
	
	
	@Test
	public void testGetFilteredResources() throws ResourceRegistryException, JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		map.put("group", "VREManagement");
		map.put("name", "SmartExecutor");
		String json = resourceRegistryClient.getFilteredResources(EService.NAME, IsIdentifiedBy.NAME, SoftwareFacet.NAME, true, map);
		logger.trace(json);
	}
	
	@Test
	public void testGetFilteredResourcesByClasses() throws ResourceRegistryException, JsonProcessingException {
		Map<String, Object> map = new HashMap<>();
		map.put("group", "VREManagement");
		map.put("name", "SmartExecutor");
		List<EService> eServices = resourceRegistryClient.getFilteredResources(EService.class, IsIdentifiedBy.class, SoftwareFacet.class, true, map);
		for(EService eService : eServices) {
			logger.trace("{}", ISMapper.marshal(eService));
		}
	}
	
}
