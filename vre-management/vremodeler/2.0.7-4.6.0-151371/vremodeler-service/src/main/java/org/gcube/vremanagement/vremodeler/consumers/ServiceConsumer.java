package org.gcube.vremanagement.vremodeler.consumers;

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.resources.GCUBEService;
import org.gcube.common.core.resources.service.MainPackage;
import org.gcube.common.core.resources.service.Package;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Service;
import org.gcube.vremanagement.vremodeler.resources.handlers.ServiceHandler;

public class ServiceConsumer extends BaseNotificationConsumer{

		private GCUBELog logger= new GCUBELog(ServiceConsumer.class);
		
		public static final GCUBENotificationTopic  serviceTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry","Service"));
		
		static{
			serviceTopic.setUseRenotifier(false);
		}
		
		private GCUBEScope scope;
		
		public ServiceConsumer(GCUBEScope scope){
			super();
			this.scope=scope;
		}
		
		public void onNotificationReceived(NotificationEvent event){
			try{
				ScopeProvider.instance.set(scope.toString());
				String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
				String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
				//logger.debug("notification received for service "+id+" and operation "+operation+" in scope "+scope);
				
				if (operation.equals("update") || operation.equals("create")){
					GCUBEService gcubeService= GHNContext.getImplementation(GCUBEService.class);
					String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
					gcubeService.load(new StringReader(profile));
					String packageName =null;
					String packageVersion = null;
					for (Package packageSW : gcubeService.getPackages()){
						if (packageSW instanceof MainPackage){
							packageName = packageSW.getName();
							packageVersion = packageSW.getVersion();
							break;
						}else if (packageName==null || packageVersion==null){
							packageName = packageSW.getName();
							packageVersion = packageSW.getVersion(); 
						}
					}
					Service service = new Service(gcubeService.getID(), gcubeService.getServiceClass(), gcubeService.getServiceName(), gcubeService.getVersion(), packageName, packageVersion );
					new ServiceHandler().add(service);
				}else if (operation.compareTo("destroy")==0){
					logger.trace("notification received for service with operation destroy");
					new ServiceHandler().drop(id);
				}

			}catch(Exception e){logger.error("error in service notification",e);}
			finally {
				ScopeProvider.instance.reset();
			}
		}
		
	
}
