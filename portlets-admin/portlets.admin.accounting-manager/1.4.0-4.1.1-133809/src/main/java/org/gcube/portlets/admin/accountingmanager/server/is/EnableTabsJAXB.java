package org.gcube.portlets.admin.accountingmanager.server.is;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */

@XmlRootElement(name = "enabletabs")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnableTabsJAXB {

	@XmlElement(name = "enabletab")
	private List<EnableTabJAXB> enableTabs = null;

	public List<EnableTabJAXB> getEnableTabs() {
		return enableTabs;
	}

	public void setEnableTabs(List<EnableTabJAXB> enableTabs) {
		this.enableTabs = enableTabs;
	}

	@Override
	public String toString() {
		return "EnableTabsJAXB [enableTabs=" + enableTabs + "]";
	}

	

}
