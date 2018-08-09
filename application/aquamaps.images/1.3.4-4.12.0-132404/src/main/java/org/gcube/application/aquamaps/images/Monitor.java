package org.gcube.application.aquamaps.images;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.images.exceptions.ImageNotFoundException;
import org.gcube.application.aquamaps.images.exceptions.InvalidRequestException;
import org.gcube.application.aquamaps.images.model.Statistics;

import com.thoughtworks.xstream.XStream;

public class Monitor extends BaseServlet {

	private static final XStream xStream=new XStream();
	
	static {
		xStream.processAnnotations(Statistics.class);
	}
	private static final long serialVersionUID = 2486605867155769417L;
	
	
	@Override
	protected InputStream handleRequest(HttpServletRequest req,
			HttpServletResponse resp) throws IOException,
			ImageNotFoundException, InvalidRequestException, Exception {		
		resp.setContentType("application/xml");
		return new ByteArrayInputStream(xStream.toXML(common.getStatistics()).getBytes("UTF-8"));
	}

}
