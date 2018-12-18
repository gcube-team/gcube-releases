package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.resourcemanagement.model.reference.entity.facet.MemoryFacet;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasPersistentMemory;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = HasPersistentMemory.NAME)
public class HasPersistentMemoryImpl<Out extends Resource, In extends MemoryFacet>
		extends HasMemoryImpl<Out, In> implements HasPersistentMemory<Out, In> {

	protected HasPersistentMemoryImpl() {
		super();
	}

	public HasPersistentMemoryImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
