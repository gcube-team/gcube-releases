/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.client.proxy;

import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class ResourceRegistryClientTest {

	private static Logger logger = LoggerFactory
			.getLogger(ResourceRegistryClientTest.class);
	
	protected ResourceRegistryClient resourceRegistryClient;
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		ScopedTest.beforeClass();
	}
	
	public ResourceRegistryClientTest(){
		resourceRegistryClient = ResourceRegistryClientFactory.create();
	}
	
	@Test
	public void testQuery() throws InvalidQueryException{
		String res = resourceRegistryClient.query("SELECT FROM V", 0, null);
		logger.trace(res);
	}
	
	
	@Test
	public void testGetFacetSchema() throws SchemaNotFoundException {
		String res = resourceRegistryClient.getFacetSchema("ContactFacet");
		logger.trace(res);
	}
	
	@Test
	public void testGetResourceSchema() throws SchemaNotFoundException {
		String res = resourceRegistryClient.getResourceSchema("HostingNode");
		logger.trace(res);
	}
	
}
