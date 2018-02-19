package org.gcube.usecases.ws.thredds;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.tika.io.IOUtils;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.client.AuthorizationFilter;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.plugins.thredds.ThreddsInfo;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.usecases.ws.thredds.faults.RemoteFileNotFoundException;
import org.gcube.usecases.ws.thredds.faults.UnableToLockException;
import org.glassfish.jersey.client.ClientConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Commons {



	static final SimpleDateFormat DATE_FORMAT= new SimpleDateFormat("dd-MM-yy:HH:mm:SS");



	public static void cleanupFolder(String toCleanPath, String destinationToken) {
		String toRestoreToken=TokenSetter.getCurrentToken();
		try{
			log.debug("Setting target token {} for cleanup request of path {} ",destinationToken,toCleanPath);
			TokenSetter.setToken(destinationToken);
			String hostname=getThreddsHost();
			DataTransferClient client=getDTClient(hostname);
			File toTransfer=File.createTempFile("clean", ".dt_temp");
			toTransfer.createNewFile();
			Destination dest=new Destination();
			dest.setCreateSubfolders(true);
			dest.setOnExistingFileName(DestinationClashPolicy.REWRITE);
			dest.setOnExistingSubFolder(DestinationClashPolicy.REWRITE);
			dest.setPersistenceId("thredds");
			dest.setSubFolder("public/netcdf/"+toCleanPath);
			log.info("Going to cleanup remote folder {} on {} ",dest.getSubFolder(),hostname);
			client.localFile(toTransfer, dest);
			log.info("Done");
		}catch(Exception e) {
			log.error("Unable to delete remote folder "+toCleanPath,e);
			throw new RuntimeException("Unable to cleanup remote folder.");
		}finally {
			log.debug("Resetting original token {} ",toRestoreToken);
			TokenSetter.set(toRestoreToken);
		}
	}


	public static String getThreddsHost(){

		SimpleQuery query =queryFor(GCoreEndpoint.class);
		query.addCondition("$resource/Profile/ServiceClass/text() eq 'SDI'")
		.addCondition("$resource/Profile/ServiceName/text() eq 'Thredds'");				
		//		.setResult("$resource/Profile/AccessPoint");

		DiscoveryClient<GCoreEndpoint> client = clientFor(GCoreEndpoint.class);

		GCoreEndpoint endpoint= client.submit(query).get(0);

		return endpoint.profile().endpoints().iterator().next().uri().getHost();
	}

	public static DataTransferClient getDTClient(String threddsHostName) throws UnreachableNodeException, ServiceNotFoundException {
		log.debug("Getting DT Client for {} ",threddsHostName);
		return DataTransferClient.getInstanceByEndpoint("http://"+threddsHostName+":80");
	}

	public static String readThreddsFile(String location) throws RemoteFileNotFoundException {
		String urlString="http://"+getThreddsHost()+":80/"+Constants.THREDDS_DATA_TRANSFER_BASE_URL+location;
		log.info("Reading file at {} ",urlString);
		try{
			return getWebClient().target(urlString).request().get().readEntity(String.class);
		}catch(Throwable t) {
			throw new RemoteFileNotFoundException("Unable to access "+urlString, t);
		}
	}



	public static void deleteThreddsFile(String location) throws RemoteFileNotFoundException {
		String urlString="http://"+getThreddsHost()+":80/"+Constants.THREDDS_DATA_TRANSFER_BASE_URL+location;
		log.info("Reading file at {} ",urlString);
		try{
			getWebClient().target(urlString).request().delete();
		}catch(Throwable t) {
			throw new RemoteFileNotFoundException("Unable to access "+urlString, t);
		}
	}	

	public static final ThreddsInfo getThreddsInfo() {
		String infoPath=getThreddsInfoPath();
		log.info("Loading thredds info from {} ",infoPath);
		WebTarget target=getWebClient().target(infoPath);
		return target.request(MediaType.APPLICATION_JSON).get(ThreddsInfo.class);
	}

	private static Client getWebClient() {
		return ClientBuilder.newClient(new ClientConfig().register(AuthorizationFilter.class));		
	}

	private static String getThreddsInfoPath() {
		return "https://"+getThreddsHost()+"/data-transfer-service/gcube/service/Capabilities/pluginInfo/REGISTER_CATALOG";
	}

	public static void lockFolder(String folderPath,String processId) throws UnableToLockException {
		PrintWriter writer=null;
		File temp=null;
		try{
			log.info("Locking remote path {} to processId {} ",folderPath,processId);
			DataTransferClient cl=getDTClient(getThreddsHost());

			Destination dest=new Destination();
			dest.setCreateSubfolders(false);
			dest.setOnExistingFileName(DestinationClashPolicy.FAIL);
			dest.setOnExistingSubFolder(DestinationClashPolicy.APPEND);
			dest.setPersistenceId("thredds");
			dest.setSubFolder(folderPath);
			dest.setDestinationFileName(Constants.LOCK_FILE);

			temp=File.createTempFile("tmp_lock", ".tmp");
			writer=new PrintWriter(temp);
			writer.write(processId);
			writer.flush();
			writer.close();

			cl.localFile(temp, dest);
		}catch(Throwable t) {
			throw new UnableToLockException("Unable to lock "+folderPath,t);
		}finally {
			if(writer!=null) IOUtils.closeQuietly(writer);
			if(temp!=null)try { 
				Files.deleteIfExists(temp.toPath()); 
			}catch(IOException e) {
				log.warn("Unable to delete temp file {} ",temp.getAbsolutePath(),e);
			}
		}

	}
	
	
}
