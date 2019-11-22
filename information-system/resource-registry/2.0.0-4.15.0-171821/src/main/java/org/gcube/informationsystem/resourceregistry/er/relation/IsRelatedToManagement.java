package org.gcube.informationsystem.resourceregistry.er.relation;

import org.gcube.informationsystem.model.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.reference.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isrelatedto.IsRelatedToAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isrelatedto.IsRelatedToAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isrelatedto.IsRelatedToNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("rawtypes")
public class IsRelatedToManagement extends RelationManagement<IsRelatedTo,ResourceManagement,ResourceManagement> {
	
	public static final PropagationConstraint DEFAULT_IS_RELATED_TO_PC;

	static {
		DEFAULT_IS_RELATED_TO_PC = new PropagationConstraintImpl();
		DEFAULT_IS_RELATED_TO_PC.setRemoveConstraint(RemoveConstraint.keep);
		DEFAULT_IS_RELATED_TO_PC.setAddConstraint(AddConstraint.unpropagate);
	}
	
	public IsRelatedToManagement() {
		super(AccessType.IS_RELATED_TO,DEFAULT_IS_RELATED_TO_PC);
	}
	
	public IsRelatedToManagement(SecurityContext workingContext, OrientGraph orientGraph) {
		super(AccessType.IS_RELATED_TO, workingContext, orientGraph, DEFAULT_IS_RELATED_TO_PC);
	}
	
	@Override
	protected IsRelatedToNotFoundException getSpecificElementNotFoundException(NotFoundException e) {
		return new IsRelatedToNotFoundException(e.getMessage(), e.getCause());
	}
	
	@Override
	protected IsRelatedToAvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(
			String message) {
		return new IsRelatedToAvailableInAnotherContextException(message);
	}
	
	@Override
	protected IsRelatedToAlreadyPresentException getSpecificERAlreadyPresentException(String message) {
		return new IsRelatedToAlreadyPresentException(message);
	}
	
	@Override
	protected ResourceManagement newSourceEntityManagement() throws ResourceRegistryException {
		return new ResourceManagement(getWorkingContext(), orientGraph);
	}
	
	@Override
	protected ResourceManagement newTargetEntityManagement() throws ResourceRegistryException {
		return new ResourceManagement(getWorkingContext(), orientGraph);
	}
	
}
