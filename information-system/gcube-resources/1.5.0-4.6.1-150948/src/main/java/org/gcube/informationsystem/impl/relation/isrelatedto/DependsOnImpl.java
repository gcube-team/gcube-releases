package org.gcube.informationsystem.impl.relation.isrelatedto;

import org.gcube.informationsystem.impl.relation.IsRelatedToImpl;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.entity.resource.Software;
import org.gcube.informationsystem.model.relation.isrelatedto.DependsOn;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value = DependsOn.NAME)
public class DependsOnImpl<Out extends Software, In extends Software> extends
		IsRelatedToImpl<Out, In> implements DependsOn<Out, In> {

	protected DependsOnImpl() {
		super();
	}

	public DependsOnImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}

}
