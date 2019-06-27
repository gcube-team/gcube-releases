package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Actor;
import org.gcube.resourcemanagement.model.reference.entity.resource.Site;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.IsOwnedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsOwnedBy.NAME)
public class IsOwnedByImpl<Out extends Site, In extends Actor> extends
		IsRelatedToImpl<Out, In> implements IsOwnedBy<Out, In> {

	protected IsOwnedByImpl() {
		super();
	}

	public IsOwnedByImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
