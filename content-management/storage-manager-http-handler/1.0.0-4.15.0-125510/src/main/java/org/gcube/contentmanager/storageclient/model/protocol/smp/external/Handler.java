package org.gcube.contentmanager.storageclient.model.protocol.smp.external;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnectionFactory;

/**
 * A handler for the <code>sm</code> protocol.
 * @author Fabio Simeoni (University of Strathclyde)
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class Handler extends URLStreamHandler {

	/**{@inheritDoc}*/
	@Override 
	protected URLConnection openConnection(URL u) throws IOException {
		return SMPURLConnectionFactory.getSmp(u);
	}
	
	/**
	 * Registers the protocol with the JVM.
	 */
	public static void activateProtocol() {
		
		String pkgs_name="java.protocol.handler.pkgs";
		String pkgs = System.getProperty(pkgs_name);
		String pkg = "org.gcube.contentmanager.storageclient.model.protocol";
		if (pkgs==null)
			pkgs = pkg ;
		else if (!pkgs.contains(pkg))
			pkgs = pkgs+"|"+pkg;
		System.setProperty(pkgs_name, pkgs);	
	}

	
	
}