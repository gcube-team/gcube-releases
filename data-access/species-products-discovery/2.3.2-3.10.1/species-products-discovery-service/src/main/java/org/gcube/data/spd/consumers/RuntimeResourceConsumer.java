package org.gcube.data.spd.consumers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.context.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuntimeResourceConsumer extends BaseNotificationConsumer {

	private static Logger logger = LoggerFactory.getLogger(RuntimeResourceConsumer.class);
	
	private static List<GCUBEScope> scopes = new ArrayList<GCUBEScope>();
			
	private static RuntimeResourceConsumer cache= new RuntimeResourceConsumer();
		
	private RuntimeResourceConsumer() {
		super();
	}


	public static RuntimeResourceConsumer getConsumer(GCUBEScope scope) {
		if (!scopes.contains(scope)) scopes.add(scope);
		return cache;
	}
	
	public static void releaseConsumer(GCUBEScope scope){
		scopes.remove(scope);
	}

	@Override
	protected synchronized void onNotificationReceived(NotificationEvent event) {
		try{
		String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
		String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
		//String date = event.getPayload().getMessage()[0].getChildNodes().item(3).getChildNodes().item(0).getNodeValue();
		String scopeNotifyed = event.getPayload().getMessage()[0].getChildNodes().item(4).getChildNodes().item(0).getNodeValue();
		ScopeProvider.instance.set(scopeNotifyed);
		logger.info("notification received for runtimeResource "+id+" and operation "+operation+","+scopeNotifyed);
						
		if (operation.equals("destroy")){
			for (GCUBEScope scope: scopes)
				if (scope.equals(scopeNotifyed) || scope.isEnclosedIn(GCUBEScope.getScope(scopeNotifyed)))
					ServiceContext.getContext().removePlugin(scope);
		} else {
			

			String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
			ServiceEndpoint serviceEndpoint = Resources.unmarshal(ServiceEndpoint.class, new StringReader(profile));

			
			if (operation.equals("update")){
				
				ServiceContext.getContext().updatePlugin(serviceEndpoint);
			} else if (operation.equals("create")){
				ServiceContext.getContext().activatePlugin(serviceEndpoint);
			}
		}
		}catch(Exception e){logger.error("error managing notification",e);}
	}
}
