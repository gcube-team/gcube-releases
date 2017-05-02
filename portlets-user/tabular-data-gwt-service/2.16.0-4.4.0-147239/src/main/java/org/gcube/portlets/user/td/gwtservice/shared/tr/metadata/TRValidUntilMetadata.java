package org.gcube.portlets.user.td.gwtservice.shared.tr.metadata;

import java.util.Date;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TRValidUntilMetadata implements TRMetadata {

	private static final long serialVersionUID = 4127072595380574045L;
	private String id = "ValidUntilMetadata";
	private String title = "Valid Until";

	private Date value;

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "TRValidUntilMetadata [value=" + value + "]";
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

}
