package org.gcube.informationsystem.notifier.impl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import org.gcube.common.core.persistence.GCUBEWSFilePersistenceDelegate;
import org.gcube.informationsystem.notifier.impl.entities.Consumer;
import org.gcube.informationsystem.notifier.impl.entities.Producer;
import org.gcube.informationsystem.notifier.util.EPR;
import org.gcube.informationsystem.notifier.util.RegistrationEventHandlerImpl;
import org.gcube.informationsystem.notifier.util.TopicMapping;

/**
 * @author Andrea Manzi (ISTI-CNR)
 * 
 *
 */
public class NotifierPersistenceDelegate extends GCUBEWSFilePersistenceDelegate<NotifierResource> {
	
	
	@SuppressWarnings("unchecked")
	protected void onLoad(NotifierResource resource, ObjectInputStream ois) throws Exception {
		super.onLoad(resource, ois);
		resource.setTopicMappingList((Hashtable<String,TopicMapping<Producer,Consumer,RegistrationEventHandlerImpl>>)ois.readObject());
	}
	
	protected void onStore(NotifierResource resource,ObjectOutputStream oos) throws Exception {
		super.onStore(resource, oos);
		synchronized (resource.getTopicMappingList()) {
			oos.writeObject(resource.getTopicMappingList());
		}
	}
}
