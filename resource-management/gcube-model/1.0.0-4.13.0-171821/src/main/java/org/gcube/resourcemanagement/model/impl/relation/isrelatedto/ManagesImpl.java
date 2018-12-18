package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Dataset;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Manages;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Manages.NAME)
public class ManagesImpl<Out extends Service, In extends Dataset> extends
		IsRelatedToImpl<Out, In> implements Manages<Out, In> {

	protected ManagesImpl() {
		super();
	}

	public ManagesImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
