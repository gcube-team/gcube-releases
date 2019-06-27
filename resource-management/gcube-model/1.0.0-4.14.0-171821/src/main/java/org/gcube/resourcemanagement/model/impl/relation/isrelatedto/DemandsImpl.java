package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;
import org.gcube.resourcemanagement.model.reference.entity.resource.VirtualService;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Demands;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Demands.NAME)
public class DemandsImpl<Out extends VirtualService, In extends Software>
		extends IsRelatedToImpl<Out, In> implements Demands<Out, In> {

	protected DemandsImpl() {
		super();
	}

	public DemandsImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
