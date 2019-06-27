/**
 * 
 */
package org.gcube.resourcemanagement.model.impl.entity.facet;

import org.gcube.informationsystem.model.impl.entity.FacetImpl;
import org.gcube.resourcemanagement.model.reference.entity.facet.ContactFacet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@JsonTypeName(value=ContactFacet.NAME)
public class ContactFacetImpl extends FacetImpl implements ContactFacet {
	
	/**
	 * Generated Serial version UID
	 */
	private static final long serialVersionUID = -4036703255922676717L;
	
	protected String title;
	protected String name;
	protected String middleName;
	protected String surname;
	protected String eMail;
	
	
	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	
	@Override
	public String getMiddleName() {
		return this.middleName;
	}

	@Override
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	
	@Override
	public String getSurname() {
		return this.surname;
	}

	@Override
	public void setSurname(String surname) {
		this.surname = surname;
	}

	
	@JsonProperty(EMAIL_PROPERTY)
	@Override
	public String getEMail() {
		return this.eMail;
	}

	@Override
	public void setEMail(String eMail) {
		this.eMail = eMail;
	}


}
