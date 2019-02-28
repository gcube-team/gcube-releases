package org.gcube.usecases.ws.thredds.engine.impl;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.tika.io.IOUtils;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.TransferResult;
import org.gcube.data.transfer.library.client.AuthorizationFilter;
import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.RemoteFileDescriptor;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.utils.ScopeUtils;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.TokenSetter;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.faults.UnableToLockException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreddsController {

	private String hostname;
	private String operatingPath;
	private String targetToken;

	public ThreddsController(String path,String targetToken) throws InternalException {		
		operatingPath=path;
		this.targetToken=targetToken;
		setTargetToken();
		hostname=getThreddsHost();
		if(hostname==null) throw new InternalException("Unable to find a thredds instance in target scope "+ScopeUtils.getCurrentScope());
		resetCallerToken();
	}

	private static final String truncate(String toTruncate) {
		return toTruncate==null?toTruncate:toTruncate.substring(0, toTruncate.length()/2)+"...";
	}

	private String callerToken=null;

	private void setTargetToken() {	
		if(callerToken==null) {
			callerToken=TokenSetter.getCurrentToken();
			log.trace("Storing caller token {}. Target Token is {}",truncate(callerToken),truncate(targetToken));
			TokenSetter.setToken(targetToken);
		}else {
			log.trace("Caller token {} already registered. Target Token is {}",truncate(callerToken),truncate(targetToken));
		}
	}

	private void resetCallerToken() {
		if(callerToken!=null) {
			log.trace("Resetting caller token {}. Target Token is {}, current is {} ",truncate(callerToken),truncate(targetToken),truncate(TokenSetter.getCurrentToken()));
			TokenSetter.setToken(callerToken);
			callerToken=null;
		}else log.trace("Caller token {} already reset [current token {}]. Target Token is {}",truncate(callerToken),truncate(TokenSetter.getCurrentToken()),truncate(targetToken));
	}

	public final ThreddsInfo getThreddsInfo() {
		setTargetToken();
		try{
			String infoPath="https://"+hostname+"/data-transfer-service/gcube/service/Capabilities/pluginInfo/REGISTER_CATALOG";;
			log.info("Loading thredds info from {} ",infoPath);
			WebTarget target=getWebClient().target(infoPath);
			return target.request(MediaType.APPLICATION_JSON).get(ThreddsInfo.class);
		}finally {
			resetCallerToken();
		}
	}


	public void lockFolder(String processId) throws UnableToLockException {
		setTargetToken();
		PrintWriter writer=null;
		File temp=null;
		try{
			log.info("Locking remote path {} to processId {} ",operatingPath,processId);
			DataTransferClient cl=getDTClient(hostname);

			Destination dest=new Destination();
			dest.setCreateSubfolders(true);
			dest.setOnExistingFileName(DestinationClashPolicy.FAIL);
			dest.setOnExistingSubFolder(DestinationClashPolicy.APPEND);
			dest.setPersistenceId(Constants.THREDDS_PERSISTENCE);
			dest.setSubFolder(operatingPath);
			dest.setDestinationFileName(Constants.LOCK_FILE);

			temp=File.createTempFile("tmp_lock", ".tmp");
			writer=new PrintWriter(temp);
			writer.write(processId);
			writer.flush();
			writer.close();

			cl.localFile(temp, dest);
		}catch(Throwable t) {
			throw new UnableToLockException("Unable to lock "+operatingPath,t);
		}finally {
			if(writer!=null) IOUtils.closeQuietly(writer);
			if(temp!=null)try { 
				Files.deleteIfExists(temp.toPath()); 
			}catch(IOException e) {
				log.warn("Unable to delete temp file {} ",temp.getAbsolutePath(),e);
			}
			resetCallerToken();
		}

	}

	public void deleteThreddsFile(String location) throws RemoteFileNotFoundException {
		setTargetToken();
		String urlString="http://"+hostname+":80/"+Constants.THREDDS_DATA_TRANSFER_BASE_URL+getPathFromStartingLocation(location);
		log.info("Deleting file at {} ",urlString);
		try{
			getWebClient().target(urlString).request().delete();
		}catch(Throwable t) {
			throw new RemoteFileNotFoundException("Unable to access "+urlString, t);
		}finally{
			resetCallerToken();
		}
	}	


	public boolean existsThreddsFile(String location) {
		try{
			getFileDescriptor(location);
			return true;
		}catch(RemoteFileNotFoundException e) {
			return false;
		}
	}

	public String readThreddsFile(String location) throws RemoteFileNotFoundException {
		setTargetToken();
		String urlString="http://"+hostname+":80/"+Constants.THREDDS_DATA_TRANSFER_BASE_URL+getPathFromStartingLocation(location);
		log.info("Reading file at {} ",urlString);
		try{
			return getWebClient().target(urlString).request().get().readEntity(String.class);
		}catch(Throwable t) {
			throw new RemoteFileNotFoundException("Unable to access "+urlString, t);
		}finally {
			resetCallerToken();
		}
	}


	public void createEmptyFolder(String targetPath) throws InternalException {
		setTargetToken();
		String toCleanPath=getPathFromStartingLocation(targetPath);
		try{
			log.info("Cleaning up {} on {} ",toCleanPath,hostname);			
			DataTransferClient client=getDTClient(hostname);
			File toTransfer=File.createTempFile("clean", ".dt_temp");
			toTransfer.createNewFile();
			Destination dest=new Destination();
			dest.setCreateSubfolders(true);
			dest.setOnExistingFileName(DestinationClashPolicy.REWRITE);
			dest.setOnExistingSubFolder(DestinationClashPolicy.REWRITE);
			dest.setPersistenceId(Constants.THREDDS_PERSISTENCE);
			dest.setSubFolder(toCleanPath);
			dest.setDestinationFileName(toTransfer.getName());
			
			log.info("Going to cleanup remote folder {} on {} ",dest.getSubFolder(),hostname);
			client.localFile(toTransfer, dest);
			this.deleteThreddsFile(targetPath+"/"+toTransfer.getName());
			log.info("Done");
			log.debug("Resulting folder descriptor : {} ",getFileDescriptor(targetPath));
		}catch(Exception e) {
			log.error("Unable to delete remote folder "+toCleanPath,e);
			throw new InternalException("Unable to cleanup remote folder.");
		}finally {
			resetCallerToken();
		}
	}

	public ThreddsCatalog createCatalog(String name) throws InternalException {
		setTargetToken();
		try{
			log.info("Creating catalog with name {} for path {} ",name,operatingPath);
			String sdiUrl="http://"+getSDIServiceHost()+"/"+Constants.SDI_THREDDS_BASE_URL;
			Response resp=getWebClient().target(sdiUrl).
					queryParam("name", name).
					queryParam("path", operatingPath).
					queryParam("folder", operatingPath).request().put(null);			
			if(!(resp.getStatus()>=200&&resp.getStatus()<300)) 
				throw new InternalException("Failed catalog registration on SDI Service. Message "+resp.readEntity(String.class));
			return getCatalog();
		}catch(Throwable t) {
			log.error("Unable to create catalog",t);
			throw new InternalException("Unable to create catalog",t); 
		}finally {
			resetCallerToken();
		}
	}

	public ThreddsCatalog getCatalog() {
		setTargetToken();
		try{ThreddsInfo info=getThreddsInfo();
		String instanceBasePath=info.getLocalBasePath();
		return info.getCatalogByFittingLocation(instanceBasePath+"/"+operatingPath);
		}finally {
			resetCallerToken();
		}
	}


	public RemoteFileDescriptor getFileDescriptor() throws RemoteFileNotFoundException {
		return getFileDescriptor(null);
	}

	public RemoteFileDescriptor getFileDescriptor(String path) throws RemoteFileNotFoundException {
		setTargetToken();
		String urlString="http://"+hostname+":80/"+Constants.THREDDS_DATA_TRANSFER_BASE_URL+getPathFromStartingLocation(path);
		log.info("Reading file at {} ",urlString);
		try{
			return getWebClient().target(urlString).queryParam("descriptor", true).request().get().readEntity(RemoteFileDescriptor.class);
		}catch(Throwable t) {
			throw new RemoteFileNotFoundException("Unable to access "+urlString, t);
		}finally {
			resetCallerToken();
		}
	}

	public InputStream getInputStream(String path) throws RemoteFileNotFoundException {
		setTargetToken();
		String urlString="http://"+hostname+":80/"+Constants.THREDDS_DATA_TRANSFER_BASE_URL+getPathFromStartingLocation(path);
		log.info("Reading file at {} ",urlString);
		try{
			return getWebClient().target(urlString).request().get().readEntity(InputStream.class);
		}catch(Throwable t) {
			throw new RemoteFileNotFoundException("Unable to access "+urlString, t);
		}finally {
			resetCallerToken();
		}
	}


	public TransferResult transferFile(Destination dest,String url,Set<PluginInvocation> invocations) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException {
		setTargetToken();
		try{DataTransferClient client=getDTClient(hostname);
		if(invocations!=null&&!invocations.isEmpty())
			return client.httpSource(url, dest,invocations);
		else return client.httpSource(url, dest);
		}finally {
			resetCallerToken();
		}
	}


	private String getPathFromStartingLocation(String location) {
		if(location!=null&&location.length()>0)return operatingPath+"/"+location;
		else return operatingPath;
	}

	private static String getSDIServiceHost(){
		return getGCoreEndpointHostname(ServiceConstants.SERVICE_CLASS, ServiceConstants.SERVICE_NAME);
	}


	private static String getThreddsHost(){
		return getGCoreEndpointHostname(ServiceConstants.SERVICE_CLASS, "Thredds");
	}

	private static String getGCoreEndpointHostname(String serviceClass,String serviceName) {
		SimpleQuery query =queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq '"+serviceClass+"'")
		.addCondition("$resource/Profile/ServiceName/text() eq '"+serviceName+"'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		GCoreEndpoint endpoint= client.submit(query).get(0);

		return endpoint.profile().endpoints().iterator().next().uri().getHost();
	}

	private static Client getWebClient() {
		return ClientBuilder.newClient(new ClientConfig().register(AuthorizationFilter.class))
				.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);		
	}

	private static DataTransferClient getDTClient(String threddsHostName) throws UnreachableNodeException, ServiceNotFoundException {
		log.debug("Getting DT Client for {} ",threddsHostName);
		return DataTransferClient.getInstanceByEndpoint("http://"+threddsHostName+":80");
	}
}
