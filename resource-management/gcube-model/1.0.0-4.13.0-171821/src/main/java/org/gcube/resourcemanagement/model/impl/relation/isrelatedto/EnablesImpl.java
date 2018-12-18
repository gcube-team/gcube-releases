package org.gcube.resourcemanagement.model.impl.relation.isrelatedto;

import org.gcube.informationsystem.model.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.entity.resource.Software;
import org.gcube.resourcemanagement.model.reference.relation.isrelatedto.Enables;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = Enables.NAME)
public class EnablesImpl<Out extends Service, In extends Software> extends
		IsRelatedToImpl<Out, In> implements Enables<Out, In> {

	protected EnablesImpl() {
		super();
	}

	public EnablesImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
