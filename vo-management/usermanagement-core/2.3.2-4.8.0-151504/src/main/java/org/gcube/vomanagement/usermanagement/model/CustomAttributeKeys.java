package org.gcube.vomanagement.usermanagement.model;
/**
 * 
 * @author Massimiliano Assante, CNR-ISTI
 * For gCube Portal Bundle (Site) Custom Attributes keys
 * 
 * use LiferayGroupManager#readCustomAttr method to read them or LiferayUserManager#readCustomAttr
 *
 */
public enum CustomAttributeKeys {
	//Group
	MANDATORY("Mandatory"),
	IS_EXTERNAL("Isexternal"),
	URL("Url"),
	POST_NOTIFICATION("Postnotificationviaemail"),
	VIRTUAL_GROUP("Virtualgroup"),
	GATEWAY_SITE_NAME("Gatewayname"),
	GATEWAY_SITE_EMAIL_SENDER("Emailsender"),
	//User
	USER_LOCATION_INDUSTRY("industry");	
	
	private String name;
	
	private CustomAttributeKeys(String name) {
		this.name = name;
	}
	
	public String getKeyName() {
		return this.name;
	}
}
