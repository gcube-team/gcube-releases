package org.gcube.portlets.user.statisticalalgorithmsimporter.server.is;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */

@XmlRootElement(name = "availableprojectconfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class AvailableProjectConfigJAXB {
	@XmlElement
	private String language;

	@XmlElement
	private String support;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	@Override
	public String toString() {
		return "AvailableProjectConfigJAXB [language=" + language + ", support=" + support + "]";
	}

	

}
