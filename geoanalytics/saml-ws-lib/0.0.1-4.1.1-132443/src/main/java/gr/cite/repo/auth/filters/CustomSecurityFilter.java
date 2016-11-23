package gr.cite.repo.auth.filters;

import gr.cite.repo.auth.app.utils.ErrorWithPadding;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

public class CustomSecurityFilter implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomSecurityFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {

	}

	protected String redirectionPage;
	protected boolean includeTarget;

	public CustomSecurityFilter(String redirectionPage, boolean includeTarget) {
		this.redirectionPage = redirectionPage;
		this.includeTarget = includeTarget;
	}
	
	public CustomSecurityFilter(){
		
	}
	
	void initFilter(String redirectionPage, boolean includeTarget){
		this.redirectionPage = redirectionPage;
		this.includeTarget = includeTarget;
	}

	protected Boolean getLoggedIn(HttpSession session) {
		Object val = session.getAttribute(SessionAttributes.LOGGED_IN_ATTRNAME);
		if (val == null || !(val instanceof Boolean)) {
			return false;
		}
		return (Boolean) val;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (!checkIfResourceIsProtected(httpRequest.getPathInfo())){
			logger.info("resource : " + httpRequest.getPathInfo() + " is not protected");
			chain.doFilter(request, response);
			return;
		}

		Boolean isLoggedIn = false;
		
		if (httpRequest.getSession(false) != null){
			logger.info("sessionID : " + httpRequest.getSession(false).getId());
			isLoggedIn = getLoggedIn(httpRequest.getSession(false));
		} else{
			logger.info("no session");
			isLoggedIn = false;
		}
		

		if (!isLoggedIn) {
			logger.info("not logged in. will be authenticated");

			HttpServletResponse httpResponse = (HttpServletResponse) response;

//			String redirectLocation = this.redirectionPage;
//			if (this.includeTarget) {
//				String resourceURL = httpRequest.getScheme() + "://"
//						+ httpRequest.getServerName() + ":"
//						+ httpRequest.getServerPort()
//						+ httpRequest.getPathInfo();
//				redirectLocation += "?target=" + resourceURL;
//			}
//
//			logger.info("will be redirected to : " + redirectLocation);
//			if (redirectLocation != null){
//				httpResponse.sendRedirect(redirectLocation);
//				return;
//			}
			
			if (httpRequest.getParameterMap().containsKey("callback")) {

				httpResponse.getWriter().write(
						new ObjectMapper()
								.writeValueAsString(new JSONPObject(httpRequest
										.getParameterMap().get("callback")[0],
										new ErrorWithPadding().setStatusCode(
												Status.FORBIDDEN.getStatusCode()))));
				httpResponse.setContentType("application/x-javascript");
			} else {
				httpResponse.sendError(Status.FORBIDDEN.getStatusCode());
			}
			return;
		} else {
			if (!checkIfUserAuthenticated(httpRequest.getPathInfo(), httpRequest.getSession(false))){
				logger.warn("user is not authorized to access : " + httpRequest.getPathInfo());
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.sendError(Status.FORBIDDEN.getStatusCode());
				return;
			}
		}

		logger.info("logged in. serving request");

		chain.doFilter(request, response);
	}

	protected Boolean checkIfUserAuthenticated(String pathInfo, HttpSession session) {
		return true;
		
	}

	protected Boolean checkIfResourceIsProtected(String pathInfo) {
		return true;
	}

	public void destroy() {

	}

}
