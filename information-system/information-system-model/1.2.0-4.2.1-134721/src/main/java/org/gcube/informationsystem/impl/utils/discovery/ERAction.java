/**
 * 
 */
package org.gcube.informationsystem.impl.utils.discovery;

import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public interface ERAction {

	public <E extends Embedded> void manageEmbeddedClass(Class<E> e) throws Exception;
	
	public <E extends Entity> void manageEntityClass(Class<E> e) throws Exception;
	
	public <R extends Relation<? extends Entity, ? extends Entity>> void manageRelationClass(Class<R> r) throws Exception;
	
}
