package org.gcube.common.core.persistence;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.common.core.state.GCUBELocalResource;
import org.gcube.common.core.state.GCUBEResourceHome;
import org.gcube.common.core.state.GCUBEWSLiteResource;
import org.gcube.common.core.state.GCUBEWSResourceKey;

/**
 * Specialisation of {@link GCUBEWSFilePersistenceDelegate} for {@link org.gcube.common.core.state.GCUBEWSLiteResource GCUBEWSResourceLite}.
 * @author Fabio Simeoni (University of Strathclyde).
 *
 * @param <RESOURCE> the type of the {@link org.gcube.common.core.state.GCUBEWSLiteResource GCUBEWSResourceLite} manager by the delegatate.
 */
public class GCUBEWSLiteFilePersistenceDelegate<LOCAL extends GCUBELocalResource,RESOURCE extends GCUBEWSLiteResource<LOCAL>> extends GCUBEWSFilePersistenceDelegate<RESOURCE> {

	/** The suffix of file serialisation of resource. */
	protected static final String WSLITE_FILE_SUFFIX = ".wslite";

	/** {@inheritDoc}*/ @Override
	public void initialise(GCUBEResourceHome<? super GCUBEWSResourceKey,GCUBEWSResourceKey,RESOURCE> home) throws Exception {
		super.initialise(home);	
		//assume delegate and resource ship together hence uses delegate's classloader
		Class<?> resourceClass = getClass().getClassLoader().loadClass(home.getResourceClass());
		if (!GCUBEWSLiteResource.class.isAssignableFrom(resourceClass)) //a broad sanity check
			throw new Exception(home.getResourceClass()+" is not compatible with delegate "+this.getClass().getSimpleName());
	}
	
	/** {@inheritDoc}*/ @Override
	protected void onLoad(RESOURCE resource, ObjectInputStream stream) throws Exception {
		super.onLoad(resource, stream);
		resource.setLocalID((String) stream.readObject());		
	}

	
	/** {@inheritDoc}*/ @Override
	public synchronized void onStore(RESOURCE resource) throws Exception {	
		resource.getLocalResource().store();//propagates request to stateful resource (load instead is lazy); 
		super.onStore(resource);	
	}
	
	/** {@inheritDoc}*/ @Override
	protected void onStore(RESOURCE resource, ObjectOutputStream stream) throws Exception {
		super.onStore(resource, stream);
		stream.writeObject(resource.getLocalID());//stores local resource ID
	}

	/** {@inheritDoc}*/ @Override
	protected String getSuffix() {return WSLITE_FILE_SUFFIX;}
	
}
