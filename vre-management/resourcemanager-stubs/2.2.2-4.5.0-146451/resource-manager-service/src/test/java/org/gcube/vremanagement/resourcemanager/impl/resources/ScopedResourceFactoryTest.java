package org.gcube.vremanagement.resourcemanager.impl.resources;

import org.gcube.common.core.scope.GCUBEScope;

public class ScopedResourceFactoryTest extends ScopedResourceFactory {

	
	public static ScopedResource newGHN(String id,GCUBEScope scope) {
		return new ScopedGHN(id, scope);
	}

	public static ScopedResource newRI(String id,GCUBEScope scope) {
		return new ScopedRunningInstance(id, scope);
	}
}
