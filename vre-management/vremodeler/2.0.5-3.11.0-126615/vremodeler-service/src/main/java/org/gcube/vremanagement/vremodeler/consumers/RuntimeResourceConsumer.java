package org.gcube.vremanagement.vremodeler.consumers;

import java.io.StringReader;
import javax.xml.namespace.QName;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.resources.GCUBERuntimeResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RuntimeResource;
import org.gcube.vremanagement.vremodeler.resources.handlers.RuntimeResourceHandler;

public class RuntimeResourceConsumer extends BaseNotificationConsumer{

	private GCUBELog logger= new GCUBELog(RuntimeResourceConsumer.class);
	
	public static final GCUBENotificationTopic  runtimeResourceTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry","RuntimeResource"));
	
	static{
		runtimeResourceTopic.setUseRenotifier(false);
	}
	
	private GCUBEScope scope;
	
	public RuntimeResourceConsumer(GCUBEScope scope){
		super();
		this.scope=scope;
	}
	
	public void onNotificationReceived(NotificationEvent event){
		try{
			ServiceContext.getContext().setScope(this.scope);
			String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
			String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
			
			logger.info("notification received for runtime resource "+id+" and operation "+operation);
			
			if (operation.compareTo("update")==0){
				logger.trace("notification received for runtime with id "+id+" in scope "+scope.toString());
				GCUBERuntimeResource gcubeRuntimeResource= GHNContext.getImplementation(GCUBERuntimeResource.class);
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				gcubeRuntimeResource.load(new StringReader(profile));
				RuntimeResource runtimeResource = new RuntimeResource(gcubeRuntimeResource.getID(), gcubeRuntimeResource.getName(), gcubeRuntimeResource.getCategory(), gcubeRuntimeResource.getDescription());
				new RuntimeResourceHandler().add(runtimeResource);
			 	
			}else if (operation.compareTo("create")==0){
				logger.trace("notification received for runtime resource with operation create");
				
				GCUBERuntimeResource gcubeRuntimeResource= GHNContext.getImplementation(GCUBERuntimeResource.class);
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				gcubeRuntimeResource.load(new StringReader(profile));	
				RuntimeResource runtimeResource = new RuntimeResource(gcubeRuntimeResource.getID(), gcubeRuntimeResource.getName(), gcubeRuntimeResource.getCategory(), gcubeRuntimeResource.getDescription());
				new RuntimeResourceHandler().add(runtimeResource);
			}else if (operation.compareTo("destroy")==0){
				logger.trace("notification received for runtime resource with operation destroy");
				new RuntimeResourceHandler().drop(id);
			}

		}catch(Exception e){logger.error("error in runtime notification",e);}
	}
	
	
}
