package org.gcube.informationsystem.resourceregistry.schema;

import java.util.List;

import org.gcube.informationsystem.model.reference.annotations.Abstract;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.types.TypeBinder;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRegistrySchemaClientTest extends ScopedTest {

	private static Logger logger = LoggerFactory.getLogger(ResourceRegistrySchemaClientTest.class);

	@Abstract
	interface AuxFacet extends Facet {
		public static final String NAME = "aux"; //AuxFacet.class.getSimpleName();
		public static final String DESCRIPTION = "This is AuxFacet used ofr Test";
		public static final String VERSION = "1.0.0";
	}
	
	@Test
	public void testCreate() throws Exception {
		ResourceRegistrySchemaClient resourceRegistrySchemaClient = ResourceRegistrySchemaClientFactory.create();
		TypeDefinition td = resourceRegistrySchemaClient.create(AuxFacet.class);
		logger.debug("{}", td);
	}
	
	@Test
	public void testRead() throws Exception {
		ResourceRegistrySchemaClient resourceRegistrySchemaClient = ResourceRegistrySchemaClientFactory.create();
		List<TypeDefinition> types = resourceRegistrySchemaClient.read(Facet.class, true);
		Assert.assertTrue(types.size()>1);
		for(TypeDefinition td : types) {
			logger.debug("{}", td);
		}
		
		types = resourceRegistrySchemaClient.read(Facet.class, false);
		Assert.assertTrue(types.size()==1);
		TypeDefinition gotFacetDefinition = types.get(0);
		TypeDefinition facetDefinition = TypeBinder.createTypeDefinition(Facet.class);
		
		
		Assert.assertTrue(gotFacetDefinition.getName().compareTo(facetDefinition.getName())==0);
		Assert.assertTrue(gotFacetDefinition.getDescription().compareTo(facetDefinition.getDescription())==0);
	}
	
	
}
