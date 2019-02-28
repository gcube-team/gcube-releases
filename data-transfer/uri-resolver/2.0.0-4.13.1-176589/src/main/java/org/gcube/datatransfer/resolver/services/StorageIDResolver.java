package org.gcube.datatransfer.resolver.services;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.resolver.ConstantsResolver;
import org.gcube.datatransfer.resolver.services.error.ExceptionManager;
import org.gcube.datatransfer.resolver.util.SingleFileStreamingOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class StorageIDResolver.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Dec 14, 2018
 */
@Path("storage")
public class StorageIDResolver {

	/**
	 *
	 */
	protected static final String STORAGEID_RESOLVER = "storageid-resolver";
	private static final String STORAGE_ID = "storage-id";

	private static Logger logger = LoggerFactory.getLogger(StorageIDResolver.class);

	private static String help = "https://wiki.gcube-system.org/gcube/URI_Resolver#STORAGE-ID_Resolver";


	/**
	 * Gets the storage id.
	 *
	 * @param req the req
	 * @param storageId the storage id
	 * @param fileName the file name
	 * @param contentType the content type
	 * @param validation the validation
	 * @return the storage id
	 * @throws WebApplicationException the web application exception
	 */
	@GET
	@Path("{storage-id}")
	public Response getStorageId(@Context HttpServletRequest req,
		@PathParam(STORAGE_ID) String storageId,
		@QueryParam(ConstantsResolver.QUERY_PARAM_FILE_NAME) String fileName,
		@QueryParam(ConstantsResolver.QUERY_PARAM_CONTENT_TYPE) String contentType,
		@QueryParam(ConstantsResolver.QUERY_PARAM_VALIDATION) Boolean validation) throws WebApplicationException {

		logger.info(this.getClass().getSimpleName()+" GET starts...");

		try{
			if(storageId==null || storageId.isEmpty()){
				logger.error(STORAGE_ID+" not found");
				throw ExceptionManager.badRequestException(req,  "Missing mandatory path parameter "+STORAGE_ID, StorageIDResolver.class, help);
			}
			return resolveStorageId(req, storageId, fileName, contentType, validation);
		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the Storage ID: "+storageId+". Please, contact the support!";
				if(e.getCause()!=null)
					error+="\n\nCaused: "+e.getCause().getMessage();
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), help);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}


	/**
	 * Resolve storage id.
	 *
	 * @param httpRequest the http request
	 * @param storageId the storage id
	 * @param fileName the file name
	 * @param contentType the content type
	 * @param validation the validation
	 * @return the response
	 * @throws Exception the exception
	 */
	protected static Response resolveStorageId(HttpServletRequest httpRequest, String storageId, String fileName, String contentType, Boolean validation) throws Exception{

		logger.info("storage-id: "+storageId+", fileName: "+fileName+", contentType: "+contentType+", validation: "+validation);

		//Checking mandatory parameter storageId
		if (storageId == null || storageId.isEmpty()) {
			logger.error("storageId not found");
			throw ExceptionManager.badRequestException(httpRequest,  "Missing mandatory path parameter "+STORAGE_ID, StorageIDResolver.class, help);
		}

		StorageClient client = getStorageClientInstance(storageId);
		String toSEID = null;
		IClient iClient = null;

		try{
			iClient = client.getClient();
			toSEID = iClient.getId(storageId); //to Storage Encrypted ID
			logger.info("Decoded ID"+" = "+ toSEID);
		}catch(Exception e){
			logger.error("Storage Client Exception when getting file from storage: ", e);
			throw ExceptionManager.notFoundException(httpRequest, "Storage Client Exception when getting file from storage with id: "+storageId, StorageIDResolver.class, help);
		}

		if(toSEID==null || toSEID.isEmpty()){
			logger.error("Decrypted id for storageId: "+storageId +" is null or empty!");
			throw ExceptionManager.notFoundException(httpRequest, "Error on decrypting the "+STORAGE_ID+ " '"+storageId+"'. Is it a valid id?", StorageIDResolver.class, help);
		}

		long size = iClient.getSize().RFileById(toSEID);

		try{

			MyFile file = iClient.getMetaFile().RFile(toSEID);
			logger.debug("MetaFile retrieved from storage? "+ (file!=null));
			//Reading the fileName from Storage Metadata only if the passed fileName is null
			if(fileName==null || fileName.isEmpty())
				fileName= file.getName();

			//Reading the contentType from Storage Metadata only if the passed contentType is null
			if(contentType==null || contentType.isEmpty())
				contentType = file.getMimeType();

		}catch (Exception e) {
			logger.warn("Error when getting file metadata from storage, printing this warning and trying to continue..", e);
		}

		fileName = fileName==null || fileName.isEmpty()?ConstantsResolver.DEFAULT_FILENAME_FROM_STORAGE_MANAGER:fileName;
		logger.info("filename retrieved is {}",fileName);

		contentType = contentType==null || contentType.isEmpty()?ConstantsResolver.DEFAULT_CONTENTTYPE_UNKNOWN_UNKNOWN:contentType;
		logger.info("contentType used is {}",contentType);

		//Building the response
		InputStream streamToWrite=iClient.get().RFileAsInputStream(toSEID); //input stream
		StreamingOutput so = new SingleFileStreamingOutput(streamToWrite);

		ResponseBuilder response = Response
				.ok(so)
				.header(ConstantsResolver.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"")
				.header(ConstantsResolver.CONTENT_LENGTH, size);

		if (contentType!= null)
			response.header("Content-Type",contentType);

		return response.build();

	}


	/**
	 * Http do head.
	 *
	 * @param req the http request
	 * @param storageId the storage id
	 * @param hproxycheck the hproxycheck
	 * @return the response
	 * @throws WebApplicationException the web application exception
	 */
	@HEAD
	@Path("{storage-id}")
	public Response httpDoHead(@Context HttpServletRequest req,
		@PathParam(STORAGE_ID) String storageId,
		@QueryParam(ConstantsResolver.HPC) Boolean hproxycheck) throws WebApplicationException {

		logger.info(this.getClass().getSimpleName()+" HEAD starts...");

		try{
			//THIS IS FOR HPROXY CHECK
			if(hproxycheck==null ||  hproxycheck){
				logger.trace("returning status 200 for Hproxy check");
				ResponseBuilder response = Response.status(HttpStatus.SC_OK);
				return response.build();
			}

			return validationPayload(req, storageId);

		}catch (Exception e) {

			if(!(e instanceof WebApplicationException)){
				//UNEXPECTED EXCEPTION managing it as WebApplicationException
				String error = "Error occurred on resolving the Storage ID: "+storageId+". Please, contact the support!";
				if(e.getCause()!=null)
					error+="\n\nCaused: "+e.getCause().getMessage();
				throw ExceptionManager.internalErrorException(req, error, this.getClass(), help);
			}
			//ALREADY MANAGED AS WebApplicationException
			logger.error("Exception:", e);
			throw (WebApplicationException) e;
		}
	}


	/**
	 * Validation payload.
	 *
	 * @param httpRequest the http request
	 * @param storageId the storage id
	 * @return the response
	 * @throws Exception the exception
	 */
	protected Response validationPayload(HttpServletRequest httpRequest, String storageId) throws Exception{

		//Checking mandatory parameter storageId
		if (storageId == null || storageId.isEmpty()) {
			logger.warn("storageId not found");
			throw ExceptionManager.badRequestException(httpRequest, "Storage Client Exception when getting file from storage with id: "+storageId, this.getClass(), help);
		}
		StorageClient client = getStorageClientInstance(storageId);
		String toSEID = null;
		IClient iClient = null;
		try{
			iClient = client.getClient();
			toSEID = iClient.getId(storageId); //to Storage Encrypted ID
			logger.debug("Decoded ID"+" = "+ toSEID);
		}catch(Exception e){
			logger.error("Storage Client Exception when getting file from storage: ", e);
			throw ExceptionManager.internalErrorException(httpRequest, "Storage Client Exception when getting file from storage with id: "+storageId, StorageIDResolver.class, help);
		}

		if(toSEID==null || toSEID.isEmpty()){
			logger.error("Decrypted id for storageId: "+storageId +" is null or empty!");
			throw ExceptionManager.notFoundException(httpRequest, "Error on decrypting the "+STORAGE_ID+ " '"+storageId+"'. Is it a valid id?", StorageIDResolver.class, help);
		}


		//Building the response
		InputStream streamToWrite=iClient.get().RFileAsInputStream(toSEID); //input stream

		byte[] bytes = new byte[1]; //1B
		int c;
		ResponseBuilder response = null;
		try {
			c = streamToWrite.read(bytes);
			logger.info(c+" byte read from InputStream");
			if(c>0){
				logger.info("at least 1 byte read, returning status 200");
				IOUtils.closeQuietly(streamToWrite);
				response = Response.status(HttpStatus.SC_OK);
			}else
				throw ExceptionManager.notFoundException(httpRequest, "The file with id: "+storageId+" is missing in the storage", StorageIDResolver.class, help);
		}
		catch (IOException e2) {
			logger.error("Error on validating the file: ",e2);
			throw ExceptionManager.internalErrorException(httpRequest, "Error on validating the file with id: "+storageId, StorageIDResolver.class, help);
		}

		if(response==null)
			throw ExceptionManager.internalErrorException(httpRequest, "Error on validating the file with id: "+storageId, StorageIDResolver.class, help);

		return response.build();

	}


	/**
	 * Gets the storage client instance.
	 *
	 * @param storageId the storage id
	 * @return the storage client instance
	 * @throws Exception the exception
	 */
	protected static StorageClient getStorageClientInstance(String storageId) throws Exception{

		MemoryType memory=null;
		if(storageId.endsWith(org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants.VOLATILE_URL_IDENTIFICATOR)){
			memory=MemoryType.VOLATILE;
			storageId=storageId.replace(org.gcube.contentmanagement.blobstorage.transport.backend.util.Costants.VOLATILE_URL_IDENTIFICATOR, "");
		}

		StorageClient client;
		if(memory==null)
			client=new StorageClient(StorageIDResolver.class.getName(), StorageIDResolver.class.getSimpleName(), STORAGEID_RESOLVER, AccessType.PUBLIC);
		else
			client=new StorageClient(StorageIDResolver.class.getName(), StorageIDResolver.class.getSimpleName(), STORAGEID_RESOLVER, AccessType.PUBLIC, memory);

		return client;
	}




}
