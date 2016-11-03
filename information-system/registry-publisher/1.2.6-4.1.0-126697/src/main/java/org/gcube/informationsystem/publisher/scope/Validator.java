package org.gcube.informationsystem.publisher.scope;

import java.util.List;

import org.gcube.common.resources.gcore.Resource;

public interface Validator <R extends Resource>{

	<R extends Resource> void validate(R resource);
	<R extends Resource> void checkScopeCompatibility(R resource, List<String> scopes);
	
	public Class<R> type();
	
}
