package org.gcube.smartgears.extensions;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.smartgears.Constants;

/**
 * A resource-specifc API handled by an {@link HttpController}.
 * 
 * @author Fabio Simeoni
 *
 */
public abstract class ApiResource extends HttpExtension {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns a {@link ApiSignature} that declares the method and media types handled by an {@link ApiResource} for a given mapping.
	 * @param mapping the mapping
	 * @return the signature
	 */
	public static ApiSignature handles(String mapping) {
		return new ApiSignature(mapping);
	}

	/**
	 * Returns an {@link ApiMethodSignature} that declares the media types handled by an {@link ApiResource} for a given method.
	 * @param method the method
	 * @return the signature
	 */
	public static ApiMethodSignature method(Method method) {
		return new ApiMethodSignature(method);
	}
	
	private final ApiSignature signature;

	/**
	 * Creates an instance with a given signature.
	 * @param signature the signature
	 */
	public ApiResource(ApiSignature signature) {

		super("extension-resource", signature.mapping());

		this.signature = signature;
	}
	
	@Override
	public Set<String> excludes() {
		return Collections.singleton(Constants.root_mapping+mapping());
	}

	/**
	 * Returns <code>true</code> if this resource supports a given method.
	 * @param method the method
	 * @return <code>true</code> if this resource supports the given method
	 */
	public boolean supports(Method method) {

		return signature.methods().contains(method);
	}

	/**
	 * Returns <code>true</code> if this resource accepts a given media type for a given method.
	 * @param method the method
	 * @param type the media type
	 * @return <code>true</code> if this resource accepts the given media type for the given method
	 */
	public boolean accepts(Method method, String type) {

		Set<String> requestTypes = signature.requestTypes().get(method);
		
		//if signature does not specify, we assume resource can work with anything or uses a default and is in charge
		return requestTypes.isEmpty() || requestTypes.contains(type);
	}

	/**
	 * Returns <code>true</code> if this resource produces a given media type for a given method.
	 * @param method the method
	 * @param type the media type
	 * @return <code>true</code> if this resource produces the given media type for the given method
	 */
	public boolean produces(Method method, String type) {

		if (type.contains("*"))
			return true;
		
		Set<String> contentTypes = signature.responseTypes().get(method);
		return contentTypes.contains(type);
	}
	
	/**
	 * Return the signature of this resource.
	 * @return the signature
	 */
	public ApiSignature signature() {
		return signature;
	}
	
	
	//adapt to http servlet API delegating to standard implementation that throws a 405, resources ovverride in line with their signature
	
	@Override
	public void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doHead(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

	@Override
	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPut(req, resp);
	}

	@Override
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doDelete(req, resp);
	}

	@Override
	public void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doOptions(req, resp);
	}

	@Override
	public void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doTrace(req, resp);
	}

}
