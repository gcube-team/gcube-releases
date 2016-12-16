package org.gcube.data_catalogue.grsf_publish_ws.utils.groups;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Production_System_Type for Fishery records
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Production_System_Type {
	
	Subsistence("Subsistence"),
	Recreational("Recreational"),
	Commercial("Commercial"), 
	Artisanal("Artisanal"),
	Semi_Industrial("Semi-industrial"),
	Industrial("Industrial"),
	Exploratory_fishery("Exploratory fishery"), 
	Unspecified("Unspecified");

	private String subGroupNameOrig;

	private Production_System_Type(String origName) {
		this.subGroupNameOrig = origName;
	}

	/**
	 * Return the original name
	 * @return
	 */
	public String getOrigName(){
		return subGroupNameOrig;
	}
	
	@JsonValue
	public String onSerialize(){
		return subGroupNameOrig.replaceAll("[^A-Za-z]", " ").toLowerCase();
	}

	@JsonCreator
	public static Production_System_Type onDeserialize(String productionString) {
		if(productionString != null) {
			for (Production_System_Type productionValue : Production_System_Type.values()) {
				if(productionValue.getOrigName().replaceAll("[^A-Za-z]", " ").equalsIgnoreCase(productionString.replaceAll("[^A-Za-z]", " ")))
					return productionValue;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getOrigName();
	}
}
