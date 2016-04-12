package gr.uoa.di.madgik.urlresolutionlibrary.url.handlers.URLResolverHandler;

import gr.uoa.di.madgik.urlresolutionlibrary.url.urlconnections.LocatorURLConnection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class URLResorverHandler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new LocatorURLConnection(u);
	}
}
