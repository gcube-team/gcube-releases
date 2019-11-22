package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Requires;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Requires.NAME)
public class RequiresImpl<Out extends Software, In extends Service> extends
		IsRelatedToImpl<Out, In> implements Requires<Out, In> {

	protected RequiresImpl() {
		super();
	}

	public RequiresImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
