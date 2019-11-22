/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.informationsystem.model.impl.embedded.HeaderImpl;
import org.gcube.informationsystem.model.impl.utils.ISMapper;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.IsIdentifiedBy;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.gcube.resourcemanagement.model.impl.entity.facet.SoftwareFacetImpl;
import org.gcube.resourcemanagement.model.impl.entity.resource.HostingNodeImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;
import org.gcube.resourcemanagement.model.reference.entity.facet.SoftwareFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;
import org.gcube.resourcemanagement.model.reference.entity.resource.HostingNode;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ResourceRegistryClientTest extends ScopedTest {
	
	private static Logger logger = LoggerFactory.getLogger(ResourceRegistryClientTest.class);
	
	protected ResourceRegistryClient resourceRegistryClient;
	
	public ResourceRegistryClientTest() {
		resourceRegistryClient = ResourceRegistryClientFactory.create();
	}
	
	@Test
	public void testQuery() throws ResourceRegistryException {
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
	
	@Test(expected = SchemaNotFoundException.class)
	public void testException() throws SchemaNotFoundException, ResourceRegistryException {
		resourceRegistryClient.getSchema(Aux.class, true);
	}
	
	/* The following tests are commented because we need to create the instances for tests. this is done in
	 * resource registry publisher, which uses client APis to test the publishing as weel as this client.
	 */
	
	// @Test
	public void testExists() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		resourceRegistryClient.exists(EService.NAME, uuid);
	}
	
	// @Test
	public void testExistsByClass() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		resourceRegistryClient.exists(EService.class, uuid);
	}
	
	// @Test
	public void testGetInstance() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		String eService = resourceRegistryClient.getInstance(EService.NAME, uuid);
		logger.trace("{}", eService);
	}
	
	// @Test
	public void testGetInstanceByClass() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("bdaccb35-7f27-45a6-8ca9-11d467cb9233");
		EService eService = resourceRegistryClient.getInstance(EService.class, uuid);
		logger.trace("{}", eService);
	}
	
	// @Test
	public void testGetInstances() throws ResourceRegistryException {
		String eServices = resourceRegistryClient.getInstances(EService.NAME, true);
		logger.trace("{}", eServices);
	}
	
	// @Test
	public void testGetInstancesByClass() throws ResourceRegistryException {
		List<EService> eServices = resourceRegistryClient.getInstances(EService.class, true);
		logger.trace("{}", eServices);
	}
	
	// @Test
	public void testGetRelatedResourcesByClasses() throws ResourceRegistryException {
		List<EService> eServices = resourceRegistryClient.getRelatedResources(EService.class, IsRelatedTo.class,
				Resource.class, Direction.out, true);
		logger.trace("{}", eServices);
	}
	
	// @Test
	public void testGetRelatedResourcesFromReferenceResourceByClasses() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("b0d15e45-62af-4221-b785-7d014f10e631");
		HostingNode hostingNode = new HostingNodeImpl();
		Header header = new HeaderImpl(uuid);
		hostingNode.setHeader(header);
		List<EService> eServices = resourceRegistryClient.getRelatedResourcesFromReferenceResource(EService.class,
				IsRelatedTo.class, HostingNode.class, hostingNode, Direction.out, true);
		logger.trace("{}", eServices);
	}
	
	// @Test
	public void testGetFilteredResourcesByClasses() throws ResourceRegistryException, JsonProcessingException {
		Map<String,Object> map = new HashMap<>();
		map.put("group", "VREManagement");
		map.put("name", "SmartExecutor");
		List<EService> eServices = resourceRegistryClient.getFilteredResources(EService.class, IsIdentifiedBy.class,
				SoftwareFacet.class, true, map);
		for(EService eService : eServices) {
			logger.trace("{}", ISMapper.marshal(eService));
		}
	}
	
	
	// @Test
	public void testGetResourcesFromReferenceFacet() throws ResourceRegistryException, JsonProcessingException {
		SoftwareFacet softwareFacet = new SoftwareFacetImpl();
		UUID uuid = UUID.fromString("cbdf3e61-524c-4800-91a6-3ff3e06fbee3");
		Header header = new HeaderImpl(uuid);
		softwareFacet.setHeader(header);
		List<EService> eServices = resourceRegistryClient.getResourcesFromReferenceFacet(EService.class, IsIdentifiedBy.class,
				SoftwareFacet.class, softwareFacet, true);
		for(EService eService : eServices) {
			logger.trace("{}", ISMapper.marshal(eService));
		}
	}
	
	
}
