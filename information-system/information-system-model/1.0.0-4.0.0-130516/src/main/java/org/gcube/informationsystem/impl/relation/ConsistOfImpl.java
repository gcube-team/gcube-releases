/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistOf;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class ConsistOfImpl<Out extends Resource, In extends Facet> extends
		RelationImpl<Out, In> implements ConsistOf<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public ConsistOfImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
