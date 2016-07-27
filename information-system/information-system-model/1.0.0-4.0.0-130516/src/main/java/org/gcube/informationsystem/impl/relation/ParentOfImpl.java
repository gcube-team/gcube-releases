/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Context;
import org.gcube.informationsystem.model.relation.ParentOf;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class ParentOfImpl<Out extends Context, In extends Context> extends
		RelationImpl<Out, In> implements ParentOf<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public ParentOfImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
