package org.gcube.application.aquamaps.images;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.images.exceptions.ImageNotFoundException;
import org.gcube.application.aquamaps.images.exceptions.InvalidRequestException;

public class Pics extends BaseServlet {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6192206746569409892L;

	
	@Override
	protected InputStream handleRequest(HttpServletRequest req,
			HttpServletResponse resp) throws IOException,
			ImageNotFoundException, InvalidRequestException, Exception {

		if(!hasScientificName(req)) throw new InvalidRequestException();
		String scientificName=getScientificName(req);
		resp.setContentType(Common.IMAGE_JPEG);
		
		return common.getSpeciesPicture(scientificName);
	}
	
	

	
}
