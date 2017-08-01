package org.gcube.data.transfer.service.transfers.engine.impl;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.data.transfer.service.transfers.engine.faults.TicketNotFoundException;

@Slf4j
@Singleton
public class TransferTicketManagerImpl implements TicketManager{

	private static ConcurrentHashMap<String, TTLContainer> theMap=new ConcurrentHashMap<>();
	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	static{
		 scheduler.scheduleAtFixedRate(new Runnable() {
			
			 private static final long TTL =30*60*1000; 
			 
			 
			@Override
			public void run() {
				log.debug("Running Ticket cleaner, TTL is "+TTL);
				int removed=0;
				for(Entry<String,TTLContainer> entry:theMap.entrySet())
					if(System.currentTimeMillis()-entry.getValue().getLastUsageTime()>TTL){						
						theMap.remove(entry.getKey());
						removed++;
					}
				log.debug("Removed "+removed+" old tickets");
				
			}
		}, 30, 30, TimeUnit.MINUTES);
	}
	
	
	public class TTLContainer {
		
		private long lastUsageTime=System.currentTimeMillis();
		private TransferTicket theTicket;
		public TTLContainer(TransferTicket theTicket) {			
			this.theTicket = theTicket;
		}
		
		
		private void update(){
			lastUsageTime=System.currentTimeMillis();			
		}
		
		public TransferTicket getTicket(){
			update();
			return theTicket;
		}
		
		public long getLastUsageTime() {
			return lastUsageTime;
		}
		
	}
	
	
	@Override
	public boolean insertUpdate(TransferTicket toInsert) {
		return (theMap.put(toInsert.getId(), new TTLContainer(toInsert))!=null);
	}

	@Override
	public TransferTicket get(String ticketId) throws TicketNotFoundException {
		if(theMap.containsKey(ticketId))return theMap.get(ticketId).getTicket();
		else throw new TicketNotFoundException(String.format("Ticket [%s] not found. Request is probably served and outdated",ticketId));
	}

}
