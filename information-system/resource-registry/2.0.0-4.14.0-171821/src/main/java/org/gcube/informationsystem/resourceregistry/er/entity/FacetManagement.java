package org.gcube.informationsystem.resourceregistry.er.entity;

import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.model.reference.entity.Facet;
import org.gcube.informationsystem.resourceregistry.api.exceptions.NotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class FacetManagement extends EntityManagement<Facet> {
	
	public FacetManagement() {
		super(AccessType.FACET);
	}
	
	public FacetManagement(SecurityContext workingContext, OrientGraph orientGraph) {
		super(AccessType.FACET, workingContext, orientGraph);
	}
	
	@Override
	protected FacetNotFoundException getSpecificElementNotFoundException(NotFoundException e) {
		return new FacetNotFoundException(e.getMessage(), e.getCause());
	}
	
	@Override
	protected FacetAvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(String message) {
		return new FacetAvailableInAnotherContextException(message);
	}
	
	@Override
	protected FacetAlreadyPresentException getSpecificERAlreadyPresentException(String message) {
		return new FacetAlreadyPresentException(message);
	}
	
	@Override
	public String serialize() throws ResourceRegistryException {
		return serializeSelfOnly().toString();
	}
	
	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		return serializeSelfOnly();
	}
	
	@Override
	protected Vertex reallyCreate() throws FacetAlreadyPresentException, ResourceRegistryException {
		return createVertex();
	}
	
	@Override
	protected Vertex reallyUpdate() throws FacetNotFoundException, ResourceRegistryException {
		Vertex facet = getElement();
		facet = (Vertex) ERManagement.updateProperties(oClass, facet, jsonNode, ignoreKeys, ignoreStartWithKeys);
		return facet;
	}
	
	@Override
	protected boolean reallyDelete() throws FacetNotFoundException, ResourceRegistryException {
		getElement().remove();
		return true;
	}
	
}
