/**
 * 
 */
package org.gcube.informationsystem.impl.relation;

import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.IsRelatedTo;

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
