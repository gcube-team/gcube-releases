package org.gcube.smartgears.managers;

import static org.gcube.smartgears.Constants.*;
import static org.gcube.smartgears.handlers.application.request.RequestError.*;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.context.application.DefaultApplicationContext;
import org.gcube.smartgears.handlers.application.ApplicationPipeline;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.handlers.application.ResponseEvent;
import org.gcube.smartgears.handlers.application.request.RequestError;
import org.gcube.smartgears.handlers.application.request.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link Filter} that executes a {@link ApplicationPipeline} of {@link RequestHandler}s before and a client request is delivered
 * to a given servlet and before the response produced by the servlet is returned to the client.
 * 
 * @author Fabio Simeoni
 * 
 */
public class RequestManager implements Filter {

	private static Logger log = LoggerFactory.getLogger(RequestManager.class);

	private final ApplicationContext context;
	private final String servlet;
	private final List<RequestHandler> handlers;

	/**
	 * Creates an instance with the name of the target servlet and a pipeline.
	 * 
	 * @param servlet the name of the servlet
	 * @param pipeline the pipeline
	 */
	public RequestManager(ApplicationContext context, String servletName, List<RequestHandler> handlers) {
		this.context = context;
		this.servlet = servletName;
		this.handlers = handlers;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httprequest = (HttpServletRequest) request;
		HttpServletResponse httpresponse = (HttpServletResponse) response;

		ApplicationPipeline<RequestHandler> pipeline = new ApplicationPipeline<RequestHandler>(handlers);
		
		if (shouldExcludeRequest(httprequest))

			chain.doFilter(request, response);

		else {

			
			// create a per-request context with temporary properties
			ApplicationContext ctx = new DefaultApplicationContext(context);

			RequestEvent event = new RequestEvent(servlet, ctx, httprequest,httpresponse);

			try {
				pipeline.forward(event);
			}
			catch(Throwable t) {

				handleError(httprequest,httpresponse,t);
				return;
			}

			try{
				// dispatch to other filters for this servlet
				chain.doFilter(request, response);
			}catch(Throwable t){
				t.printStackTrace();
				handleError(httprequest,httpresponse,t);
			}

			ResponseEvent responseEvent = new ResponseEvent(servlet, ctx, httprequest, httpresponse);

			try {

				pipeline.reverse().forward(responseEvent);
			}
			catch(Throwable t) {
				t.printStackTrace();
				handleError(httprequest,httpresponse,t);
				return;
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// propagate filter initialisation to handler
		try {

			for (RequestHandler handler : handlers)

				handler.start(context);

		} catch (Throwable t) {

			throw new ServletException(t);

		}
	}

	@Override
	public void destroy() {

		for (RequestHandler handler : handlers)
			try {

				handler.stop();

			} catch (Throwable t) {

				log.error("cannot terminate handler {} for application {} ", handler, context.name());
			}

	}

	// helpers

	private boolean shouldExcludeRequest(HttpServletRequest request) {

		String query = request.getQueryString();


		if ("wsdl".equals(query))
			return true;

		String path = request.getPathInfo();
		if (path==null) path = request.getServletPath();
		
		log.debug("checking should exclude context path with excludes {} ", context.configuration().excludes());
		
		
		for (String exclude : context.configuration().excludes())
			if (
					(EXCLUDE_ALL).equals(exclude) || 
					(exclude.endsWith(EXCLUDE_ALL) && path!=null && path.startsWith(exclude.substring(0,exclude.length()-2))) ||
					exclude.equals(path)
					){
				log.debug(" context path {} exluded ", path);
				return true;
			}
				
		log.debug(" context path {} NOT exluded ", path);
		return false;
	}

	private void handleError(HttpServletRequest request, HttpServletResponse response,Throwable t) throws IOException {

		RequestError error = t instanceof RequestException?
				RequestException.class.cast(t).error():
					application_error;

				if (error == application_error) {
					response.sendError(error.code(),error.message());
				}else {
					if (error == request_not_authorized_error){
						response.setHeader("WWW-Authenticate", "Basic realm=\"Smartgears\"");
						log.info("setting WWW-Authenticate to response header");
					}
					response.getWriter().write("Error ("+error.code()+") : "+error.message()+"\nStacktrace:\n");
					t.printStackTrace(response.getWriter());
					response.setStatus(error.code());
				}

	}
}
