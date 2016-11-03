package org.gcube.portlets.admin.vredeployer.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GHNSite implements Serializable {
	
	String location;
	String country;
	String domain;
	public GHNSite() {
		super();
	}
	public GHNSite(String location, String country, String domain) {
		super();
		this.location = location;
		this.country = country;
		this.domain = domain;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}	

}
