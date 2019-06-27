/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Mar 20, 2014
 *
 */
public enum SPECIAL_CATEGORY_TYPE {

	DIMENSION("DIMENSION", "DIMENSION"),
	TIMEDIMENSION("TIMEDIMENSION", "TIMEDIMENSION"),
	CODENAME("CODENAME", "CODENAME"),
	CODE("CODE", "CODE"),
	ANNOTATION("ANNOTATION", "ANNOTATION"),
	CODEDESCRIPTION("CODEDESCRIPTION", "CODEDESCRIPTION"),
	NONE("NONE", "NONE"),
	UNKNOWN("UNKNOWN", "UNKNOWN");
	
	private String id;
	private String label;

	/**
	 * 
	 */
	private SPECIAL_CATEGORY_TYPE(String id, String label) {
		this.id = id;
		this.label = label;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
