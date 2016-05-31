/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.server;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.portlets.user.trendylyzer_portlet.server.utils.StorageUtil;


public class DownloadServlet extends HttpServlet{

	private static final long serialVersionUID = -8423345575690165644L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String requestType = (String)req.getParameter("type");
		
		String smpUrl = (String)req.getParameter("url");
		
		String fileName = req.getParameter("name");
		
		System.out.println("DOWNLOAD SERVLET");
		System.out.println("SMP URL: "+smpUrl);
		System.out.println("File name: "+fileName);

		try{
			if (requestType==null || !requestType.toUpperCase().contentEquals("IMAGES"))
				resp.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );
			
			InputStream inputStream = StorageUtil.getStorageClientInputStream(smpUrl);
			
			OutputStream out = resp.getOutputStream();
			IOUtils.copy(inputStream, resp.getOutputStream());
			out.close();
		} catch (Exception e) {
			System.out.println("Error during external item sending "+smpUrl);
			e.printStackTrace();
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error during data retrieving: "+e.getMessage());
			return;
		}
	}


}
