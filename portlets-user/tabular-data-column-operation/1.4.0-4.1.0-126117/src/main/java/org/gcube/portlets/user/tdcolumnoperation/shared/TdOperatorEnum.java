/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.shared;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 30, 2014
 *
 */
public enum TdOperatorEnum {
	

	CHAR_SEQUENCE("CHAR_SEQUENCE", "CHAR SEQUENCE", "Split by char sequence"),
	REGEX("REGEX", "REGEX", "Split by regular expression"),
	INDEX("INDEX", "INDEX", "Split by index"),
	
	MERGE("MERGE", "MERGE", "Merge the columns adding a separator");
	
	private String id;
	private String label;
	private String description;
	
	TdOperatorEnum(String id, String label, String description){
		this.id = id;
		this.label = label;
		this.description = description;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
