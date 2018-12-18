package org.gcube.datacatalogue.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status Group and sub groups (for both Stock and Fishery, only Aggregated records)
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum Status {

	Pending("Pending"),
	Approved("Approved"),
	Rejected("Rejected"), // for rejecting a given record
	Reject_Merge("Reject Merge"), // for rejecting a merge (i.e. current status of the record is
	// To Be Merged
	Archived("Archived"),
	Hidden("Hidden"),
	To_be_Merged("To Be Merged");

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
			String modifiedString = statusString.trim().replace("-", " ").replace("_", " ");
			for(Status status : Status.values()) {
				if (status.toString().equalsIgnoreCase(modifiedString))
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

	public static Status fromString(String string){
		if(string == null || string.isEmpty())
			return null;

		for(Status value: Status.values())
			if(value.toString().equalsIgnoreCase(string))
				return value;

		return null;
	}
}
