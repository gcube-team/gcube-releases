/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.server.local;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.gcube.portlets.widgets.file_dw_import_wizard.client.progress.OperationState;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessionManager;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessions;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportStatus;


public class FileUploadServlet extends HttpServlet {

//	protected static Logger logger = LoggerFactory.getLogger(LocalUploadServlet.class);
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4197748678713054285L;
	 Logger logger = Logger.getLogger("");
	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.log(Level.SEVERE,"Post");

		String sessionId = (String) request.getParameter("sessionId");

		logger.log(Level.SEVERE,"sessionId: "+sessionId);

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		ImportSessions importSession = ImportSessionManager.getInstance().getSession(sessionId);
		
		if (importSession == null)
		{
			logger.log(Level.SEVERE,"Error getting the upload session, no session found for id "+sessionId);
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
			importSession.setStatus(ImportStatus.FAILED);
			logger.log(Level.SEVERE,"Error processing request in upload servlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR-Error during request processing: "+e.getMessage());
			return;
		}
		
		if (uploadItem == null) {
			importSession.getUploadProgress().setFailed("An error occured elaborating the HTTP request: No file found", "Upload request without file");
			importSession.setStatus(ImportStatus.FAILED);
//			logger.error("Error processing request in upload servlet: No file to upload");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No file to upload");
			return;
		}
		
		String contentType = uploadItem.getContentType();
		

			
		try {
		    logger.log(Level.SEVERE, "call setImportFile");
			Util.setImportFile(importSession, uploadItem.getInputStream(), uploadItem.getName(), contentType);
		} catch (Exception e) {
		    logger.log(Level.SEVERE, "Exception from import."+ e);

			importSession.getUploadProgress().setFailed("An error occured elaborating the file", Util.exceptionDetailMessage(e));
			importSession.setStatus(ImportStatus.FAILED);
//			logger.error("Error elaborating the stream", e);
			uploadItem.delete();
			response.getWriter().write("ERROR-"+e.getMessage());
			return;
		}
		
		uploadItem.delete();
		
		response.getWriter().write("OK");
		
//		logger.trace("changing state");
		importSession.getUploadProgress().setState(OperationState.COMPLETED);
	}

}
