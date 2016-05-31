package org.gcube.smartgears.extensions;

import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.handlers.application.request.RequestError.*;
import static org.gcube.smartgears.utils.Utils.*;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.smartgears.context.application.ApplicationContext;

/**
 * An {@link HttpExtension} that dispatches to one or more {@link ApiResource}s, handling the generic, HTTP-aspects
 * aspects of their client interactions
 * 
 * @author Fabio Simeoni
 * 
 */
public class HttpController extends HttpExtension {

	private static final long serialVersionUID = 1L;

	private final Map<String, ApiResource> resources = new HashMap<String, ApiResource>();
	
	
	HttpController() {
	}

	/**
	 * Creates an instance with a given name and a given mapping.
	 * 
	 * @param name the name
	 * @param mapping the mapping
	 */
	public HttpController(String name, String mapping) {
		super(name, mapping);
	}

	/**
	 * Adds one ore more {@link ApiResource}s to this controller.
	 * 
	 * @param resources the resources
	 */
	public void addResources(ApiResource... resources) {

		notNull("API resources", resources);

		for (ApiResource resource : resources)
			this.resources.put(resource.mapping(), resource);
	}

	@Override
	public void init(ApplicationContext context) throws Exception {
		super.init(context);
		for (ApiResource resource : resources.values())
			resource.init(context);

	}

	@Override
	public Set<String> excludes() {

		Set<String> resourceExcludes = new LinkedHashSet<String>();
		
		for (ApiResource resource : resources.values())
			resourceExcludes.addAll(resource.excludes());
		
		return resourceExcludes;
	}		
	
	// final because we dispatch to http servlet method inside, resources can use init(Context)
	@Override
	public final void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException {

		HttpServletRequest request = HttpServletRequest.class.cast(req);
		HttpServletResponse response = HttpServletResponse.class.cast(resp);

		ApiResource resource = resourceFor(request.getPathInfo());

		checkMethod(resource, request, response);
		
		checkContentTyperHeader(resource, request, response);
		checkAcceptHeader(resource, request, response);

		setContentTypeHeader(resource, request, response);
		
		dispatch(resource, request, response);
		

	}

	// helpers

	private void setContentTypeHeader(ApiResource resource, HttpServletRequest request, HttpServletResponse response) {

		Method method = valueOf(request.getMethod());
		
		Set<String> responseTypes = resource.signature().responseTypes().get(method);
		
		if (responseTypes.isEmpty())
			//overridden by resources that e.g. create something and return more specific codes
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		else 
			if (responseTypes.size() == 1 && !response.containsHeader(content_type))
				response.addHeader(content_type, responseTypes.iterator().next());
		
		
	}

	
	private ApiResource resourceFor(String path) {

		if (path==null) {
			path = "/";
		}
		else
		// some tolerance for trailing slashes
		if (path.length()>1 && path.endsWith("/"))
			path = path.substring(0, path.length() - 1);

		ApiResource resource = resources.get(path);

		if (resource == null)
			resource_notfound_error.fire();

		return resource;
	}
	
	

	private void checkMethod(ApiResource resource, HttpServletRequest request, HttpServletResponse response) {

		Method method = valueOf(request.getMethod());

		if (!resource.supports(method)) {
			response.addHeader(allow, toSingleString(resource.signature().methods()));
			method_unsupported_error.fire("this resource does not support method  " + method);
		}

	}

	private void dispatch(ApiResource resource, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Method method = valueOf(request.getMethod());

		// dispatches
		switch (method) {

		case HEAD:
			resource.doHead(request, response);
			break;
		case GET:
			resource.doGet(request, response);
			break;
		case POST:
			resource.doPost(request, response);
			break;
		case PUT:
			resource.doPut(request, response);
			break;
		case DELETE:
			resource.doDelete(request, response);
			break;
		case OPTIONS:
			resource.doOptions(request, response);
			break;
		case TRACE:
			resource.doTrace(request, response);
		}
	}

	private void checkContentTyperHeader(ApiResource resource, HttpServletRequest request, HttpServletResponse response) {

		Method method = valueOf(request.getMethod());

		// if request specifies a media-type, we check it against signature
		// if it doesn't we let it pass and let the resource apply default or complain.
		String requestTypes = request.getHeader(content_type);
		if (requestTypes != null) {
			String type = null;
			for (String value : valuesOf(requestTypes))
				if (resource.accepts(method, value)) {
					type = value;
					break;
				}
			if (type == null)
				incoming_contenttype_unsupported_error.fire("this resource does not accept " + requestTypes);
		}
	}

	private void checkAcceptHeader(ApiResource resource, HttpServletRequest request, HttpServletResponse response) {

		Method method = valueOf(request.getMethod());

		// check match on outgoing media type, if any
		String responseType = request.getHeader(accept);
		if (responseType != null) {
			String type = null;
			for (String value : valuesOf(responseType))
				if (resource.produces(method, value)) {
					type = value;
					break;
				}
			if (type == null)
				outgoing_contenttype_unsupported_error.fire("this resource cannot produce " + responseType);
		}

	}

	private String toSingleString(Collection<? extends Object> values) {
		StringBuilder builder = new StringBuilder();

		for (Object value : values)
			builder.append(value).append(",");

		String concat = builder.toString();
		return concat.substring(0, concat.length() - 1);

	}

	private String[] valuesOf(String header) {
		return header.split(",");
	}

	private Method valueOf(String method) {

		try {
			return Method.valueOf(method);
		} catch (Exception e) {
			throw method_unsupported_error.toException("unsupported method " + method);
		}
	}
}
