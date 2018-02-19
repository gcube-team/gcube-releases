package org.gcube.portlets.user.workspaceapplicationhandler.util;

public class Util {
	
	
	public static String getGcubeGenericQueryString(String secondaryType, String appId){
		
		return "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
				"where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"' and  $profile/Profile/Body/AppId/string() " +
				" eq '" + appId + "'" +
				"return $profile";
	}

}
