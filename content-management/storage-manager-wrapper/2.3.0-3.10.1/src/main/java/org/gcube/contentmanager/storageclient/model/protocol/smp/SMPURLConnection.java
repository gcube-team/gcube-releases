package org.gcube.contentmanager.storageclient.model.protocol.smp;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 
* This is invoked by the platform with a URL of the right protocol.
* @author Fabio Simeoni (University of Strathclyde), @author Roberto Cirillo (ISTI-CNR)
* @deprecated this class will be deleted and will be changed with the factory class invocation: SMPURLConnectionFactory
*
*/
@Deprecated
public class SMPURLConnection extends URLConnection {

	SMPConnection smp;
	Logger logger= LoggerFactory.getLogger(SMPURLConnection.class);
	/**
	 * Constructs a new instance for a given <code>sm</code> URL.
	 * @param url the URL.
	 */
	@Deprecated
	public SMPURLConnection(URL url) {
		super(url);
		this.smp=SMPURLConnectionFactory.getSmp(url);
	}

	/**{@inheritDoc}*/
	@Override 
	public synchronized void connect() throws IOException {
			connected=true;
	}
	
	/**{@inheritDoc}*/
	@Override 
	public synchronized InputStream getInputStream() throws IOException {
		return smp.getInputStream();
	}
}
