package org.gcube.portlets.widgets.fileupload.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public final class UploadServlet extends HttpServlet {

	private static final Logger _log = LoggerFactory.getLogger(UploadServlet.class);

	/**
	 * use tomcat temp as base upload folder
	 */
	private static String UPLOAD_LOCATION = System.getProperty("java.io.tmpdir");

	public void init() {
		if (System.getenv("CATALINA_TMPDIR") != null && System.getenv("CATALINA_TMPDIR").compareTo("") != 0) {
			UPLOAD_LOCATION = System.getenv("CATALINA_TMPDIR");
		}
	}
	
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			uploadFile(request);
		} catch (FileUploadException fue) {
			throw new ServletException(fue);
		}
	}

	private void uploadFile(final HttpServletRequest request) throws FileUploadException, IOException {
		
		if (!ServletFileUpload.isMultipartContent(request)) {
			throw new FileUploadException("ERROR: multipart request not found");
		}
	
		FileItemFactory fileItemFactory = new DiskFileItemFactory();
		ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
		
		FileItemIterator fileItemIterator = servletFileUpload.getItemIterator(request);

		HttpSession session = request.getSession();
		UploadProgress uploadProgress = UploadProgress.getUploadProgress(session);

		while (fileItemIterator.hasNext()) {
			FileItemStream fileItemStream = fileItemIterator.next();
				
			String filePath = fileItemStream.getName();
			String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
			
			String normalizedString = Normalizer.normalize(fileName, Normalizer.Form.NFD);
			String sanitizedFileName = normalizedString.replaceAll("[^\\x00-\\x7F]", "");
			//generate the random dir 
			File theRandomDir = new File(UPLOAD_LOCATION + File.separator + UUID.randomUUID().toString());
			theRandomDir.mkdir();
			_log.debug("Created temp upload directory in: " + theRandomDir);
			
			//create the file
			File file = new File(theRandomDir, sanitizedFileName);
			Long size = Long.parseLong(request.getHeader("Content-Length"));
			_log.debug("size: " + size + " bytes sanitized File name="+sanitizedFileName);
			_log.debug("path: " + file.getAbsolutePath());
						
			//instanciate the progress listener
			UploadProgressListener uploadProgressListener = new UploadProgressListener(sanitizedFileName, uploadProgress, file.getAbsolutePath());
			UploadProgressInputStream inputStream = new UploadProgressInputStream(fileItemStream.openStream(), size);
			inputStream.addListener(uploadProgressListener);

			//actually copying
			Streams.copy(inputStream, new FileOutputStream(file), true);
			//finished
			_log.info("uploaded file " + file.getAbsolutePath());
		}
	}
}
