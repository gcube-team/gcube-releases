package org.gcube.smartgears.extensions.resource;

import static org.gcube.smartgears.Constants.application_xml;
import static org.gcube.smartgears.extensions.HttpExtension.Method.GET;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.extensions.ApiResource;
import org.gcube.smartgears.extensions.ApiSignature;

/**
 * An {@link ApiResource} of {@link RemoteResource} at {@link #mapping}.
 * 
 * @author Fabio Simeoni
 *
 */
public class ProfileResource extends ApiResource {

	private static final long serialVersionUID = 1L;
	
	public static final String mapping = "/profile";
	
	private static final ApiSignature signature = handles(mapping).with(method(GET).produces(application_xml)); 
	
	ProfileResource() {
		super(signature);
	}
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		Resources.marshal(context().profile(GCoreEndpoint.class),resp.getWriter());
	}

}
