package org.gcube.vremanagement.vremodeler.consumers;

import java.io.StringReader;
import javax.xml.namespace.QName;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GHNContext.Status;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.resources.GCUBEHostingNode;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Ghn;
import org.gcube.vremanagement.vremodeler.resources.handlers.GHNHandler;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;


public class GHNConsumer extends BaseNotificationConsumer{
	
	public static GCUBENotificationTopic  ghnTopic;
	
	static{
		ghnTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry","GHN"));
		ghnTopic.setUseRenotifier(false);
	}
	
	private GCUBELog logger= new GCUBELog(GHNConsumer.class);
		
	 
		
	private GCUBEScope scope;
	
	public GHNConsumer(GCUBEScope scope){
		super();
		this.scope=scope;
	}
	
	public void onNotificationReceived(NotificationEvent event){
		try{
			//logger.trace("notification received");
			ServiceContext.getContext().setScope(this.scope);
			String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
			String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
			//logger.trace("ghn notification received id: "+id+" op: "+operation);
			
			Dao<Ghn, String> ghnDao =
		            DaoManager.createDao(DBInterface.connect(), Ghn.class);
			
			if (operation.equals("update")){
				GCUBEHostingNode gcubeHostingNode= GHNContext.getImplementation(GCUBEHostingNode.class);
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				gcubeHostingNode.load(new StringReader(profile));
				if (ghnDao.idExists(gcubeHostingNode.getID())){
					if (gcubeHostingNode.getNodeDescription().getStatus()!=Status.CERTIFIED)
						new GHNHandler().drop(gcubeHostingNode.getID());
				}else 
					if (gcubeHostingNode.getNodeDescription().getStatus()==Status.CERTIFIED){
						Ghn ghn = new Ghn(gcubeHostingNode.getID(), gcubeHostingNode.getNodeDescription().getName(),gcubeHostingNode.getSite().getLocation(), 
								gcubeHostingNode.getSite().getCountry(), gcubeHostingNode.getSite().getDomain(), gcubeHostingNode.getNodeDescription().getMemory().getAvailable(), gcubeHostingNode.getNodeDescription().getLocalAvailableSpace(), false); 
						ghn.setSecurityEnabled(gcubeHostingNode.getNodeDescription().isSecurityEnabled());
						new GHNHandler().add(ghn);
					}
				
			} else if (operation.equals("destroy")){
				logger.trace("removing a GHN from DB");
				new GHNHandler().drop(id);
			} else if (operation.equals("create")){
				GCUBEHostingNode gcubeHostingNode= GHNContext.getImplementation(GCUBEHostingNode.class);
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				gcubeHostingNode.load(new StringReader(profile));
				if (gcubeHostingNode.getNodeDescription().getStatus()==Status.CERTIFIED && !ghnDao.idExists(gcubeHostingNode.getID())){
					Ghn ghn = new Ghn(gcubeHostingNode.getID(), gcubeHostingNode.getNodeDescription().getName(),gcubeHostingNode.getSite().getLocation(), 
							gcubeHostingNode.getSite().getCountry(), gcubeHostingNode.getSite().getDomain(), gcubeHostingNode.getNodeDescription().getMemory().getAvailable(), gcubeHostingNode.getNodeDescription().getLocalAvailableSpace(), false); 
					ghn.setSecurityEnabled(gcubeHostingNode.getNodeDescription().isSecurityEnabled());
					new GHNHandler().add(ghn);
				}
			}

		}catch(Exception e){logger.error("error in notification received",e);}
	}
}
