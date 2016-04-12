package org.gcube.common.homelibrary.jcr.workspace.util;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;

public class Utils {
	
	public static String getRootScope(String scope){
		ScopeBean tmpScope = new ScopeBean(scope);
		while (!tmpScope.is(Type.INFRASTRUCTURE))
			tmpScope = tmpScope.enclosingScope();
		return tmpScope.toString();
	}
	
	/**
	 * Get Group name by scope
	 * @param scope
	 * @return
	 */
	public static String getGroupByScope(String scope) {
		String VREFolder;
		if (scope.startsWith("/"))			
			VREFolder = scope.replace("/", "-").substring(1);
		else
			VREFolder = scope.replace("/", "-");
		return VREFolder;

	}

	
}
