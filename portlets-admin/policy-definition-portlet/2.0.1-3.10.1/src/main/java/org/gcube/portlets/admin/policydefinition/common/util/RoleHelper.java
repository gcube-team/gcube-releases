package org.gcube.portlets.admin.policydefinition.common.util;

public class RoleHelper {

	public static final String SERVICE_PREFIX = "GHN.";
	
	public static String getRole(String role){
		if(role.indexOf(SERVICE_PREFIX) >= 0)
			return null;
		return role;
	}
	
	public static String getServiceCategory(String role){
		if(role.indexOf(SERVICE_PREFIX) < 0)
			return null;
		return viewRole(role);
	}
	
	public static String viewRole(String role){
		if(role.indexOf(SERVICE_PREFIX) >= 0)
			return role.substring(SERVICE_PREFIX.length());
		return role;
	}

}
