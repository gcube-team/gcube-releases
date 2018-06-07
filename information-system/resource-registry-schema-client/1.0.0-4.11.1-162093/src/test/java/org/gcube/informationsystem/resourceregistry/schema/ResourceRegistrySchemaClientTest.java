package org.gcube.informationsystem.resourceregistry.schema;

import java.util.List;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRegistrySchemaClientTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(ResourceRegistrySchemaClientTest.class);

	interface Aux extends Facet {
		
	}
	
	@Test
	public void testCreate() throws Exception {
		ResourceRegistrySchemaClient resourceRegistrySchemaClient = ResourceRegistrySchemaClientFactory.create();
		TypeDefinition td = resourceRegistrySchemaClient.create(Aux.class);
		logger.debug("{}", td);
	}
	
	@Test
	public void testRead() throws Exception {
		ResourceRegistrySchemaClient resourceRegistrySchemaClient = ResourceRegistrySchemaClientFactory.create();
		List<TypeDefinition> types = resourceRegistrySchemaClient.read(Facet.class, true);
		for(TypeDefinition td : types) {
			logger.debug("{}", td);
		}
	}
	
	
}
