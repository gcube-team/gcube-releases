package org.gcube.resources.federation.fhnmanager.cl.fwsimpl;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.gcube.resources.federation.fhnmanager.api.exception.FHNManagerException;

@Provider
public class RESTClientFilter implements ClientResponseFilter {

	@Override
	public void filter(ClientRequestContext arg0, ClientResponseContext response) throws IOException {

		if (response.getStatus() == Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
			String output = convertStreamToString(response.getEntityStream());

			String[] tokens = output.split("\n");
					
				Class<?> clazz;
				try {
					clazz = Class.forName(tokens[0]);
					Constructor<?> constructor = clazz.getConstructor(String.class);
					FHNManagerException instance = (FHNManagerException)  constructor.newInstance(tokens[1]);
					throw instance;
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					new ProcessingException("Unable to parse exception response: " + output);
				}
			
		}

	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
