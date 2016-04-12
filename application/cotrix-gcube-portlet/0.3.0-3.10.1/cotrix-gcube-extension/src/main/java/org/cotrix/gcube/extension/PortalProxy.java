package org.cotrix.gcube.extension;

import org.cotrix.gcube.stubs.PortalUser;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 *
 */
public interface PortalProxy {

	public PortalUser getPortalUser();

	public void publish(String news);

}