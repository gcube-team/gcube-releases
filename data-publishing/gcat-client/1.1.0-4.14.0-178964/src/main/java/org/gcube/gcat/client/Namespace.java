package org.gcube.gcat.client;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Namespace extends GCatClient implements org.gcube.gcat.api.interfaces.Namespace {
	
	public Namespace() throws MalformedURLException {
		super(NAMESPACES);
	}
	
	public Namespace(URL enforcedServiceURL) throws MalformedURLException {
		super(enforcedServiceURL, NAMESPACES);
	}
	
	@Override
	public String list() {
		return super.list(null);
	}
	
}
