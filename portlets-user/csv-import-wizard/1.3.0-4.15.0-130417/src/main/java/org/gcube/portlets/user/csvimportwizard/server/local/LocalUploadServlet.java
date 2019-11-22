/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.server.local;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationState;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSession;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportSessionManager;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVImportStatus;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class LocalUploadServlet extends HttpServlet {

	protected static Logger logger = LoggerFactory.getLogger(LocalUploadServlet.class);
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4197748678713054285L;

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.trace("Post");

		String sessionId = (String) request.getParameter("sessionId");

		logger.trace("sessionId: "+sessionId);

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		CSVImportSession importSession = CSVImportSessionManager.getInstance().getSession(sessionId);
		
		if (importSession == null)
		{
			logger.error("Error getting the upload session, no session found for id "+sessionId);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR-Error getting the user session, no session found for id "+sessionId);
			return ;
		}
		
		LocalUploadListener uploadListener = new LocalUploadListener(importSession.getUploadProgress());
		upload.setProgressListener(uploadListener);
		
		FileItem uploadItem = null;
		
		try {
			List items = upload.parseRequest(request);
			Iterator it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField()	&& "uploadFormElement".equals(item.getFieldName())) {
					uploadItem = item;
				}
			}
		} catch (FileUploadException e) {
			importSession.getUploadProgress().setFailed("An error occured elaborating the HTTP request", Util.exceptionDetailMessage(e));
			importSession.setStatus(CSVImportStatus.FAILED);
			logger.error("Error processing request in upload servlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR-Error during request processing: "+e.getMessage());
			return;
		}
		
		if (uploadItem == null) {
			importSession.getUploadProgress().setFailed("An error occured elaborating the HTTP request: No file found", "Upload request without file");
			importSession.setStatus(CSVImportStatus.FAILED);
			logger.error("Error processing request in upload servlet: No file to upload");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No file to upload");
			return;
		}
		
		String contentType = uploadItem.getContentType();
		
		logger.trace("ContentType: "+contentType);
		
		/* TODO is necessary? 
		 	if (contentType.equals("application/octet-stream")){
		 
			logger.trace("We try to guess the content using the extension");
			String name = uploadItem.getName();
			
			int lastIndex = name.lastIndexOf('.');
			if (lastIndex>=0){
				String extension = name.substring(lastIndex+1);
				logger.trace("extension "+extension);
				contentType = MimeTypeUtil.getMimeType(extension);
				logger.trace("new contenttype: "+contentType);
			} else logger.trace("No extensions found");
		}
		*/
			
		try {
			Util.setImportFile(importSession, uploadItem.getInputStream(), uploadItem.getName(), contentType);
		} catch (Exception e) {
			importSession.getUploadProgress().setFailed("An error occured elaborating the file", Util.exceptionDetailMessage(e));
			importSession.setStatus(CSVImportStatus.FAILED);
			logger.error("Error elaborating the stream", e);
			uploadItem.delete();
			response.getWriter().write("ERROR-"+e.getMessage());
			return;
		}
		
		uploadItem.delete();
		
		response.getWriter().write("OK");
		
		logger.trace("changing state");
		importSession.getUploadProgress().setState(OperationState.COMPLETED);
	}

}
