package org.gcube.vremanagement.resourcemanager.impl.resources;


import java.util.List;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBECS;
import org.gcube.common.core.resources.GCUBECSInstance;
import org.gcube.common.core.resources.GCUBECollection;
import org.gcube.common.core.resources.GCUBEExternalRunningInstance;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBEMCollection;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.vremanagement.resourcemanager.impl.contexts.ServiceContext;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.ISTemplateQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBECSInstanceQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBECSQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBECollectionQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEExternalRIQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericResourceQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEMCollectionQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBERuntimeResourceQuery;
import org.gcube.common.core.informationsystem.client.queries.GCUBEServiceQuery;
import org.gcube.common.core.informationsystem.publisher.ISPublisher;

import com.thoughtworks.xstream.annotations.XStreamOmitField;


/**
 * 
 * Models a generic scoped {@link GCUBEResource}
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class ScopedAnyResource extends ScopedResource {

	@XStreamOmitField
	GCUBEResource profile = null;

	@SuppressWarnings("rawtypes")
	@XStreamOmitField
	Class profileClass = null;
	
	protected ScopedAnyResource(String id, String type, GCUBEScope scope) {
		super(id, type, scope);
	}

	/**
	 * Gets the profile of the Scoped Resource
	 * @throws Exception if the resource was not found
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes"})
	protected void find() throws Exception {
		
			ISClient client = GHNContext.getImplementation(ISClient.class);
			Class query = null;	
			if (this.type.compareToIgnoreCase(GCUBECollection.TYPE) == 0) {query = GCUBECollectionQuery.class; profileClass = GCUBECollection.class;}
			else if (this.type.compareToIgnoreCase(GCUBEMCollection.TYPE) == 0) {query = GCUBEMCollectionQuery.class; profileClass = GCUBEMCollection.class;}			
			else if (this.type.compareToIgnoreCase(GCUBEGenericResource.TYPE) == 0) {query = GCUBEGenericResourceQuery.class; profileClass = GCUBEGenericResource.class;}
			else if (this.type.compareToIgnoreCase(GCUBEExternalRunningInstance.TYPE) == 0) {query = GCUBEExternalRIQuery.class; profileClass = GCUBEExternalRunningInstance.class;}
			else if (this.type.compareToIgnoreCase(GCUBECS.TYPE) == 0) {query = GCUBECSQuery.class; profileClass = GCUBECS.class;}		
			else if (this.type.compareToIgnoreCase(GCUBECSInstance.TYPE) == 0) {query = GCUBECSInstanceQuery.class; profileClass = GCUBECSInstance.class;}
			else if (this.type.compareToIgnoreCase(GCUBEService.TYPE) == 0) {query = GCUBEServiceQuery.class; profileClass = GCUBEService.class;}
			else if (this.type.compareToIgnoreCase(GCUBERuntimeResource.TYPE) == 0) {query = GCUBERuntimeResourceQuery.class; profileClass = GCUBERuntimeResource.class;}
			else throw new Exception("Unknown resource type: " + this.type);
			List <GCUBEResource> profiles=null;
			ISTemplateQuery realquery =null;
			try {	
				realquery = (ISTemplateQuery) client.getQuery(query);
				logger.info("execution query "+query+" on scope "+scope+" with id: "+this.id);
				realquery.addAtomicConditions(new AtomicCondition("/ID",this.id));
				profiles = client.execute(realquery, GCUBEScope.getScope(this.scope));
				if ((profiles != null) && (profiles.size() > 0)){ 
					logger.info("profile found ");
					this.profile = profiles.get(0);
				}else{
					GCUBEScope enclosingScope=GCUBEScope.getScope(this.scope).getEnclosingScope();
					logger.info("profile not found. Try on enclosing scope: "+enclosingScope);
					// obviously, in the case of adding, the resource is not in the current scope, therefore we look upstairs (the enclosing scope)		
					this.profile = (GCUBEResource) client.execute(realquery, enclosingScope).get(0);
				}
		} catch (Throwable e) {
			getLogger().error("bad query. Caused by: "+e.getCause());
			throw new Exception("unable to find the target resource (ID=" + id + "). Possible cause: " + e.getMessage(), e);						
		}
//		try{
//			if ((profiles != null) && (profiles.size() > 0)){ 
//				logger.info("profile found ");
//				this.profile = profiles.get(0);
//			}else{
//				GCUBEScope enclosingScope=GCUBEScope.getScope(this.scope).getEnclosingScope();
//				logger.info("profile not found. Try on enclosing scope: "+enclosingScope);
//				// obviously, in the case of adding, the resource is not in the current scope, therefore we look upstairs (the enclosing scope)		
//				this.profile = (GCUBEResource) client.execute(realquery, enclosingScope).get(0);
//			}
//		}catch(Throwable e){
//			getLogger().error("bad query. Caused by: "+e.getCause());
//			throw new Exception("unable to find the target resource (ID=" + id + "). Possible cause: " + e.getMessage(), e);						
//
//		}
				
	}
	
	@Override
	protected void addToScope() throws ResourceNotFound, Exception {
		this.findResource();
		getLogger().debug("Adding scope to resource profile");
		try {
			profile.addScope(this.getScope());
			getLogger().debug("republish the resource "+profile.getID()+" with scope added "+this.getScope());
			//republish the resource
			ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
			publisher.updateGCUBEResource(this.profile, ServiceContext.getContext().getScope(), ServiceContext.getContext());
			getLogger().debug("ServiceContext scope: "+ServiceContext.getContext().getScope());
			//republish also in the infrastructure scope, if needed
			if (ServiceContext.getContext().getScope().getType() == GCUBEScope.Type.VO) {
				if (this.profile.getScopes().values().contains(ServiceContext.getContext().getScope().getEnclosingScope())) {
					getLogger().debug(" published also on: "+ServiceContext.getContext().getScope().getEnclosingScope());
					publisher.updateGCUBEResource(this.profile, ServiceContext.getContext().getScope().getEnclosingScope(), ServiceContext.getContext());
				}
			}		
		} catch (Exception e) {
			this.noHopeForMe("Failed to add the scope ("+ this.getScope()+") to resource " + this.getId(), e);
		}
	}

	@Override
	protected void removeFromScope() throws ResourceNotFound, Exception {		
		this.findResource();
		getLogger().debug("Removing scope from resource profile");
		try {
			profile.removeScope(this.getScope());	
			//republish the resource
			ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
			publisher.updateGCUBEResource(this.profile, ServiceContext.getContext().getScope(), ServiceContext.getContext());
			//republish also in the infrastructure scope, if needed
			if (ServiceContext.getContext().getScope().getType() == GCUBEScope.Type.VO) {
				if (this.profile.getScopes().values().contains(ServiceContext.getContext().getScope().getEnclosingScope())) {
					publisher.updateGCUBEResource(this.profile, ServiceContext.getContext().getScope().getEnclosingScope(), ServiceContext.getContext());
				}
			}
		} catch (Exception e) {
			this.noHopeForMe("Failed to remove the scope ("+ this.getScope()+") from resource " + this.getId(), e);
		}
		
	}
	
}
