package org.gcube.smartgears.handlers.application.request;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.smartgears.Constants.oauth_secret;
import static org.gcube.smartgears.Constants.scope_header;
import static org.gcube.smartgears.Constants.token_header;
import static org.gcube.smartgears.handlers.application.request.RequestError.internal_server_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.invalid_request_error;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.handlers.application.ResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = Constants.request_context_retriever)
public class RequestContextRetriever extends RequestHandler {

	private static Logger log = LoggerFactory.getLogger(RequestContextRetriever.class);
	
	
	@Override
	public String getName() {
		return Constants.request_context_retriever;
	}

	@Override
	public void handleRequest(RequestEvent call) {
		String token = call.request().getParameter(token_header)==null?	call.request().getHeader(token_header):call.request().getParameter(token_header);
		String scope = call.request().getParameter(scope_header)==null?	call.request().getHeader(scope_header):call.request().getParameter(scope_header);
		
		if (token==null && call.request().getHeader(Constants.authorization_header)!=null){
			String basicAuthorization  = call.request().getHeader(Constants.authorization_header);
			String base64Credentials = basicAuthorization.substring("Basic".length()).trim();
			String credentials = new String(DatatypeConverter.parseBase64Binary(base64Credentials));
			// credentials = username:password
			final String[] values = credentials.split(":",2);
			token = values[1];
		}
			
		if (token==null && scope==null && call.request().getParameter(oauth_secret)!=null)
			token = call.request().getParameter(oauth_secret);
		
		//Gives priority to the token
		if (token!=null)
			this.retreiveAndSetInfo(token, call);
		else if (scope!=null)
			ScopeProvider.instance.set(scope);
		
	}

	@Override
	public void handleResponse(ResponseEvent e) {
		SecurityTokenProvider.instance.reset();
		AuthorizationProvider.instance.reset();
		ScopeProvider.instance.reset();
		log.debug("resetting all the Thread local for this call.");
	}
	
	private void retreiveAndSetInfo(String token, RequestEvent call){
		log.info("retrieving context using token {} ", token);
		AuthorizationEntry authEntry = null;
		try{
			authEntry = authorizationService().get(token);	
		}catch(ObjectNotFound onf){
			log.warn("rejecting call to {}, invalid token {}",call.context().name(),token);
			invalid_request_error.fire(call.context().name()+" invalid token : "+token);
		}catch(Exception e){
			log.error("error contacting authorization service",e);
			internal_server_error.fire("error contacting authorization service");
		}
		
		AuthorizationProvider.instance.set(new Caller(authEntry.getClientInfo(), authEntry.getQualifier()));
		SecurityTokenProvider.instance.set(token);
		ScopeProvider.instance.set(authEntry.getContext());
		log.info("retrieved request authorization info {} in scope {} ", AuthorizationProvider.instance.get(), authEntry.getContext());
	}
}
