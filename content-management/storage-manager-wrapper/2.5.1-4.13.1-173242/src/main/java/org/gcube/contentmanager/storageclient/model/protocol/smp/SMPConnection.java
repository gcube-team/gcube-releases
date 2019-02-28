package org.gcube.contentmanager.storageclient.model.protocol.smp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanager.storageclient.wrapper.ISClientConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of URLConnection used for smp url
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public abstract class SMPConnection extends URLConnection {
	
	Logger logger= LoggerFactory.getLogger(SMPURLConnection.class);

	public SMPConnection(URL url) {
		super(url);
	}

	public abstract URLConnection init(URL url);

	/**{@inheritDoc}*/
	@Override
	public synchronized void connect() throws IOException {
		connected=true;
	}
	
	@Override
	public abstract InputStream getInputStream() throws IOException;
	
	
	protected abstract InputStream storageClient(String url) throws Exception;
	
	/**
	 * This method has been moved in Configuration class
	 * @param rootScope
	 * @return
	 * @throws Exception
	 * @see org.gcube.contentmanager.storageclient.wrapper.Configuration
	 * 
	 */
	@Deprecated
	protected String retrieveEncryptionPhrase() throws Exception {
		String currentScope=ScopeProvider.instance.get();
		logger.debug("retrieve encryption prhase on scope: "+currentScope);
		String encryptedKey=null;
		ISClientConnector isclient=new ISClientConnector();
		encryptedKey=isclient.retrievePropertyValue("PassPhrase", currentScope);
		String decryptString=org.gcube.common.encryption.StringEncrypter.getEncrypter().decrypt(encryptedKey);
		return decryptString;
	}
	

}