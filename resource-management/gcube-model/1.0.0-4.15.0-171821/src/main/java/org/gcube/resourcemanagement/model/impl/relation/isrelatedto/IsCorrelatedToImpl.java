package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.IsCorrelatedTo;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsCorrelatedTo.NAME)
public class IsCorrelatedToImpl<Out extends Dataset, In extends Dataset>
		extends IsRelatedToImpl<Out, In> implements IsCorrelatedTo<Out, In> {

	protected IsCorrelatedToImpl() {
		super();
	}

	/**
	 * @param source
	 * @param target
	 * @param relationProperty
	 */
	public IsCorrelatedToImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
