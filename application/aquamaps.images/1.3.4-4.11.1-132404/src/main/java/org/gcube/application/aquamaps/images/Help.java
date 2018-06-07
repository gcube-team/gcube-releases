package org.gcube.application.aquamaps.images;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Help extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5119931684394676178L;
	private static final Logger logger = LoggerFactory.getLogger(Help.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		InputStream is=null;
		try{
			is=Common.get().getHelpStream();
			IOUtils.copy(is, resp.getOutputStream());
		}catch(Exception e){
			logger.error("Unexpected error ",e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}finally{
			if(is!=null) IOUtils.closeQuietly(is);
			try{
				IOUtils.closeQuietly(resp.getOutputStream());
			}catch (Throwable t){
				logger.error("",t);
			}
		}
			
		
	}
	
}
