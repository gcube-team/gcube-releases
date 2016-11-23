package org.gcube.portlets.user.td.gwtservice.shared.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.share.Contacts;

/**
 * Template Description
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TemplateData implements Serializable {

	private static final long serialVersionUID = -2884032748710752646L;
	private long id;
	private String name;
	private String description;
	private String agency;
	private String category;
	private Contacts owner;
	private ArrayList<Contacts> contacts;
	private Date creationDate;

	public TemplateData() {
		super();
	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param description
	 * @param agency
	 * @param category
	 * @param owner
	 * @param contacts
	 */
	public TemplateData(long id, String name, String description,
			String agency, String category, Contacts owner,
			ArrayList<Contacts> contacts, Date creationDate) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.agency = agency;
		this.category = category;
		this.owner = owner;
		this.contacts = contacts;
		this.creationDate = creationDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {	
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
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

	public ArrayList<Contacts> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contacts> contacts) {
		this.contacts = contacts;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return "TemplateData [id=" + id + ", name=" + name + ", description="
				+ description + ", agency=" + agency + ", category=" + category
				+ ", owner=" + owner + ", contacts=" + contacts
				+ ", creationDate=" + creationDate + "]";
	}

}
