package org.gcube.smartgears.extensions.resource;

import static java.util.Collections.*;
import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.extensions.HttpExtension.Method.*;
import static org.gcube.smartgears.handlers.application.request.RequestError.*;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.extensions.ApiResource;
import org.gcube.smartgears.extensions.ApiSignature;
import org.gcube.smartgears.handlers.application.lifecycle.ProfilePublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An {@link ApiResource} of {@link RemoteResource} at {@link #mapping}.
 * 
 * @author Fabio Simeoni
 * 
 */
public class ScopesResource extends ApiResource {

	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(ScopesResource.class);

	public static final String mapping = "/scopes";

	private static final ApiSignature signature = handles(mapping).with(method(GET).produces(application_xml)).with(
			method(POST).accepts(application_xml));

	private ProfilePublisher publisher;

	ScopesResource() {
		super(signature);
	}

	@Override
	public boolean supports(Method method) {

		return method == GET || method == POST;
	}

	@Override
	public void init(ApplicationContext context) throws Exception {
		super.init(context);
		publisher = new ProfilePublisher(context);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			Resources.marshal(new Scopes(context().profile(GCoreEndpoint.class).scopes().asCollection()), resp.getWriter());
		}
		catch(Exception e) {
			invalid_request_error.fire("cannot parse request body",e);
		}

	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
				
		Scope wrapper = Resources.unmarshal(Scope.class, req.getReader());
		String scope = wrapper.value;

		if (scope == null || scope.isEmpty())
			invalid_request_error.fire("missing scope in request body");

		GCoreEndpoint profile = context().profile(GCoreEndpoint.class);

		if (wrapper.delete)
			delete(profile, scope);
		else
			add(profile, scope);
	}

	private void add(GCoreEndpoint profile, String scope) {

		log.info("adding {} to {}", scope, context().name());

		publisher.addTo(singletonList(scope));
	}

	private void delete(GCoreEndpoint profile, String scope) {
	
		if (profile.scopes().size()==1)
			illegal_state_error.fire("cannot remove this resource from "+scope+", as it is its only scope.");
		
		log.info("removing {} from {}", scope, context().name());

		publisher.removeFrom(singletonList(scope));
	}

	// helper classes

	@XmlRootElement(name="scopes")
	public static class Scopes {

		@XmlElement(name = "scope")
		public Collection<String> values;

		Scopes() {
		}

		public Scopes(Collection<String> scopes) {
			this.values = scopes;
		}
	}

	@XmlRootElement(name = "scope")
	public static class Scope {

		@XmlAttribute
		public boolean delete;

		@XmlValue
		public String value;

		Scope() {
		}

		public Scope(String scope) {
			this.value = scope;
		}
	}
}
