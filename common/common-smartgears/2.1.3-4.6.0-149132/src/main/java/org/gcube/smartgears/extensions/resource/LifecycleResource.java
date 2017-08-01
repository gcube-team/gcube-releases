package org.gcube.smartgears.extensions.resource;

import static org.gcube.smartgears.Constants.application_xml;
import static org.gcube.smartgears.extensions.HttpExtension.Method.GET;
import static org.gcube.smartgears.extensions.HttpExtension.Method.POST;
import static org.gcube.smartgears.handlers.application.request.RequestError.illegal_state_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.invalid_request_error;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.extensions.ApiResource;
import org.gcube.smartgears.extensions.ApiSignature;
import org.gcube.smartgears.lifecycle.application.ApplicationState;

/**
 * An {@link ApiResource} of {@link RemoteResource} at {@link #mapping}.
 * 
 * @author Fabio Simeoni
 * 
 */
public class LifecycleResource extends ApiResource {

	private static final long serialVersionUID = 1L;

	public static final String mapping = "/lifecyle";

	private static final ApiSignature signature = handles(mapping).with(method(GET).produces(application_xml)).with(
			method(POST).accepts(application_xml));


	LifecycleResource() {
		super(signature);
	}
		

	@Override
	public boolean supports(Method method) {

		return method == GET || method == POST;
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			Resources.marshal(new State(context().lifecycle().state()), resp.getWriter());
		}
		catch(Exception e) {
			invalid_request_error.fire("cannot parse request body",e);
		}

	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		State wrapper = Resources.unmarshal(State.class, req.getReader());
		String value = wrapper.value;

		if (value == null || value.isEmpty())
			invalid_request_error.fire("missing state in request body");

		ApplicationState state = null;
		
		try {
			state = ApplicationState.valueOf(value);
		}
		catch(Exception e) {
			invalid_request_error.fire(value+" is an unkown resource state",e);
		}
		
		try {
			context().lifecycle().moveTo(state);
		}
		catch(Exception e) {
			illegal_state_error.fire("invalid state transition for this resource"+value, e);
		}
	}

	// helper classes

	@XmlRootElement(name="state")
	public static class State {

		@XmlValue
		public String value;

		State() {
		}

		public State(ApplicationState state) {
			this.value=state.name();
		}
	}
}
