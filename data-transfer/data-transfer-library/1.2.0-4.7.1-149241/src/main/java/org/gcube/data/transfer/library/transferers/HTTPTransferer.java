package org.gcube.data.transfer.library.transferers;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.data.transfer.library.client.Client;
import org.gcube.data.transfer.library.faults.InitializationException;
import org.gcube.data.transfer.library.faults.InvalidSourceException;
import org.gcube.data.transfer.library.model.LocalSource;
import org.gcube.data.transfer.library.model.StorageSource;
import org.gcube.data.transfer.library.model.URLSource;
import org.gcube.data.transfer.library.utils.StorageUtils;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;

@Slf4j
public class HTTPTransferer extends Transferer {

	HTTPTransferer(Client client) {
		super(client);		
	}

	private URL link;

	private String toDeleteStorageId=null;
	
	@Override
	protected TransferRequest prepareRequest()throws InitializationException {		
		return new TransferRequest("", new HttpDownloadSettings(link, HttpDownloadOptions.DEFAULT),this.destination,this.invocations);		
	}


	private URL getHttpLink() throws RemoteBackendException, FileNotFoundException, InvalidSourceException, MalformedURLException{
		if(source instanceof LocalSource){
			toDeleteStorageId=StorageUtils.putOntoStorage(((LocalSource)source).getTheSource());
			return new URL(StorageUtils.getUrlById(toDeleteStorageId));
		}else if (source instanceof StorageSource){			
			return new URL(StorageUtils.getUrlById(((StorageSource)source).getTheSource()));
		}else if (source instanceof URLSource){
			return ((URLSource)source).getTheSource();
		}else throw new InvalidSourceException("Source cannot be handled "+source);
	}

	@Override
	protected void prepare() throws InitializationException {		
		try{
			link=getHttpLink();
			log.debug("Obtained link "+link);
			super.prepare();		
		}catch(RemoteBackendException e){
			throw new InitializationException(e);
		}catch(FileNotFoundException e){
			throw new InitializationException(e);
		}catch(InvalidSourceException e){
			throw new InitializationException(e);
		}catch (MalformedURLException e){
			throw new InitializationException(e);
		}
	}
	
	@Override
	protected void clean() {
		// TODO Auto-generated method stub
		super.clean();
	}
}
