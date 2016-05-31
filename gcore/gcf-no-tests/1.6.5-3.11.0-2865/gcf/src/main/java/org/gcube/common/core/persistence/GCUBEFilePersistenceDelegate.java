package org.gcube.common.core.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEResourceHome;
import org.gcube.common.core.state.GCUBEStatefulResource;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEStatefulResource.TaskContext;
import org.globus.wsrf.NoSuchResourceException;

/**
 * A partial specialisation of {@link GCUBEPersistenceDelegate} suitable for file storage.
 * 
 * @param <RESOURCE> the resource type.
 * @param <RESOURCEID> the identifier of the resource type.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public abstract class GCUBEFilePersistenceDelegate<RESOURCEID,RESOURCE extends GCUBEStatefulResource<RESOURCEID>> extends GCUBEPersistenceDelegate<RESOURCEID,RESOURCE> {
	
	/** The root directory for file storage */
	private File storageRoot;

	/**
	 * Returns the root directory for file storage.
	 * @return the root.
	 */
	protected File getStorageRoot() {return this.storageRoot;}
	
	/**{@inheritDoc}*/
	public synchronized void initialise(GCUBEResourceHome<? super RESOURCEID,RESOURCEID,RESOURCE> home) throws Exception {
		super.initialise(home);
		//assume delegate and resource ship together hence uses delegate's classloader
		Class<?> resourceClass = getClass().getClassLoader().loadClass(home.getResourceClass());
		storageRoot = home.getServiceContext().getPersistentFile(resourceClass.getSimpleName());
		storageRoot.mkdirs();
	}
		
	/**{@inheritDoc}*/
	protected void onLoad(RESOURCE resource,boolean firstLoad) throws Exception {
		
		File file = this.getFileFromResource(resource);
		if (!file.exists())	throw new NoSuchResourceException();
		
		//rebuild resource form file
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois=null;
		try {
			ois=new ObjectInputStream(fis);
			this.onLoad(resource,ois);
		}
		finally{
			try{if (ois!=null) ois.close();}catch(Exception e){logger.warn("Could not close stream on resource file deserialisation");}
		} 
		
			 
	}	
	
	/**{@inheritDoc}*/
	protected void onStore(RESOURCE resource) throws Exception {
	
		File tmpFile = null;
		ObjectOutputStream oos = null;
		try{
			//safe writing using renameTo which is atomic (should be on most platforms)..
			tmpFile = File.createTempFile("resource",".tmp",this.getStorageRoot());
			FileOutputStream fos = new FileOutputStream(tmpFile);
			oos = new ObjectOutputStream(fos);
			this.onStore(resource,oos);
			oos.flush();
			fos.getFD().sync();//wait until all is written out onto FS
			
			File file = this.getFileFromResource(resource);
			//if (file.exists()) file.delete();//make it safe on all platforms that do not silently overwrite
			
			if (!tmpFile.renameTo(file)) //waiting for first OS in which rename does not overwrites to trigger an otherwise dangereous prior delete
				throw new Exception("Could not rename temporary serialisation file for "+resource.getClass().getSimpleName()+"("+resource.getID()+")");
			
		} 
		catch (Exception e) {
			if (tmpFile!=null) {
				tmpFile.delete();
			}
			throw e;
		}
		finally {
			try {if (oos!=null) oos.close();}catch(Exception e){logger.warn("Could not close stream on resource file serialisation");}	
		}
	}
	/** 
	 * Deserialises the state of resource from a {@link java.io.ObjectInputStream ObjectInputStream}.
	 * Extends in accordance with resource semantics.
	 * 
	 * @param stream the stream.
	 * @throws Exception if the stream could not be processed.
	 * */
	protected void onLoad(RESOURCE resource,ObjectInputStream stream) throws Exception {
		//rebuild task map
		Map<String,String> tasks = (Map) stream.readObject();
		for (Map.Entry<String,String> e: tasks.entrySet())
			resource.getScheduledTasks().put(e.getKey(), new GCUBEWSResource.TaskContext(null,GCUBEScope.getScope(e.getValue())));
	}

	/** 
	 * Serialises the state of a resource into a {@link java.io.ObjectOutputStream ObjectOutputStream}.
	 * Extend in accordance with resource semantics.
	 * 
	 * @param stream the stream.
	 * @throws Exception if the stream could not be processed.
	 * */
	protected void onStore(RESOURCE resource,ObjectOutputStream stream) throws Exception {
		//serialise serializable info from task map: excluding scheduled handlers
		//which must be restarted manually. At least keeps track of VRE context.
		Map<String,String> tasks = new HashMap<String,String>();
		for (Map.Entry<String,TaskContext> e: resource.getScheduledTasks().entrySet())
			tasks.put(e.getKey(), e.getValue().scope.toString());
		stream.writeObject(tasks);
	}
	
	/**{@inheritDoc}*/
	protected void onRemove(RESOURCE resource) throws IOException {getFileFromResource(resource).delete();}


	/** {@inheritDoc} */
	public synchronized Collection<RESOURCEID> getResourceIdentifiers() {
		Collection<RESOURCEID> identifiers = new HashSet<RESOURCEID>();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {return name.endsWith(getSuffix());}
		};
		String[] resourceFiles = this.getStorageRoot().list(filter);
		if (resourceFiles!=null) 
			for (String fileName : this.getStorageRoot().list(filter)) identifiers.add(this.getIDFromFileName(fileName.substring(0,fileName.lastIndexOf(this.getSuffix()))));
		return identifiers;
	}
	
	/**
	 * Returns the file serialisation of a resource.
	 * @return the file serialisation.
	 */
	protected abstract File getFileFromResource(RESOURCE resource);

	/**
	 * Returns the suffix for resource file serialisations.
	 * @return the suffix
	 */
	protected abstract String getSuffix();

	/**
	 * Returns a resource identifier from a string. 
	 * @param s the string.
	 * @return the identifier.
	 */
	protected abstract RESOURCEID getIDFromFileName(String s);

}
