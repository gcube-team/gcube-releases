package org.gcube.data_catalogue.grsf_publish_ws.utils.groups;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Abundance_Level for Stock records
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Abundance_Level {

	Intermediate_Abundance("Intermediate abundance"),
	Low_Abundance("Low abundance"),
	Uncertain_Not_Assessed("Uncertain/Not assessed"),
	Not_applicable("Not applicable"),
	Depleted("Depleted");

	private String subGroupNameOrig;

	private Abundance_Level(String origName) {
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
	public static Abundance_Level onDeserialize(String abundanceString) {
		if(abundanceString != null) {
			for (Abundance_Level abundanceValue : Abundance_Level.values()) {
				if(abundanceValue.getOrigName().replaceAll("[^A-Za-z]", " ").equalsIgnoreCase(abundanceString.replaceAll("[^A-Za-z]", " ")))
					return abundanceValue;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getOrigName();
	}
}
