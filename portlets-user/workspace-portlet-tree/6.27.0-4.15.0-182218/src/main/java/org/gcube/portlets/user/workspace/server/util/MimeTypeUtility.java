/**
 * 
 */
package org.gcube.portlets.user.workspace.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MimeTypeUtil.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 *         Copied from org.gcube.common.homelibrary.util.MimeTypeUtil
 * @author Federico De Faveri defaveri@isti.cnr.it
 * 
 *         Jul 5, 2019
 */
public class MimeTypeUtility {

	private static final String DOT_STRING = ".";

	protected static Logger logger = LoggerFactory.getLogger(MimeTypeUtility.class);

	/**
	 * 
	 */
	public static final String BINARY_MIMETYPE = "application/octet-stream";
	public static final String[] ZIP_MIMETYPES = new String[] { "application/octet-stream", "application/x-compress",
			"application/x-compressed", "application/x-zip-compressed", "application/x-gzip", "application/x-winzip",
			"application/x-zip", "application/zip", "multipart/x-zip" };

	protected static final Map<String, List<String>> mimetype_extension_map = new LinkedHashMap<String, List<String>>();
	protected static final Map<String, String> extension_mimetype_map = new LinkedHashMap<String, String>();

	static {
		InputStream extensionToMimetype = MimeTypeUtility.class
				.getResourceAsStream("/WsExtensionToMimeTypeMap.properties");
		InputStream mimetypeToExtension = MimeTypeUtility.class
				.getResourceAsStream("/WsMimeTypeToExtensionMap.properties");
		try {
			loadExtensions(extensionToMimetype);
			loadMimeTypes(mimetypeToExtension);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load extensions.
	 *
	 * @param is the is
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static void loadExtensions(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = br.readLine();

		while (line != null) {
			String[] split = line.split("=");
			if (split.length == 2) {
				String mimeType = split[0];
				String extension = split[1];
				extension_mimetype_map.put(extension, mimeType);
			}
			line = br.readLine();
		}
		br.close();

	}

	/**
	 * Load mime types.
	 *
	 * @param is the is
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected static void loadMimeTypes(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = br.readLine();

		while (line != null) {
			String[] split = line.split("=");
			if (split.length == 2) {
				String mimeType = split[0];
				String extension = split[1];
				List<String> toExtensions = mimetype_extension_map.get(mimeType);
				if (toExtensions == null) {
					toExtensions = new ArrayList<String>();
				}
				toExtensions.add(extension);
				mimetype_extension_map.put(mimeType, toExtensions);
				// mimetype_extension_map.put(mimeType, extension);
			}
			line = br.readLine();
		}
		br.close();
	}

	/**
	 * Gets the extension.
	 *
	 * @param mimeType the mime type.
	 * @return the related list of Extensions.
	 */
	public static List<String> getExtension(String mimeType) {
		return mimetype_extension_map.get(mimeType);
	}

	/**
	 * Gets the name with extension.
	 *
	 * @param name     the file name.
	 * @param mimeType the mime type.
	 * @return the right name.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String getNameWithExtension(String name, String mimeType) throws IOException {

		logger.debug("Deriving the file extension for file name: " + name + ", with mimeType: " + mimeType);

		if (mimeType == null || mimeType.isEmpty()) {
			logger.debug("Input mime type is null or empty returning passed name: " + name);
			return name;
		}

		String declaredExtension = FilenameUtils.getExtension(name);
		logger.debug("The name " + name + " contains the extension: " + declaredExtension);

		if (declaredExtension.equals("exe"))
			return name;

		List<String> extensions = MimeTypeUtility.getExtension(mimeType);
		logger.trace("Extension/s available for input mimetype: " + mimeType + " into map is/are: " + extensions);

		String toMimeTypeExtension = "";
		if (extensions != null) {
			toMimeTypeExtension = extensions.get(extensions.size() - 1); // I'm reading the last extension in the map
																			// (myme type - list of extensions)
			logger.debug("Using the last extension read into list of available extensions: " + toMimeTypeExtension);
		}

		// MANAGING ALREADY DECLARED EXTENSION IN THE FILE NAME
		if (!declaredExtension.isEmpty() && !toMimeTypeExtension.isEmpty()) {
			String dEextL = declaredExtension.toLowerCase();
			String mtExtL = toMimeTypeExtension.toLowerCase();

			// The extension writes in the file name is matching the mime type extension
			// declared in the map
			if (dEextL.equals(mtExtL)) {
				logger.trace("The Extension declared in the name " + name
						+ " is matching derived mime type extension so returning the input name: " + name);
				return name;
			}

//			if(!dEextL.trim().contains(" ")) {
//				logger.trace("The Extension declared in the name "+name+" seems a valid suffix (without other spaces) so returning the input name: "+name);
//				return name;
//			}

			logger.debug("No logic seems to match the extension declared in the name " + declaredExtension
					+ " as a valid extension so I'm adding the extension derived from mime type map: "
					+ toMimeTypeExtension);
		}

		// CHECKING THE FOLLOWING IN ORDER TO AVOID THE DOT AS LAST CHAR OF FILENAME
		if (toMimeTypeExtension.isEmpty()) {
			return name;
		}

		String fullname = String.format("%s%s%s", name, DOT_STRING, toMimeTypeExtension);
		logger.trace("returning full name:" + fullname);
		return fullname;

	}

	/**
	 * Check if the content type is a zip type.
	 * 
	 * @param contentType the content type to check.
	 * @return <code>true</code> if is a zip file, <code>false</code> otherwise.
	 */
	public static boolean isZipContentType(String contentType) {
		for (String zip_mimetype : ZIP_MIMETYPES)
			if (zip_mimetype.equals(contentType))
				return true;
		return false;
	}

	/**
	 * Get mime type by file.
	 *
	 * @param filenameWithExtension the filename with extension
	 * @param tmpFile               the tmp file
	 * @return the mime type of the given file
	 */
	public static String getMimeType(String filenameWithExtension, File tmpFile) {
		MediaType mediaType = null;
		try {

			TikaConfig config = TikaConfig.getDefaultConfig();
			Detector detector = config.getDetector();
			TikaInputStream stream = TikaInputStream.get(tmpFile);
			Metadata metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY, filenameWithExtension);

			mediaType = detector.detect(stream, metadata);

		} catch (IOException e) {
			logger.error("Error detecting mime type for file " + filenameWithExtension);
		}

		return mediaType.getBaseType().toString();
	}

	/**
	 * Get mime type by inpustream.
	 *
	 * @param filenameWithExtension the filename with extension
	 * @param input                 the input
	 * @return the mime type of the given file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String getMimeType(String filenameWithExtension, InputStream input) throws IOException {

		MediaType mediaType = null;
		try {
			TikaConfig config = TikaConfig.getDefaultConfig();
			Detector detector = config.getDetector();
			TikaInputStream stream = TikaInputStream.get(input);
			Metadata metadata = new Metadata();
			metadata.add(Metadata.RESOURCE_NAME_KEY, filenameWithExtension);

			mediaType = detector.detect(stream, metadata);

		} catch (IOException e) {
			logger.error("Error detecting mime type for file " + filenameWithExtension);
		} finally {
//				if (file!=null)
//					file.close();
		}

		return mediaType.getBaseType().toString();

	}

	/**
	 * Gets the mime type to extension map.
	 *
	 * @return the mime type to extension map
	 */
	public static Map<String, List<String>> getMimeTypeToExtensionMap() {
		return mimetype_extension_map;
	}

	/**
	 * Gets the extension to mime type map.
	 *
	 * @return the extension to mime type map
	 */
	public static Map<String, String> getExtensionToMimeTypeMap() {
		return extension_mimetype_map;
	}

}
