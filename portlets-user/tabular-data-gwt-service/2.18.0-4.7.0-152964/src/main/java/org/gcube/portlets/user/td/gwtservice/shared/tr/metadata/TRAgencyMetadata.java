package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;



/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TRAgencyMetadata implements TRMetadata {
	
	private static final long serialVersionUID = -7693467120874749946L;
	String id="AgencyMetadata";
	String title="Agency";
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TRAgencyMetadata [value=" + value + "]";
	}

	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	
}
