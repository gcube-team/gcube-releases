package org.gcube.data.transfer.service.transfers.engine.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.data.transfer.service.transfers.engine.faults.ManagedException;
import org.gcube.data.transfer.service.transfers.engine.faults.NotSupportedMethodException;

@Slf4j
public class RequestHandler implements Runnable{


	private TransferTicket ticket;

	
	private TicketManager manager;

	

	public RequestHandler(TransferRequest servingRequest,TicketManager manager) {
		super();	
		ticket=new TransferTicket(servingRequest);
		log.debug("Created Handler, request is : "+servingRequest);
		this.manager=manager;
		manager.insertUpdate(ticket);
	}



	@Override
	public void run() {
		try{
			log.trace("Request handling started. Ticket is "+ticket);
			switch(ticket.getSettings().getOptions().getMethod()){
			case DirectTransfer : throw new NotSupportedMethodException("Unable to manage request [ID "+ticket.getId()+"]. Method not supported : "+ticket.getSettings().getOptions().getMethod());
			case HTTPDownload :{

				File output =null;
				BufferedInputStream in = null;
				BufferedOutputStream out=null;
				boolean complete=false;
				try{
					updateTicket("Opening connection",0d,Status.TRANSFERRING,0l);
					HttpDownloadSettings options=(HttpDownloadSettings) (ticket.getSettings());
					log.debug("HttpDownload, opening channel");

					try {
						output=File.createTempFile("http_", "");
						out=new BufferedOutputStream(new FileOutputStream(output));
					} catch (IOException e) {
						log.warn("Unable to create destination file.",e);
						throw new ManagedException("Cannot save file in host");
					}


					log.debug("Opening read buffer "+options.getSource().toString());				
					ticket.setDestinationFileName(output.getAbsolutePath());

					try{
						in = new BufferedInputStream(options.getSource().openStream());
					}catch(Exception e){
						log.debug("Unable to open connection ",e);
						throw new ManagedException("Cannot open connection to source");
					}

					try{
						long receivedTotal=0l;
						byte[] internalBuf=new byte[1024];
						int received=0;
						while ((received=in.read(internalBuf))!=-1){
							out.write(internalBuf);
							receivedTotal+=received;
							updateTicket("Transferring",0d,Status.TRANSFERRING,receivedTotal);					            
						}
					}catch(IOException e){
						log.debug("Unable to read from source",e);
						throw new ManagedException("Unable to read from source.");
					}

					complete=true;


				}finally{
					if(!complete) if(output!=null) Files.deleteIfExists(null);
					if(in!=null)in.close();
					if(out!=null) {
						out.flush();
						out.close();
					}

				}
			}
			
			
			
			updateTicket("Completed transfer",1d,Status.SUCCESS);
			
			}
		}catch(NotSupportedMethodException e){
			setError(e.getMessage());
		}catch(ManagedException e){
			setError(e.getMessage());
		}catch(Throwable t){
			setError("Unexpected error while downloading : "+t.getMessage());
			log.error("Unexpected error occurred",t);
		}
	}


	private void setError(String message){
		updateTicket(message,ticket.getPercent(),Status.ERROR);
	}

	private void updateTicket(String message,double percent,Status status){
		updateTicket(message,percent,status,ticket.getTransferredBytes());
	}

	private void updateTicket(String message,double percent,Status status,long readBytes){
		ticket.setStatus(status);
		ticket.setMessage(message);
		ticket.setPercent(percent);
		ticket.setTransferredBytes(readBytes);
		manager.insertUpdate(ticket);
	}



}
