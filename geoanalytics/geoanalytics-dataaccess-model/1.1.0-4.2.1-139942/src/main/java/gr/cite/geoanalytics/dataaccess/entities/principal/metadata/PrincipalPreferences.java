package gr.cite.geoanalytics.dataaccess.entities.principal.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="contactInfo")
@XmlAccessorType(value = XmlAccessType.PUBLIC_MEMBER)
public class PrincipalPreferences {
	
	private String language = null;

	@XmlElement(name="language", required=false)
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	
	
}
