/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.er.entity;

import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.entity.facet.FacetNotFoundException;
import org.gcube.informationsystem.resourceregistry.er.ERManagement;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientVertex;

/**
 * @author Luca Frosini (ISTI - CNR)
 * 
 */
public class FacetManagement extends EntityManagement<Facet> {

	public FacetManagement() {
		super(AccessType.FACET);
	}
	
	public FacetManagement(OrientGraph orientGraph) {
		super(AccessType.FACET, orientGraph);
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
	public Vertex reallyCreate() throws FacetAlreadyPresentException, ResourceRegistryException {
		return createVertex();
	}

	@Override
	public Vertex reallyUpdate() throws FacetNotFoundException, ResourceRegistryException {
		Vertex facet = getElement();
		facet = (Vertex) ERManagement.updateProperties(oClass, facet, jsonNode, ignoreKeys, ignoreStartWithKeys);
		((OrientVertex) facet).save();
		return facet;
	}
	
	@Override
	public boolean reallyDelete() throws FacetNotFoundException, ResourceRegistryException {
		getElement().remove();
		return true;
	}

}
