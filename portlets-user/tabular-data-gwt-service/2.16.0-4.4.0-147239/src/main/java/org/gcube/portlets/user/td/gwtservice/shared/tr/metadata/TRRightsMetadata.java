package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;



/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TRRightsMetadata implements TRMetadata {
	
	private static final long serialVersionUID = -7693467120874749946L;
	String id="RightsMetadata";
	String title="Rights";
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TRRightsMetadata [value=" + value + "]";
	}

	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	
}
