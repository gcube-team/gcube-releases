/**
 * 
 */
package org.gcube.informationsystem.impl.utils;

import org.gcube.informationsystem.impl.utils.discovery.SchemaAction;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
class ObjectMappingERAction implements SchemaAction {

	protected ObjectMapper objectMapper;
	
	public ObjectMappingERAction(ObjectMapper objectMapper){
		this.objectMapper = objectMapper;
	}
	
	@Override
	public <E extends Embedded> void manageEmbeddedClass(Class<E> e)
			throws Exception {
		objectMapper.registerSubtypes(e);
	}

	@Override
	public <E extends Entity> void manageEntityClass(Class<E> e)
			throws Exception {
		objectMapper.registerSubtypes(e);
	}

	@Override
	public <R extends Relation<? extends Entity, ? extends Entity>> void manageRelationClass(
			Class<R> r) throws Exception {
		objectMapper.registerSubtypes(r);
	}

}
