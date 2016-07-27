package org.gcube.smartgears.provider;

import java.lang.reflect.Method;
import java.util.List;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.smartgears.configuration.Mode;

/**
 * An implementation of {@link ScopedPublisher} that simulates remote publication.
 * <p>
 * Used for applications and or containers that operate in {@link Mode#offline}.
 * 
 * @author Fabio Simeoni
 * 
 */
public class OfflinePublisher implements ScopedPublisher {

	@Override
	public <T extends Resource> T create(T resource, List<String> scopes) throws RegistryNotFoundException {

		// fragile! bypass restrictions reflectively and set new scope
		for (String scope : scopes)
			try {
				Method m = resource.getClass().getSuperclass().getDeclaredMethod("addScope", String.class);
				m.setAccessible(true);
				m.invoke(resource, scope);
			} catch (Exception e) {
				throw new RuntimeException("could not simulate publication in scope " + scope, e);
			}
		return resource;
	}

	@Override
	public <T extends Resource> T remove(T resource, List<String> scopes) throws RegistryNotFoundException {
		// fragile! bypass restrictions reflectively and set new scope
		for (String scope : scopes)
			try {
				Method m = resource.getClass().getSuperclass().getDeclaredMethod("removeScope", String.class);
				m.setAccessible(true);
				m.invoke(resource, scope);
			} catch (Exception e) {
				throw new RuntimeException("could not simulate publication remove from scope " + scope, e);
			}
		return resource;
	}

	
	@Override
	public <T extends Resource> T update(T resource) throws RegistryNotFoundException {
		// do nothing
		return resource;
	}

}
