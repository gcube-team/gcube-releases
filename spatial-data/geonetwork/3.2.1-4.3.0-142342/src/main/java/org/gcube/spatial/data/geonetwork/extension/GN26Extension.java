package org.gcube.spatial.data.geonetwork.extension;

import it.geosolutions.geonetwork.GN26Client;

public class GN26Extension extends GN26Client {

	public GN26Extension(String serviceURL) {		
		super(serviceURL);
	}

	public GN26Extension(String serviceURL, String username, String password) {
		super(serviceURL, username, password);
		super.connection=new HttpUtilsExtensions(username, password);
	}

}
