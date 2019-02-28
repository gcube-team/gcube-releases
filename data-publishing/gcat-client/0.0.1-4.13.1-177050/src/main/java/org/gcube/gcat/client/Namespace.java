package org.gcube.gcat.client;

import java.net.MalformedURLException;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class Namespace extends GCatClient implements org.gcube.gcat.api.interfaces.Namespace {
	
	public Namespace() throws MalformedURLException {
		super(NAMESPACES);
	}

	@Override
	public String list() {
		return super.list(null);
	}
	
}
