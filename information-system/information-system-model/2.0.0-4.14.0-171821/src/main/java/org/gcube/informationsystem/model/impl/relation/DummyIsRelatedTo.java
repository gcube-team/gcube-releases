/**
 * 
 */
package org.gcube.informationsystem.model.impl.relation;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.entity.Resource;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=IsRelatedTo.NAME)
public class DummyIsRelatedTo<Out extends Resource, In extends Resource>
		extends IsRelatedToImpl<Out, In> implements IsRelatedTo<Out, In>{

	public DummyIsRelatedTo(){
		super();
	}

	public DummyIsRelatedTo(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
}
