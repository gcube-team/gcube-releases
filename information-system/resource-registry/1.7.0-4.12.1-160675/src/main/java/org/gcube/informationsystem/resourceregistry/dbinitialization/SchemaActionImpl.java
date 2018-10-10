package org.gcube.informationsystem.resourceregistry.dbinitialization;

import org.gcube.informationsystem.impl.utils.discovery.SchemaAction;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.gcube.informationsystem.types.TypeBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class SchemaActionImpl implements SchemaAction {
	
	private static Logger logger = LoggerFactory.getLogger(SchemaActionImpl.class);
	
	protected SchemaManagement schemaManagement;
	
	public SchemaActionImpl() {
		this.schemaManagement = new SchemaManagementImpl();
	}
	
	@Override
	public <R extends Relation<? extends Entity,? extends Entity>> void manageRelationClass(Class<R> r)
			throws Exception {
		try {
			String json = TypeBinder.serializeType(r);
			logger.trace(json);
			if(ConsistsOf.class.isAssignableFrom(r)) {
				schemaManagement.create(json, AccessType.CONSISTS_OF);
			} else if(IsRelatedTo.class.isAssignableFrom(r)) {
				schemaManagement.create(json, AccessType.IS_RELATED_TO);
			} else {
				schemaManagement.create(json, AccessType.RELATION);
			}
		} catch(Exception ex) {
			logger.error("Error creating schema for {} type {} : {}", Relation.NAME, r.getSimpleName(),
					ex.getMessage());
			throw ex;
		}
	}
	
	@Override
	public <E extends Entity> void manageEntityClass(Class<E> e) throws Exception {
		try {
			String json = TypeBinder.serializeType(e);
			logger.trace(json);
			if(Facet.class.isAssignableFrom(e)) {
				schemaManagement.create(json, AccessType.FACET);
			} else if(Resource.class.isAssignableFrom(e)) {
				schemaManagement.create(json, AccessType.RESOURCE);
			} else {
				schemaManagement.create(json, AccessType.ENTITY);
			}
		} catch(Exception ex) {
			logger.error("Error creating schema for {} type {} : {}", Entity.NAME, e.getSimpleName(), ex.getMessage());
			throw ex;
		}
	}
	
	@Override
	public <E extends Embedded> void manageEmbeddedClass(Class<E> e) throws Exception {
		try {
			String json = TypeBinder.serializeType(e);
			logger.trace(json);
			schemaManagement.create(json, AccessType.EMBEDDED);
		} catch(Exception ex) {
			logger.error("Error creating schema for {} type {} : {}", Embedded.NAME, e.getSimpleName(),
					ex.getMessage());
			throw ex;
		}
		
	}
}
