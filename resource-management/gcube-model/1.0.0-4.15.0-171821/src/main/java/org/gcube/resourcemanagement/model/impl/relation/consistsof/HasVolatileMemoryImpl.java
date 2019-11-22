package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.MemoryFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasVolatileMemory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasVolatileMemory.NAME)
public class HasVolatileMemoryImpl<Out extends Resource, In extends MemoryFacet>
		extends HasMemoryImpl<Out, In> implements HasVolatileMemory<Out, In> {

	protected HasVolatileMemoryImpl() {
		super();
	}

	public HasVolatileMemoryImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
