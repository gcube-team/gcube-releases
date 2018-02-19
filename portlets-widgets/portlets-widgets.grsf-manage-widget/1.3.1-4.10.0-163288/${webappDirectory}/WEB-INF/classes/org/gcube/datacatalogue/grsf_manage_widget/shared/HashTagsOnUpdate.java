package org.gcube.datacatalogue.grsf_manage_widget.shared;

/**
 * A list of hastags to send along the update
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum HashTagsOnUpdate {
	
	MERGE("merge"),
	REVERTED_MERGE("reverted_merge"),
	CONNECT("connect"),
	SHORTNAME_UPDATED("shortname_updated"),
	TRACEABILITY_FLAG_SET("traceability_flag_set"),
	TRACEABILITY_FLAG_UNSET("traceability_flag_unset");
	
	private String string;
	
	HashTagsOnUpdate(String asString){
		this.string = asString;
	}

	public String getString() {
		return string;
	}

}
