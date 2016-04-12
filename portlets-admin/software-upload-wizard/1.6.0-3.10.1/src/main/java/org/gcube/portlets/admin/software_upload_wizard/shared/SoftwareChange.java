package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;

public class SoftwareChange implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 828157586190151742L;
	
	private Integer ticketNumber = null;
	private String description = "";
	
	public SoftwareChange() {
	
	}

	public SoftwareChange(Integer ticketNumber, String description) {
		super();
		this.ticketNumber = ticketNumber;
		this.description = description;
	}

	public Integer getTicketNumber() {
		return ticketNumber;
	}

	public void setTicketNumber(Integer ticketNumber) {
		this.ticketNumber = ticketNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
