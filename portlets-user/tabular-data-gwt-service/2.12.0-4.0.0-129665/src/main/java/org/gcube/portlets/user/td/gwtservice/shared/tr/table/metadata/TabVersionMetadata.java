package org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata;



/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabVersionMetadata implements TabMetadata {
	
	
	private static final long serialVersionUID = 5208229342328376604L;

	String id="VersionMetadata";
	String title="Version";
	
	private String version;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "TabVersionMetadata [version=" + version + "]";
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	
}
