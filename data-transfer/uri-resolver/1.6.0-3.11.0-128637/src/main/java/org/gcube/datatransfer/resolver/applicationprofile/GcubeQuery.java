package org.gcube.datatransfer.resolver.applicationprofile;

/**
 * The Class GcubeQuery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 14, 2016
 */
public class GcubeQuery {


	/**
	 * Gets the gcube generic query string.
	 *
	 * @param secondaryType the secondary type
	 * @param appId the app id
	 * @return the gcube generic query string
	 */
	public static String getGcubeGenericQueryString(String secondaryType, String appId){

		return "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
				"where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"' and  $profile/Profile/Body/AppId/string() " +
				" eq '" + appId + "'" +
				"return $profile";
	}

}
