/**
 * 
 */
package org.gcube.informationsystem.model.impl.utils.discovery;

import org.gcube.informationsystem.model.reference.embedded.Embedded;
import org.gcube.informationsystem.model.reference.entity.Entity;
import org.gcube.informationsystem.model.reference.relation.Relation;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public interface SchemaAction {

	public <E extends Embedded> void manageEmbeddedClass(Class<E> e) throws Exception;
	
	public <E extends Entity> void manageEntityClass(Class<E> e) throws Exception;
	
	public <R extends Relation<? extends Entity, ? extends Entity>> void manageRelationClass(Class<R> r) throws Exception;
	
}
