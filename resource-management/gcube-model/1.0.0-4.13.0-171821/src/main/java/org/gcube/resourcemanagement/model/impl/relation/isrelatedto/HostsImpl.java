package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.entity.resource.Site;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Hosts;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Hosts.NAME)
public class HostsImpl<Out extends Site, In extends Service> extends
		IsRelatedToImpl<Out, In> implements Hosts<Out, In> {

	protected HostsImpl() {
		super();
	}

	public HostsImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
