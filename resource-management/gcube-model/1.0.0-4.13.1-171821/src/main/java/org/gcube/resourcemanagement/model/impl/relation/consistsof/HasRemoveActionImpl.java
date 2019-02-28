package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasAddAction;

/**
 * An implementation of the {@link HasRemoveAction} relation.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
public class HasRemoveActionImpl<Out extends Service, In extends ActionFacet>
	extends HasActionImpl<Out, In> implements HasAddAction<Out, In>{
	
	protected HasRemoveActionImpl() {
		super();
	}

	public HasRemoveActionImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
}
