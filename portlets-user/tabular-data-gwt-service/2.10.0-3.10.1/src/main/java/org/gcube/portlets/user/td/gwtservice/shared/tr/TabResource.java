package org.gcube.portlets.user.td.gwtservice.shared.tr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabResource implements Serializable {

	private static final long serialVersionUID = -8353499109124097114L;

	private TRId trId;
	private String id;// For grid only
	private String name;
	private String description;
	private String agency;
	private Date date;
	private String right;
	private Date validFrom;
	private Date validUntilTo;
	private String licence;
	private Contacts owner;
	private ArrayList<Contacts> contacts;
	private boolean valid;
	private boolean finalized;
	private boolean locked;
	
	public TabResource() {
	}

	public TabResource(String id, String name, String description,
			String agency, Date date, String right, Date validFrom, Date validUntilTo, 
			String licence, TRId trId) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.agency = agency;
		this.date = date;
		this.validFrom = validFrom;
		this.validUntilTo = validUntilTo;
		this.right = right;
		this.licence = licence;
		this.trId = trId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public TRId getTrId() {
		return trId;
	}

	public void setTrId(TRId trId) {
		this.trId = trId;
	}

	public String getTabResourceType() {
		String t = "";
		if (trId != null && trId.getTabResourceType() != null) {
			t = trId.getTabResourceType().toString();
		}
		return t;
	}
	
	public String getTableTypeName() {
		String t = "";
		if (trId != null && trId.getTableTypeName() != null) {
			t = trId.getTableTypeName();
		}
		return t;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Contacts getOwner() {
		return owner;
	}

	public void setOwner(Contacts owner) {
		this.owner = owner;
	}

	public String getOwnerLogin() {
		String login = null;
		if (owner != null) {
			login = owner.getLogin();
		}
		return login;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isFinalized() {
		return finalized;
	}

	public void setFinalized(boolean finalized) {
		this.finalized = finalized;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUntilTo() {
		return validUntilTo;
	}

	public void setValidUntilTo(Date validUntilTo) {
		this.validUntilTo = validUntilTo;
	}
	
	public String getLicence() {
		return licence;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public ArrayList<Contacts> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contacts> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "TabResource [trId=" + trId + ", id=" + id + ", name=" + name
				+ ", description=" + description + ", agency=" + agency
				+ ", date=" + date + ", right=" + right + ", validFrom="
				+ validFrom + ", validUntilTo=" + validUntilTo + ", licence="
				+ licence + ", owner=" + owner + ", contacts=" + contacts
				+ ", valid=" + valid + ", finalized=" + finalized + ", locked="
				+ locked + "]";
	}

	
	

}
