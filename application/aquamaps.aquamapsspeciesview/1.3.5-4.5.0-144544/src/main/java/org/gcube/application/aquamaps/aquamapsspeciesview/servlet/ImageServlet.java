package org.gcube.application.aquamaps.aquamapsspeciesview.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageServlet extends HttpServlet {



	/**
	 * 
	 */
	private static final long serialVersionUID = 5385921394364316932L;

	private static final String IMAGE_BASE_URL="http://www.fishbase.org/images/thumbnails/jpg/";


	private static final Logger logger = LoggerFactory.getLogger(ImageServlet.class);

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handleRequest(req, resp);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
		handleRequest(req, resp);
	}


	protected void handleRequest(HttpServletRequest request, HttpServletResponse response){


		//		imageName="tn_Ababd_u0.jpg";

		InputStream is=null;

		if(request.getParameter(Tags.PIC_NAME)!=null){
			String imageName=request.getParameter(Tags.PIC_NAME);
			//FISH MAP RETRIEVAL
			try{
				if(imageName.endsWith(".gif")) response.setContentType(Tags.IMAGE_GIF);
				else if (imageName.endsWith(".png"))response.setContentType(Tags.IMAGE_PNG);
				else response.setContentType(Tags.IMAGE_PNG);
				response.setStatus(HttpServletResponse.SC_OK);	
				try{
					URL url = new URL(IMAGE_BASE_URL+imageName);
					URLConnection uc = url.openConnection();
					is=uc.getInputStream();
					IOUtils.copy(is, response.getOutputStream());

				}catch(Exception e){
					try{
						response.setContentType(Tags.IMAGE_JPEG);
						is=	ImageServlet.class.getResourceAsStream("resources/imageNotFound.jpg");
						IOUtils.copy(is, response.getOutputStream());
					}catch(Exception e1)
					{throw e1;}
				}

			}catch(Exception e){			
				logger.error("Unable to serve request for image "+imageName,e);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}finally{
				if(is!=null) IOUtils.closeQuietly(is);
				try{
					IOUtils.closeQuietly(response.getOutputStream());
				}catch (Throwable t){
					logger.error("",t);
				}
			}
		}else response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
}
