/**
 * 
 */
package org.gcube.portlets.admin.software_upload_wizard.server.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.gcube.portlets.admin.software_upload_wizard.server.data.ImportSession;
import org.gcube.portlets.admin.software_upload_wizard.server.data.SoftwareFile;
import org.gcube.portlets.admin.software_upload_wizard.server.filetypes.FileValidationOutcome;
import org.gcube.portlets.admin.software_upload_wizard.server.filetypes.FileValidator;
import org.gcube.portlets.admin.software_upload_wizard.server.filetypes.FileValidatorException;
import org.gcube.portlets.admin.software_upload_wizard.server.importmanagers.ImportSessionManager;
import org.gcube.portlets.admin.software_upload_wizard.server.softwareprofile.Package;
import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationProgress;
import org.gcube.portlets.admin.software_upload_wizard.shared.OperationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Luigi Fortunati luigi.fortunati@isti.cnr.it
 * 
 */
@Singleton
public class FileUploadServlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(FileUploadServlet.class);

	ImportSessionManager importSessionManager;

	@Inject
	public FileUploadServlet(ImportSessionManager importSessionManager) {
		this.importSessionManager = importSessionManager;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4197748678713054285L;

	@SuppressWarnings("rawtypes")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		log.debug("Invoked HTTP POST on upload servlet");

		// Retrieve import session data, return error if session is not found.
		ImportSession importSession = importSessionManager.getImportSession();
		String fileTypeName = request.getParameterValues("fileType")[0];
		String packageId = request.getParameterValues("packageId")[0];
		Package package_ = null;
		try {
			package_ = importSession.getServiceProfile().getService().getPackage(UUID.fromString(packageId));
		} catch (Exception e) {
			handleError(response, "Error occurred while retrieving package in session. " + e.getMessage(),
					importSession.getUploadProgress());
			return;
		}

		// Recover file
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Listen for progress updates
		IOperationProgress uploadProgress = new OperationProgress();
		importSession.setUploadProgress(uploadProgress);
		UploadListener uploadListener = new UploadListener(uploadProgress);
		upload.setProgressListener(uploadListener);

		// Parse POST request items
		FileItem uploadItem = null;
		try {
			log.trace("Parsing HTTP POST request");
			List items = upload.parseRequest(request);
			Iterator it = items.iterator();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				log.trace("Found item with name: " + item.getName());

				if (!item.isFormField() && "uploadFormElement".equals(item.getFieldName())) {
					log.trace("Found uploaded file with name:" + item.getName());
					uploadItem = item;
				}
			}
		} catch (FileUploadException e) {
			handleError(response, "Error occurred while processing HTTP POST request", uploadProgress);
			return;
		}

		// Check if no file was uploaded
		if (uploadItem == null) {
			handleError(response, "Error occurred when processing the HTTP POST request: no file found", uploadProgress);
			return;
		}

		// Save file
		log.trace("Creating temporary file...");
		File fileTmp = File.createTempFile("import", "archive");
		fileTmp.deleteOnExit();
		IOUtils.copy(uploadItem.getInputStream(), new FileOutputStream(fileTmp));
		log.trace("Temporary file created");

		log.trace("Validating uploaded file...");
		try {
			// Checks on file type
			FileValidationOutcome validation = FileValidator.validateFile(fileTmp, package_, fileTypeName);
			if (!validation.isValid()) {
				fileTmp.delete();
				uploadItem.delete();
				handleError(response, validation.getDetails(), uploadProgress);
				return;
			}
		} catch (FileValidatorException e) {
			fileTmp.delete();
			uploadItem.delete();
			handleError(response, e.getMessage(), uploadProgress);
			return;
		}
		log.trace("Uploaded file is a valid file, saving it into import session...");

		// File is good, save it into session data structure
		SoftwareFile softwareFile = package_.getFilesContainer().createNewFile();

		// Set file properties in session
		softwareFile.setFilename(uploadItem.getName());
		softwareFile.setTypeName(fileTypeName);
		softwareFile.setFile(fileTmp);

		// Set completed state
		uploadProgress.setState(OperationState.COMPLETED);

		log.debug("File upload completed:" + uploadItem.getName());

		uploadItem.delete();

		response.getWriter().write("OK");
	}

	private void handleError(HttpServletResponse response, String errorMsg, IOperationProgress uploadProgress)
			throws IOException {
		uploadProgress.setState(OperationState.FAILED);
		uploadProgress.setDetails(errorMsg);

		log.error(errorMsg);

		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMsg);
	}

	class UploadListener implements ProgressListener {

		IOperationProgress progress;

		public UploadListener(IOperationProgress progress) {
			this.progress = progress;
		}

		/**
		 * {@inheritDoc}
		 */
		public void update(long pBytesRead, long pContentLength, int pItems) {
			progress.setProgress(pContentLength, pBytesRead);
		}

	}

}
