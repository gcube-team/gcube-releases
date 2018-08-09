package org.gcube.spatial.data.sdi.engine.impl.gn.extension;

import it.geosolutions.geonetwork.GN3Client;

public class GN3Extension extends GN3Client {

	public GN3Extension(String serviceURL) {
		super(serviceURL);
		// TODO Auto-generated constructor stub
	}

	public GN3Extension(String serviceURL, String username, String password) {
		super(serviceURL, username, password);
		super.connection=new HttpUtilsExtensions(username, password);
	}

}
