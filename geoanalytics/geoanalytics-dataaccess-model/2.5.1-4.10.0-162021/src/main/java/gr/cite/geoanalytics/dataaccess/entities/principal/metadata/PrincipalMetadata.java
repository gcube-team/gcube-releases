package gr.cite.geoanalytics.dataaccess.entities.principal.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(name="principalMetadata")
@XmlAccessorType(value = XmlAccessType.PUBLIC_MEMBER)
@XmlSeeAlso({PrincipalContactInfo.class, PrincipalPreferences.class})
public class PrincipalMetadata {
	
	private boolean anonymous = false;
	private PrincipalContactInfo contactInfo = null;
	private PrincipalPreferences preferences = null;

	@XmlAttribute(required = false)
	public boolean isAnonymous() {
		return anonymous;
	}

	public void setAnonymous(boolean anonymous) {
		this.anonymous = anonymous;
	}
	
	@XmlElement(name="contactInfo", required = false)
	public PrincipalContactInfo getContactInfo() {
		return contactInfo;
	}
	
	public void setContactInfo(PrincipalContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	@XmlElement(name="preferences", required = false)
	public PrincipalPreferences getPreferences() {
		return preferences;
	}

	public void setPreferences(PrincipalPreferences preferences) {
		this.preferences = preferences;
	}
}
