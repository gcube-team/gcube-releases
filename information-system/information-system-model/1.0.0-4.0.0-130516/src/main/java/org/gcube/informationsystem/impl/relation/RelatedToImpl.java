/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.RelatedTo;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class RelatedToImpl<Out extends Resource, In extends Resource> extends
		RelationImpl<Out, In> implements RelatedTo<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public RelatedToImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
