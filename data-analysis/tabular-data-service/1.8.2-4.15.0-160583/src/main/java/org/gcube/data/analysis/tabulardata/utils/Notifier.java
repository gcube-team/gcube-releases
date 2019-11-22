package org.gcube.data.analysis.tabulardata.utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.AffectedObject;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.UpdateEvent;
import org.gcube.data.analysis.tabulardata.metadata.notification.StorableNotification;
import org.gcube.data.analysis.tabulardata.metadata.notification.UpdateTabularResourceLink;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RelationLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class Notifier {

	private static Logger logger = LoggerFactory.getLogger(Notifier.class);


	public synchronized List<StorableNotification> onLinkUpdated(List<RelationLink> links){
		List<StorableNotification> notifications = new ArrayList<>();
		if (links.size()>0){
			for (RelationLink link : links){
				StorableNotification notification = new StorableNotification(link.getLinksToTabulaResource(), 
						AffectedObject.TABULAR_RESOURCE, UpdateEvent.NEW_VERSION, new UpdateTabularResourceLink());
				logger.trace("adding notification: "+notification.toString());
				notifications.add(notification);
			}
		}
		return notifications;
	}
}
