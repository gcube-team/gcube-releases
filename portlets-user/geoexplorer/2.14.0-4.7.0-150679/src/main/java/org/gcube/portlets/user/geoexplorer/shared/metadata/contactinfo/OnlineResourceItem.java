package org.gcube.portlets.user.geoexplorer.shared.metadata.contactinfo;

import java.io.Serializable;

public class OnlineResourceItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8798044840774449424L;

	
	/**
     * Name of the online resources.
     */
    private String name;

    /**
     * Detailed text description of what the online resource is/does.
     */
    private String description;

    /**
     * Location (address) for on-line access using a Uniform Resource Locator address or
     * similar addressing scheme such as http://www.statkart.no/isotc211.
     */
    private String linkage;

    /**
     * The connection protocol to be used.
     */
    private String protocol;

    /**
     * Creates an initially empty on line resource.
     */
    public OnlineResourceItem() {
    }

	public OnlineResourceItem(String name, String description, String linkage,
			String protocol) {
		this.name = name;
		this.description = description;
		this.linkage = linkage;
		this.protocol = protocol;
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

	public String getLinkage() {
		return linkage;
	}

	public void setLinkage(String linkage) {
		this.linkage = linkage;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OnlineResourceItem [name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", linkage=");
		builder.append(linkage);
		builder.append(", protocol=");
		builder.append(protocol);
		builder.append("]");
		return builder.toString();
	}

}
