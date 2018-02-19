package org.gcube.usecases.ws.thredds.engine.impl;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.tika.io.IOUtils;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.client.AuthorizationFilter;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.RemoteFileDescriptor;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsCatalog;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.spatial.data.sdi.utils.ScopeUtils;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.TokenSetter;
import org.gcube.usecases.ws.thredds.faults.InternalException;
import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.faults.UnableToLockException;
import org.glassfish.jersey.client.ClientConfig;

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
	
	
	private String callerToken=null;
	
	private void setTargetToken() {		
		callerToken=TokenSetter.getCurrentToken();
		TokenSetter.setToken(targetToken);		
	}
	
	private void resetCallerToken() {
		TokenSetter.setToken(callerToken);
		callerToken=null;
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
			dest.setCreateSubfolders(false);
			dest.setOnExistingFileName(DestinationClashPolicy.FAIL);
			dest.setOnExistingSubFolder(DestinationClashPolicy.APPEND);
			dest.setPersistenceId("thredds");
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
		setTargetToken();
		String urlString="http://"+hostname+":80/"+Constants.THREDDS_DATA_TRANSFER_BASE_URL+getPathFromStartingLocation(location);
		log.info("Checking file at {} ",urlString);
		try{
			Response resp=getWebClient().target(urlString).request().head();
			return resp.getStatus()>=200&&resp.getStatus()<300;
		}catch(Throwable t) {
			return false;
		}finally{
			resetCallerToken();
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
	
	
	public void cleanupFolder(String targetPath) throws InternalException {
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
			dest.setPersistenceId("thredds");
			dest.setSubFolder(toCleanPath);
			log.info("Going to cleanup remote folder {} on {} ",dest.getSubFolder(),hostname);
			client.localFile(toTransfer, dest);
			log.info("Done");
		}catch(Exception e) {
			log.error("Unable to delete remote folder "+toCleanPath,e);
			throw new InternalException("Unable to cleanup remote folder.");
		}finally {
			resetCallerToken();
		}
	}
	
	public ThreddsCatalog createCatalog() {
		return null;
	}
	
	public ThreddsCatalog getCatalog() {
		return null;
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
	
	
	
	private String getPathFromStartingLocation(String location) {
		if(location!=null&&location.length()>0)return operatingPath+"/"+location;
		else return operatingPath;
	}
	
	private static String getThreddsHost(){

		SimpleQuery query =queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'SDI'")
		.addCondition("$resource/Profile/ServiceName/text() eq 'Thredds'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		GCoreEndpoint endpoint= client.submit(query).get(0);

		return endpoint.profile().endpoints().iterator().next().uri().getHost();
	}
	
	private static Client getWebClient() {
		return ClientBuilder.newClient(new ClientConfig().register(AuthorizationFilter.class));		
	}
	
	private static DataTransferClient getDTClient(String threddsHostName) throws UnreachableNodeException, ServiceNotFoundException {
		log.debug("Getting DT Client for {} ",threddsHostName);
		return DataTransferClient.getInstanceByEndpoint("http://"+threddsHostName+":80");
	}
}
