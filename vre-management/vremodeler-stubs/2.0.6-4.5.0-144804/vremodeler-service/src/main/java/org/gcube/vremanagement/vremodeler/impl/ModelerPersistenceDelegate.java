package org.gcube.vremanagement.vremodeler.impl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.common.core.persistence.GCUBEWSFilePersistenceDelegate;
import org.gcube.vremanagement.vremodeler.utils.reports.DeployReport;


public class ModelerPersistenceDelegate extends GCUBEWSFilePersistenceDelegate<ModelerResource>{

	
	protected synchronized void onLoad(ModelerResource resource, ObjectInputStream ois) throws Exception {
		super.onLoad(resource, ois);
		resource.setId((String)ois.readObject());
		resource.setDeployReport((DeployReport)ois.readObject());
		resource.setUseCloud(ois.readBoolean());
		if (resource.isUseCloud())resource.setNumberOfVMsForCloud(ois.readInt());
	}
	
	
	protected synchronized void onStore(ModelerResource resource,ObjectOutputStream oos) throws Exception {
		super.onStore(resource, oos);
		oos.writeObject(resource.getId());
		oos.writeObject(resource.getDeployReport());
		oos.writeBoolean(resource.isUseCloud());
		if (resource.isUseCloud())oos.writeInt(resource.getNumberOfVMsForCloud());
	}
}
