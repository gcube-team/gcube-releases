package org.gcube.common.core.persistence;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.common.core.state.GCUBELocalResource;
import org.gcube.common.core.state.GCUBEResourceHome;

public class GCUBELRFilePersistenceDelegate<RESOURCE extends GCUBELocalResource> extends GCUBEFilePersistenceDelegate<String,RESOURCE> {

	/** The suffix of file serialisation of resource. */
	protected static final String LOCAL_FILE_SUFFIX = ".local";

	/**{@inheritDoc}*/
	public void initialise(GCUBEResourceHome<? super String,String,RESOURCE> home) throws Exception {
		super.initialise(home);
		//assume delegate and resource ship together hence uses delegate's classloader
		Class<?> resourceClass = getClass().getClassLoader().loadClass(home.getResourceClass());
		if (!GCUBELocalResource.class.isAssignableFrom(resourceClass))
			throw new Exception(home.getResourceClass()+" is not compatible with delegate "+getClass().getSimpleName());
	}
	
	/** {@inheritDoc} */
	protected File getFileFromResource(RESOURCE resource){return new File(getStorageRoot(),resource.getID()+getSuffix());}
	
	/**{@inheritDoc}*/
	protected void onLoad(RESOURCE resource, ObjectInputStream stream) throws Exception {
		resource.setLinks(stream.readInt());
	}
	/**{@inheritDoc}*/
	protected void onStore(RESOURCE resource, ObjectOutputStream stream) throws Exception {
		stream.writeInt(resource.getLinks());
	}
	/**{@inheritDoc}*/
	protected String getSuffix() {return LOCAL_FILE_SUFFIX;}
	
	/**{@inheritDoc}*/
	protected String getIDFromFileName(String s) {return s;}

}
