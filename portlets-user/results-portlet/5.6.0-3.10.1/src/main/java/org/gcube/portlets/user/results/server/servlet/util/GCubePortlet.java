package org.gcube.portlets.user.results.server.servlet.util;

import java.io.Serializable;

/**
 * 
 * @author massi
 *
 */
public class GCubePortlet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8098787138241780876L;
	
	private String portletContext;
	private String portletName;
	private String portletStatus;

	
	public GCubePortlet() {
		super();
	}
	
	public GCubePortlet(String portletContext, String portletName, String portletStatus) {
		super();
		this.portletContext = portletContext;
		this.portletName = portletName;
		this.portletStatus = portletStatus;
	}

	public String getPortletContext() {
		return portletContext;
	}

	public void setPortletContext(String portletContext) {
		this.portletContext = portletContext;
	}

	public String getPortletName() {
		return portletName;
	}
	
	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}
	
	public String getPortletStatus() {
		return portletStatus;
	}
	
	public void setPortletStatus(String portletStatus) {
		this.portletStatus = portletStatus;
	}
	
	public String getPortletClass4Layout() {
		return portletContext + "#" + portletName;
	}	
}
