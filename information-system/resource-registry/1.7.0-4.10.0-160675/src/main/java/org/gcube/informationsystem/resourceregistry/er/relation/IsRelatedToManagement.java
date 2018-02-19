package org.gcube.informationsystem.resourceregistry.er.relation;

import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.relation.IsRelatedTo;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
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
	
	public IsRelatedToManagement() {
		super(AccessType.IS_RELATED_TO);
	}
	
	public IsRelatedToManagement(SecurityContext workingContext, OrientGraph orientGraph) {
		super(AccessType.IS_RELATED_TO, workingContext, orientGraph);
	}
	
	@Override
	protected IsRelatedToNotFoundException getSpecificElementNotFoundException(ERNotFoundException e) {
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
