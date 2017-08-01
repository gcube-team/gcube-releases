/**
 *
 */
package org.gcube.datacatalogue.metadatadiscovery.reader;


/**
 * The Class QueryForResourceUtil.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 26, 2017
 */
public class QueryForResourceUtil {


	/**
	 * Query for generic resource by id. Returns a query string to get a generic resource by input resource id
	 *
	 * @param resourceId the resource id
	 * @return the string
	 */
	public static synchronized String queryForGenericResourceById(String resourceId){

		return String.format("declare namespace ic = 'http://gcube-system.org/namespaces/informationsystem/registry'; " +
						"for $resource in collection('/db/Profiles')//Document/Data/ic:Profile/Resource " +
						"where ($resource/ID/text() eq '%s') return $resource", resourceId);
	}



	/**
	 * Gets the gcube generic query string for secondary type.
	 *
	 * @param secondaryType the secondary type
	 * @return the gcube generic query string for secondary type
	 */
	public static synchronized String getGcubeGenericQueryStringForSecondaryType(String secondaryType){

		return "for $profile in collection('/db/Profiles/GenericResource')//Resource" +
				" where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"'" +
				" return $profile";
	}

}
