package org.gcube.informationsystem.resourceregistry.context;

import org.codehaus.jettison.json.JSONObject;
import org.gcube.informationsystem.model.AccessType;
import org.gcube.informationsystem.model.embedded.PropagationConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.AddConstraint;
import org.gcube.informationsystem.model.embedded.PropagationConstraint.RemoveConstraint;
import org.gcube.informationsystem.model.relation.IsParentOf;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.er.ERNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.RelationAvailableInAnotherContextException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isparentof.IsParentOfAlreadyPresentException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.relation.isparentof.IsParentOfNotFoundException;
import org.gcube.informationsystem.resourceregistry.context.security.SecurityContext;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.gcube.informationsystem.resourceregistry.er.relation.RelationManagement;
import org.gcube.informationsystem.resourceregistry.utils.Utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings("rawtypes")
public class IsParentOfManagement extends RelationManagement<IsParentOf,ContextManagement,ContextManagement> {
	
	public IsParentOfManagement() {
		super(AccessType.IS_PARENT_OF);
	}
	
	public IsParentOfManagement(OrientGraph orientGraph) throws ResourceRegistryException {
		this();
		this.orientGraph = orientGraph;
		getWorkingContext();
	}
	
	@Override
	protected SecurityContext getWorkingContext() throws ResourceRegistryException {
		if(workingContext == null) {
			workingContext = ContextUtility.getInstance()
					.getSecurityContextByUUID(DatabaseEnvironment.CONTEXT_SECURITY_CONTEXT_UUID);
		}
		return workingContext;
	}
	
	@Override
	protected void checkJSON() throws ResourceRegistryException {
		super.checkJSON();
		
		// Check propagation constraint.
		if(jsonNode.has(Relation.PROPAGATION_CONSTRAINT)) {
			StringBuilder message = null;
			JsonNode propagationConstraint = jsonNode.get(Relation.PROPAGATION_CONSTRAINT);
			if(propagationConstraint.has(PropagationConstraint.REMOVE_PROPERTY)) {
				String removeProperty = propagationConstraint.get(PropagationConstraint.REMOVE_PROPERTY).asText();
				RemoveConstraint removeConstraint = RemoveConstraint.valueOf(removeProperty);
				if(removeConstraint != RemoveConstraint.keep) {
					message = new StringBuilder();
					message.append(RemoveConstraint.class.getSimpleName());
					message.append(" can only be ");
					message.append(RemoveConstraint.keep.name());
				}
			}
			
			if(propagationConstraint.has(PropagationConstraint.ADD_PROPERTY)) {
				String addProperty = propagationConstraint.get(PropagationConstraint.ADD_PROPERTY).asText();
				AddConstraint addConstraint = AddConstraint.valueOf(addProperty);
				if(addConstraint != AddConstraint.unpropagate) {
					if(message == null) {
						message = new StringBuilder();
					} else {
						message.append(" and ");
					}
					message.append(AddConstraint.class.getSimpleName());
					message.append(" can only be ");
					message.append(AddConstraint.unpropagate.name());
				}
			}
			
			if(message != null) {
				throw new ResourceRegistryException(message.toString());
			}
		}
		
	}
	
	@Override
	protected IsParentOfNotFoundException getSpecificElementNotFoundException(ERNotFoundException e) {
		return new IsParentOfNotFoundException(e.getMessage(), e.getCause());
	}
	
	@Override
	protected RelationAvailableInAnotherContextException getSpecificERAvailableInAnotherContextException(
			String message) {
		return new RelationAvailableInAnotherContextException(message);
	}
	
	@Override
	protected IsParentOfAlreadyPresentException getSpecificERAlreadyPresentException(String message) {
		return new IsParentOfAlreadyPresentException(message);
	}
	
	@Override
	public JSONObject serializeAsJson() throws ResourceRegistryException {
		return serializeAsJson(false, true);
	}
	
	public JSONObject serializeAsJson(boolean includeSource, boolean includeTarget) throws ResourceRegistryException {
		JSONObject relation = serializeSelfOnly();
		
		try {
			Vertex source = element.getVertex(Direction.OUT);
			ContextManagement sourceContextManagement = new ContextManagement(orientGraph);
			sourceContextManagement.setElement(source);
			if(includeSource) {
				relation.put(Relation.SOURCE_PROPERTY, sourceContextManagement.serializeSelfOnly());
			}
			
			Vertex target = element.getVertex(Direction.IN);
			ContextManagement targetContextManagement = new ContextManagement(orientGraph);
			targetContextManagement.setElement(target);
			if(includeTarget) {
				relation.put(Relation.TARGET_PROPERTY, targetContextManagement.serializeSelfOnly());
			}
			
		} catch(ResourceRegistryException e) {
			logger.error("Unable to correctly serialize {}. {}", element, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE, e);
			throw e;
		} catch(Exception e) {
			logger.error("Unable to correctly serialize {}. {}", element, Utility.SHOULD_NOT_OCCUR_ERROR_MESSAGE, e);
			throw new ResourceRegistryException(e);
		}
		
		return relation;
	}
	
	@Override
	public boolean addToContext() throws ERNotFoundException, ContextException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeFromContext() throws ERNotFoundException, ContextException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected ContextManagement newSourceEntityManagement() throws ResourceRegistryException {
		return new ContextManagement(orientGraph);
	}
	
	@Override
	protected ContextManagement newTargetEntityManagement() throws ResourceRegistryException {
		return new ContextManagement(orientGraph);
	}
	
}
