package org.gcube.data_catalogue.grsf_publish_ws.utils.groups;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status Group and sub groups (for both Stock and Fishery, only Aggregated records)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Status {
	
	Pending("Pending"),
	Approved("Approved"),
	Rejected("Rejected"),
	Archived("Archived"),
	Hidden("Hidden"),
	Merged("Merged");

	private String origName;

	private Status(String origName) {
		this.origName = origName;
	}

	/**
	 * Return the original name
	 * @return
	 */
	public String getOrigName(){
		return origName;
	}
	
	/**
	 * Get the json value
	 * @return
	 */
	@JsonValue
	public String onSerialize(){
		return origName.toLowerCase();
	}

	/**
	 * Get back an enum from json string
	 * @param statusString
	 * @return
	 */
	@JsonCreator
	public static Status onDeserialize(String statusString) {
		if(statusString != null) {
			for(Status status : Status.values()) {
				if (status.toString().equalsIgnoreCase(statusString.trim()))
					return status;
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
