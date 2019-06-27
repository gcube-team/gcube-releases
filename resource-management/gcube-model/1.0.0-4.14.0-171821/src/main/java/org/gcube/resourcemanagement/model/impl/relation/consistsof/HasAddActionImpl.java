package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasAddAction;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * An implementation of the {@link HasAddAction} relation.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
@JsonTypeName(value = HasAddAction.NAME)
public class HasAddActionImpl<Out extends Service, In extends ActionFacet>
	extends HasActionImpl<Out, In> implements HasAddAction<Out, In>{
	
	protected HasAddActionImpl() {
		super();
	}

	public HasAddActionImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
}
