package org.gcube.data.transfer.library;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.gcube.data.transfer.library.client.AuthorizationFilter;
import org.gcube.data.transfer.library.client.Client;
import org.gcube.data.transfer.library.faults.CommunicationException;
import org.gcube.data.transfer.library.faults.RemoteServiceException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.BeforeClass;
import org.junit.Test;


public class TestClientCalls {

	static String hostname="http://node3-d-d4s.d4science.org:80";
//	static String hostname="http://pc-fabio.isti.cnr.it:8080";
	static String scope="/gcube/devNext";
	static Client client;
	
	
	@BeforeClass
	public static void init() throws ServiceNotFoundException{
		TokenSetter.set(scope);
		client=new Client(hostname);
	}
	
	
	@Test
	public void getCapabilties() throws CommunicationException{
		System.out.println(client.getCapabilties());
	}
	
	
	@Test 
	public void doTheTransfer() throws MalformedURLException, RemoteServiceException{
		Destination dest=new Destination("outputFile");
		dest.setCreateSubfolders(true);
		dest.setSubFolder("bla/bla/bllaaa");
		dest.setOnExistingFileName(DestinationClashPolicy.ADD_SUFFIX);
		dest.setOnExistingSubFolder(DestinationClashPolicy.APPEND);
		TransferRequest request= new TransferRequest("", new HttpDownloadSettings(new URL("http://goo.gl/oLP7zG"), HttpDownloadOptions.DEFAULT),dest);
		System.out.println("Submitting "+request);
		TransferTicket ticket=client.submit(request);
		System.out.println("Ticket is "+ticket);	
		
		boolean continuePolling=true;
		do{
		ticket=client.getTransferStatus(ticket.getId());
		System.out.println("Status : "+ticket);
		continuePolling=ticket.getStatus().equals(Status.PENDING)||ticket.getStatus().equals(Status.TRANSFERRING)||ticket.getStatus().equals(Status.WAITING);
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){}
		}while(continuePolling);
	}
	
	

	@Test
	public void directCall(){		
		javax.ws.rs.client.Client client = ClientBuilder.newClient(new ClientConfig().register(AuthorizationFilter.class));
		WebTarget target=client.target(hostname+"/data-transfer-service"+ServiceConstants.APPLICATION_PATH+"Capabilities");
//		WebTarget target=client.target(hostname+"/data-transfer-service/gcube/service/Capabilities");
		System.out.println("Asking capabilities to target : "+target.getUri());
		System.out.println(target.
				request(MediaType.APPLICATION_XML).get(TransferCapabilities.class));
	}
	
}
