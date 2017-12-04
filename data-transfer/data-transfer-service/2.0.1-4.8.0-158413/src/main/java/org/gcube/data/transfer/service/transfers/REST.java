package org.gcube.data.transfer.service.transfers;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

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
import org.gcube.data.transfer.service.transfers.engine.RequestManager;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import lombok.extern.slf4j.Slf4j;

@Path(ServiceConstants.REST_SERVLET_NAME)
@Slf4j
public class REST {


	@Inject
	RequestManager requests;



	@QueryParam(ServiceConstants.DESTINATION_FILE_NAME) String destinationFileName;
	@QueryParam(ServiceConstants.CREATE_DIRS) @DefaultValue("true") Boolean createDirs;
	@QueryParam(ServiceConstants.ON_EXISTING_FILE) @DefaultValue("ADD_SUFFIX") DestinationClashPolicy onExistingFile;
	@QueryParam(ServiceConstants.ON_EXISTING_DIR) @DefaultValue("APPEND") DestinationClashPolicy onExistingDirectory;
	@QueryParam(ServiceConstants.SOURCE_ID) String sourceID;

	@FormDataParam(ServiceConstants.MULTIPART_FILE) InputStream uploadedFile;
	@FormDataParam(ServiceConstants.MULTIPART_FILE) FormDataContentDisposition uploadedFileDetails;
	@FormDataParam("plugin-invocations") Set<PluginInvocation> pluginInvocations;
	
	
	@POST
	@Path("/{method}/{destinationId}/{subPath: .*}")
	@Consumes(MediaType.WILDCARD)
	@Produces(MediaType.APPLICATION_JSON)
	
	public Object serveFileUpload(@PathParam("method") String methodString, 
			@PathParam("destinationId") String destinationID, @PathParam("subPath") String subPath ){
		try{
			log.debug("Plugin invocation set : {} ",pluginInvocations);
			TransferRequest request=formRequestFromREST(methodString, destinationID, subPath,pluginInvocations);
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



	private TransferRequest formRequestFromREST(String methodString,String destinationID,String subPath, Set<PluginInvocation> pluginInvocations){
		log.info("Creating TransferRequest from REST invocation method : {}, dest ID {}, sub Path {} ",methodString,destinationID,subPath);
		TransferMethod method=null;
		try{
			method=TransferMethod.valueOf(methodString);
		}catch (Throwable t) {
			throw new WebApplicationException("Invalid selected method "+methodString,Status.BAD_REQUEST);}

		
		
		
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
			destination.setDestinationFileName(destinationFileName==null?sourceID:destinationFileName);
			try{
				HttpDownloadSettings settings=new HttpDownloadSettings(new URL(sourceID), new HttpDownloadOptions());
				resultingRequest.setSettings(settings);
				break;
			}catch(MalformedURLException e){
				throw new WebApplicationException("Source "+sourceID+" is not a valid URL.",Status.BAD_REQUEST);
			}
		}
		default: throw new WebApplicationException("Unsupported selected method "+methodString,Status.BAD_REQUEST);
		}
		return resultingRequest;
	}

}
