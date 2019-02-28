package org.gcube.portlets.user.geoexplorer.shared.metadata.distributioninfo;

import java.io.Serializable;

import org.gcube.portlets.user.geoexplorer.shared.metadata.ResponsiblePartyItem;

public class DistributorItem implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5522346887982289656L;
	/**
     * Party from whom the resource may be obtained. This list need not be exhaustive.
     *
     * @return Party from whom the resource may be obtained.
     */
	private ResponsiblePartyItem distributorContact;

	public DistributorItem() {
	}
	
	public DistributorItem(ResponsiblePartyItem distributorContact) {
		this.distributorContact = distributorContact;
	}

	public ResponsiblePartyItem getDistributorContact() {
		return distributorContact;
	}

	public void setDistributorContact(ResponsiblePartyItem distributorContact) {
		this.distributorContact = distributorContact;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DistributorItem [distributorContact=");
		builder.append(distributorContact);
		builder.append("]");
		return builder.toString();
	}
	
	

}
