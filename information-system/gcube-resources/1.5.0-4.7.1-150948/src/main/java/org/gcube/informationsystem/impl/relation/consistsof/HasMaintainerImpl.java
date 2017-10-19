package org.gcube.informationsystem.impl.relation.consistsof;

import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.entity.facet.ContactFacet;
import org.gcube.informationsystem.model.relation.consistsof.HasMaintainer;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasMaintainer.NAME)
public class HasMaintainerImpl<Out extends Resource, In extends ContactFacet>
		extends HasContactImpl<Out, In> implements HasMaintainer<Out, In> {

	protected HasMaintainerImpl() {
		super();
	}

	public HasMaintainerImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
