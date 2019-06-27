package org.gcube.data.transfer.service.transfers;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.data.transfer.model.DeletionReport;
import org.gcube.data.transfer.model.Destination;
import org.gcube.data.transfer.model.DestinationClashPolicy;
import org.gcube.data.transfer.model.PluginInvocation;
import org.gcube.data.transfer.model.ServiceConstants;
import org.gcube.data.transfer.model.TransferRequest;
import org.gcube.data.transfer.model.TransferTicket;
import org.gcube.data.transfer.model.options.FileUploadOptions;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.TransferOptions.TransferMethod;
import org.gcube.data.transfer.model.settings.FileUploadSettings;
import org.gcube.data.transfer.model.settings.HttpDownloadSettings;
import org.gcube.data.transfer.service.transfers.engine.AccountingManager;
import org.gcube.data.transfer.service.transfers.engine.PersistenceProvider;
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.gcube.data.transfer.service.transfers.engine.faults.DestinationAccessException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import lombok.extern.slf4j.Slf4j;

@Path(ServiceConstants.REST_SERVLET_NAME+"/{destinationId}/{subPath : \\S*}")
@Slf4j
public class REST {


	@PathParam("destinationId") String destinationID;
	@PathParam("subPath") String subPath;



	@Inject
	RequestManager requests;
	@Inject 
	PersistenceProvider persistence;

	@POST	
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)	
	public Object serveFileUpload(@QueryParam("method") @DefaultValue("FileUpload")String methodString,
			@QueryParam(ServiceConstants.DESTINATION_FILE_NAME) String destinationFileName,
			@QueryParam(ServiceConstants.CREATE_DIRS) @DefaultValue("true") Boolean createDirs,
			@QueryParam(ServiceConstants.ON_EXISTING_FILE) @DefaultValue("ADD_SUFFIX") DestinationClashPolicy onExistingFile,
			@QueryParam(ServiceConstants.ON_EXISTING_DIR) @DefaultValue("APPEND") DestinationClashPolicy onExistingDirectory,
			@QueryParam(ServiceConstants.SOURCE_ID) String sourceID,

			@FormDataParam(ServiceConstants.MULTIPART_FILE) InputStream uploadedFile,
			@FormDataParam(ServiceConstants.MULTIPART_FILE) FormDataContentDisposition uploadedFileDetails,
			@FormDataParam("plugin-invocations") Set<PluginInvocation> pluginInvocations){
		try{
			String pathString="<"+destinationID+">/"+subPath;			
			log.info("Received POST request at {} ",pathString);
			log.debug("Plugin invocation set : {} ",pluginInvocations);
			TransferRequest request=formRequestFromREST(methodString, destinationID, subPath,pluginInvocations,
					createDirs,onExistingFile,onExistingDirectory,
					uploadedFile,uploadedFileDetails,destinationFileName,sourceID);
			log.info("Received REST Request {} ",request);


			TransferTicket ticket=requests.put(request);

			if(ticket.getSettings().getOptions().getMethod().equals(TransferMethod.FileUpload))
			{
				log.debug("Resulting sync ticket {} ",ticket);				

				return ticket;

			}
			else{
				return ticket;
			}
		}catch(WebApplicationException e){
			log.error("Unable to serve request",e);
			throw e;
		}
	}



	private TransferRequest formRequestFromREST(String methodString,String destinationID,String subPath, Set<PluginInvocation> pluginInvocations,
			Boolean createDirs, DestinationClashPolicy onExistingFile, 
			DestinationClashPolicy onExistingDirectory,
			InputStream uploadedFile,
			FormDataContentDisposition uploadedFileDetails,
			String destinationFileName,
			String sourceID){
		log.info("Creating TransferRequest from REST invocation method : {}, dest ID {}, sub Path {} ",methodString,destinationID,subPath);
		TransferMethod method=null;
		try{
			method=TransferMethod.valueOf(methodString);
		}catch (Throwable t) {
			throw new WebApplicationException("Invalid selected method "+methodString,t,Status.BAD_REQUEST);}




		Destination destination=new Destination();
		destination.setCreateSubfolders(createDirs);
		destination.setPersistenceId(destinationID);
		destination.setSubFolder(subPath);
		destination.setOnExistingSubFolder(onExistingDirectory);
		destination.setOnExistingFileName(onExistingFile);



		TransferRequest resultingRequest=new TransferRequest();			
		resultingRequest.setDestinationSettings(destination);		
		resultingRequest.setPluginInvocations(pluginInvocations);
		switch(method){
		case FileUpload : {
			//			if(destinationFileName==null) throw new WebApplicationException("Parameter "+ServiceConstants.DESTINATION_FILE_NAME+" is mandatory.",Status.BAD_REQUEST);				
			if(uploadedFileDetails==null) throw new WebApplicationException("Missing multipart  "+ServiceConstants.MULTIPART_FILE+" details.",Status.BAD_REQUEST);
			if(uploadedFile==null) throw new WebApplicationException("Missing multipart  "+ServiceConstants.MULTIPART_FILE+" stream.",Status.BAD_REQUEST);
			destination.setDestinationFileName(destinationFileName!=null?destinationFileName:uploadedFileDetails.getFileName());				
			FileUploadSettings uploadSettings=new FileUploadSettings(uploadedFile,new FileUploadOptions());
			resultingRequest.setSettings(uploadSettings);
			break;
		}
		case DirectTransfer :{
			throw new WebApplicationException("Unsupported selected method "+methodString,Status.BAD_REQUEST);
		}
		case HTTPDownload :{
			if(sourceID==null) throw new WebApplicationException("Parameter "+ServiceConstants.SOURCE_ID+" is mandatory.",Status.BAD_REQUEST);
			if(destinationFileName!=null)destination.setDestinationFileName(destinationFileName);
			try{
				HttpDownloadSettings settings=new HttpDownloadSettings(new URL(sourceID), new HttpDownloadOptions());
				resultingRequest.setSettings(settings);
				break;
			}catch(MalformedURLException e){
				throw new WebApplicationException("Source "+sourceID+" is not a valid URL.",e,Status.BAD_REQUEST);
			}
		}
		default: throw new WebApplicationException("Unsupported selected method "+methodString,Status.BAD_REQUEST);
		}
		return resultingRequest;
	}


	
	@GET
	@Produces("*/*")
	public Response getFile(
			@QueryParam("descriptor") @DefaultValue("false") Boolean getDescriptor) {	
		String pathString="<"+destinationID+">/"+subPath;		
		log.info("Received GET request at {} , descriptor option is {} ",pathString,getDescriptor);
		long volume=0l;
		boolean success=true;
		String path=destinationID+":"+subPath;
		String mimeType="N/A";
		try{
			if(getDescriptor) return Response.ok(persistence.getDescriptor(destinationID, subPath), MediaType.APPLICATION_JSON_TYPE).build();
			
						
			
			
			File persisted= persistence.getPersistedFile(destinationID, subPath);
			if(!persisted.exists()) throw new WebApplicationException("File "+pathString+" doesn't exists.",Status.NOT_FOUND);
			if(persisted.isDirectory()) throw new WebApplicationException("The selected path "+pathString+" is a directory.",Status.BAD_REQUEST);
			mimeType= new MimetypesFileTypeMap().getContentType(persisted);
			volume=persisted.length();
			
			return Response.ok(persisted, mimeType).build();
		}catch(DestinationAccessException e) {
			success=false;
			throw new WebApplicationException("Unable to access selected path "+pathString,e,Status.INTERNAL_SERVER_ERROR);
		}finally {
			if(!getDescriptor)
				account(true,volume,success,path,mimeType);
		}
	}


	@DELETE	
	@Produces(MediaType.APPLICATION_JSON)
	public DeletionReport deleteFile() {	
		String pathString="<"+destinationID+">/"+subPath;		
		log.info("Received DELETE request at {}",pathString);
	
		long volume=0l;
		boolean success=true;
		String path=destinationID+":"+subPath;
		String mimeType="N/A";
		
		try{
			File theFile=persistence.getPersistedFile(destinationID, subPath);
			volume=theFile.length();
			mimeType= new MimetypesFileTypeMap().getContentType(theFile);
			
			
			return persistence.delete(destinationID, subPath);
		}catch(DestinationAccessException e) {			
			throw new WebApplicationException("Unable to access selected path "+pathString,e,Status.INTERNAL_SERVER_ERROR);
		}finally {
			account(false,volume,success,path,mimeType);
		}
	}
	
	
	private static void account(boolean read,long volume,boolean success,String path,String mimetype) {
		AccountingManager manager=AccountingManager.get();
		String id=manager.createNewRecord();
		if(read) manager.setRead(id);
		else manager.setDelete(id);
		
		manager.setSuccessful(id, success);
		manager.setVolumne(id, volume);
		manager.setMimeType(id, mimetype);
		manager.setResourceURI(id, path);
		manager.account(id);
	}
}
