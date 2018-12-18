package org.gcube.contentmanagement.blobstorage.service.operation;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.gcube.contentmanagement.blobstorage.resource.MemoryType;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter;
import org.gcube.contentmanagement.blobstorage.service.directoryOperation.Encrypter.EncryptionException;
import org.gcube.contentmanagement.blobstorage.transport.TransportManager;
import org.gcube.contentmanagement.blobstorage.transport.TransportManagerFactory;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants;

public class GetHttpsUrl extends Operation {

//	private OutputStream os;
	TransportManager tm;
	
	@Deprecated
	public static final String URL_SEPARATOR=Costants.URL_SEPARATOR;
	@Deprecated
	public static final String VOLATILE_URL_IDENTIFICATOR = Costants.VOLATILE_URL_IDENTIFICATOR;

	
	public GetHttpsUrl(String[] server, String user, String pwd, String bucket, Monitor monitor, boolean isChunk, String backendType, String[] dbs) {
		super(server, user, pwd, bucket, monitor, isChunk, backendType, dbs);
	}
	
	@Override
	public String initOperation(MyFile file, String remotePath, String author,
			String[] server, String rootArea, boolean replaceOption) {
		return getRemoteIdentifier(remotePath, rootArea);
	}

	@Override
	public String initOperation(MyFile resource, String RemotePath,
			String author, String[] server, String rootArea) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object doIt(MyFile myFile) throws RemoteBackendException {
		String resolverHost=myFile.getResolverHOst();
		String urlBase="smp://"+resolverHost+Costants.URL_SEPARATOR;
		String urlParam="";
		try {
			String id=getId(myFile.getAbsoluteRemotePath(), myFile.isForceCreation(), myFile.getGcubeMemoryType(), myFile.getWriteConcern(), myFile.getReadPreference());
			String phrase=myFile.getPassPhrase();
//			urlParam =new StringEncrypter("DES", phrase).encrypt(id);
			urlParam = new Encrypter("DES", phrase).encrypt(id);
//			String urlEncoded=URLEncoder.encode(urlParam, "UTF-8");
		} catch (EncryptionException e) {
			throw new RemoteBackendException(" Error in getUrl operation problem to encrypt the string", e.getCause());
		}
		logger.info("URL generated: "+urlBase+urlParam);
		String smpUrl=urlBase+urlParam;
		logger.info("URL generated: "+smpUrl);
		smpUrl=smpUrl.replace("smp://", "https://");
		URL httpsUrl=null;
		try {
			httpsUrl=translate(new URL(smpUrl));
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("URL translated: "+httpsUrl);
		if(myFile.getGcubeMemoryType().equals(MemoryType.VOLATILE)){
			return httpsUrl.toString()+Costants.VOLATILE_URL_IDENTIFICATOR;
		}
		return httpsUrl.toString();
	}
	
	private String getId(String path, boolean forceCreation, MemoryType memoryType, String writeConcern, String readPreference){
		String id=null;
		if(tm ==null){
			TransportManagerFactory tmf= new TransportManagerFactory(server, user, password);
			tm=tmf.getTransport(backendType, memoryType, dbNames, writeConcern, readPreference);
		}
		try {
			id = tm.getId(bucket, forceCreation);
		} catch (Exception e) {
			tm.close();
			throw new RemoteBackendException(" Error in GetUrl operation. Problem to discover remote file:"+bucket+" "+ e.getMessage(), e.getCause());			}
		if (logger.isDebugEnabled()) {
			logger.debug(" PATH " + bucket);
		}
		return id;
	}
	
	private URL translate(URL url) throws IOException {
		logger.debug("translating: "+url);
		String urlString=url.toString();
		String baseUrl="https://"+url.getHost()+"/";
		logger.debug("base Url extracted is: "+baseUrl);
//		int index=urlString.lastIndexOf(".org/");
		String params = urlString.substring(baseUrl.length());
		logger.debug("get params: "+baseUrl+" "+params);
		//encode params
		params=Base64.encodeBase64URLSafeString(params.getBytes("UTF-8"));
//		URLEncoder.encode(params, "UTF-8");
		// merge string
		urlString=baseUrl+params;
		logger.info("uri translated in https url: "+urlString);
		return new URL(urlString);
	}
}
