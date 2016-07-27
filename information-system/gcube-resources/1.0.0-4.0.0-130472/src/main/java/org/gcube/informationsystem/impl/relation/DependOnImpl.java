/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.relation.DependOn;
import org.gcube.informationsystem.model.resource.Software;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class DependOnImpl<Out extends Software, In extends Software>
		extends RelatedToImpl<Out, In> implements
		DependOn<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public DependOnImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
