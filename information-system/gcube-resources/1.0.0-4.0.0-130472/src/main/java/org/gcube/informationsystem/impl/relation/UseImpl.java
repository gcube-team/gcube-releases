/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.relation.Use;
import org.gcube.informationsystem.model.resource.EService;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class UseImpl<Out extends EService, In extends EService>
		extends RelatedToImpl<Out, In> implements
		Use<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public UseImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
