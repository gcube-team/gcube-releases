package org.gcube.common.vremanagement.deployer.impl.contexts;

import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.GCUBEResource.AddScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.RemoveScopeEvent;
import org.gcube.common.core.resources.GCUBEResource.ResourceConsumer;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.vremanagement.deployer.impl.resources.BaseTypedPackage;
import org.gcube.common.vremanagement.deployer.impl.resources.KeyData;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource;
import org.gcube.common.vremanagement.deployer.impl.state.DeployerResource.NoSuchPackageException;

/**
 * Scope manager for gCube packages deployed in the gHN.
 * It reacts to add/remove to/from scope operations related to local RIs and in turn it adds and removes 
 * the same scop(s)e from their packages and dependencies (if any)  
 * 
 * @author Manuele Simi (ISTI-CNR)
 * @see ResourceConsumer
 */
public class RIConsumer extends ResourceConsumer {
	
	protected final GCUBELog  logger = new GCUBELog(RIConsumer.class);
	
	private GCUBEService service;
	
	private DeployerResource resource;
	
	protected RIConsumer(GCUBEService service, DeployerResource resource) {
		this.service = service;
		this.resource = resource;
	}


	/* (non-Javadoc)
	 * @see org.gcube.common.core.resources.GCUBEResource.ResourceConsumer#onAddScope(org.gcube.common.core.resources.GCUBEResource.AddScopeEvent)
	 */
	@Override
	protected void onAddScope(AddScopeEvent event) {
		super.onAddScope(event);
		logger.trace("New detected scope(s) for ServiceClass=" + service.getServiceClass() + ",ServiceName=" + this.service.getServiceName() +",ServiceVersion=" + this.service.getVersion() );	
		for (Package servicePackage : service.getPackages()) {
			try {
				BaseTypedPackage base = this.getPackage(servicePackage);				
				this.addScopeToDeps(base.getKey(), toSet(event.getPayload()));
			} catch (NoSuchPackageException e) {
				logger.trace("Package not found: "+ e.getMessage());
			}
			
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.common.core.resources.GCUBEResource.ResourceConsumer#onRemoveScope(org.gcube.common.core.resources.GCUBEResource.RemoveScopeEvent)
	 */
	@Override
	protected void onRemoveScope(RemoveScopeEvent event) {	
		super.onRemoveScope(event);
		logger.trace("Removed scope(s) for ServiceClass=" + service.getServiceClass() + ",ServiceName=" + this.service.getServiceName() +",ServiceVersion=" + this.service.getVersion() );
		for (Package servicePackage : service.getPackages()) {
			try {				
				BaseTypedPackage base = this.getPackage(servicePackage);				
				this.removeScopeFromDeps(base.getKey(), toSet(event.getPayload()));				
			} catch (NoSuchPackageException e) {
				logger.trace("Package not found: "+ e.getMessage());
			}
		}		
	}
	
	/**
	 * Recursively removes scopes from a package and its the dependencies
	 * 
	 * @param key the key of the package
	 * @param scopes the scopes to remove
	 */
	private void removeScopeFromDeps(KeyData key, Set<GCUBEScope> scopes) {
		
		try {
			BaseTypedPackage tpackage = this.resource.getPackage(key);
			logger.trace("Removing scopes from package " + tpackage.getKey().toString());
			tpackage.removeScopes(scopes);
			for (KeyData dep : tpackage.getDependencies() ) removeScopeFromDeps(dep,scopes);
		} catch (NoSuchPackageException e) {
			logger.warn("Unable to remove the scopes from the package: " + e.getMessage());
		}		
	}


	/**
	 * Recursively adds scopes to a package and its the dependencies
	 * 
	 * @param key the key of the package
	 * @param scopes the scope to add
	 */
	private void addScopeToDeps(KeyData key, Set<GCUBEScope> scopes) {
		try {
			BaseTypedPackage tpackage = this.resource.getPackage(key);
			logger.trace("Adding scopes to package " + tpackage.getKey().toString());
			tpackage.setScopes(scopes);
			for (KeyData dep : tpackage.getDependencies() ) addScopeToDeps(dep, scopes);
		} catch (NoSuchPackageException e) {
			logger.warn("Unable to add the new scopes to the package: " + e.getMessage());
		}		
	}


	/**
	 * Retrieves the package from the service's state
	 * @param servicePackage the package to retrieve
	 * @return the package
	 * @throws NoSuchPackageException if the package is not in the service's state
	 */
	private BaseTypedPackage getPackage(Package servicePackage) throws NoSuchPackageException {
		KeyData base = new KeyData(this.service.getServiceClass(), this.service.getServiceName(), this.service.getVersion(), 
				servicePackage.getName(), servicePackage.getVersion());		
		return this.resource.getPackage(base);
	}

	
	/**
	 * Converts the input array in a {@link Set}
	 * @param scopes the input array
	 * @return the set filled with the array elements
	 */
	private Set<GCUBEScope> toSet(GCUBEScope[] scopes) {
		Set<GCUBEScope> retScopes = new HashSet<GCUBEScope>();
		for (GCUBEScope scope : scopes)
			retScopes.add(scope);
		return retScopes;
	}
}
