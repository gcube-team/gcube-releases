/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.relation.ManagedBy;
import org.gcube.informationsystem.model.resource.HostingNode;
import org.gcube.informationsystem.model.resource.Site;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class ManagedByImpl<Out extends HostingNode, In extends Site>
		extends RelatedToImpl<Out, In> implements
		ManagedBy<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public ManagedByImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
