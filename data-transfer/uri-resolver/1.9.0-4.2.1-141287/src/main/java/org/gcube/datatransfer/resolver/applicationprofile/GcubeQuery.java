package org.gcube.datatransfer.resolver.applicationprofile;


/**
 * The Class GcubeQuery.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Dec 20, 2016
 */
public class GcubeQuery {


	/**
	 * The Enum FIELD_TYPE.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Dec 20, 2016
	 */
	public static enum FIELD_TYPE {RESOURCE_NAME, APP_ID}


	/**
	 * Gets the gcube generic resource.
	 *
	 * @param secondaryType the secondary type
	 * @param type the type
	 * @param fieldValue the field value
	 * @return the gcube generic resource
	 */
	public static String getGcubeGenericResource(String secondaryType, FIELD_TYPE type, String fieldValue){

		String query = "for $profile in collection('/db/Profiles/GenericResource')//Resource " +
						"where $profile/Profile/SecondaryType/string() eq '"+secondaryType+"'";
		switch (type) {
		case APP_ID:
			query+= " and  $profile/Profile/Body/AppId/string() eq '" + fieldValue + "'";

			break;
		case RESOURCE_NAME:
			query+= " and  $profile/Profile/Name/string() eq '" + fieldValue + "'";

			break;
		default:
			break;
		}

		return query+=" return $profile";
	}

}
