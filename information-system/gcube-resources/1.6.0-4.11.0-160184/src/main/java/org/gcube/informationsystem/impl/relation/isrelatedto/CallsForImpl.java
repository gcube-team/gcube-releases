package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.Service;
import org.gcube.informationsystem.model.relation.isrelatedto.CallsFor;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = CallsFor.NAME)
public class CallsForImpl<Out extends Service, In extends Service> extends
		IsRelatedToImpl<Out, In> implements CallsFor<Out, In> {

	protected CallsForImpl() {
		super();
	}

	public CallsForImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
