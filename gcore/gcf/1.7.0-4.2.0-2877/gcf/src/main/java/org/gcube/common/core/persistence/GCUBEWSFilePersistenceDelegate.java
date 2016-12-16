package org.gcube.common.core.persistence;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.List;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEResourceHome;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.state.GCUBEWSResourceKey;

/**
 * An specialisation of {@link GCUBEFilePersistenceDelegate} to {@link GCUBEWSResource GCUBEWSResources}.
 * 
 *@param RESOURCE the WS-Resource type.
 *@author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEWSFilePersistenceDelegate<RESOURCE extends GCUBEWSResource> extends GCUBEFilePersistenceDelegate<GCUBEWSResourceKey,RESOURCE> {

	/** The suffix of file serialisation of resource. */
	protected static final String WSRESOURCE_FILE_SUFFIX = ".wsresource";
	
	protected volatile GCUBEStatefulPortTypeContext ctxt;
	
	/**{@inheritDoc}*/
	public void initialise(GCUBEResourceHome<? super GCUBEWSResourceKey,GCUBEWSResourceKey,RESOURCE>  home) throws Exception {
		//assume delegate and resource ship together hence uses delegate's classloader
		Class<?> resourceClass = getClass().getClassLoader().loadClass(home.getResourceClass());
		if (!GCUBEWSResource.class.isAssignableFrom(resourceClass)) //a broad sanity check
			throw new Exception(home.getResourceClass()+" is not compatible with delegate "+this.getClass().getSimpleName());
		super.initialise(home);
		this.ctxt = ((GCUBEWSHome) home).getPortTypeContext();
	}
	

	/**{@inheritDoc}*/
	@SuppressWarnings("unchecked")
	protected void onLoad(RESOURCE resource,ObjectInputStream stream) throws Exception {
			super.onLoad(resource,stream);
			resource.getResourcePropertySet().setGHN((String) stream.readObject());
			resource.getResourcePropertySet().setServiceID((String) stream.readObject());
			resource.getResourcePropertySet().setServiceName((String) stream.readObject());
			resource.getResourcePropertySet().setServiceClass((String) stream.readObject());
			resource.getResourcePropertySet().setRI((String) stream.readObject());
			resource.getResourcePropertySet().setScope((List<String>) stream.readObject());
			resource.getResourcePropertySet().setTerminationTime((Calendar) stream.readObject());
	}
	
	/**{@inheritDoc}*/
	protected void onStore(RESOURCE resource,ObjectOutputStream stream) throws Exception {
		
		//serialise gCube properties
		super.onStore(resource,stream);
		stream.writeObject(resource.getResourcePropertySet().getGHN());
		stream.writeObject(resource.getResourcePropertySet().getServiceID());
		stream.writeObject(resource.getResourcePropertySet().getServiceName());
		stream.writeObject(resource.getResourcePropertySet().getServiceClass());
		stream.writeObject(resource.getResourcePropertySet().getRI());
		stream.writeObject(resource.getResourcePropertySet().getScope());
		
		//serialise lifetime properties
		stream.writeObject(resource.getTerminationTime());
	}
	
	/**{@inheritDoc}*/
	protected String getSuffix() {return WSRESOURCE_FILE_SUFFIX;}
	
	/** {@inheritDoc} */
	protected File getFileFromResource(RESOURCE resource){return new File(getStorageRoot(),resource.getID().getValue()+getSuffix());}

	/**{@inheritDoc}*/
	protected GCUBEWSResourceKey getIDFromFileName(String s) {return ctxt.makeKey(s);}

}
