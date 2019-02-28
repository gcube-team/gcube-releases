package org.gcube.resourcemanagement.model.impl.relation.consistsof;

import org.gcube.informationsystem.model.impl.relation.ConsistsOfImpl;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.resourcemanagement.model.reference.entity.facet.ActionFacet;
import org.gcube.resourcemanagement.model.reference.entity.resource.Service;
import org.gcube.resourcemanagement.model.reference.relation.consistsof.HasAction;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * An implementation of the {@link HasAction} relation.
 * 
 * @author Manuele Simi (ISTI CNR)
 *
 */
@JsonTypeName(value = HasAction.NAME)
public class HasActionImpl<Out extends Service, In extends ActionFacet> 
	extends ConsistsOfImpl<Out, In> implements HasAction<Out, In> {
	 
	protected HasActionImpl() {
		super();
	}
	
	public HasActionImpl(Out source, In target,
			PropagationConstraint propagationConstraint) {
		super(source, target, propagationConstraint);
	}
}
