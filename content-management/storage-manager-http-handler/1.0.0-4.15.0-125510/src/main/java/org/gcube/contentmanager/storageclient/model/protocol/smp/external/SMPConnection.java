package org.gcube.contentmanager.storageclient.model.protocol.smp.external;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.gcube.contentmanager.storageclient.model.protocol.smp.external.SMPConnection;

/**
 * An extension of URLConnection used for smp url
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public abstract class SMPConnection extends URLConnection {

	public SMPConnection(URL url) {
		super(url);
	}

	public abstract SMPConnection init(URL url);
	
	/**{@inheritDoc}*/
	@Override
	public synchronized void connect() throws IOException {
		connected=true;
	}
	
	@Override
	public abstract InputStream getInputStream() throws IOException;
	
	
	protected abstract InputStream storageClient(String url) throws Exception;

}