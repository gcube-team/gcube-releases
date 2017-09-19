package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;

import java.util.Date;



/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TRValidSinceMetadata implements TRMetadata {
	
	private static final long serialVersionUID = 5425320654892310426L;
	private String id="ValidSinceMetadata";
	private String title="Valid Since";
	
	private Date value;

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TRValidSinceMetadata [value=" + value + "]";
	}

	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	
}
