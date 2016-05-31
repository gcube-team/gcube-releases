package org.gcube.portlets.admin.software_upload_wizard.server.util;

import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeUtil {
	
	private static final Logger log = LoggerFactory.getLogger(ScopeUtil.class);
	
	public static String getScope(){
		String scope = ScopeProvider.instance.get();
		if (scope == null) scope = "/gcube/devsec";
		ScopeProvider.instance.set(scope);
		return scope;
	}

	public static String getInfrastructure(String scope){
		String scopeInfrastructure =  scope.substring(scope.indexOf("/")+1, scope.indexOf("/", scope.indexOf("/")+1));
		log.debug("Computed infrastructure: " + scopeInfrastructure);
		return scopeInfrastructure;
	}
}
