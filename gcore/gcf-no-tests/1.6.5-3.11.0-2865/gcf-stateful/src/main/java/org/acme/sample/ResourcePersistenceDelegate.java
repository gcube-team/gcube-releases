package org.acme.sample;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


import org.gcube.common.core.persistence.GCUBEWSFilePersistenceDelegate;

/**
 * Resource Persistence Delegate 
 * 
 * Implements Resource RPs persistence on file
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public class ResourcePersistenceDelegate extends GCUBEWSFilePersistenceDelegate<Resource>{

	protected void onLoad(Resource resource,ObjectInputStream ois) throws Exception {
		
		super.onLoad(resource,ois);
		resource.setName((String)ois.readObject());
		int visits = (Integer)ois.readObject();
		for (int i =0; i<visits; i++) resource.addVisit();
	}
	
	protected void onStore(Resource resource,ObjectOutputStream oos) throws Exception {
		
		super.onStore(resource,oos);
		oos.writeObject(resource.getName());
		oos.writeObject(resource.getVisits());
		
	}
}
