/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import org.gcube.informationsystem.model.embedded.AccessPolicy;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.entity.resource.Actor;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.consistsof.HasContact;
import org.gcube.informationsystem.model.relation.isrelatedto.Hosts;
import org.gcube.informationsystem.types.TypeBinder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class SchemaManagementImplTest {

	private static Logger logger = LoggerFactory.getLogger(SchemaManagementImplTest.class);
	
	@Test
	public void registerEmbeddedTypeSchema() throws Exception{
		Class<?> clz = AccessPolicy.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
		//new SchemaManagementImpl().registerEmbeddedTypeSchema(json);
	}
	
	@Test
	public void getEmbeddedTypeSchema() throws Exception{
		String json = new SchemaManagementImpl().getEmbeddedTypeSchema(AccessPolicy.NAME);
		logger.trace(json);
	}
	
	
	@Test
	public void registerFacetSchema() throws Exception{
		Class<?> clz = ContactFacet.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
		//new SchemaManagementImpl().registerFacetSchema(json);
	}
	
	@Test
	public void getFacetSchema() throws Exception{
		String json = new SchemaManagementImpl().getFacetSchema(ContactFacet.NAME);
		logger.trace(json);
	}
	
	
	@Test
	public void registerResourceSchema() throws Exception{
		Class<?> clz = Actor.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
		//new SchemaManagementImpl().registerFacetSchema(json);
	}
	
	@Test
	public void getResourceSchema() throws Exception{
		String json = new SchemaManagementImpl().getResourceSchema(Actor.NAME);
		logger.trace(json);
	}
	
	
	@Test
	public void registerRelation() throws Exception{
		Class<?> clz = Hosts.class;
		String json = TypeBinder.serializeType(clz);
		logger.trace(json);
		//new SchemaManagementImpl().registerFacetSchema(json);
	}
}
