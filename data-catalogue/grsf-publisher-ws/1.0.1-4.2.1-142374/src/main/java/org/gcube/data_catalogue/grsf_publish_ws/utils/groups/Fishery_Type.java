package org.gcube.data_catalogue.grsf_publish_ws.utils.groups;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Type for Fishery records
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Fishery_Type {

	Fishing_Activity("Fishing Activity"), // for fishery  
	Fishing_Description("Fishing Description"); // for fishery

	private String subGroupNameOrig;

	private Fishery_Type(String origName) {
		this.subGroupNameOrig = origName;
	}

	/**
	 * Return the original name
	 * @return
	 */
	public String getOrigName(){
		return subGroupNameOrig;
	}

	/**
	 * Get the json value
	 * @return
	 */
	@JsonValue
	public String onSerialize(){
		return subGroupNameOrig.toLowerCase();
	}

	/**
	 * Get back an enum from json string
	 * @param typeString
	 * @return
	 */
	@JsonCreator
	public static Fishery_Type onDeserialize(String typeString) {
		if(typeString != null) {
			for(Fishery_Type type : Fishery_Type.values()) {
				if(type.getOrigName().equalsIgnoreCase(typeString.trim()))
					return type;
			}
		}
		return null;
	}
	
	/**
	 * Used by reflection
	 */
	@Override
	public String toString() {
		return getOrigName();
	}
}
