package org.gcube.smartgears.handlers.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.smartgears.context.application.ApplicationContext;

/**
 * An {@link ApplicationEvent} that occurs when an application receives a request.
 * 
 * @author Fabio Simeoni
 * 
 */
public class RequestEvent extends ApplicationEvent<RequestHandler> {

	private final String servlet;
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	
	/**
	 * Creates an instance with the application context, the client request, and the name of the target servlet.
	 * 
	 * @param context the context of the application
	 * @param servlet the name of the target servlet
	 * @param request the client request
	 */
	public RequestEvent(String servlet,ApplicationContext context, HttpServletRequest request, HttpServletResponse response) {
		super(context);
		this.request = request;
		this.response=response;
		this.servlet = servlet;
	}

	/**
	 * Returns the name of the target servlet.
	 * 
	 * @return the name of the servlet.
	 */
	public String servlet() {
		return servlet;
	}

	public String uri() {
		String query = request().getQueryString();
		return query==null?request().getRequestURI():request().getRequestURI()+"?"+query;
	}
	
	/**
	 * Returns the client request.
	 * 
	 * @return the request
	 */
	public HttpServletRequest request() {
		return request;
	}
	
	/**
	 * Returns the response.
	 * 
	 * @return the response
	 */
	public HttpServletResponse response() {
		return response;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[req=" + request().getRemoteHost() + ",resp="
				+ response.toString().substring(0, 12) + "]";
	}
}
