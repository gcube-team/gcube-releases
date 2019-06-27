package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.LegalBody;
import org.gcube.resourcemanagement.model.reference.entity.resource.Person;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.BelongsTo;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = BelongsTo.NAME)
public class BelongsToImpl<Out extends Person, In extends LegalBody> extends
		IsRelatedToImpl<Out, In> implements BelongsTo<Out, In> {

	protected BelongsToImpl() {
		super();
	}

	public BelongsToImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
