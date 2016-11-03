package org.gcube.data.transfer.service;

import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.service.transfers.Capabilities;
import org.gcube.data.transfer.service.transfers.Requests;
import org.gcube.data.transfer.service.transfers.TransferStatus;
import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;


public class TestCall extends JerseyTest {

	@Override
	protected Application configure() {
		System.out.println("Configuration for "+ServiceConstants.APPLICATION_PATH);
		AbstractBinder binder=new AbstractBinder() {
			
			@Override
			protected void configure() {
				bindFactory(TicketManagerProvider.class).to(TicketManager.class);
				bindFactory(CapabilitiesProviderFactory.class).to(CapabilitiesProvider.class);
				bindFactory(RequestManagerFactory.class).to(RequestManager.class);
				bindFactory(PersistenceProviderFactory.class).to(PersistenceProvider.class);
			}
		};
		
		ResourceConfig config= new ResourceConfig(Capabilities.class,Requests.class,TransferStatus.class);
		config.register(binder);
		config.register(DebugExceptionMapper.class);
		return config;
	}
	
	@Test
	public void capabilities(){
		WebTarget target=target(ServiceConstants.CAPABILTIES_SERVLET_NAME);		
		System.out.println(target.request(MediaType.APPLICATION_XML).get(TransferCapabilities.class));
	}
	
	@Test(expected=NotFoundException.class)
	public void testTicketNotFound(){
		System.out.println(getTicketById("myVeryLongestId"));
	}
	
	@Test
	public void transfer() throws MalformedURLException{
		TransferRequest request=new TransferRequest("", new HttpDownloadSettings(new URL("http://goo.gl/oLP7zG"), HttpDownloadOptions.DEFAULT),new Destination("something.txt"));
		TransferTicket submissionResponse=submit(request);
		System.out.println("Obtained "+submissionResponse);
		boolean continuePolling=true;
		do{
		TransferTicket ticket=getTicketById(submissionResponse.getId());
		System.out.println("Status : "+ticket);
		continuePolling=ticket.getStatus().equals(Status.PENDING)||ticket.getStatus().equals(Status.TRANSFERRING)||ticket.getStatus().equals(Status.WAITING);
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){}
		}while(continuePolling);
	}
	
	
	private TransferTicket getTicketById(String id){
		return target(ServiceConstants.STATUS_SERVLET_NAME).path(id).request(MediaType.APPLICATION_XML).get(TransferTicket.class);
	}
	
	private TransferTicket submit(TransferRequest req){
		return target(ServiceConstants.REQUESTS_SERVLET_NAME).request(MediaType.APPLICATION_XML).post(Entity.entity(req,MediaType.APPLICATION_XML),TransferTicket.class);
	}
}
