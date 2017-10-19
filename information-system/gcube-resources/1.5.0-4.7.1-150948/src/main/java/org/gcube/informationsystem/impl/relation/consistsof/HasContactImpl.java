package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasContact;

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
