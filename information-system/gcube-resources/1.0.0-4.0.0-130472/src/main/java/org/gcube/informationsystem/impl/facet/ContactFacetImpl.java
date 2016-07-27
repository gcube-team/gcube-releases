/**
 * 
 */
package org.gcube.informationsystem.impl.facet;

import java.net.URL;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.facet.ContactFacet;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 */
public class ContactFacetImpl extends FacetImpl implements ContactFacet {

	protected String name;
	
	protected String eMail;
	
	protected URL website;
	
	protected String address;
	
	protected String phoneNumber;
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@JsonProperty("eMail")
	@Override
	public String getEMail() {
		return this.eMail;
	}

	@Override
	public void setEMail(String eMail) {
		this.eMail = eMail;
	}

	@Override
	public URL getWebsite() {
		return this.website;
	}

	@Override
	public void setWebsite(URL website) {
		this.website = website;
	}

	@Override
	public String getAddress() {
		return this.address;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Override
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	@Override
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
