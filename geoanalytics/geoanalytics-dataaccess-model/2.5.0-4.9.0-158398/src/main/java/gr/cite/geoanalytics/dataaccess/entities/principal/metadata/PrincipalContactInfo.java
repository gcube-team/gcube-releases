package gr.cite.geoanalytics.dataaccess.entities.principal.metadata;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="contactInfo")
@XmlAccessorType(value = XmlAccessType.PUBLIC_MEMBER)
public class PrincipalContactInfo {
	
	private String firstName = null;
	private String lastName = null;
	private String eMail = null;
	private String tel = null;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String geteMail() {
		return eMail;
	}
	public void seteMail(String eMail) {
		
	   try {
	      InternetAddress emailAddr = new InternetAddress(eMail);
	      emailAddr.validate();
	   } catch (AddressException ex) {
		   throw new IllegalArgumentException("Invalid email address");
	   }
	   this.eMail = eMail;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	
}
