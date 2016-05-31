package org.gcube.datatransfer.resolver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.contentmanagement.blobstorage.resource.MyFile;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.datatransfer.resolver.MultiReadHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * The Class StorageIDResolver.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 19, 2016
 */
public class StorageIDResolver extends HttpServlet {

	private static final long serialVersionUID = -5208562956923156697L;

	protected static final String SMP_ID = "smp-id";
	protected static final String VALIDATION = "validation";
	protected static final String CONTENT_TYPE = "contentType";
	protected static final String FILE_NAME = "fileName";

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(StorageIDResolver.class);

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig conf) throws ServletException {
		Handler.activateProtocol();
		super.init(conf);

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String smpID =null;
		String fileName =null;
		String contentType =null;
		boolean validatingURI = false;

//		logger.info("The http session id is: " + request.getSession().getId());

		smpID = request.getParameter(SMP_ID);

		if (smpID == null || smpID.equals("")) {
			logger.warn(SMP_ID+" not found");
			response.sendError(404);
			return;
		}

		fileName  = request.getParameter(FILE_NAME);


		if (fileName == null || fileName.equals("")) {
			logger.warn(FILE_NAME+" not found");
			fileName = null;
		}

		contentType  = request.getParameter(CONTENT_TYPE);

		if (contentType == null || contentType.equals("")) {
			logger.warn(CONTENT_TYPE+" not found");
			contentType = null;
		}

		String validation = request.getParameter(VALIDATION);
		validatingURI = Boolean.parseBoolean(validation);
		logger.info("validation? "+validatingURI);

		//we should not unescape the filename with spaces

		logger.info(SMP_ID+" = "+ smpID);
		InputStream in = null;
		String toSEID = null;

		try {
			OutputStream out = response.getOutputStream();
			StorageClient client = new StorageClient(StorageIDResolver.class.getName(), StorageIDResolver.class.getSimpleName(), StorageIDResolver.class.getName(), AccessType.PUBLIC);

			try{
				IClient icClient = client.getClient();
				toSEID = icClient.getId(smpID); //to Storage Encrypted ID
				logger.debug("Decoded ID"+" = "+ toSEID);

				if(toSEID==null){
					String error = "Decrypted id is null, thrown exception!";
					throw new Exception(error);
				}

				in=icClient.get().RFileAsInputStream(toSEID); //input stream

			}catch (Exception e) {
				response.sendError(404);
				logger.error("Storage Client Exception when getting file from storage: ", e);
				return;
			}

			//CASE InputStream NULL
			if(in==null){
				logger.error("Input stream returned from storage is null, sending status error 404");
				sendErrorQuietly(response, 404);
				return;
			}

			try{
				MyFile file = client.getClient().getMetaFile().RFile(toSEID);
				logger.debug("MetaFile retrieved from storage? "+ (file!=null));

				if(fileName==null || fileName.isEmpty()){ //filename
					fileName = file.getName();
					logger.debug("filename read from MetaFile: "+ fileName);
				}

				if(contentType==null || contentType.isEmpty()){ //mime type
					contentType = file.getMimeType();
					logger.debug("contentType read from MetaFile: "+ contentType);
				}

			}catch (Exception e) {
				logger.warn("Error when getting file metadata from storage, printing this warning and trying to continue..", e);
			}

			//VALIDATING PARAMETERS: FILENAME AND CONTENT TYPE
			if(fileName==null || fileName.isEmpty())
				fileName = ConstantsHttpResolver.DEFAULT_FILENAME_FROM_STORAGE_MANAGER;

			if(contentType==null || contentType.isEmpty())
				contentType = ConstantsHttpResolver.DEFAULT_CONTENTTYPE_UNKNOWN_UNKNOWN;

			//The filename should be a quoted string. (According to Section 19.5.1 of RFC 2616)
			//http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html#sec19.5.1
			response.setHeader(ConstantsHttpResolver.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
			response.setContentType(contentType);

			//CASE VALIDATION
			if(validatingURI){
				byte[] bytes = new byte[1]; //1B
				int c = in.read(bytes);
				logger.info(c+" byte read from InputStream");
				if(c>0){
					logger.info("at least 1 byte read, returning status 200");
					IOUtils.closeQuietly(in);
					response.setStatus(200);
					return;
				}
			}

			try {

				IOUtils.copy(in, out);

			} catch (IOException e){
				logger.warn("IOException class name: "+e.getClass().getSimpleName());
				if (e.getClass().getSimpleName().equals("ClientAbortException"))
					logger.warn("Skipping ClientAbortException: "+e.getMessage());
				else
					throw e; //Sending Exceptions

			}	catch (NullPointerException e) {
				logger.warn("NullPointerException during copy, skipping printStrackTrace");
				sendErrorQuietly(response, 404);

			}	catch (Exception e) {
				logger.error("Exception: ",e);
				sendErrorQuietly(response, 404);

			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}

		} catch (Exception e) {
			logger.error("Exception:", e);
			IOUtils.closeQuietly(in);
			sendErrorQuietly(response, 404);
			return;
		}

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		logger.info("doHead working..");

		//ADDING PARAMETER TO PERFORM ONLY VALIDATION
		Map<String, String[]> additionalParams = new HashMap<String, String[]>();
		String[] value = new String[1];
		value[0] = "true";
		additionalParams.put(VALIDATION, value);
		MultiReadHttpServletRequest request = new MultiReadHttpServletRequest(req, additionalParams);
        doGet(request, resp);
	}

	/**
	 * Send error quietly.
	 *
	 * @param response the response
	 * @param code the code
	 */
	protected void sendErrorQuietly(HttpServletResponse response, int code){

		if(response!=null){
			try {
				response.sendError(code);
				logger.info("Response sent error: "+code);
			} catch (IOException ioe) {
				 // ignore
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		doGet(request,response);
	}


}