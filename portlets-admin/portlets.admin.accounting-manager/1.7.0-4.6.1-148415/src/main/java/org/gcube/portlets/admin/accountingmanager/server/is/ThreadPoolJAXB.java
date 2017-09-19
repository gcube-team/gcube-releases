package org.gcube.portlets.admin.accountingmanager.server.is;

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

@XmlRootElement(name = "threadpool")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreadPoolJAXB {
	@XmlElement
	private String timeout;

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	@Override
	public String toString() {
		return "ThreadPoolJAXB [timeout=" + timeout + "]";
	}

}
