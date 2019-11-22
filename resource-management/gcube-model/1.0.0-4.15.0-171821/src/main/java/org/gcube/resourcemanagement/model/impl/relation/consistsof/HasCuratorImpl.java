package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasCurator;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasCurator.NAME)
public class HasCuratorImpl<Out extends Resource, In extends ContactFacet>
		extends HasContactImpl<Out, In> implements HasCurator<Out, In> {

	protected HasCuratorImpl() {
		super();
	}

	public HasCuratorImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
