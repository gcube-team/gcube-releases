/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface ConsistOf<Out extends Resource, In extends Facet>
		extends Relation<Out, In> {
	
	public static final String NAME = ConsistOf.class.getSimpleName();
	
}
