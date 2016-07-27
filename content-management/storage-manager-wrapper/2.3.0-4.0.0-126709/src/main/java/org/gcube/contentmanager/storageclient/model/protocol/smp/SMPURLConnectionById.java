package org.gcube.contentmanager.storageclient.model.protocol.smp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter;
import org.gcube.contentmanagement.blobstorage.service.operation.GetUrl;
import org.gcube.contentmanager.storageclient.protocol.utils.Utils;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.ISClientConnector;
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
	
	
// without scope test
		protected InputStream storageClient(String url) throws Exception {
			logger.info("url :" + url);
			String [] urlParam=url.split(GetUrl.URL_SEPARATOR);
			String protocol=urlParam[0];
			protocol=protocol.substring(0, protocol.length()-1);
			logger.debug("protocol is "+protocol);
			String infrastructure=Utils.extractInfrastructureNewVersion(urlParam[2]);///extractInfrastructure(urlParam[0]);
			String rootScope="/"+infrastructure;
			logger.debug("rootScope validating "+rootScope);
			String currentScope=ScopeProvider.instance.get();
			logger.info("current scope found(ScopeProvider): "+currentScope+". Setting scope before  call StorageClient object: "+ rootScope);
			if(Utils.validationScope2(rootScope))
				ScopeProvider.instance.set(rootScope);
			else{
				String infra=null;
				if (currentScope == null){
					infra=Utils.checkVarEnv(Utils.INFRASTRUCTURE_ENV_VARIABLE_NAME);
					if (infra!=null){
						rootScope="/"+infra;
					}else{
						throw new IllegalStateException("a valid scope is needed. Scope found: "+rootScope);
					}
				}else{
					rootScope=currentScope;
				}
			}
			String encrypted=retrieveStringEncrypted(urlParam);
			logger.debug("String encrypted "+encrypted);
			String phrase=retrieveEncryptionPhrase(rootScope);
			String location=null;
			if(Base64.isBase64(encrypted) && (protocol.equalsIgnoreCase("http"))){
				byte[] valueDecoded= Base64.decodeBase64(encrypted);
				String encryptedID = new String(valueDecoded);
				location=new StringDecrypter("DES", phrase).decrypt(encryptedID);
			}else{
				location=new StringDecrypter("DES", phrase).decrypt(encrypted);
			}
			
//			String location=new StringDecrypter("DES", phrase).decrypt(encrypted);
			IClient client=null;
			client=new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED).getClient();
			InputStream is=null;
			is=client.get().RFileAsInputStream(location);
			ScopeProvider.instance.set(currentScope);
			return is;
		}
		

	private String  retrieveStringEncrypted(String[] urlParam) {
		String encrypted=urlParam[3];
		int i=4;
		while(i < urlParam.length){
			encrypted=encrypted+GetUrl.URL_SEPARATOR+urlParam[i];
			i++;
		}
		return encrypted;
	}

	/**
	 * This method has been moved in StorageClient class
	 * @param rootScope
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	private String retrieveEncryptionPhrase(String rootScope) throws Exception {
		String currentScope=ScopeProvider.instance.get();
		String scope=rootScope;
		ScopeProvider.instance.set(scope);
		logger.debug("set scope: "+scope);
		String encryptedKey=null;
		ISClientConnector isclient=new ISClientConnector();
		encryptedKey=isclient.retrievePropertyValue("PassPhrase", scope);
		String decryptString=org.gcube.common.encryption.StringEncrypter.getEncrypter().decrypt(encryptedKey);
		ScopeProvider.instance.set(currentScope);
		return decryptString;
	}
	
	

}
