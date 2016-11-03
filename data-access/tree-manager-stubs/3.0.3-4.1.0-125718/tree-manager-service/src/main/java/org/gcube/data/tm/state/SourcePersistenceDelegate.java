/**
 * 
 */
package org.gcube.data.tm.state;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.common.core.persistence.GCUBELRFilePersistenceDelegate;
import org.gcube.data.tmf.api.Source;

/**
 * 
 * Persistence delegate for {@link SourceResource}s.
 * 
 * @author Fabio Simeoni
 *
 */
public class SourcePersistenceDelegate extends GCUBELRFilePersistenceDelegate<SourceResource> {

	/**{@inheritDoc}*/
	@Override
	protected void onLoad(SourceResource resource, ObjectInputStream stream) throws Exception {
		
		super.onLoad(resource,stream);
		
		String name = (String) stream.readObject();
		
		resource.setPluginName(name);
		resource.setSource((Source) stream.readObject());
		
	}

	/**{@inheritDoc}*/
	@Override
	protected void onStore(SourceResource resource, ObjectOutputStream stream) throws Exception {
	
		super.onStore(resource, stream);
		stream.writeObject(resource.getPluginName());
		stream.writeObject(resource.source());
				
	}
	
	/**{@inheritDoc}*/
	@Override
	protected void onLoad(SourceResource resource, boolean firstLoad) throws Exception {
		super.onLoad(resource, firstLoad);
		if (firstLoad)
			resource.source().lifecycle().resume(); 
	}

	
}