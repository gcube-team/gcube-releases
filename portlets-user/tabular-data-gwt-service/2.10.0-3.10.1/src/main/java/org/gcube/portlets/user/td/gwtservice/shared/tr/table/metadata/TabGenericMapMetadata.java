package org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata;

import java.util.HashMap;


/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabGenericMapMetadata implements TabMetadata {
	private static final long serialVersionUID = -2663624208642658528L;

	String id="GenericMapMetadata";
	String title="Generic Map";
	
	private HashMap<String, String> metadataMap = new HashMap<String, String>();

	public HashMap<String, String> getMetadataMap() {
		return metadataMap;
	}

	public void setMetadataMap(HashMap<String, String> metadataMap) {
		this.metadataMap = metadataMap;
	}

	@Override
	public String toString() {
		return "TabGenericMapMetadata [metadataMap=" + metadataMap + "]";
	}


	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	
	
}
