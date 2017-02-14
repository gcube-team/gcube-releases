package org.gcube.datacatalogue.grsf_manage_widget.shared;

/**
 * Status of a grsf record.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public enum GRSFStatus {

	APPROVED("Approved"),
	REJECTED("Rejected"),
	PENDING("Pending"),
	ARCHIVED("Archived"),
	HIDDEN("Hidden");
	
	private String asString;

	private GRSFStatus(String asString) {
		this.asString = asString;
	}

	@Override
	public String toString() {
		return asString;
	}

	public static GRSFStatus fromString(String string){
		if(string == null || string.isEmpty())
			return null;
		
		for(GRSFStatus value: GRSFStatus.values())
			if(value.toString().equalsIgnoreCase(string))
				return value;
				
		return null;
	}
}
