/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.server.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * The Class HttpDownloadUtility.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class HttpDownloadUtility {
    
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(HttpDownloadUtility.class);
	
	private static final int BUFFER_SIZE = 1024;
 
    /**
     * Download file.
     *
     * @param fileURL the file url
     * @return the file
     * @throws Exception the exception
     */
    public static File downloadFile(String fileURL) throws Exception {
        
    	try{
    		logger.info("Trying donwload jar at "+fileURL);
    		
    		URL url = new URL(fileURL);

	        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
	        int responseCode = httpConn.getResponseCode();
	 
	        // always check HTTP response code first
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            String fileName = "";
	            String disposition = httpConn.getHeaderField("Content-Disposition");
	            String contentType = httpConn.getContentType();
	            int contentLength = httpConn.getContentLength();
	 
	            if (disposition != null) {
	                // extracts file name from header field
	                int index = disposition.indexOf("filename=");
	                if (index > 0) {
	                    fileName = disposition.substring(index + 10,
	                            disposition.length() - 1);
	                }
	            } else {
	                // extracts file name from URL
	                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
	                        fileURL.length());
	            }
	 
	            logger.info("Content-Type = " + contentType);
	            logger.info("Content-Disposition = " + disposition);
	            logger.info("Content-Length = " + contentLength);
	            logger.info("fileName = " + fileName);
	 
	            // opens input stream from the HTTP connection
	            InputStream inputStream = httpConn.getInputStream();
	            
	            if(fileName.isEmpty())
	            	fileName = UUID.randomUUID().toString();
	            
	            File file = File.createTempFile(fileName, ".jar"); 
	            
	            // opens an output stream to save into file
	            FileOutputStream outputStream = new FileOutputStream(file);
	 
	            int bytesRead = -1;
	            byte[] buffer = new byte[BUFFER_SIZE];
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
	 
	            outputStream.close();
	            inputStream.close();
	            
	            httpConn.disconnect();
	            logger.info("created file: "+file.getAbsolutePath() +" returning");
	            return file;
	        } else {
	           logger.warn("No file to download. Server replied HTTP code: " + responseCode);
	           logger.info("returning null");
	           return null;
	        }
    	} catch (SocketTimeoutException e) {
			logger.error("Error SocketTimeoutException with url "  +fileURL, e);
			throw new Exception("Error SocketTimeoutException");
		} catch (MalformedURLException e) {
			logger.error("Error MalformedURLException with url "  +fileURL, e);
			throw new Exception("Error MalformedURLException");
		} catch (IOException e) {
			logger.error("Error IOException with url " + fileURL, e);
			throw new Exception("Error IOException");
		}catch (Exception e) {
			logger.error("Error Exception with url " +fileURL, e);
			throw new Exception("Error Exception");
		}
    	
    }
}
