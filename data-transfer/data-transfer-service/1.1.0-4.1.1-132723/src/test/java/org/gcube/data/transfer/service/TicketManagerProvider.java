package org.gcube.data.transfer.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.model.utils.DateWrapper;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.data.transfer.service.transfers.engine.faults.TicketNotFoundException;
import org.gcube.data.transfer.service.transfers.engine.impl.TransferTicketManagerImpl;
import org.glassfish.hk2.api.Factory;

public class TicketManagerProvider implements Factory<TicketManager> {

	
	@Override
	public void dispose(TicketManager instance) {
		
	}
	
	@Override
	public TicketManager provide() {
		return new TransferTicketManagerImpl(){
//			@Override
//			public TransferTicket get(String ticketId)
//					throws TicketNotFoundException{
//					URL url=null;
//					try{
//						url=new URL("http://some.where.com");
//					}catch(MalformedURLException e){}
//					TransferRequest request=new TransferRequest(UUID.randomUUID().toString(), new HttpDownloadSettings(url,new HttpDownloadOptions()));
//					return new TransferTicket(request, org.gcube.data.transfer.model.TransferTicket.Status.STOPPED, 1005467l, .57d, 123345, new DateWrapper(),"/dev/null","bona");
////				throw new TicketNotFoundException();
//			}
		};
	}
}
