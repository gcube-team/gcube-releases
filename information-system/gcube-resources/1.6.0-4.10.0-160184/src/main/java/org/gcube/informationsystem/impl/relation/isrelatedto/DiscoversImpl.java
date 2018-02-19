package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.relation.isrelatedto.Discovers;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Discovers.NAME)
public class DiscoversImpl<Out extends EService, In extends EService> extends
		IsRelatedToImpl<Out, In> implements Discovers<Out, In> {

	protected DiscoversImpl() {
		super();
	}

	public DiscoversImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
