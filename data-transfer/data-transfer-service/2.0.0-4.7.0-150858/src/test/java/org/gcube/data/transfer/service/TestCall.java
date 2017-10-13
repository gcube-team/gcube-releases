package org.gcube.data.transfer.service;

import java.io.File;
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
import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.PluginManager;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


public class TestCall extends JerseyTest {

	public static class MyBinder extends AbstractBinder{

		public MyBinder() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void configure() {
			bindFactory(TicketManagerProvider.class).to(TicketManager.class);
			bindFactory(CapabilitiesProviderFactory.class).to(CapabilitiesProvider.class);
			bindFactory(RequestManagerFactory.class).to(RequestManager.class);
			bindFactory(PersistenceProviderFactory.class).to(PersistenceProvider.class);
			bindFactory(PluginManagerFactory.class).to(PluginManager.class);
			
		}
	}
	
	
	@Override
	protected Application configure() {
		System.out.println("Configuration for "+ServiceConstants.APPLICATION_PATH);
		
		ResourceConfig config= new ResourceConfig(Capabilities.class,ProviderLoggingListener.class);
		config.register(new MyBinder());
		config.register(DebugExceptionMapper.class);
		
		
		//Multipart
		config.packages("org.glassfish.jersey.media.multipart");
		config.packages("org.gcube.data.transfer.service.transfers");
	    config.register(MultiPartFeature.class);
		return config;
	}
	
	
	@Override
	protected void configureClient(ClientConfig config) {
		// TODO Auto-generated method stub
		super.configureClient(config);
		config.register(MultiPartFeature.class);
	}
	
	@Test
	public void capabilities(){
		WebTarget target=target(ServiceConstants.CAPABILTIES_SERVLET_NAME);
		System.err.println(target.getUri());
		System.out.println(target.request(MediaType.APPLICATION_JSON).get(TransferCapabilities.class));
	}
	
	@Test(expected=NotFoundException.class)
	public void testTicketNotFound(){
		System.out.println(getTicketById("myVeryLongestId"));
	}
	
	@Test
	public void transfer() throws MalformedURLException{
		TransferRequest request=new TransferRequest("", new HttpDownloadSettings(new URL("http://data.d4science.org/bm1sRTg0Y1ZZZHRraUZuNG1IUGdvOUVFMnlOcTlFRmlHbWJQNStIS0N6Yz0"), HttpDownloadOptions.DEFAULT),new Destination("something"));
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
	
	@Test
	public void RESTUpload(){
		FormDataMultiPart multipart = new FormDataMultiPart();
				File toSend=new File("/home/fabio/life@dev2.d4science.org");
				Assert.assertTrue(toSend!=null);
				Assert.assertTrue(toSend.exists());
				multipart.bodyPart(new FileDataBodyPart(ServiceConstants.MULTIPART_FILE, toSend));
				WebTarget target=target(ServiceConstants.REST_SERVLET_NAME).path(ServiceConstants.REST_FILE_UPLOAD).path("data-transfer-service").path("my/sub/path");
				
				
				System.out.println(target.request().post(Entity.entity(multipart, multipart.getMediaType())));
	}
	
	
	private TransferTicket getTicketById(String id){
		return target(ServiceConstants.STATUS_SERVLET_NAME).path(id).request(MediaType.APPLICATION_JSON).get(TransferTicket.class);
	}
	
	private TransferTicket submit(TransferRequest req){
		return target(ServiceConstants.REQUESTS_SERVLET_NAME).request(MediaType.APPLICATION_JSON).post(Entity.entity(req,MediaType.APPLICATION_JSON),TransferTicket.class);
	}
}
