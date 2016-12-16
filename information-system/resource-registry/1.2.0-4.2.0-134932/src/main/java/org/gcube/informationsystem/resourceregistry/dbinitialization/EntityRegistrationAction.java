/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.gcube.informationsystem.impl.utils.discovery.ERAction;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.resources.impl.SchemaManagementImpl;
import org.gcube.informationsystem.types.TypeBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class EntityRegistrationAction implements ERAction {

	
	private static Logger logger = LoggerFactory.getLogger(EntityRegistrationAction.class);
	
	protected SchemaManagement schemaManagement;
	
	public EntityRegistrationAction(){
		this.schemaManagement = new SchemaManagementImpl();
	}
	
	@Override
	public <R extends Relation<? extends Entity, ? extends Entity>> void manageRelationClass(
			Class<R> r) throws Exception {
		try{
			String json = TypeBinder.serializeType(r);
			logger.trace(json);
			if (ConsistsOf.class.isAssignableFrom(r)) {
				schemaManagement.registerConsistOfSchema(json);
			} else if(IsRelatedTo.class.isAssignableFrom(r)){
				schemaManagement.registerRelatedToSchema(json);
			} else {
				schemaManagement.registerRelationSchema(json);
			}
		} catch(Exception ex){
			logger.error("Error creating schema for {} type {} : {}", Relation.NAME, r.getSimpleName(), ex.getMessage());
			throw ex;
		}
	}
	
	@Override
	public <E extends Entity> void manageEntityClass(Class<E> e) throws Exception {
		try{
			String json = TypeBinder.serializeType(e);
			logger.trace(json);
			if (Facet.class.isAssignableFrom(e)) {
				schemaManagement.registerFacetSchema(json);
			} else if(Resource.class.isAssignableFrom(e)){
				schemaManagement.registerResourceSchema(json);
			} else {
				schemaManagement.registerEntitySchema(json);
			}
		} catch(Exception ex){
			logger.error("Error creating schema for {} type {} : {}", Entity.NAME, e.getSimpleName(), ex.getMessage());
			throw ex;
		}
	}
	
	@Override
	public <E extends Embedded> void manageEmbeddedClass(Class<E> e) throws Exception {
		if(e==Embedded.class){
			logger.trace("Discarding {} because is just a convenient interface", e);
			return;
		}
		
		try {
			String json = TypeBinder.serializeType(e);
			logger.trace(json);
			schemaManagement.registerEmbeddedTypeSchema(json);
		} catch (Exception ex) {
			logger.error("Error creating schema for {} type {} : {}",
					Embedded.NAME, e.getSimpleName(),
					ex.getMessage());
			throw ex;
		}

	}
}
