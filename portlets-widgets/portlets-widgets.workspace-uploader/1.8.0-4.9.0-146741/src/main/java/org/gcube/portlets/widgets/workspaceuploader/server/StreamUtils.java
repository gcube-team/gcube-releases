/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class StreamUtils.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 2, 2015
 */
public class StreamUtils {
	
	public static Logger logger = LoggerFactory.getLogger(StreamUtils.class);
	
	/**
	 * Stream2file.
	 *
	 * @param in the in
	 * @param prefix the prefix
	 * @param suffix the suffix
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static File stream2file(InputStream in, String prefix, String suffix) throws IOException {
		final File tempFile = File.createTempFile(prefix, suffix);
		logger.debug("Creating temp file: " + tempFile.getAbsolutePath());
		tempFile.deleteOnExit();
		
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
			
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			logger.debug("Created temp file: " + tempFile.getAbsolutePath());
		}catch (Exception e) {
			// TODO: handle exception
		}
		return tempFile;
	}
	


	/**
	 * Stream2file recorder.
	 *
	 * @param in the in
	 * @param totalBytes the total bytes
	 * @param prefix the prefix
	 * @param suffix the suffix
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static File stream2fileRecorder(InputStream in, long totalBytes, String prefix, String suffix) throws IOException {
		final File tempFile = File.createTempFile(prefix, suffix);
		logger.debug("Created temp file: " + tempFile.getAbsolutePath());
		tempFile.deleteOnExit();
		
		/*UploadProgress uploadProgress = new UploadProgress();
		//instanciate the progress listener
		UploadProgressListener uploadProgressListener = new UploadProgressListener(uploadProgress);
		UploadProgressInputStream inputStream = new UploadProgressInputStream(in, totalBytes);
		inputStream.addListener(uploadProgressListener);*/

		try (FileOutputStream out = new FileOutputStream(tempFile)) {
			IOUtils.copy(in, out);
			
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return tempFile;
	}

	/**
	 * Open input stream.
	 *
	 * @param file the file
	 * @return the file input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file
						+ "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file
					+ "' does not exist");
		}
		return new FileInputStream(file);
	}

	/**
	 * Delete temp file.
	 *
	 * @param file the file
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static boolean deleteTempFile(File file) throws IOException{
		if (file.exists()) {
			String path = file.getAbsolutePath();
			logger.debug("Deleting file: "+path);
			boolean deleted = file.delete();
			logger.debug("Deleted? "+deleted);
			return deleted;
		}else
			throw new IOException("File '" + file
					+ "' doesn't exist");
	}
	
}
