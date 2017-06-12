package org.gcube.data.transfer.library.transferers;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.gcube.data.transfer.library.TransferResult;
import org.gcube.data.transfer.library.client.Client;
import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.RemoteServiceException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.library.model.LocalSource;
import org.gcube.data.transfer.library.model.Source;
import org.gcube.data.transfer.library.model.StorageSource;
import org.gcube.data.transfer.library.model.URLSource;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.TransferTicket.Status;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Transferer {

	protected Client client;

	protected Transferer(Client client){
		this.client=client;
	}




	protected Source source=null;
	protected boolean prepared=false;
	protected Destination destination=null;
	protected Set<PluginInvocation> invocations=null;
	
	
	public void setDestination(Destination destination) {
		this.destination = destination;
	}
	
	public void setInvocations(Set<PluginInvocation> invocations) {
		this.invocations = invocations;
	}
	
	public Transferer localFile(File f) throws InvalidSourceException{
		source=new LocalSource(f);
		return this;
	}

	public Transferer localFile(String path) throws InvalidSourceException{
		try{
			File f=new File(path);
			return localFile(f);
		}catch(NullPointerException e){
			throw new InvalidSourceException("Path is null",e);
		}

	}

	public Transferer storageFileId(String fileId) throws InvalidSourceException{
		source=new StorageSource(fileId);
		return this;
	}

	public Transferer fromURL(URL sourceURL) throws InvalidSourceException{		
		source=new URLSource(sourceURL);
		return this;
	}

	
	public void reset(){
		this.destination=null;
		this.source=null;
		this.invocations=null;
	}

	public TransferResult transfer() throws SourceNotSetException, InvalidSourceException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		try{			
			checkDestination();
			checkSource();
			checkPluginInvocations();
			prepare();
			TransferRequest request=prepareRequest();
			log.debug("Request is {}, sending it to {}",request,client.getEndpoint());
			TransferResult result=doTheTransfer(request);
			return result;
		}finally{
			clean();
		}
	}

	protected TransferResult doTheTransfer(TransferRequest request) throws FailedTransferException{
		try{
			TransferTicket submissionResponse= client.submit(request);
			boolean continuePolling=true;
			TransferTicket ticket=null;
			do{
				ticket=client.getTransferStatus(submissionResponse.getId());
				System.out.println("Status : "+ticket);
				continuePolling=ticket.getStatus().equals(Status.PENDING)||ticket.getStatus().equals(Status.TRANSFERRING)||ticket.getStatus().equals(Status.WAITING);
				try{
					Thread.sleep(500);
				}catch(InterruptedException e){}
			}while(continuePolling);
			if(ticket.getStatus().equals(Status.ERROR)) throw new FailedTransferException("Remote Message : "+ticket.getMessage());
			if(ticket.getStatus().equals(Status.STOPPED)) throw new FailedTransferException("Stopped transfer : "+ticket.getMessage());
			long elapsedTime=System.currentTimeMillis()-ticket.getSubmissionTime().value.getTimeInMillis();
			return new TransferResult(source, client.getEndpoint(), elapsedTime, ticket.getTransferredBytes(), ticket.getDestinationFileName());
		}catch(RemoteServiceException e){
			throw new FailedTransferException(e);
		}
	}

	protected void checkDestination() throws InvalidDestinationException,DestinationNotSetException, UnreachableNodeException{
		if(destination==null) throw new DestinationNotSetException();
		if(!destination.getPersistenceId().equals(Destination.DEFAULT_PERSISTENCE_ID)){
			Set<String> availablePersistenceIds=getDestinationCapabilities().getAvailablePersistenceIds();
			if(!availablePersistenceIds.contains(destination.getPersistenceId()))
				throw new InvalidDestinationException("Declared persistence id "+destination.getPersistenceId()+" not found. Available are "+availablePersistenceIds);
		}
	}
	
	protected void checkSource() throws SourceNotSetException, InvalidSourceException{
		if(source==null) throw new SourceNotSetException();
		source.validate();
	}
	
	protected void checkPluginInvocations(){
		
	}
	
	protected abstract TransferRequest prepareRequest() throws InitializationException;


	protected void prepare() throws InitializationException{
		prepared=true;
	}
	protected void clean(){

	}

	public TransferCapabilities getDestinationCapabilities() throws UnreachableNodeException {
		try{
			return client.getCapabilties();
		}catch(Exception e){
			throw new UnreachableNodeException(e);
		}
	}


}
