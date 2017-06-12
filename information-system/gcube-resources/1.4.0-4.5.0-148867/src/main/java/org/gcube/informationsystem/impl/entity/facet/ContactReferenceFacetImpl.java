/**
 * 
 */
package org.gcube.informationsystem.impl.entity.facet;

import java.net.URL;

import org.gcube.informationsystem.impl.entity.FacetImpl;
import org.gcube.informationsystem.model.entity.facet.ContactReferenceFacet;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ContactReferenceFacet.NAME)
public class ContactReferenceFacetImpl extends FacetImpl implements ContactReferenceFacet {
	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = 286704827655240356L;

	protected URL website;
	
	protected String address;
	
	protected String phoneNumber;
	
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
