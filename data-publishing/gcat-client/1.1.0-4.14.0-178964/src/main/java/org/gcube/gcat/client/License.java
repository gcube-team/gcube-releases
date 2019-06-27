package org.gcube.gcat.client;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class License extends GCatClient implements org.gcube.gcat.api.interfaces.License {
	public License() throws MalformedURLException {
		super(LICENSES);
	}
	
	public License(URL enforcedServiceURL) throws MalformedURLException {
		super(enforcedServiceURL, LICENSES);
	}

	@Override
	public String list() {
		return super.list(null);
	}
	
}
