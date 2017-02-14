package org.gcube.portlets.admin.accountingmanager.server.is;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */

@XmlRootElement(name = "enabletab")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnableTabJAXB {
	@XmlElement
	private String name;

	@XmlElementWrapper(name = "roles", required = false)
	@XmlElement(name = "role", required = false)
	private List<String> roles;

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "EnableTabJAXB [name=" + name + ", roles=" + roles + "]";
	}

}
