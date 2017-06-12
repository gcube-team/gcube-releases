package org.gcube.datatransfer.scheduler.impl.state;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


import org.gcube.common.core.persistence.GCUBEWSFilePersistenceDelegate;

public class SchedulerResourcePersistenceDelegate extends GCUBEWSFilePersistenceDelegate<SchedulerResource>{

	protected void onLoad(SchedulerResource resource,ObjectInputStream ois) throws Exception {
		
		super.onLoad(resource,ois);
		resource.setName((String)ois.readObject());	
		resource.setActiveTransfers((String[]) ois.readObject());
		resource.setNumOfActiveTransfers((String)ois.readObject());
		resource.setCheckDBThread((String)ois.readObject());

	}
	
	protected void onStore(SchedulerResource resource,ObjectOutputStream oos) throws Exception {
		
		super.onStore(resource,oos);
		oos.writeObject(resource.getName());
		oos.writeObject(resource.getActiveTransfers());
		oos.writeObject(resource.getNumOfActiveTransfers());
		oos.writeObject(resource.getCheckDBThread());
	}
}
