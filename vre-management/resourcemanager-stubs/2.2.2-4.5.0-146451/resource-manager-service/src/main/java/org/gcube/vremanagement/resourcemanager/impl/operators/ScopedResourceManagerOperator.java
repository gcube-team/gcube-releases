package org.gcube.vremanagement.resourcemanager.impl.operators;

import java.util.HashSet;
import java.util.Set;

import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceItem;
import org.gcube.vremanagement.resourcemanager.stubs.binder.ResourceList;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResource;
import org.gcube.vremanagement.resourcemanager.impl.resources.ScopedResourceFactory;
import org.gcube.vremanagement.resourcemanager.impl.state.ScopeState;


/**
 * Add and remove list of {@link ScopedResource}s to/from a given scope
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ScopedResourceManagerOperator extends Operator {	
	

	private ResourceList resources;		
	
	public ScopedResourceManagerOperator(ScopeState scopeState, OperatorConfig configuration, ResourceList resources, ACTION action) {
		this.configuration = configuration;
		this.resources = resources;
		this.action = action;	
		this.scopeState = scopeState;
	}
	
	public void exec() throws Exception {
		
		Set<ScopedResource> toadd = new HashSet<ScopedResource>();
		Set<ScopedResource> toremove = new HashSet<ScopedResource>();
		ScopedResource sresource = null;
		for (ResourceItem resource : this.resources.getResource()) {		
			try {
				sresource = ScopedResourceFactory.newResource(this.scopeState.getScope(),resource.getID(), resource.getType());
				if (this.action == ACTION.ADD) {
					logger.info("Adding resource " + sresource.getId() + " (" + sresource.getType() + ") to scope " + this.configuration.scope.toString());
					toadd.add(sresource);
				} else if (this.action == ACTION.REMOVE) {
					logger.info("Removing resource " + sresource.getId() + " (" + sresource.getType() + ") from scope " + this.configuration.scope.toString());
					toremove.add(sresource);
				}			
				
				//add the resource item to the session
				this.configuration.session.addResource(sresource);
			} catch (Exception e) {
				logger.error("Unable to manage the resource",e);
			}
		}
		if(toadd.size() > 0)
			configuration.scopeState.addResources(toadd);
		if(toremove.size() > 0)
			configuration.scopeState.removeResources(toremove);
		
		this.configuration.session.save();
	}

}
