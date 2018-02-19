package org.gcube.common.mycontainer.dependencies;

import org.gcube.common.core.informationsystem.publisher.ISPublisher;
import org.gcube.common.core.informationsystem.publisher.ISPublisherException;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.mycontainer.MyContainerDependencies;

/**
 * An {@link ISPublisher} that dispatches invocations to an implementation set on {@link MyContainerDependencies}, typically
 * a mock created in the scope of a test.
 * 
 * @author Fabio Simeoni
 *
 */
public class ProxyISPublisher implements ISPublisher {

	private ISPublisher publisher;
	
	public ProxyISPublisher() {
		publisher = MyContainerDependencies.resolve(ISPublisher.class);
	}
	
	public void registerWSResource(GCUBEWSResource resource,
			GCUBEScope... scope) throws ISPublisherException {
		publisher.registerWSResource(resource, scope);
	}

	public void updateWSResource(GCUBEWSResource resource, GCUBEScope... scope)
			throws ISPublisherException {
		publisher.updateWSResource(resource, scope);
	}

	public void removeWSResource(GCUBEWSResource resource, GCUBEScope... scope)
			throws ISPublisherException {
		publisher.removeWSResource(resource, scope);
	}

	public String registerGCUBEResource(GCUBEResource resource,
			GCUBEScope scope, GCUBESecurityManager manager)
			throws ISPublisherException {
		return publisher.registerGCUBEResource(resource, scope, manager);
	}

	public void removeGCUBEResource(String ID, String type, GCUBEScope scope,
			GCUBESecurityManager manager) throws ISPublisherException {
		publisher.removeGCUBEResource(ID, type, scope, manager);
	}

	public void updateGCUBEResource(GCUBEResource resource, GCUBEScope scope,
			GCUBESecurityManager manager) throws ISPublisherException {
		publisher.updateGCUBEResource(resource, scope, manager);
	}
	
	
	
}
