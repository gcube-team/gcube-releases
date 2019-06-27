package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.EService;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Discovers;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Discovers.NAME)
public class DiscoversImpl<Out extends EService, In extends EService> extends
		CallsForImpl<Out, In> implements Discovers<Out, In> {

	protected DiscoversImpl() {
		super();
	}

	public DiscoversImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
