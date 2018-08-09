package org.gcube.portlets.user.geoexplorer.shared.metadata;

import java.io.Serializable;

import org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo.ContactItem;

public class ResponsiblePartyItem implements Serializable{
 
    /**
	 * 
	 */
	private static final long serialVersionUID = -2036723706618029728L;

	/**
     * Name of the responsible person- surname, given name, title separated by a delimiter.
     */
    private String individualName;

    /**
     * Name of the responsible organization.
     */
    private String organisationName;

    /**
     * Role or position of the responsible person
     */
    private String positionName;

    /**
     * Address of the responsible party.
     */
    private ContactItem contactInfo;

    /**
     * Function performed by the responsible party.
     * see: CI_RoleCode
     */
    private String role;

    /**
     * Constructs an initially empty responsible party.
     */
    public ResponsiblePartyItem() {
    }
    
	public String getIndividualName() {
		return individualName;
	}



	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}



	public String getOrganisationName() {
		return organisationName;
	}



	public void setOrganisationName(String organisationName) {
		this.organisationName = organisationName;
	}



	public String getPositionName() {
		return positionName;
	}



	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}



	public ContactItem getContactInfo() {
		return contactInfo;
	}



	public void setContactInfo(ContactItem contactInfo) {
		this.contactInfo = contactInfo;
	}



	public String getRole() {
		return role;
	}



	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ResponsiblePartyItem [individualName=");
		builder.append(individualName);
		builder.append(", organisationName=");
		builder.append(organisationName);
		builder.append(", positionName=");
		builder.append(positionName);
		builder.append(", contactInfo=");
		builder.append(contactInfo);
		builder.append(", role=");
		builder.append(role);
		builder.append("]");
		return builder.toString();
	}
}
