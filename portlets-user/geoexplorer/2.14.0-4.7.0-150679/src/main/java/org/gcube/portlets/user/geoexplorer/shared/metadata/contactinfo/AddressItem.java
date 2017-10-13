package org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo;

import java.io.Serializable;
import java.util.Collection;

public class AddressItem implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -63353671502456238L;

	/**
     * State, province of the location.
     */
    private String administrativeArea;

    /**
     * The city of the location
     */
    private String city;

   /**
     * Country of the physical address.
     */
    private String country;

    /**
     * ZIP or other postal code.
     */
    private String postalCode;

    /**
     * Address line for the location (as described in ISO 11180, Annex A).
     */
    private Collection<String> deliveryPoints;

    /**
     * Address of the electronic mailbox of the responsible organization or individual.
     */
    private Collection<String> electronicMailAddresses;

    /**
     * Constructs an initially empty address.
     */
    public AddressItem() {
    }

	public AddressItem(String administrativeArea, String city, String country,
			String postalCode, Collection<String> deliveryPoints,
			Collection<String> electronicMailAddresses) {
		this.administrativeArea = administrativeArea;
		this.city = city;
		this.country = country;
		this.postalCode = postalCode;
		this.deliveryPoints = deliveryPoints;
		this.electronicMailAddresses = electronicMailAddresses;
	}

	public String getAdministrativeArea() {
		return administrativeArea;
	}

	public void setAdministrativeArea(String administrativeArea) {
		this.administrativeArea = administrativeArea;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Collection<String> getDeliveryPoints() {
		return deliveryPoints;
	}

	public void setDeliveryPoints(Collection<String> deliveryPoints) {
		this.deliveryPoints = deliveryPoints;
	}

	public Collection<String> getElectronicMailAddresses() {
		return electronicMailAddresses;
	}

	public void setElectronicMailAddresses(
			Collection<String> electronicMailAddresses) {
		this.electronicMailAddresses = electronicMailAddresses;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Address [administrativeArea=");
		builder.append(administrativeArea);
		builder.append(", city=");
		builder.append(city);
		builder.append(", country=");
		builder.append(country);
		builder.append(", postalCode=");
		builder.append(postalCode);
		builder.append(", deliveryPoints=");
		builder.append(deliveryPoints);
		builder.append(", electronicMailAddresses=");
		builder.append(electronicMailAddresses);
		builder.append("]");
		return builder.toString();
	}

    
}
