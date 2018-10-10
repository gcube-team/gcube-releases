package org.gcube.spatial.data.geonetwork.test;

import java.net.MalformedURLException;

import org.gcube.spatial.data.geonetwork.extension.HttpUtilsExtensions;

import it.geosolutions.geonetwork.exception.GNServerException;

public class HttpUtilsExtensionTest {

	public static void main(String[] args) throws MalformedURLException, GNServerException {
		System.out.println(new HttpUtilsExtensions("admin", "admin").
				getJSON("http://node3-d-d4s.d4science.org/geonetwork/srv/api/0.1/users"));
		
	}

}
