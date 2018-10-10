package org.gcube.contentmanager.storageclient.model.protocol.smp;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.Configuration;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 
* This is invoked by the platform with a URL of the right protocol.
* @author Fabio Simeoni (University of Strathclyde), @author Roberto Cirillo (ISTI-CNR)
*
*/
public class SMPURLConnectionOld extends SMPConnection{

	
	Logger logger= LoggerFactory.getLogger(SMPURLConnectionOld.class);
	/**
	 * Constructs a new instance for a given <code>sm</code> URL.
	 * @param url the URL.
	 */
	public SMPURLConnectionOld(URL url) {
		super(url);
	}
	
	public SMPConnection init(URL url){
		return new SMPURLConnectionOld(url);
	}
	
	/**{@inheritDoc}*/
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
	

	protected InputStream storageClient(String url) throws Exception {
		logger.info("url :" + url);
		String [] urlParam=url.split("\\?");
		logger.info("String encrypted "+urlParam[1]);
		String param=new StringDecrypter("DES", retrieveEncryptionPhrase()).decrypt(urlParam[1]);
		logger.info("String decrypted: "+param);
		String [] getParam=param.split("\\&");
		String serviceClass=null;
		String serviceName=null;
		String owner=null;
		String accessType=null;
		String memoryType=null;
		String scopeType=null;
		AccessType type = null;
		MemoryType mType = null;
		String server= null;
		String [] par1;
		for(String par : getParam){
			if(par.contains("ServiceClass")){
				par1=par.split("=");
				serviceClass=par1[1];
			}else if(par.contains("ServiceName")){
				par1=par.split("=");
				serviceName=par1[1];
			}else if(par.contains("owner")){
				par1=par.split("=");
				owner=par1[1];
			}else if(par.contains("scope")){
				par1=par.split("=");
				scopeType=par1[1];
			}else if(par.contains("server")){
				par1=par.split("=");
				server=par1[1];
			}else if(par.contains("AccessType")){
				par1=par.split("=");
				accessType=par1[1];			
				if(accessType.equalsIgnoreCase("public")){
					type=AccessType.PUBLIC;
				}else if(accessType.equalsIgnoreCase("shared")){
					type=AccessType.SHARED;
				}
			}else if(par.contains("MemoryType")){
				par1=par.split("=");
				memoryType=par1[1];			
				if(memoryType.equalsIgnoreCase("VOLATILE")){
					mType=MemoryType.VOLATILE;
				}else{
					mType=MemoryType.PERSISTENT;
				}	
			}
		}
	// throw an exception if one or more uri parameters are invalid	
		if((serviceName==null) || (serviceClass==null) || (owner == null) || (scopeType==null) || (type == null)){
			logger.error("Bad Parameter in URI");
			if (type == null){
				logger.error("URI generated from a private file");
				throw new MalformedURLException("The uri is generated from a private file. It cannot be accessed");
			}
			if (serviceName==null) throw new MalformedURLException("The uri generated has an invalid serviceName");
			if (serviceClass==null) throw new MalformedURLException("The uri generated has an invalid serviceClass");
			if (owner==null) throw new MalformedURLException("The uri generated has an invalid owner");
			if (scopeType==null) throw new MalformedURLException("The uri is generated has an invalid scopeType.");
		}
		String location=extractLocation(urlParam[0]);
		logger.info("Parameters from URI "+serviceClass+" "+serviceName+" "+owner+" "+type+" "+mType +" location: "+urlParam[0]+" scope: "+scopeType);
		IClient client=null;
		String currentScope=ScopeProvider.instance.get();
		logger.info("current scope used: "+currentScope+". scope found on url is: "+ scopeType);
//		ScopeProvider.instance.set(scopeType);
		if(mType != null){
			if(server!=null)
				client=new StorageClient(serviceClass, serviceName, owner, type,  mType, server).getClient();
			else
				client=new StorageClient(serviceClass, serviceName, owner, type,  mType).getClient();
		}else{
			if (server != null)
				client=new StorageClient(serviceClass, serviceName, owner, server, type ).getClient();
			else
				client=new StorageClient(serviceClass, serviceName, owner, type).getClient();
		}
		InputStream is=null;
		is=client.get().RFileAsInputStream(location);
//		ScopeProvider.instance.set(currentScope);
		return is;
	}

	private String extractLocation(String url) {
		String [] loc=url.split("//");
		logger.info("url extracted: "+loc[1]);
		return loc[1];
	}

}
