package org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.data.publishing.gCatFeeder.service.engine.Infrastructure;
import org.gcube.data.publishing.gCatFeeder.service.engine.Storage;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InternalError;
import org.gcube.data.publishing.gCatFeeder.service.model.reports.ExecutionReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageImpl implements Storage{

	private static final Logger log= LoggerFactory.getLogger(StorageImpl.class);

	protected static ObjectMapper mapper=new ObjectMapper();
	
	@Inject
	private Infrastructure infra;
	
	private final IClient getClient(){
		return new StorageClient("data-publishing", "gcat-feeder", infra.getClientID(infra.getCurrentToken()), AccessType.SHARED, MemoryType.PERSISTENT).getClient();
	}
	
	
	//return Id
	private final String putOntoStorage(File source) throws RemoteBackendException, FileNotFoundException{
		IClient client=getClient();
		log.debug("Uploading local file "+source.getAbsolutePath());
		String id=client.put(true).LFile(new FileInputStream(source)).RFile(UUID.randomUUID().toString());
		log.debug("File uploaded. ID : "+id);
		String toReturn= client.getHttpUrl().RFile(id);
		log.debug("Created URL : "+toReturn);
		return toReturn;
	}
	
	
	protected File asFile(ExecutionReport report) throws InternalError {
		try {
			File f=File.createTempFile("report", ".json");
			String serialized=mapper.writeValueAsString(report);
			 
			Files.write(Paths.get(f.getAbsolutePath()), serialized.getBytes());
			
			return f;
		} catch (IOException e) {
			throw new InternalError("Unable to rite report : ",e);
		}
	}
	
	@Override
	public String storeReport(ExecutionReport report) throws InternalError {		
		try {
			return putOntoStorage(asFile(report));
		} catch (RemoteBackendException | FileNotFoundException e) {
			throw new InternalError("Unable to store report ",e);
		}
	}
	
}
