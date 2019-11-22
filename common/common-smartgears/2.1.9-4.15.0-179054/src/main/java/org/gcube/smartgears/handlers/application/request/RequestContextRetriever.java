package org.gcube.smartgears.handlers.application.request;

import static org.gcube.common.authorization.client.Constants.authorizationService;
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

	private static final String BEARER_AUTH_PREFIX ="Bearer"; 
	private static final String BASIC_AUTH_PREFIX ="Basic"; 


	@Override
	public String getName() {
		return Constants.request_context_retriever;
	}

	@Override
	public void handleRequest(RequestEvent call) {
		String token = call.request().getParameter(token_header)==null?	call.request().getHeader(token_header):call.request().getParameter(token_header);
		String scope = call.request().getParameter(scope_header)==null?	call.request().getHeader(scope_header):call.request().getParameter(scope_header);

		if (token==null && call.request().getHeader(Constants.authorization_header)!=null){

			String authorization  = call.request().getHeader(Constants.authorization_header);

			if (authorization.contains(BASIC_AUTH_PREFIX)) {
				String base64Credentials = authorization.substring(BASIC_AUTH_PREFIX.length()).trim();
				String credentials = new String(DatatypeConverter.parseBase64Binary(base64Credentials));
				// credentials = username:password
				final String[] values = credentials.split(":",2);
				token = values[1];
			} else if (authorization.contains(BEARER_AUTH_PREFIX)) 
				token = authorization.substring(BEARER_AUTH_PREFIX.length()).trim();
		}

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
