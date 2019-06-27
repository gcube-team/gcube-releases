/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ProxyRegistry.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.remote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Utility to simply access in a singleton manner to the
 * remote RPC proxy implemented by
 * {@link org.gcube.portlets.admin.resourcemanagement.server.ServiceProxyImpl}.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ProxyRegistry {
	private static ServiceProxyAsync instance = null;
	private static final String proxyURI = "ServiceProxy";

	/**
	 * Access to the singleton instance of proxy to contact the
	 * servlet implementing the server side logics of the application.
	 * @return the proxy used to invoke the remote servlet.
	 */
	public static synchronized ServiceProxyAsync getProxyInstance() {
		if (instance == null) {
			String moduleURL = GWT.getModuleBaseURL() + proxyURI;
			instance = (ServiceProxyAsync) GWT.create(ServiceProxy.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) instance;
			endpoint.setServiceEntryPoint(moduleURL);
		}
		return instance;
	}
}
