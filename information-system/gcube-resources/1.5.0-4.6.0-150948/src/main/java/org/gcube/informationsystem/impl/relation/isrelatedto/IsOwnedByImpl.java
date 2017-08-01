package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.Actor;
import org.gcube.informationsystem.model.entity.resource.Site;
import org.gcube.informationsystem.model.relation.isrelatedto.IsOwnedBy;

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
