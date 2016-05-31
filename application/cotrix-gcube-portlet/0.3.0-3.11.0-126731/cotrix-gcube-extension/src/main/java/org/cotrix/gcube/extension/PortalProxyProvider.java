package org.cotrix.gcube.extension;

import org.cotrix.gcube.stubs.SessionToken;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public interface PortalProxyProvider {

	public PortalProxy getPortalProxy(SessionToken sessionToken);

}