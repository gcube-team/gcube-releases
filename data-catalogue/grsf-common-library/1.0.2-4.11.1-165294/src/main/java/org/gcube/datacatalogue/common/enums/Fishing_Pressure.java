package org.gcube.datacatalogue.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;


/**
 * Fishing_Pressure for Stock records
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Fishing_Pressure {
	
	Not_Applicable("Not applicable"),
	Moderate_Fishing_Mortality("Moderate fishing mortality"),
	High_Fishing_Mortality("High fishing mortality"),
	No_Or_Low_Fishing_Mortality("No or low fishing mortality"),
	Uncertain_Not_assessed("Uncertain/Not assessed");
	
	private String subGroupNameOrig;
	
	private Fishing_Pressure(String origName) {
		this.subGroupNameOrig = origName;
	}

	/**
	 * Return the original name
	 * @return
	 */
	public String getOrigName(){
		return subGroupNameOrig;
	}

	@JsonCreator
	public static Fishing_Pressure onDeserialize(String fishing_Pressure) {
		if(fishing_Pressure != null) {
			for (Fishing_Pressure fishingPressure : Fishing_Pressure.values()) {
				if(fishingPressure.getOrigName().replaceAll("[^A-Za-z]", " ").equalsIgnoreCase(fishing_Pressure.trim().replaceAll("[^A-Za-z]", " ")))
					return fishingPressure;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getOrigName();
	}
	
}
