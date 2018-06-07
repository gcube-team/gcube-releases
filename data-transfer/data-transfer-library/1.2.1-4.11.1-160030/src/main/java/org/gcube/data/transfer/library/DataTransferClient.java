package org.gcube.data.transfer.library;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.gcube.data.transfer.library.faults.DestinationNotSetException;
import org.gcube.data.transfer.library.faults.FailedTransferException;
import org.gcube.data.transfer.library.faults.HostingNodeNotFoundException;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidDestinationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.faults.ServiceNotFoundException;
import org.gcube.data.transfer.library.faults.SourceNotSetException;
import org.gcube.data.transfer.library.faults.UnreachableNodeException;
import org.gcube.data.transfer.library.transferers.Transferer;
import org.gcube.data.transfer.library.transferers.TransfererBuilder;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.TransferCapabilities;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataTransferClient {

	private Transferer transferer=null;
	
	private DataTransferClient(Transferer transferer) {
		this.transferer=transferer;
	}
	
	public static DataTransferClient getInstanceByEndpoint(String endpoint) throws UnreachableNodeException, ServiceNotFoundException{
		log.debug("Getting transferer for endpoint : "+endpoint);
		return new DataTransferClient(TransfererBuilder.getTransfererByHost(endpoint));		
	}
	
	public static DataTransferClient getInstanceByNodeId(String id) throws HostingNodeNotFoundException, UnreachableNodeException, ServiceNotFoundException{
		log.debug("Getting transferer for nodeId : "+id);
		return new DataTransferClient(TransfererBuilder.getTransfererByhostingNodeId(id));		
	}
	
	
	public TransferCapabilities getDestinationCapabilities() throws InitializationException{
		return this.transferer.getDestinationCapabilities();
	}
	
	
	public TransferResult localFile(String path,Destination dest)throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return localFile(path,dest,Collections.<PluginInvocation> emptySet());
	}
	
	public TransferResult localFile(File file,Destination dest)throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return localFile(file,dest,Collections.<PluginInvocation> emptySet());
	}
	
	public TransferResult httpSource(String url,Destination dest)throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return httpSource(url,dest,Collections.<PluginInvocation> emptySet());
	}
	
	public TransferResult httpSource(URL url,Destination dest)throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return httpSource(url,dest,Collections.<PluginInvocation> emptySet());
	}
	
	public TransferResult storageId(String id,Destination dest)throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return storageId(id,dest,Collections.<PluginInvocation> emptySet());
	}
	
	public TransferResult localFile(String path,Destination dest,PluginInvocation pluginInvocation) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return localFile(path,dest,Collections.singleton(pluginInvocation));
	}
	
	public TransferResult localFile(File file,Destination dest,PluginInvocation pluginInvocation) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return localFile(file,dest,Collections.singleton(pluginInvocation));
	}
	
	public TransferResult httpSource(String url,Destination dest,PluginInvocation pluginInvocation) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return httpSource(url,dest,Collections.singleton(pluginInvocation));
	}
	
	public TransferResult httpSource(URL url,Destination dest,PluginInvocation pluginInvocation) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return httpSource(url,dest,Collections.singleton(pluginInvocation));
	}
	
	public TransferResult storageId(String id,Destination dest,PluginInvocation pluginInvocation) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		return storageId(id,dest,Collections.singleton(pluginInvocation));
	}
	
	@Synchronized("transferer")
	public TransferResult localFile(String path,Destination dest,Set<PluginInvocation> pluginInvocations) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		if(transferer==null) throw new RuntimeException("Transferer not set, please set destination before trying to transfer");
		log.debug("Sending local file {} to {} : {} - {}",path,transferer.getDestinationCapabilities().getHostName(),dest,pluginInvocations);
		transferer.localFile(path);
		return doTheTransfer(dest, pluginInvocations);
	}
	
	@Synchronized("transferer")
	public TransferResult localFile(File file,Destination dest,Set<PluginInvocation> pluginInvocations) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		if(transferer==null) throw new RuntimeException("Transferer not set, please set destination before trying to transfer");
		log.debug("Sending local file {} to {} : {} - {}",file.getAbsolutePath(),transferer.getDestinationCapabilities().getHostName(),dest,pluginInvocations);
		transferer.localFile(file);
		return doTheTransfer(dest, pluginInvocations);
	}
	
	@Synchronized("transferer")
	public TransferResult httpSource(String url,Destination dest,Set<PluginInvocation> pluginInvocations) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		if(transferer==null) throw new RuntimeException("Transferer not set, please set destination before trying to transfer");
		log.debug("Passed url string : "+url);
		try{
			return this.httpSource(new URL(url),dest,pluginInvocations);
		}catch(MalformedURLException e){
			throw new InvalidSourceException("Invalid url : "+url);
		}
	}
	
	@Synchronized("transferer")
	public TransferResult httpSource(URL url,Destination dest,Set<PluginInvocation> pluginInvocations) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		if(transferer==null) throw new RuntimeException("Transferer not set, please set destination before trying to transfer");
		log.debug("Sending from url {} to {} : {} - {}",url,transferer.getDestinationCapabilities().getHostName(),dest,pluginInvocations);
		transferer.fromURL(url);
		return doTheTransfer(dest, pluginInvocations);
	}
	
	@Synchronized("transferer")
	public TransferResult storageId(String id,Destination dest,Set<PluginInvocation> pluginInvocations) throws InvalidSourceException, SourceNotSetException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		if(transferer==null) throw new RuntimeException("Transferer not set, please set destination before trying to transfer");
		log.debug("Sending from storage id {} to {} : {} - {}",id,transferer.getDestinationCapabilities().getHostName(),dest,pluginInvocations);
		transferer.storageFileId(id);
		return doTheTransfer(dest, pluginInvocations);
	}
	
	private TransferResult doTheTransfer(Destination dest,Set<PluginInvocation> pluginInvocations) throws SourceNotSetException, InvalidSourceException, FailedTransferException, InitializationException, InvalidDestinationException, DestinationNotSetException{
		try{
			transferer.setDestination(dest);
			transferer.setInvocations(pluginInvocations);
			return transferer.transfer();
		}finally{
			transferer.reset();
		}
	}
}
