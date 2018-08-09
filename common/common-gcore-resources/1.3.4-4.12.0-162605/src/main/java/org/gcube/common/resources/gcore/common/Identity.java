package org.gcube.common.resources.gcore.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"subject", "caSubject"})
@XmlRootElement(name = "RunningInstanceIdentity")
public class Identity {
	
	@XmlElement(name="subject")
	public String subject;
	
	@XmlElement(name="CASubject")
	public String caSubject;

	public void subjects(String subject, String caSubject) {
		this.subject=subject;
		this.caSubject=caSubject;
	}
	
	public String subject() {
		return this.subject;
	}
	
	public String caSubject() {
		return this.caSubject;
	}
	
	@Override
	public String toString() {
		return "Identity [subject=" + subject + ", caSubject=" + caSubject
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((caSubject == null) ? 0 : caSubject.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Identity other = (Identity) obj;
		if (caSubject == null) {
			if (other.caSubject != null)
				return false;
		} else if (!caSubject.equals(other.caSubject))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}
	
	
}
