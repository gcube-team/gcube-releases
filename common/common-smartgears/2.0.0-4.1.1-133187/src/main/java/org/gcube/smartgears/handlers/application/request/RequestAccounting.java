package org.gcube.smartgears.handlers.application.request;

import static org.gcube.smartgears.Constants.called_method_header;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.accounting.datamodel.UsageRecord.OperationResult;
import org.gcube.accounting.datamodel.usagerecords.ServiceUsageRecord;
import org.gcube.accounting.persistence.AccountingPersistence;
import org.gcube.accounting.persistence.AccountingPersistenceFactory;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.smartgears.Constants;
import org.gcube.smartgears.context.application.ApplicationContext;
import org.gcube.smartgears.handlers.application.RequestEvent;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.gcube.smartgears.handlers.application.ResponseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = Constants.request_accounting)
public class RequestAccounting extends RequestHandler {

	private static Logger log = LoggerFactory.getLogger(RequestAccounting.class);

	private static ThreadLocal<Long> startCallThreadLocal = new ThreadLocal<Long>(); 
			
	@Override
	public void handleRequest(RequestEvent e) {
		ApplicationContext context = e.context();
		String calledMethod = e.request().getHeader(called_method_header);
		if (calledMethod==null){
			calledMethod = e.request().getRequestURI().substring(e.request().getContextPath().length());
			if (calledMethod.startsWith("/"))
				calledMethod = calledMethod.replaceFirst("/","");
		}
		CalledMethodProvider.instance.set(calledMethod);
		String caller = AuthorizationProvider.instance.get()!=null? AuthorizationProvider.instance.get().getClient().getId(): "UNKNOWN";		
		startCallThreadLocal.set(System.currentTimeMillis());
		log.info("REQUEST START ON {}:{}({}) CALLED FROM {}@{} IN SCOPE {} ", 
				context.configuration().name(),context.configuration().serviceClass(), CalledMethodProvider.instance.get(), 
				caller, e.request().getRemoteHost(),  ScopeProvider.instance.get());
	}

	@Override
	public void handleResponse(ResponseEvent e) {
		ApplicationContext context = e.context();
		String caller = AuthorizationProvider.instance.get()!=null? AuthorizationProvider.instance.get().getClient().getId(): "UNKNOWN";
		String callerQualifier = AuthorizationProvider.instance.get()!=null? AuthorizationProvider.instance.get().getTokenQualifier(): "UNKNOWN";
		//retieves caller Ip when there is a proxy
		String callerIp = e.request().getHeader("x-forwarded-for");
        if(callerIp==null)
        	callerIp=e.request().getRemoteHost();
                
		generateAccounting(caller,callerQualifier,callerIp==null?"UNKNOWN":callerIp , context);
		
		log.info("REQUEST SERVED ON {}:{}({}) CALLED FROM {}@{} IN SCOPE {} FINISHED IN {} millis", 
				context.configuration().name(),context.configuration().serviceClass(), CalledMethodProvider.instance.get(),
				caller, callerIp, ScopeProvider.instance.get(), System.currentTimeMillis()-startCallThreadLocal.get());
		startCallThreadLocal.remove();
		CalledMethodProvider.instance.reset();
	}

	void generateAccounting(String caller, String callerQualifier, String remoteHost,  ApplicationContext context){
		AccountingPersistenceFactory.setFallbackLocation(context.container().persistence().location());
		AccountingPersistence persistence = AccountingPersistenceFactory.getPersistence();
		ServiceUsageRecord serviceUsageRecord = new ServiceUsageRecord();
		try{
			
			serviceUsageRecord.setConsumerId(caller);
			serviceUsageRecord.setCallerQualifier(callerQualifier);
			serviceUsageRecord.setScope(ScopeProvider.instance.get());
			serviceUsageRecord.setServiceClass(context.configuration().serviceClass());
			serviceUsageRecord.setServiceName(context.configuration().name());

			serviceUsageRecord.setHost(context.container().configuration().hostname()+":"+context.container().configuration().port());
			serviceUsageRecord.setCalledMethod(CalledMethodProvider.instance.get());
			serviceUsageRecord.setCallerHost(remoteHost);
			serviceUsageRecord.setOperationResult(OperationResult.SUCCESS);
			serviceUsageRecord.setDuration(System.currentTimeMillis()-startCallThreadLocal.get());
			persistence.account(serviceUsageRecord);
			
		}catch(Exception ex){
			log.warn("invalid record passed to accounting ",ex);
		}
	}

}
