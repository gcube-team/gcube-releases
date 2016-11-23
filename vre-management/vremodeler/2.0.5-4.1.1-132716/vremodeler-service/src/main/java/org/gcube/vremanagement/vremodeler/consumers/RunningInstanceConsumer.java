package org.gcube.vremanagement.vremodeler.consumers;

import java.io.StringReader;
import javax.xml.namespace.QName;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.BaseNotificationConsumer;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.NotificationEvent;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.ServiceContext;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.Ghn;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.RunningInstance;
import org.gcube.vremanagement.vremodeler.resources.handlers.RunningInstancesHandler;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

public class RunningInstanceConsumer extends BaseNotificationConsumer{
	
	private GCUBELog logger= new GCUBELog(RunningInstanceConsumer.class);
	
	public static final GCUBENotificationTopic riTopic= new GCUBENotificationTopic(new QName("http://gcube-system.org/namespaces/informationsystem/registry","RunningInstance"));
	
	static{
		riTopic.setPrecondition("//operationType[text()='create']");
		riTopic.setUseRenotifier(false);
	}
	
	private GCUBEScope scope;
	
	public RunningInstanceConsumer(GCUBEScope scope){
		super();
		this.scope=scope;
	}
	
	public void onNotificationReceived(NotificationEvent event){
		try{
			logger.trace("notification received for RI");
			ServiceContext.getContext().setScope(this.scope);
			String id= event.getPayload().getMessage()[0].getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
			String operation=event.getPayload().getMessage()[0].getChildNodes().item(2).getChildNodes().item(0).getNodeValue();
			if (operation.compareTo("create")==0){
				logger.trace("adding a new RI in DB");
				GCUBERunningInstance gcubeRunningInstance= GHNContext.getImplementation(GCUBERunningInstance.class);
				String profile=event.getPayload().getMessage()[0].getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
				gcubeRunningInstance.load(new StringReader(profile));
				RunningInstance ri= new RunningInstance(gcubeRunningInstance.getID(), gcubeRunningInstance.getServiceClass(), gcubeRunningInstance.getServiceName());
				Dao<Ghn, String> ghnDao = DaoManager.createDao(DBInterface.connect(), Ghn.class); 
				new RunningInstancesHandler(ghnDao.queryForId(gcubeRunningInstance.getGHNID())).add(ri);
			} else if (operation.compareTo("destroy")==0){
				logger.trace("removing a RI from DB");
				new RunningInstancesHandler().drop(id);
			}

		}catch(Exception e){logger.error("error in notification received",e);}
	}

}
