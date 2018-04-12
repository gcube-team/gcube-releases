
package org.gcube.portlets.admin.vredeployer.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

public class GHNBean extends BaseModel {
	
	private static final long serialVersionUID = 1L; 
	/**
	 * default const. for serialization
	 */
	public GHNBean() {	}
	/**
	 * 
	 * @param id
	 * @param host
	 * @param memory
	 * @param uptime
	 * @param isSelected
	 */
	public GHNBean(String id, String host, String memory, String domain, 
			 boolean isSelected, String diskSpace, String location, String country, boolean isSecure) {
		set("id", id);
		set("host", host);
		set("memory", memory);
		set("domain", domain);
		
		set("isSelected", isSelected);
		set("diskspace", diskSpace);
		set("location", location);
		set("country", country);
		set("isSecure", isSecure);
	}
	
	/**
	 * 
	 * @return
	 */
	
	public String getDiskspace() {
		return (String) get("diskspace");
	}
	public String getLocation() {
		return (String) get("location");
	}
	public String getCountry() {
		return (String) get("country");
	}
	public String getSecurity() {
		return (String) get("security");
	}
	
	public String getId() {
		return (String) get("id");
	}
	
	public String getHost() {
		return (String) get("host");
	}
	
	public String getMemory() {
		return (String) get("memory");
	}

	public String getDomain() {
		return (String) get("domain");
	}

	public boolean isSelected() {
		return ((Boolean) get("isSelected")).booleanValue();
	}
	
	public boolean isSecure() {
		return ((Boolean) get("isSecure")).booleanValue();
	}
	
}
