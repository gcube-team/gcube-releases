/**
 * 
 */
package org.gcube.informationsystem.model.relation;

import org.gcube.informationsystem.model.entity.Context;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public interface ParentOf<Out extends Context, In extends Context> 
		extends Relation<Out, In> {

	public static final String NAME = ParentOf.class.getSimpleName();
	
}
