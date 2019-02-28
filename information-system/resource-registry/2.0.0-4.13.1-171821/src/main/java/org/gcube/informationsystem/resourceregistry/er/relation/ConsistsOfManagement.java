package org.gcube.informationsystem.resourceregistry.er.relation;

import org.gcube.informationsystem.model.impl.embedded.PropagationConstraintImpl;
import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.reference.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.reference.relation.ConsistsOf;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.consistsOf.ConsistsOfAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.consistsOf.ConsistsOfAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.consistsOf.ConsistsOfNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.er.entity.FacetManagement;
import org.gcube.informationsystem.resourceregistry.er.entity.ResourceManagement;

import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("rawtypes")
public class ConsistsOfManagement extends RelationManagement<ConsistsOf,ResourceManagement,FacetManagement> {
	
	public static final PropagationConstraint DEFAULT_CONSISTS_OF_PC;

	static {
		DEFAULT_CONSISTS_OF_PC = new PropagationConstraintImpl();
		DEFAULT_CONSISTS_OF_PC.setRemoveConstraint(RemoveConstraint.cascadeWhenOrphan);
		DEFAULT_CONSISTS_OF_PC.setAddConstraint(AddConstraint.propagate);
	}
	
	public ConsistsOfManagement() {
		super(AccessType.CONSISTS_OF, DEFAULT_CONSISTS_OF_PC);
	}
	
	public ConsistsOfManagement(SecurityContext workingContext, OrientGraph orientGraph) {
		super(AccessType.CONSISTS_OF, workingContext, orientGraph, DEFAULT_CONSISTS_OF_PC);
	}
	
	@Override
	protected ConsistsOfNotFoundException getSpecificElementNotFoundException(NotFoundException e) {
		return new ConsistsOfNotFoundException(e.getMessage(), e.getCause());
	}
	
	@Override
	protected ConsistsOfAvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(
			String message) {
		return new ConsistsOfAvailableInAnotherContextException(message);
	}
	
	@Override
	protected ConsistsOfAlreadyPresentException getSpecificERAlreadyPresentException(String message) {
		return new ConsistsOfAlreadyPresentException(message);
	}
	
	@Override
	protected ResourceManagement newSourceEntityManagement() throws ResourceRegistryException {
		return new ResourceManagement(getWorkingContext(), orientGraph);
	}
	
	@Override
	protected FacetManagement newTargetEntityManagement() throws ResourceRegistryException {
		return new FacetManagement(getWorkingContext(), orientGraph);
	}
	
}
