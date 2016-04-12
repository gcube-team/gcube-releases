package gr.uoa.di.madgik.urlresolutionlibrary.url.urlconnections;

import gr.uoa.di.madgik.urlresolutionlibrary.ResolveFactory;
import gr.uoa.di.madgik.urlresolutionlibrary.exceptions.URLResolverException;
import gr.uoa.di.madgik.urlresolutionlibrary.streamable.Streamable;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class LocatorURLConnection extends URLConnection {
	private Streamable locator;
	private String urlStr;
	private URL url;

	public LocatorURLConnection(URL url) {
		super(url);
		this.url = url;
	}

	private void parseURL() {
		this.urlStr = url.toString();
	}

	/**
	 * Registers the protocol with the JVM.
	 */
	public static void activateProtocol() {
		String pkgs_name = "java.protocol.handler.pkgs";
		String pkgs = System.getProperty(pkgs_name);
		String pkg = "gr.uoa.di.madgik.urlresolutionlibrary.url.handlers";
		if (pkgs == null)
			pkgs = pkg;
		else if (!pkgs.contains(pkg))
			pkgs = pkgs + "|" + pkg;

		System.out.println("pkgs : " + pkgs);
		System.setProperty(pkgs_name, pkgs);
	}

	@Override
	public void connect() throws IOException {
		try {
			parseURL();
			locator = ResolveFactory.getStreamableLocator(urlStr);
		} catch (URLResolverException e) {
			throw new IOException("Error getting streamable locator", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized InputStream getInputStream() throws IOException {
		InputStream is = null;

		if (!connected)
			this.connect();

		try {
			is = new BufferedInputStream(locator.getInputStream());
		} catch (Exception e) {
			throw new IOException("Couldn't get InputStream of : ", e);
		}

		return is;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		if (locator != null)
			locator.close();
	}

}
