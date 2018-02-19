package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasDeveloper;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasDeveloper.NAME)
public class HasDeveloperImpl<Out extends Resource, In extends ContactFacet>
		extends HasContactImpl<Out, In> implements HasDeveloper<Out, In> {

	protected HasDeveloperImpl() {
		super();
	}

	public HasDeveloperImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
