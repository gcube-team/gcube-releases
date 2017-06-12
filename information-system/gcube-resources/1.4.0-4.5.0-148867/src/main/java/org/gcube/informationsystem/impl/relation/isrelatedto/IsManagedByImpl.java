package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.entity.resource.Site;
import org.gcube.informationsystem.model.relation.isrelatedto.IsManagedBy;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = IsManagedBy.NAME)
public class IsManagedByImpl<Out extends HostingNode, In extends Site> extends
		IsRelatedToImpl<Out, In> implements IsManagedBy<Out, In> {

	protected IsManagedByImpl() {
		super();
	}

	public IsManagedByImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
