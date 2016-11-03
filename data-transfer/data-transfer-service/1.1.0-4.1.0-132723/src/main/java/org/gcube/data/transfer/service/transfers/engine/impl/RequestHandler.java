package org.gcube.data.transfer.service.transfers.engine.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.TicketManager;
import org.gcube.data.transfer.service.transfers.engine.faults.DestinationAccessException;
import org.gcube.data.transfer.service.transfers.engine.faults.ManagedException;
import org.gcube.data.transfer.service.transfers.engine.faults.NotSupportedMethodException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestHandler implements Runnable{


	private TransferTicket ticket;
	private PersistenceProvider persistenceProvider;

	private TicketManager manager;



	public RequestHandler(TransferRequest servingRequest,TicketManager manager,PersistenceProvider persistenceProvider) {
		super();	
		ticket=new TransferTicket(servingRequest);
		log.debug("Created Handler, request is : "+servingRequest);
		this.manager=manager;
		this.persistenceProvider=persistenceProvider;
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
						output=prepareDestination(ticket.getDestinationSettings());
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
		try{
			long elapsedTime=System.currentTimeMillis()-ticket.getSubmissionTime().getValue().getTimeInMillis();
			long average=(readBytes/((elapsedTime==0?1:elapsedTime)))*1000;
			ticket.setAverageTransferSpeed(average);
		}catch(Exception e){
			log.warn("Unable to evaluate average ",e);
		}
		manager.insertUpdate(ticket);
	}

	private File prepareDestination(Destination dest) throws DestinationAccessException{
		File persistenceFolder=persistenceProvider.getPersistenceFolderById(dest.getPersistenceId());
		if(!persistenceFolder.canWrite()) throw new DestinationAccessException("Cannot write to selecte persistenceFolder [ID :"+dest.getPersistenceId()+"]");
		log.debug("Got Persistence folder ID {}, PATH {}",persistenceFolder.getAbsolutePath(),dest.getPersistenceId());
		String subFolderName=dest.getSubFolder();
		File subFolder=persistenceFolder;
		if(subFolderName!=null){
			log.debug("Looking for subFolder : "+subFolder);
			if(subFolderName.startsWith(File.pathSeparator)) throw new DestinationAccessException("SubFolder cannot be absolute.");
			//			 String[] pathItems=subFolderName.split(File.pathSeparator);
			//			 for(String subPath:pathItems){				
			////				 Set<String> existingFiles=new HashSet<String>(Arrays.asList(subFolder.list()));
			//				 subFolder=new File(subFolder,subPath);
			//				 if(subFolder.exists()){
			//					 if(!subFolder.canRead()) throw new DestinationAccessException("Cannot write to "+subFolder.getAbsolutePath());
			//				 }else if(dest.getCreateSubfolders()) subFolder.mkdir();
			//				 else throw new DestinationAccessException("Destination subfolder {} not found. Set createSubFolder=true to create intermediary directories.");
			//			 }

			subFolder=new File(persistenceFolder,subFolderName);
			if(subFolder.exists()){
				if(!subFolder.canRead()) throw new DestinationAccessException("Cannot write to "+subFolder.getAbsolutePath());
				manageClash(dest.getOnExistingSubFolder(),subFolder);
			}else if(dest.getCreateSubfolders()) subFolder.mkdirs();
			else throw new DestinationAccessException("SubFolder not found. Use createSubFolders=true to create it.");
		}
		
		File destination=new File(subFolder,dest.getDestinationFileName());
		if(destination.exists()) return manageClash(dest.getOnExistingFileName(),destination);
		else {
			try{
			destination.createNewFile();
			return destination;
			}catch(IOException e){
				throw new DestinationAccessException("Unable to create file ",e);
			}
		}
	}

	private static final File manageClash(DestinationClashPolicy policy, File clashing ) throws DestinationAccessException{
		log.debug("Managing clash for {}, policy is {} ",clashing.getAbsolutePath(),policy);
		boolean dir=clashing.isDirectory();
		try{
			switch(policy){
			case ADD_SUFFIX : {
				int counter=1;
				while(clashing.exists()){
					clashing=new File(clashing.getParentFile(),clashing.getName()+"("+counter+")");
				}
				if(dir)clashing.mkdirs();
				else clashing.createNewFile();
				break;
			}
			case FAIL: throw new DestinationAccessException("Found existing "+clashing.getAbsolutePath()+"policy is "+policy); 
			case REWRITE : {
				Files.deleteIfExists(Paths.get(clashing.getAbsolutePath()));			
				if(dir)clashing.mkdirs();
				else clashing.createNewFile();
				break;
			}	
			}
		}catch(IOException e){
			throw new DestinationAccessException("Unable to rewrite existing destination",e);
		}
		return clashing;
	}

}
