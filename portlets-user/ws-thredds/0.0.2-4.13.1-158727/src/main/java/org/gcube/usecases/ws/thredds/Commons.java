package org.gcube.usecases.ws.thredds;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.File;
import java.text.SimpleDateFormat;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.data.transfer.library.DataTransferClient;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Commons {

	static class Constants{
		public static final String LAST_UPDATE_TIME="WS-SYNCH.LAST_UPDATE";
	}
	
	
	static final SimpleDateFormat DATE_FORMAT= new SimpleDateFormat("dd-MM-yy:HH:mm:SS");
	
	
	
	static void cleanupFolder(String toCleanPath, String destinationToken) {
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
}
