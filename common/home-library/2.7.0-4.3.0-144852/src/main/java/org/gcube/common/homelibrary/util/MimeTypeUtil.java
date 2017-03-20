/**
 * 
 */
package org.gcube.common.homelibrary.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class MimeTypeUtil {

	protected static final Logger logger = LoggerFactory.getLogger(MimeTypeUtil.class);


	/**
	 * 
	 */
	public static final String BINARY_MIMETYPE = "application/octet-stream";
	public static final String[] ZIP_MIMETYPES = new String[]{
		"application/octet-stream",
		"application/x-compress",
		"application/x-compressed",
		"application/x-zip-compressed",
		"application/x-gzip",
		"application/x-winzip",
		"application/x-zip",
		"application/zip",
		"multipart/x-zip"};



	protected static final Map<String, String> mimetype_extension_map = new LinkedHashMap<String, String>();
	protected static final Map<String, String> extension_mimetype_map = new LinkedHashMap<String, String>();

	static{
		InputStream extensionToMimetype = MimeTypeUtil.class.getResourceAsStream(
				"/ExtensionToMimeTypeMap.properties");
		InputStream mimetypeToExtension = MimeTypeUtil.class.getResourceAsStream(
				"/MimeTypeToExtensionMap.properties");
		try {
			loadExtensions(extensionToMimetype);
			loadMimeTypes(mimetypeToExtension);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected static void loadExtensions(InputStream is) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = br.readLine();

		while(line != null){
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

	protected static void loadMimeTypes(InputStream is) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = br.readLine();

		while(line != null){
			String[] split = line.split("=");
			if (split.length == 2) {
				String mimeType = split[0];
				String extension = split[1];
				mimetype_extension_map.put(mimeType, extension);
			}
			line = br.readLine();
		}
		br.close();
	}

	/**
	 * @param mimeType the mime type.
	 * @return the related extension.
	 */
	public static String getExtension(String mimeType)
	{
		return mimetype_extension_map.get(mimeType);
	}

		/**
		 * @param name the file name.
		 * @param mimeType the mime type.
		 * @return the right name.
		 * @throws IOException 
		 */
		public static String getNameWithExtension(String name, String mimeType) throws IOException
		{

			logger.trace("getNameWithExtension name: "+name+" mimeType: "+mimeType);

			if (mimeType == null) return name;

			String extension = MimeTypeUtil.getExtension(mimeType);
			logger.trace("extension: "+extension);

			if (extension == null) extension = "";
			else extension = "." + extension;

			if (name.toLowerCase().endsWith(extension.toLowerCase())) {
				logger.trace("extension already exist in name, returning name");
				return name;
			}

			logger.trace("returning "+name+extension);

			return name+extension;
		}

		/**
		 * Check if the content type is a zip type.
		 * @param contentType the content type to check.
		 * @return <code>true</code> if is a zip file, <code>false</code> otherwise.
		 */
		public static boolean isZipContentType(String contentType)
		{
			for (String zip_mimetype:ZIP_MIMETYPES) if (zip_mimetype.equals(contentType)) return true;
			return false;
		}

		/**
		 * Get mime type by file
		 * @param filenameWithExtension
		 * @param tmpFile
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
		 * Get mime type by inpustream
		 * @param filenameWithExtension
		 * @param file
		 * @return the mime type of the given file
		 * @throws IOException
		 */
		public static String getMimeType(String filenameWithExtension, InputStream input) throws IOException{
	
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
			}finally{
//				if (file!=null)
//					file.close();
			}
			
			
		return mediaType.getBaseType().toString();

		}


	}
