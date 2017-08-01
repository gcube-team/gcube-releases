package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.Dataset;
import org.gcube.informationsystem.model.relation.isrelatedto.IsCorrelatedTo;

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
