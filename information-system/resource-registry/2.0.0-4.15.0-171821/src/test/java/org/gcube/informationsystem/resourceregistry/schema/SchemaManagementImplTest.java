/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.schema;

import java.util.List;

import org.gcube.informationsystem.model.reference.ISManageable;
import org.gcube.informationsystem.model.reference.embedded.Embedded;
import org.gcube.informationsystem.model.reference.embedded.Header;
import org.gcube.informationsystem.model.reference.entity.Entity;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.model.reference.relation.Relation;
import org.gcube.informationsystem.types.TypeBinder;
import org.gcube.informationsystem.types.TypeBinder.TypeDefinition;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.Actor;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Hosts;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SchemaManagementImplTest {

	private static Logger logger = LoggerFactory
			.getLogger(SchemaManagementImplTest.class);

	@Test
	public void registerEmbeddedTypeSchema() throws Exception {
		Class<? extends Embedded> clz = Header.class;
		String json = TypeBinder.serializeType(clz);
		logger.debug(json);
		//new SchemaManagementImpl().create(json, AccessType.EMBEDDED);
	}

	@Test
	public void getEmbeddedTypeSchema() throws Exception {
		String json = new SchemaManagementImpl().read(Embedded.NAME, false);
		logger.debug(json);
	}

	@Test
	public void registerFacetSchema() throws Exception {
		Class<? extends Facet> clz = ContactFacet.class;
		String json = TypeBinder.serializeType(clz);
		TypeBinder.deserializeTypeDefinition(json);
	}

	@Test
	public void getFacetSchema() throws Exception {
		String json = new SchemaManagementImpl().read(ContactFacet.NAME, false);
		logger.info(json);
		List<TypeDefinition> typeDefinitions = TypeBinder.deserializeTypeDefinitions(json);
		logger.info("{}", typeDefinitions);
		
	}

	@Test
	public void registerEntityTypeSchema() throws Exception {
		Class<? extends Entity> clz = Entity.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
		// new SchemaManagementImpl().registerEntitySchema(json);
	}

	@Test
	public void registerResourceSchema() throws Exception {
		Class<? extends Resource> clz = Resource.class;
		String json = TypeBinder.serializeType(clz);
		logger.info(json);
		// new SchemaManagementImpl().registerFacetSchema(json);
	}

	@Test
	public void getResourceSchema() throws Exception {
		String json = new SchemaManagementImpl().read(Actor.NAME, false);
		logger.trace(json);
	}

	@Test
	public void registerRelation() throws Exception {
		Class<? extends ISManageable> clz = Hosts.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
		// new SchemaManagementImpl().registerFacetSchema(json);
	}

	@Test
	public void getList() throws Exception {
		logger.debug("\n\n\n");

		boolean includeSubTypes = true;

		SchemaManagement schemaManagement = new SchemaManagementImpl();
		String list = schemaManagement.read(Embedded.NAME, includeSubTypes);
		logger.debug("{} list : {}", Embedded.NAME, list);

		ObjectMapper mapper = new ObjectMapper();
		List<TypeDefinition> typeDefinitions = mapper.readValue(list,
				new TypeReference<List<TypeDefinition>>() {
				});
		logger.debug("{}", typeDefinitions);

		list = schemaManagement.read(Entity.NAME,  includeSubTypes);
		logger.debug("{} list : {}", Entity.NAME, list);

		list = schemaManagement.read(Resource.NAME,  includeSubTypes);
		logger.debug("{} list : {}", Resource.NAME, list);

		list = schemaManagement.read(Facet.NAME,  includeSubTypes);
		logger.debug("{} list : {}", Facet.NAME, list);

		list = schemaManagement.read(Relation.NAME,  includeSubTypes);
		logger.debug("{} list : {}", Relation.NAME, list);

		list = schemaManagement.read(ConsistsOf.NAME,  includeSubTypes);
		logger.debug("{} list : {}", ConsistsOf.NAME, list);

		list = schemaManagement.read(IsRelatedTo.NAME, includeSubTypes);
		logger.debug("{} list : {}", IsRelatedTo.NAME, list);

	}
}
