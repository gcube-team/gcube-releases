/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.RelationProperty;
import org.gcube.informationsystem.model.relation.Run;
import org.gcube.informationsystem.model.resource.EService;
import org.gcube.informationsystem.model.resource.Software;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class RunImpl<Out extends EService, In extends Software>
		extends RelatedToImpl<Out, In> implements
		Run<Out, In> {

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public RunImpl(Out source, In target,
			RelationProperty relationProperty) {
		super(source, target, relationProperty);
	}

}
