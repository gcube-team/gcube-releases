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
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Collection;
import org.gcube.vremanagement.vremodeler.resources.handlers.CollectionHandler;

public class CollectionConsumer extends BaseNotificationConsumer{
	
	private GCUBELog logger= new GCUBELog(GHNConsumer.class);
	
	public static final GCUBENotificationTopic collectionTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry",GCUBEGenericResource.TYPE));
	
	static{
		collectionTopic.setUseRenotifier(false);
		collectionTopic.setPrecondition("//profile[contains(.,'<SecondaryType>GCUBECollection</SecondaryType>') and contains(.,'<user>true</user>')]");
	}
	
	private GCUBEScope scope;
	
	public CollectionConsumer(GCUBEScope scope){
		super();
		this.scope=scope;
	}
	
	public void onNotificationReceived(NotificationEvent event){
		try{
			ScopeProvider.instance.set(scope.toString());
			//logger.debug("notification received in scope "+scope);
			ServiceContext.getContext().setScope(this.scope);
			String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
			String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
						
			if (operation.compareTo("create")==0){
				logger.trace("adding a new Collection in DB");
				GCUBEGenericResource collection= GHNContext.getImplementation(GCUBEGenericResource.class);
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				collection.load(new StringReader(profile));
				Collection coll = new Collection(collection.getID(), collection.getName(), collection.getDescription());
				new CollectionHandler().add(coll);
			} else if (operation.compareTo("destroy")==0){
				logger.trace("removing a collection from DB");
				new CollectionHandler().drop(id);
			}

		}catch(Exception e){logger.error("error in notification received",e);}
		finally {
			ScopeProvider.instance.reset();
		}
	}

}
