package org.gcube.application.framework.http.content.access.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.gcube.application.framework.contentmanagement.content.impl.DigitalObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentConsumers {

	private static final Logger logger = LoggerFactory.getLogger(ContentConsumers.class);
	
	public static String getHTMLContent(String url) throws IOException{
		URL pageLink = new URL(url);
        URLConnection pc = pageLink.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(pc.getInputStream()));
        String inputLine;
        String document = new String();
        StringBuffer sb = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
        	sb.append(inputLine);
        in.close();
        return sb.toString();
	}
	
//	public static byte[] getRawContent(String url) throws IOException {
//		URL u = new URL(url);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		InputStream is = null;
//		try {
//			is = u.openStream();
//			byte[] byteChunk = new byte[4096]; 							
//			int n;
//			while ((n = is.read(byteChunk)) > 0) {
//				baos.write(byteChunk, 0, n);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (is != null) {
//				is.close();
//			}
//		}
//		return baos.toByteArray();
//	}
	
	/**
	 * 
	 * @param url the path to where the file resides
	 * @param out the output stream onto which to write the file
	 * @return
	 * @throws IOException
	 */
	
	public static void getRawContent(String url, OutputStream out) throws IOException {
		URL u = new URL(url);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = u.openStream();
			byte[] byteChunk = new byte[4096]; 							
			int n;
			while ((n = is.read(byteChunk)) > 0) {
				out.write(byteChunk, 0, n);
//				baos.write(byteChunk);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				is.close();
			}
		}
//		return baos.toByteArray();
	}
	
	
	
}
