package org.gcube.smartgears.handlers.application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.smartgears.context.application.ApplicationContext;

/**
 * A {@link ApplicationEvent} that occurs when the application returns a response to a given request.
 
 * @author Fabio Simeoni
 * 
 */
public class ResponseEvent extends RequestEvent {

	/**
	 * Creates an instance with the name of the target servlet, the context of the application, the client request, and the application response.
	 * 
	 * @param servlet the name of the servlet
	 * @param context the context of the application
	 * @param request the request
	 * @param response the response
	 */
	public ResponseEvent(String servlet, ApplicationContext context, HttpServletRequest request, HttpServletResponse response) {
		super(servlet, context, request, response);
	}

}
