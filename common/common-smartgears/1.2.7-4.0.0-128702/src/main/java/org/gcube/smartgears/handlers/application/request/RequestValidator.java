package org.gcube.smartgears.handlers.application.request;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.smartgears.Constants.authorization_header;
import static org.gcube.smartgears.Constants.request_validation;
import static org.gcube.smartgears.Constants.scope_header;
import static org.gcube.smartgears.Constants.token_header;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_failed_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_unavailable_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.invalid_request_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.request_not_authorized_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.internal_server_error;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.BannedService;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.handlers.application.ResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = Constants.request_validation)
public class RequestValidator extends RequestHandler {

	private static Logger log = LoggerFactory.getLogger(RequestValidator.class);

	private ApplicationContext context;

	@Override
	public void handleRequest(RequestEvent call) {
		
		log.info("executing request validator ON REQUEST");
		
		context = call.context();

		validateAgainstLifecycle(call);
						
		if (!validateToken(call)){
			String scope = call.request().getHeader(scope_header);
			validateScope(scope);
			log.info("received call to {} in scope {}",call.uri(),scope);
		}
	}
	
	@Override
	public void handleResponse(ResponseEvent e){
		log.info("executing request validator on RESPONSE");
		SecurityTokenProvider.instance.reset();
		AuthorizationProvider.instance.reset();
		ScopeProvider.instance.reset();
	}
	
	private void validateAgainstLifecycle(RequestEvent call) {

		switch(context.lifecycle().state()) {

		case stopped :
			application_unavailable_error.fire(); break;

		case failed: 
			application_failed_error.fire(); break;

		default: 
			//nothing to do, but avoids warnings
		}


	}

	private void validateScope(String scope) {
			
		if (scope == null) {
			log.info("rejecting unscoped call to {}",context.name());
			invalid_request_error.fire("call is unscoped"); 
		}
		
		if (!context.profile(GCoreEndpoint.class).scopes().contains(scope)) {
			log.info("rejecting call to {} in invalid scope {}",context.name(),scope);
			invalid_request_error.fire(context.name()+" cannot be called in scope "+scope);
		}

		ScopeProvider.instance.set(scope);

	}

	private boolean validateToken(RequestEvent call){
		
		String token = call.request().getParameter(token_header)==null?
				call.request().getHeader(token_header):call.request().getParameter(token_header);
				
		if ((token == null) && call.request().getHeader(scope_header)==null){
			if (call.request().getHeader(authorization_header)!=null){
				String basicAuthorization  = call.request().getHeader(authorization_header);
				String base64Credentials = basicAuthorization.substring("Basic".length()).trim();
				String credentials = new String(DatatypeConverter.parseBase64Binary(base64Credentials));
				// credentials = username:password
				final String[] values = credentials.split(":",2);
				token = values[1];
				UserInfo info = retreiveAndSetInfo(token, call);
				if (!info.getUserName().equals(values[0])) {
					log.info("rejecting call to {}, username {} not valid for token {}",context.name(),values[0],token);
					request_not_authorized_error.fire(context.name()+": username "+values[0]+" not valid for token "+token);
				}
				return true;
			}else {
				log.info("rejecting call to {}, authorization required",context.name(),token);
				request_not_authorized_error.fire(context.name()+": authorization required");
			}
				
		} 

		log.trace("token is "+token);

		if (token!=null){
			retreiveAndSetInfo(token, call);
			return true;
		} 
		log.info("invalid token, returning false");
		return false;
	}

	@Override
	public String toString() {
		return request_validation;
	}
	
	private UserInfo retreiveAndSetInfo(String token, RequestEvent call){
		ScopeProvider.instance.set("/"+call.context().container().configuration().infrastructure());
		AuthorizationEntry authEntry = null;
		try{
			authEntry = authorizationService().build().get(token);	
		}catch(ObjectNotFound onf){
			log.warn("rejecting call to {}, invalid token {}",context.name(),token);
			invalid_request_error.fire(context.name()+" invalid token : "+token);
		}catch(Exception e){
			log.error("error contacting authorization service",e);
			internal_server_error.fire("error contacting authorization service");
		}
		
		//check if the called service is banned for the AuthorizationEntry
		if (authEntry.getBannedServices().contains(
				new BannedService(call.context().configuration().serviceClass(), call.context().configuration().name()))){
					log.error("rejecting call to {}, invalid token {}: service is banned for this token",context.name(),token);
					invalid_request_error.fire("rejecting call to "+context.name()+", invalid token "+token+": service is banned for this token");
		}
		
		UserInfo info = new UserInfo(authEntry.getUserName(), authEntry.getRoles(), authEntry.getBannedServices());
	
		AuthorizationProvider.instance.set(info);
		validateScope(authEntry.getScope());
		log.info("retrieved request authorization info "+AuthorizationProvider.instance.get()+" in scope "+ScopeProvider.instance.get());
		SecurityTokenProvider.instance.set(token);
		return info;
	}
			
}
