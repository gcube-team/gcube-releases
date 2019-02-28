package org.gcube.smartgears.extensions.resource;

import static org.gcube.smartgears.Constants.application_xml;
import static org.gcube.smartgears.extensions.HttpExtension.Method.GET;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.BridgedApplicationConfiguration;
import org.gcube.smartgears.extensions.ApiResource;
import org.gcube.smartgears.extensions.ApiSignature;

/**
 * An {@link ApiResource} of {@link RemoteResource} at {@link #mapping}.
 * 
 * @author Fabio Simeoni
 *
 */
public class ConfigurationResource extends ApiResource {

	private static final long serialVersionUID = 1L;
	
	public static final String mapping = "/configuration";
	
	private static final ApiSignature signature = handles(mapping).with(method(GET).produces(application_xml)); 
	
	ConfigurationResource() {
		super(signature);
	}
		
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ApplicationConfiguration config = BridgedApplicationConfiguration.class.cast(context().configuration()).inner();
		Resources.marshal(config,resp.getWriter());
	}

}
