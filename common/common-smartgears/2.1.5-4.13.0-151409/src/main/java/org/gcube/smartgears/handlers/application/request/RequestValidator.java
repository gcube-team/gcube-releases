package org.gcube.smartgears.handlers.application.request;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_failed_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.application_unavailable_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.internal_server_error;
import static org.gcube.smartgears.handlers.application.request.RequestError.invalid_request_error;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.PolicyUtils;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.ServiceIdentifier;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = Constants.request_validation)
public class RequestValidator extends RequestHandler {

	@XmlAttribute(required=false, name="oauth")
	@Deprecated
	boolean oauthCompatibility = false; 
	
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
		
		rejectUnauthorizedCalls(call);
		
		validateScopeCall();
		
		if (SecurityTokenProvider.instance.get()!=null)
			validatePolicy(SecurityTokenProvider.instance.get(), call);

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

	private void validateScopeCall() {
		
		String scope = ScopeProvider.instance.get();
		
		if (scope == null) {
			log.warn("rejecting unscoped call to {}",context.name());
			invalid_request_error.fire("call is unscoped"); 
		}
		
		ScopeBean bean = new ScopeBean(scope);
		
		ContainerConfiguration conf = context.container().configuration();
		if (!conf.allowedContexts().contains(scope) && 
				!(conf.authorizeChildrenContext() && bean.is(Type.VRE) && conf.allowedContexts().contains(bean.enclosingScope().toString()) ) ) {
			log.warn("rejecting call to {} in invalid context {}, allowed context are {}",context.name(),scope,context.container().configuration().allowedContexts());
			invalid_request_error.fire(context.name()+" cannot be called in scope "+scope);
		}
	}

	private void rejectUnauthorizedCalls(RequestEvent call){
		
		String token = SecurityTokenProvider.instance.get();
		String scope = ScopeProvider.instance.get();
		
		if (token == null && scope==null){
			log.warn("rejecting call to {}, authorization required",context.name(),token);
			if (call.context().container().configuration().authenticationEnpoint()==null){
					log.warn("rejecting call to {}, authorization required",context.name(),token);
					RequestError.request_not_authorized_error.fire(context.name()+": authorization required");
			}else {
				log.info("authorization enpoint found on configuration, redirecting the call");
				String recallLocation = String.format("http://%s:%d%s", call.context().container().configuration().hostname(), call.context().container().configuration().port(), call.uri());				
				//call.response().setHeader("Allowed-Contexts", call.context().container().configuration().allowedContexts().toString());
				try {
					call.response().sendRedirect(context.container().configuration().authenticationEnpoint()+"?Recall-Location="+recallLocation);
				} catch (IOException e) {
					log.error("errror redirecting call",e );
				}
			}

		} 
	}

	@Override
	public String toString() {
		return getName();
	}

	private void validatePolicy(String token, RequestEvent call){
		log.info("accessing policy validator with token {} ", token);
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

	}
	
	

}
