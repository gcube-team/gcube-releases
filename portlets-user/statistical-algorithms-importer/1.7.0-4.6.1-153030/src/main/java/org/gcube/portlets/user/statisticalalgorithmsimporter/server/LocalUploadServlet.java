/**
 * 
 */
package org.gcube.portlets.user.statisticalalgorithmsimporter.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.CodeFileUploadSession;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.FileUploadListener;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.file.FileUtil;
import org.gcube.portlets.user.statisticalalgorithmsimporter.server.util.ServiceCredentials;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.Constants;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadMonitor;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.file.FileUploadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.allen_sauer.gwt.log.client.Log;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class LocalUploadServlet extends HttpServlet {

	protected static Logger logger = LoggerFactory
			.getLogger(LocalUploadServlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4197748678713054285L;

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		logger.trace("Post");

		HttpSession session = request.getSession();

		if (session == null) {
			logger.error("Error getting the upload session, no session valid found: "
					+ session);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR-Error getting the user session, no session found"
							+ session);
			return;
		}
		logger.info("Code Import session id: " + session.getId());

		ServiceCredentials serviceCredentials;

		String scopeGroupId = request.getHeader(Constants.CURR_GROUP_ID);
		if (scopeGroupId == null || scopeGroupId.isEmpty()) {
			scopeGroupId = request.getParameter(Constants.CURR_GROUP_ID);
			if (scopeGroupId == null || scopeGroupId.isEmpty()) {
				logger.error("CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: "
						+ scopeGroupId);
				throw new ServletException(
						"CURR_GROUP_ID is null, it is a mandatory parameter in custom servlet: "
								+ scopeGroupId);
			}
		}

		try {
			serviceCredentials = SessionUtil.getServiceCredentials(request,
					scopeGroupId);

		} catch (Exception e) {
			logger.error(
					"Error retrieving credentials:" + e.getLocalizedMessage(),
					e);
			throw new ServletException(e.getLocalizedMessage());
		}

		CodeFileUploadSession fileUploadSession = new CodeFileUploadSession();
		FileUploadMonitor fileUploadMonitor = new FileUploadMonitor();

		
		String id = UUID.randomUUID().toString();
		logger.info("Upload Id: " + id);
		fileUploadSession.setId(id);
		fileUploadSession.setFileUploadState(FileUploadState.STARTED);
		// fileUploadSession.setCsvImportMonitor(csvImportMonitor);
		SessionUtil.setFileUploadMonitor(request, serviceCredentials,
				fileUploadMonitor);

		try {
			SessionUtil.setCodeFileUploadSession(request, serviceCredentials,
					fileUploadSession);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw new ServletException(e.getLocalizedMessage());
		}

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		FileUploadListener uploadListener = new FileUploadListener(
				fileUploadMonitor);
		upload.setProgressListener(uploadListener);

		FileItem uploadItem = null;
		Log.info("Start upload file ");
		try {
			List items = upload.parseRequest(request);
			Iterator it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (!item.isFormField()
						&& Constants.FILE_UPLOADED_FIELD.equals(item
								.getFieldName())) {
					uploadItem = item;
				}
			}
		} catch (FileUploadException e) {
			try {
				FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(
						request, serviceCredentials);
				fum.setFailed("An error occured elaborating the HTTP request",
						FileUtil.exceptionDetailMessage(e));
				SessionUtil.setFileUploadMonitor(request, serviceCredentials,
						fum);
				fileUploadSession.setFileUploadState(FileUploadState.FAILED);

				SessionUtil.setCodeFileUploadSession(request,
						serviceCredentials, fileUploadSession);
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
				throw new ServletException(e1.getLocalizedMessage());
			}
			logger.error("Error processing request in upload servlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"ERROR-Error during request processing: " + e.getMessage());
			return;
		}

		if (uploadItem == null) {
			try {
				FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(
						request, serviceCredentials);
				fum.setFailed(
						"An error occured elaborating the HTTP request: No file found",
						"Upload request without file");
				SessionUtil.setFileUploadMonitor(request, serviceCredentials,
						fum);
				fileUploadSession.setFileUploadState(FileUploadState.FAILED);

				SessionUtil.setCodeFileUploadSession(request,
						serviceCredentials, fileUploadSession);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
				throw new ServletException(e.getLocalizedMessage());
			}
			logger.error("Error processing request in upload servlet: No file to upload");
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"No file to upload");
			return;
		}

		String contentType = uploadItem.getContentType();

		logger.trace("ContentType: " + contentType);

		try {
			FileUtil.setImportCodeFile(fileUploadSession,
					uploadItem.getInputStream(), uploadItem.getName(),
					contentType);
		} catch (Exception e) {
			try {
				FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(
						request, serviceCredentials);
				fum.setFailed("An error occured elaborating the file",
						FileUtil.exceptionDetailMessage(e));
				SessionUtil.setFileUploadMonitor(request, serviceCredentials,
						fum);
				fileUploadSession.setFileUploadState(FileUploadState.FAILED);

				SessionUtil.setCodeFileUploadSession(request,
						serviceCredentials, fileUploadSession);
			} catch (Exception e1) {
				logger.error(e1.getLocalizedMessage(), e1);
				throw new ServletException(e1.getLocalizedMessage());
			}
			logger.error("Error elaborating the stream", e);
			uploadItem.delete();
			response.getWriter().write("ERROR-" + e.getMessage());
			return;
		}

		uploadItem.delete();

		logger.trace("changing state");
		try {
			FileUploadMonitor fum = SessionUtil.getFileUploadMonitor(request,
					serviceCredentials);
			fum.setState(FileUploadState.COMPLETED);
			SessionUtil.setFileUploadMonitor(request, serviceCredentials, fum);

			SessionUtil.setCodeFileUploadSession(request, serviceCredentials,
					fileUploadSession);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServletException(e.getLocalizedMessage());
		}
		response.getWriter().write("OK");
	}

}
