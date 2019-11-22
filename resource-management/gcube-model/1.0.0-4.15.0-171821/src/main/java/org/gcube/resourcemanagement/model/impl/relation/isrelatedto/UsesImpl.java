package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Uses;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Uses.NAME)
public class UsesImpl<Out extends EService, In extends EService> extends
	CallsForImpl<Out, In> implements Uses<Out, In> {

	protected UsesImpl() {
		super();
	}

	public UsesImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
