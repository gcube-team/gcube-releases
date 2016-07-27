/**
 * 
 */
package org.gcube.portlets.widgets.file_dw_import_wizard.server.local;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.gcube.portlets.widgets.file_dw_import_wizard.server.file.ImportSessions;

import org.gcube.portlets.widgets.file_dw_import_wizard.shared.FileType;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 * 
 */
public class Util {
//	static Logger logger = Logger.getLogger("logger");

	public static final String[] ZIP_MIMETYPES = new String[] {
			"application/x-compress", "application/x-compressed",
			"application/x-gzip", "application/x-winzip", "application/x-zip",
			"application/zip", "multipart/x-zip","application/x-zip-compressed" };

	/**
	 * Check if the content type is a zip type.
	 * 
	 * @param contentType
	 *            the content type to check.
	 * @return <code>true</code> if is a zip file, <code>false</code> otherwise.
	 */
	public static boolean isZipContentType(String contentType) {
		for (String zip_mimetype : ZIP_MIMETYPES)
			if (zip_mimetype.equals(contentType))
				return true;
		return false;
	}

	/**
	 * Unzip the specified stream
	 * 
	 * @param is
	 *            the zip stream.
	 * @param os
	 *            the output stream.
	 * @throws Exception
	 */
	public static String unZip(InputStream is, OutputStream os)
			throws Exception {
		try {
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null && !entry.isDirectory())
				;

			if (entry == null || entry.isDirectory()) {
				zis.close();
				os.close();
				throw new Exception("Unzip error: No file entry found");
			}
			IOUtils.copy(zis, os);
			zis.closeEntry();
			zis.close();
			os.close();
			return entry.getName();

		} catch (IOException e) {
			throw new Exception("Unzip error: " + e.getMessage(), e);
		}

	}

	public static String exceptionDetailMessage(Throwable t) {
		StringWriter out = new StringWriter();
		PrintWriter writer = new PrintWriter(out);
		t.printStackTrace(writer);

		StringBuilder message = new StringBuilder("Error message:\n");
		message.append(out.toString());

		return message.toString();
	}

	public static void setImportFile(ImportSessions importSession,
			InputStream is, String name, String mimeType) throws Exception {
		File tmp;
		if (importSession.getType() == FileType.DARWINCORE)
			{
				tmp = File.createTempFile("import", "zip");
			}
		else
			tmp = File.createTempFile("import", "");

		// FIXME manage it
		tmp.deleteOnExit();
		//boolean iszip = isZipContentType(mimeType);
		
		
			IOUtils.copy(is, new FileOutputStream(tmp));
		

		// logger.trace("upload completed");

		importSession.setName(name);
		importSession.setFile(tmp);
	}

}
