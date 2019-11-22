/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 17, 2014
 *
 */
public class TdFlowModel implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2352926790735853890L;
	
	protected String licenceId;
	protected String name;
	protected String description;
	protected String agency;
	protected String rights;
	
	protected Date toDate;
	protected Date fromDate;

	private String behaviourId;
	
	/**
	 * 
	 */
	public TdFlowModel() {
	}

	public String getLicenceId() {
		return licenceId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getAgency() {
		return agency;
	}

	public String getRights() {
		return rights;
	}

	public Date getToDate() {
		return toDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setLicenceId(String licenceId) {
		this.licenceId = licenceId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @param validString
	 */
	public void setBehaviourId(String behaviourId) {
		this.behaviourId = behaviourId;	
	}

	public String getBehaviourId() {
		return behaviourId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TdFlowModel [licenceId=");
		builder.append(licenceId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", agency=");
		builder.append(agency);
		builder.append(", rights=");
		builder.append(rights);
		builder.append(", toDate=");
		builder.append(toDate);
		builder.append(", fromDate=");
		builder.append(fromDate);
		builder.append(", behaviourId=");
		builder.append(behaviourId);
		builder.append("]");
		return builder.toString();
	}
	
}
