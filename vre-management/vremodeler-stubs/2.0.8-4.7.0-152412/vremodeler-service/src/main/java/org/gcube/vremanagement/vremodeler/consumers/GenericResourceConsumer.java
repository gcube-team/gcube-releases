package org.gcube.vremanagement.vremodeler.consumers;

import java.io.StringReader;

import javax.xml.namespace.QName;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.resources.GCUBEGenericResource;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.GenericResource;
import org.gcube.vremanagement.vremodeler.resources.handlers.FunctionalityHandler;
import org.gcube.vremanagement.vremodeler.resources.handlers.GenericResourceHandler;
import org.gcube.vremanagement.vremodeler.resources.kxml.KGCUBEGenericFunctionalityResource;

public class GenericResourceConsumer extends BaseNotificationConsumer{

	private GCUBELog logger= new GCUBELog(GenericResourceConsumer.class);
	
	public static final GCUBENotificationTopic  functionalityTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry","GenericResource"));
		
	static{
		functionalityTopic.setUseRenotifier(true);
	}
	
	private String functionalityResourceId;
	private GCUBEScope scope;
	
	public GenericResourceConsumer(GCUBEScope scope, String resourceId){
		super();
		this.scope=scope;
		this.functionalityResourceId= resourceId;
	}
	
	public void onNotificationReceived(NotificationEvent event){
		try{
			ScopeProvider.instance.set(scope.toString());
			String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
			String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
			
			//logger.debug("notification received for genericResource "+id+" and operation "+operation+ " in scope "+this.scope);
			
			if ((operation.compareTo("update")==0) && id.compareTo(this.functionalityResourceId)==0){
				logger.trace("notification received for functionalityResource with id "+id+" in scope "+scope.toString());
				KGCUBEGenericFunctionalityResource resource= new KGCUBEGenericFunctionalityResource();
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				resource.load(new StringReader(profile));
				//FunctionalityHandler
			 	FunctionalityHandler functionalityHandler= new FunctionalityHandler();
			 	functionalityHandler.clearTable();
			 	for (FunctionalityPersisted functionality: resource.fromResourceToPersistedList())
			 		functionalityHandler.add(functionality);
			}else if (operation.compareTo("create")==0){
				logger.trace("notification received for generic resource with operation create");
				
				GCUBEGenericResource genericResource= GHNContext.getImplementation(GCUBEGenericResource.class);
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				genericResource.load(new StringReader(profile));
				if (genericResource.getName().equals("FuctionalitiesResource")){
					FunctionalityHandler functionalityHandler= new FunctionalityHandler();
					KGCUBEGenericFunctionalityResource functResource=new KGCUBEGenericFunctionalityResource();
					functResource.load(new StringReader(profile));
					this.functionalityResourceId=functResource.getID();
					for (FunctionalityPersisted functionality: functResource.fromResourceToPersistedList())
				 		functionalityHandler.add(functionality);
				} else{
					GenericResource generic = new GenericResource(genericResource.getID(), genericResource.getType(),
							genericResource.getName(), genericResource.getDescription(), genericResource.getBody());
					new GenericResourceHandler().add(generic);
				}
			}else if (operation.compareTo("destroy")==0){
				logger.trace("notification received for generic resource with operation destroy");
				new GenericResourceHandler().drop(id);
			}

		}catch(Exception e){logger.error("error in functionality notification",e);}
		finally {
			ScopeProvider.instance.reset();
		}
	}
	
}
