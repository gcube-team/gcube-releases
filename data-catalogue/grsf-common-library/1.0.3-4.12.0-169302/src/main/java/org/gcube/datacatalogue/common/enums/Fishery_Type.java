package org.gcube.datacatalogue.common.enums;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Type for Fishery records
 * @author Costantino Perciante (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public enum Fishery_Type {

	Fishing_Unit("Fishing Unit"), // for fishery  
	Other_Fishery("Other Fishery"); // for fishery

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
	
	public static List<String> getTypesAsListString(){
		List<String> toReturn = new ArrayList<String>(Stock_Type.values().length);
		for(Fishery_Type type : Fishery_Type.values())
			toReturn.add(type.getOrigName());
		return toReturn;
	}
}
