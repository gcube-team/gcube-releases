package org.gcube.vremanagement.resourcemanager.impl.resources;


import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import java.util.Collections;
import java.util.List;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.impl.XQuery;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.Software;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;

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
	Resource profile = null;

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
	protected void find() throws Exception {

		Class<? extends Resource> queryType = null;	
		if (this.type.compareToIgnoreCase(GCUBEGenericResource.TYPE) == 0) {queryType = GenericResource.class; profileClass = GCUBEGenericResource.class;}
		else if (this.type.compareToIgnoreCase(GCUBEService.TYPE) == 0) {queryType = Software.class; profileClass = GCUBEService.class;}
		else if (this.type.compareToIgnoreCase(GCUBERuntimeResource.TYPE) == 0) {queryType = ServiceEndpoint.class; profileClass = GCUBERuntimeResource.class;}
		else throw new Exception("Unknown resource type: " + this.type);
		List<? extends Resource> profiles=null;

		try {
			
			XQuery query = queryFor(queryType);

			query.addCondition(String.format("$resource/ID/string() eq '%s'",this.id));
			logger.info("execution query "+query+" on scope "+scope+" with id: "+this.id);
			
			DiscoveryClient<? extends Resource> client = clientFor(queryType);

			//I'm not sure the scopeProvider is set in this thread
			ScopeProvider.instance.set(this.getScope().toString());
			
			profiles = client.submit(query);

			if ((profiles != null) && (profiles.size() > 0)){ 
				logger.info("profile found ");
				this.profile = profiles.get(0);
			}else{

				String enclosingScope = GCUBEScope.getScope(this.scope).getEnclosingScope().toString();
				try{
					ScopeProvider.instance.set(GCUBEScope.getScope(this.scope).getEnclosingScope().toString());
					logger.info("profile not found. Try on enclosing scope: "+enclosingScope);
					// obviously, in the case of adding, the resource is not in the current scope, therefore we look upstairs (the enclosing scope)		
					this.profile = client.submit(query).get(0);
				}finally{
					ScopeProvider.instance.set(this.scope.toString());
				}
			}
		} catch (Throwable e) {
			getLogger().error("bad query. Caused by: "+e.getCause());
			throw new Exception("unable to find the target resource (ID=" + id + "). Possible cause: " + e.getMessage(), e);						
		}
	}

	@Override
	protected void addToScope() throws ResourceNotFound, Exception {
		this.findResource();
		getLogger().debug("Adding scope to resource profile");
		try {

			getLogger().debug("republish the resource "+profile.id()+" with scope added "+this.getScope());
			//patch: serviceContext altered by is-publisher	
			/*GCUBEScope currentServiceContext=ServiceContext.getContext().getScope();
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
			getLogger().debug(": "+profile.getID()+"\n\t the ServiceContext is "+ServiceContext.getContext().getScope());
			//patch: serviceContext altered by is-publisher
			ServiceContext.getContext().setScope(currentServiceContext);*/

			ScopedPublisher publisher  = RegistryPublisherFactory.scopedPublisher();
			publisher.create(profile, Collections.singletonList(this.getScope().toString()));


		} catch (Exception e) {
			this.noHopeForMe("Failed to add the scope ("+ this.getScope()+") to resource " + this.getId(), e);
		}
	}

	@Override
	protected void removeFromScope() throws ResourceNotFound, Exception {		
		this.findResource();
		getLogger().debug("Removing scope from resource profile");	
		try {
			//patch: serviceContext altered by is-publisher	
			/*GCUBEScope currentServiceContext=ServiceContext.getContext().getScope();
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
			//patch: serviceContext altered by is-publisher
			ServiceContext.getContext().setScope(currentServiceContext);
			*/
			ScopedPublisher publisher  = RegistryPublisherFactory.scopedPublisher();
			publisher.remove(profile, Collections.singletonList(this.getScope().toString()));
		} catch (Exception e) {
			this.noHopeForMe("Failed to remove the scope ("+ this.getScope()+") from resource " + this.getId(), e);
		}

	}

}
