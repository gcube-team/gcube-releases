/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;
import org.gcube.resourcemanagement.model.reference.entity.resource.Schema;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.IsCompliantWith;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsCompliantWith.NAME)
public class IsCompliantWithImpl<Out extends Dataset, In extends Schema>
		extends IsRelatedToImpl<Out, In> implements IsCompliantWith<Out, In> {

	protected IsCompliantWithImpl() {
		super();
	}

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsCompliantWithImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
