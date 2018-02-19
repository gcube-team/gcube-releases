package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.EService;
import org.gcube.informationsystem.model.entity.resource.HostingNode;
import org.gcube.informationsystem.model.relation.isrelatedto.Hosts;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Hosts.NAME)
public class HostsImpl<Out extends HostingNode, In extends EService> extends
		IsRelatedToImpl<Out, In> implements Hosts<Out, In> {

	protected HostsImpl() {
		super();
	}

	public HostsImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
