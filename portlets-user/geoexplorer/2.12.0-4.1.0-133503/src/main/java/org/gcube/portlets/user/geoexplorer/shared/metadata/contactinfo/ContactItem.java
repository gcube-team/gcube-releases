package org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo;

import java.io.Serializable;

//import org.opengis.metadata.citation.Telephone;

public class ContactItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8224178123605538413L;
	
	
	 /**
     * Supplemental instructions on how or when to contact the individual or organization.
     */
    private String contactInstructions;

    /**
     * Time period (including time zone) when individuals can contact the organization or
     * individual.
     */
    private String hoursOfService;

    /**
     * On-line information that can be used to contact the individual or organization.
     */
    private OnlineResourceItem onlineResource;

    /**
     * Physical and email address at which the organization or individual may be contacted.
     */
    private AddressItem address;

    /**
     * Telephone numbers at which the organization or individual may be contacted.
     */
    private TelephoneItem phone;

    /**
     * Constructs an initially empty contact.
     */
    public ContactItem() {
    }
    
	public ContactItem(String contactInstructions, String hoursOfService,
			OnlineResourceItem onlineResource, AddressItem address, TelephoneItem phone) {
		this.contactInstructions = contactInstructions;
		this.hoursOfService = hoursOfService;
		this.onlineResource = onlineResource;
		this.address = address;
		this.phone = phone;
	}

	public String getContactInstructions() {
		return contactInstructions;
	}

	public void setContactInstructions(String contactInstructions) {
		this.contactInstructions = contactInstructions;
	}

	public String getHoursOfService() {
		return hoursOfService;
	}

	public void setHoursOfService(String hoursOfService) {
		this.hoursOfService = hoursOfService;
	}

	public OnlineResourceItem getOnlineResource() {
		return onlineResource;
	}

	public void setOnlineResource(OnlineResourceItem onlineResource) {
		this.onlineResource = onlineResource;
	}

	public AddressItem getAddress() {
		return address;
	}

	public void setAddress(AddressItem address) {
		this.address = address;
	}

	public TelephoneItem getPhone() {
		return phone;
	}

	public void setPhone(TelephoneItem phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContactItem [contactInstructions=");
		builder.append(contactInstructions);
		builder.append(", hoursOfService=");
		builder.append(hoursOfService);
		builder.append(", onlineResource=");
		builder.append(onlineResource);
		builder.append(", address=");
		builder.append(address);
		builder.append(", phone=");
		builder.append(phone);
		builder.append("]");
		return builder.toString();
	}
    
}
