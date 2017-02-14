package org.gcube.common.resources.gcore;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceMediator {

	private static final Logger log = LoggerFactory.getLogger(ResourceMediator.class);
	
	public static void setId(Resource resource, String id) {
	     resource.setId(id);
	     log.debug(id+"new id resource: "+resource.id());
	}
	
	public static void setScope(Resource resource, String scope){
		resource.scopes().add(scope);
	}
	public static void removeScope(Resource resource, String scope){
		resource.scopes().remove(scope);
	}
	
}
