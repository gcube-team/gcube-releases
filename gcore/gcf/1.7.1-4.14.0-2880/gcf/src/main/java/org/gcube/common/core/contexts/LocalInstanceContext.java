package org.gcube.common.core.contexts;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.common.PlatformDescription;
import org.gcube.common.core.utils.logging.GCUBELog;

public class LocalInstanceContext {

	private GCUBELog logger = new GCUBELog(LocalInstanceContext.class);
	
	private Map<String, GCUBERunningInstance> instances = new HashMap<String,GCUBERunningInstance>();
	
	private static LocalInstanceContext context = new LocalInstanceContext();

	private File storageLocation;
	
	private LocalInstanceContext() {
		this.storageLocation = new File(GHNContext.getContext().getVirtualPlatformsLocation() + File.separator + ".instances");
		try {
			this.loadInstances();
		} catch (Exception e) {
			logger.warn("Failed to load the local instances, starting from an empty list", e);		
		}
	}

	/**
	 * 
	 * @return the local context
	 */
	public static LocalInstanceContext getContext() {
		return context;
	}
	
	/**
	 * Registers the instance in the context
	 * @param instance
	 * @throws Exception
	 */
	public synchronized void registerInstance(GCUBERunningInstance instance) throws Exception {
		this.instances.put(instance.getID(),instance);
		instance.store(new FileWriter(new File(this.storageLocation + File.separator + instance.getID() + ".xml")));
	}


	/**
	 * Unregisters the instance from the context
	 * @param instance
	 */
	public synchronized void unregisterInstance(GCUBERunningInstance instance) {
		this.instances.remove(instance.getID());
		new File(this.storageLocation + File.separator + instance.getID() + ".xml").delete();
	}
	/**
	 * Gets the registered instance given its ID
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public synchronized GCUBERunningInstance getInstance(String id) throws Exception {
		if (this.instances.containsKey(id))
			return this.instances.get(id);
		else
			throw new Exception("Instance not found");
	}
	
	/**
	 * Gets a registered instance
	 * @param clazz the class of the instance
	 * @param name the name of the instance
	 * @return the instance
	 * @throws Exception
	 */
	public synchronized GCUBERunningInstance getInstance(String clazz, String name) throws Exception{
		for (GCUBERunningInstance ri : this.instances.values()) {
			if ((ri.getServiceClass().equalsIgnoreCase(clazz))
				&& (ri.getServiceName().equalsIgnoreCase(name)))
				return ri;
		}
		throw new Exception("Instance not found");
	}
	
	/**
	 * Gets all the instances registered in this context
	 * @return the instances
	 */ 
	public synchronized Collection<GCUBERunningInstance> getAllInstances() {
		return Collections.unmodifiableCollection(this.instances.values());
	}
	
	/**
	 * Gets all the instances belonging to the platform
	 * @param platform the filtering platform
	 * @return the instances
	 */
	public synchronized List<GCUBERunningInstance> getAllInstancesForPlatform(PlatformDescription platform) {
		List<GCUBERunningInstance> ret = new ArrayList<GCUBERunningInstance>();
		for (GCUBERunningInstance ri : this.instances.values()) {
			if ( (ri.getPlatform().getName().equals(platform.getName())) 
					&& (ri.getPlatform().getVersion() == platform.getVersion()) 
					&& (ri.getPlatform().getMinorVersion() == platform.getMinorVersion()))
				ret.add(ri);
		}
		return ret;
	}
	
	private void loadInstances() throws Exception {
		if (!this.storageLocation.exists()) {
			if (!this.storageLocation.mkdirs())
				throw new Exception("Unable to create the storage location");
			return;
		}
		File[] serializedInstances = this.storageLocation.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (file.getName().endsWith(".xml"))
					return true;
				else
					return false;
			}
		});
		for (File serializedInstance : serializedInstances) {
			try {
				GCUBERunningInstance instance = GHNContext.getImplementation(GCUBERunningInstance.class);
				instance.load(new FileReader(serializedInstance));
				this.instances.put(instance.getID(), instance);
			} catch (Exception e) {
				logger.warn("Failed to laod from " + serializedInstance.getAbsolutePath(),e);
			}
		}
	}
}
