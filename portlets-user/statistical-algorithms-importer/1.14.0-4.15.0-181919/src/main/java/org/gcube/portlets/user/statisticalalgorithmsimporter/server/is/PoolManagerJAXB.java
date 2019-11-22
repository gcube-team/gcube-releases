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

@XmlRootElement(name = "poolmanager")
@XmlAccessorType(XmlAccessType.FIELD)
public class PoolManagerJAXB {

	@XmlElement(name = "enable")
	private boolean enable;

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "PoolManagerJAXB [enable=" + enable + "]";
	}
	
}
