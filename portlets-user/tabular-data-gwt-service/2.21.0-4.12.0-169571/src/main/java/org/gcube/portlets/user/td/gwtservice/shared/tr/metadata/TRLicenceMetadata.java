package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;



/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TRLicenceMetadata implements TRMetadata {
	
	private static final long serialVersionUID = 4127072595380574045L;
	String id="LicenceMetadata";
	String title="Licence";
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	@Override
	public String toString() {
		return "TRLicenceMetadata [id=" + id + ", title=" + title + ", value="
				+ value + "]";
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	
}
