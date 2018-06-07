package org.gcube.informationsystem.resourceregistry.er.relation;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.relation.ConsistsOf;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
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
	
	public ConsistsOfManagement() {
		super(AccessType.CONSISTS_OF);
	}
	
	public ConsistsOfManagement(SecurityContext workingContext, OrientGraph orientGraph) {
		super(AccessType.CONSISTS_OF, workingContext, orientGraph);
	}
	
	@Override
	protected ConsistsOfNotFoundException getSpecificElementNotFoundException(ERNotFoundException e) {
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
