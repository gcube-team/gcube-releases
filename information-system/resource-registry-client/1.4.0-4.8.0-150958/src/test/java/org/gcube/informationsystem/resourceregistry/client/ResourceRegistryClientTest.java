/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client;

import java.util.List;
import java.util.UUID;

import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClient;
import org.gcube.informationsystem.resourceregistry.client.ResourceRegistryClientFactory;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	//@Test
	public void testGetResource() throws ResourceRegistryException {
		UUID uuid = UUID.fromString("");
		EService eService = resourceRegistryClient.getInstance(EService.class, uuid);
		logger.trace("{}", eService);
	}
	
}
