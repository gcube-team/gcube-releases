package org.gcube.data.tm.state;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.gcube.common.core.persistence.GCUBEWSFilePersistenceDelegate;

/**
 * Extends {@link GCUBEWSFilePersistenceDelegate} to (de)serialise {@link AccessorResource}s.
 * @author Fabio Simeoni
 *
 */
public class TBinderPersistenceDelegate extends GCUBEWSFilePersistenceDelegate<TBinderResource> {

	/**{@inheritDoc}*/
	@SuppressWarnings("unchecked")
	protected void onLoad(TBinderResource binder, ObjectInputStream stream) throws Exception {
		
		super.onLoad(binder, stream);
		binder.activations = (Set<String>) stream.readObject();
		binder.setPluginProperty();
		
	}
	
	/**{@inheritDoc}*/
	protected void onStore(TBinderResource binder, ObjectOutputStream stream) throws Exception {
		super.onStore(binder, stream);
		stream.writeObject(binder.activations);
	}
	
	/**{@inheritDoc}*/
	@Override
	protected void onLoad(TBinderResource binder, boolean firstLoad) throws Exception {
		super.onLoad(binder, firstLoad);
	}
}
