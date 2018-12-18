/**
 *
 */
package org.gcube.dataharvest.datamodel;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 22, 2018
 */
public enum HarvestedDataKey {

	ACCESSES("VRE Accesses"),
	USERS("VRE Users"),
	DATA_METHOD_DOWNLOAD("Data/Method download"),
	NEW_CATALOGUE_METHODS("New Catalogue Methods"),
	NEW_CATALOGUE_DATASETS("New Catalogue Datasets"),
	NEW_CATALOGUE_DELIVERABLES("New Catalogue Deliverables"),
	NEW_CATALOGUE_APPLICATIONS("New Catalogue Applications"),
	SOCIAL_POSTS("VRE Social Interations Posts"),
	SOCIAL_REPLIES("VRE Social Interations Replies"),
	SOCIAL_LIKES("VRE Social Interations Likes"),
	METHOD_INVOCATIONS("VRE Methods Invocation"),
	VISUAL_TOOLS("VRE Visual Tools");

	private String key;

	HarvestedDataKey(String key){
		this.key = key;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

}
