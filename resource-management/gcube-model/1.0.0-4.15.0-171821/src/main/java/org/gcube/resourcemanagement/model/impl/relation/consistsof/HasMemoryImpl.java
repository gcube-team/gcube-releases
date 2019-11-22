package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.MemoryFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasMemory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasMemory.NAME)
public abstract class HasMemoryImpl<Out extends Resource, In extends MemoryFacet>
		extends ConsistsOfImpl<Out, In> implements HasMemory<Out, In> {

	protected HasMemoryImpl() {
		super();
	}

	public HasMemoryImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
