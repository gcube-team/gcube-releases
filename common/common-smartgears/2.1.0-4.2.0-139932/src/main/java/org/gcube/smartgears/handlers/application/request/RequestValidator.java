package org.gcube.smartgears.handlers.application.request;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.smartgears.Constants.scope_header;
import static org.gcube.smartgears.Constants.token_header;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_failed_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_unavailable_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.internal_server_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.invalid_request_error;

import java.io.IOException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.PolicyUtils;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.ServiceIdentifier;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.handlers.application.ResponseEvent;
import org.gcube.smartgears.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = Constants.request_validation)
public class RequestValidator extends RequestHandler {

	private static Logger log = LoggerFactory.getLogger(RequestValidator.class);

	private ApplicationContext context;

	@Override
	public String getName() {
		return Constants.request_validation;
	}
	
	@Override
	public void handleRequest(RequestEvent call) {

		log.trace("executing request validator ON REQUEST");

		context = call.context();

		validateAgainstLifecycle(call);

		if (!validateToken(call)){
			String scope = call.request().getParameter(scope_header)==null?	call.request().getHeader(scope_header):call.request().getParameter(scope_header);
			validateScope(scope);
			log.info("received call to {} in context {}",call.uri(),scope);
		}

	}

	@Override
	public void handleResponse(ResponseEvent e){
		SecurityTokenProvider.instance.reset();
		AuthorizationProvider.instance.reset();
		ScopeProvider.instance.reset();
		log.debug("resetting all the Thread local for this call.");
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
			log.warn("rejecting unscoped call to {}",context.name());
			invalid_request_error.fire("call is unscoped"); 
		}

		if (!context.container().configuration().allowedContexts().contains(scope)) {
			log.warn("rejecting call to {} in invalid context {}, allowed context are {}",context.name(),scope,context.container().configuration().allowedContexts());
			invalid_request_error.fire(context.name()+" cannot be called in scope "+scope);
		}

		ScopeProvider.instance.set(scope);
	}

	private boolean validateToken(RequestEvent call){


		String token = call.request().getParameter(token_header)==null?	call.request().getHeader(token_header):call.request().getParameter(token_header);

		String scope = call.request().getParameter(scope_header)==null?	call.request().getHeader(scope_header):call.request().getParameter(scope_header);

		if (token == null && scope==null){
			log.warn("rejecting call to {}, authorization required",context.name(),token);
			if (call.context().container().configuration().authenticationEnpoint()==null){
				if (call.request().getHeader(Constants.authorization_header)!=null){
					String basicAuthorization  = call.request().getHeader(Constants.authorization_header);
					String base64Credentials = basicAuthorization.substring("Basic".length()).trim();
					String credentials = new String(DatatypeConverter.parseBase64Binary(base64Credentials));
					// credentials = username:password
					final String[] values = credentials.split(":",2);
					token = values[1];
					ClientInfo info = retreiveAndSetInfo(token, call);
					if (!(info instanceof UserInfo) || !info.getId().equals(values[0])) {
						log.warn("rejecting call to {}, username {} not valid for token {}",context.name(),values[0],token);
						RequestError.request_not_authorized_error.fire(context.name()+": username "+values[0]+" not valid for token "+token);
					}
					return true;
				}else {
					log.warn("rejecting call to {}, authorization required",context.name(),token);
					RequestError.request_not_authorized_error.fire(context.name()+": authorization required");
				}
			}else {
				log.info("authorization enpoint found on configuration, redirecting the call");
				String recallLocation = String.format("http://%s:%d%s", call.context().container().configuration().hostname(), call.context().container().configuration().port(), call.uri());				
				//call.response().setHeader("Allowed-Contexts", call.context().container().configuration().allowedContexts().toString());
				try {
					call.response().sendRedirect(context.container().configuration().authenticationEnpoint()+"?Recall-Location="+recallLocation);
				} catch (IOException e) {
					log.error("errror redirecting call",e );
				}
				return false;
			}

		} 

		log.trace("token is "+token);

		if (token!=null){
			retreiveAndSetInfo(token, call);
			return true;
		} 
		log.info("invalid token, checking the context");
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}

	private ClientInfo retreiveAndSetInfo(String token, RequestEvent call){
		log.info("accessing  validator with token {} ", token);
		AuthorizationEntry authEntry = null;
		try{
			authEntry = authorizationService().get(token);	
		}catch(ObjectNotFound onf){
			log.warn("rejecting call to {}, invalid token {}",context.name(),token);
			invalid_request_error.fire(context.name()+" invalid token : "+token);
		}catch(Exception e){
			log.error("error contacting authorization service",e);
			internal_server_error.fire("error contacting authorization service");
		}

		ServiceIdentifier serviceIdentifier = Utils.getServiceInfo(call.context()).getServiceIdentifier();

		for (Policy policy: authEntry.getPolicies())
			if (PolicyUtils.isPolicyValidForClient(policy.getServiceAccess(), serviceIdentifier)){
				log.error("rejecting call to {} : {} is not allowed to contact the service ",context.name(),authEntry.getClientInfo().getId());
				invalid_request_error.fire("rejecting call to "+context.name()+": "+authEntry.getClientInfo().getId()+" is not allowed to contact the service");
			}	

		AuthorizationProvider.instance.set(new Caller(authEntry.getClientInfo(), authEntry.getQualifier()));
		validateScope(authEntry.getContext());
		log.info("retrieved request authorization info {} in scope {} ", AuthorizationProvider.instance.get(), authEntry.getContext());
		SecurityTokenProvider.instance.set(token);
		return authEntry.getClientInfo();
	}
	
	

}
