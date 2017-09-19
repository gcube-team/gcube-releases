package org.gcube.data_catalogue.grsf_publish_ws.utils.groups;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Source Group and sub groups (for both Stock and Fishery) -> look at "Database Sources"
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Sources {

	FIRMS("FIRMS"),
	RAM("RAM"),
	FISHSOURCE("FishSource"),
	GRSF("GRSF");

	private String subGroupNameOrig;

	private Sources(String origName) {
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
		return subGroupNameOrig.toLowerCase();
	}

	@JsonCreator
	public static Sources onDeserialize(String sourceString) {
		if(sourceString != null) {
			for(Sources source : Sources.values()) {
				if (source.toString().equalsIgnoreCase(sourceString.trim()))
					return source;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getOrigName();
	}
	
	public static String getAsList(){
		return "[" + Arrays.asList(
				FIRMS.toString().toLowerCase(), 
				RAM.toString().toLowerCase(), 
				FISHSOURCE.toString().toLowerCase(), 
				GRSF.toString().toLowerCase()) + "]";
	}
}
