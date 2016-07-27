/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.relation.Host;
import org.gcube.informationsystem.model.resource.EService;
import org.gcube.informationsystem.model.resource.HostingNode;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */

public class HostImpl<Out extends HostingNode, In extends EService>
		extends RelatedToImpl<Out, In> implements
		Host<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public HostImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
