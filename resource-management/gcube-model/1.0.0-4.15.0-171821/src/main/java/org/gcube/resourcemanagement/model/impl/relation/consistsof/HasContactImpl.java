package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasContact;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasContact.NAME)
public abstract class HasContactImpl<Out extends Resource, In extends ContactFacet>
		extends ConsistsOfImpl<Out, In> implements HasContact<Out, In> {

	protected HasContactImpl() {
		super();
	}

	public HasContactImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
