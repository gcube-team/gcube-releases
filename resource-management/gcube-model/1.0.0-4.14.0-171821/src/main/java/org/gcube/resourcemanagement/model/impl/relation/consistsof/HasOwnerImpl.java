package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasOwner;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasOwner.NAME)
public class HasOwnerImpl<Out extends Resource, In extends ContactFacet>
		extends HasContactImpl<Out, In> implements HasOwner<Out, In> {

	protected HasOwnerImpl() {
		super();
	}

	public HasOwnerImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
