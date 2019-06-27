package org.gcube.data.access.ckanconnector;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.gcube.common.authorization.client.Constants;
import org.gcube.common.scope.api.ScopeProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

public class TestCall extends JerseyTest{

	@Override
	protected Application configure() {
		return new ResourceConfig(ConnectorManager.class);
	}

	@Test
	public void replace(){
		String query="gcube-token=d423aed7-e9e2-424a-b9e7-2bbbd151d9c4-98187548&listOfVres=nextnextadmin";
		String newQueryString = query.replaceFirst("&?gcube-token=[^&$]*&?", "");
		System.out.println(newQueryString);
		newQueryString = newQueryString.replaceFirst("&?listOfVres=[^&$]*&?", "");
		System.out.println(newQueryString);
	}
	
	@Test
	public void connect() {
		final Response ret = target("connect").request().get(Response.class);
		System.out.println("return is "+ret);
	}

	@Test
	public void createOrganization() throws Exception{
		String callUrl = "https://ckan-d-d4s.d4science.org/ckan-connector/organization/CreationTest?gcube-token=34c34146-ab38-42d5-9332-f325e8b2b930";
		URL url = new URL(callUrl);
		HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
		connection.setRequestMethod("PUT");
		
		System.out.println(connection.getResponseCode());
	}

	

}
