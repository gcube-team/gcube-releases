package org.gcube.contentmanager.storageclient.model.protocol.smp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.service.operation.GetHttpUrl;
import org.gcube.contentmanagement.blobstorage.service.operation.GetUrl;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is invoked by the platform with a URL of the smp protocol with new format (>= 2.2.0 version) .
* @author Roberto Cirillo (ISTI-CNR)
 *
 * Example: smp://data.gcube.org?uhdnfounhcfnshfnrhbvyaeegytf6dfawiuawgcyg
 */
public class SMPURLConnectionById extends SMPConnection {

	private Logger logger= LoggerFactory.getLogger(SMPURLConnectionOld.class);
	private String serviceClass="Storage-manager";
	private String serviceName="resolver-uri";
	private String owner="storage-manager";
	
	/**
	 * Constructs a new instance for a given <code>sm</code> URL.
	 * @param url the URL.
	 */
	public SMPURLConnectionById(URL url) {
		super(url);
	}
	
	public SMPConnection init(URL url){
		return new SMPURLConnectionById(url);
	}
	
	/**{@inheritDoc}
	 * internal handler implementation
	 * */
	@Override 
	public synchronized InputStream getInputStream() throws IOException {
		if (!connected) 
			this.connect(); 
		try {
		    return storageClient(this.url.toString());			
		}
		catch(Exception e) {
			IOException ioe = new IOException();
			ioe.initCause(e);
			throw ioe;
		}			
	}
	
	
	/**
	 * Get a StorageManager instance from url 
	 */
	
	protected InputStream storageClient(String url) throws Exception {
		logger.info("url :" + url);
		String [] urlParam=url.split(Costants.URL_SEPARATOR);
		String protocol=urlParam[0];
		protocol=protocol.substring(0, protocol.length()-1);
		logger.debug("protocol is "+protocol);
		if(ScopeProvider.instance.get() == null){
			throw new RuntimeException("Scope not set");
		}
		String encrypted=retrieveStringEncrypted(urlParam);
		MemoryType memory=null;
		if(encrypted.contains(Costants.VOLATILE_URL_IDENTIFICATOR)){
			memory=MemoryType.VOLATILE;
			encrypted=encrypted.replace(Costants.VOLATILE_URL_IDENTIFICATOR, "");
		}
		logger.debug("String encrypted "+encrypted);
		String phrase=retrieveEncryptionPhrase();
		String location=null;
		if(Base64.isBase64(encrypted) && (protocol.equalsIgnoreCase("http"))){
			byte[] valueDecoded= Base64.decodeBase64(encrypted);
			String encryptedID = new String(valueDecoded);
			location=new StringDecrypter("DES", phrase).decrypt(encryptedID);
		}else{
			location=new StringDecrypter("DES", phrase).decrypt(encrypted);
		}
		IClient client=null;
		if(memory!=null)
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, memory).getClient();
		else
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED).getClient();
		InputStream is=null;
		is=client.get().RFileAsInputStream(location);
		return is;
	}

	private String  retrieveStringEncrypted(String[] urlParam) {
		String encrypted=urlParam[3];
		int i=4;
		while(i < urlParam.length){
			encrypted=encrypted+Costants.URL_SEPARATOR+urlParam[i];
			i++;
		}
		return encrypted;
	}


	

}
