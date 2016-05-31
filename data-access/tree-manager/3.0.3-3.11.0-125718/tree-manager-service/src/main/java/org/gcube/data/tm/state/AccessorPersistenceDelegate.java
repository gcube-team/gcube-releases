package org.gcube.data.tm.state;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.gcube.common.core.persistence.GCUBEWSFilePersistenceDelegate;
import org.gcube.common.core.persistence.GCUBEWSLiteFilePersistenceDelegate;

/**
 * Extends {@link GCUBEWSFilePersistenceDelegate} to (de)serialise {@link AccessorResource}s.
 * @author Fabio Simeoni
 *
 */
public class AccessorPersistenceDelegate extends GCUBEWSLiteFilePersistenceDelegate<SourceResource,AccessorResource> {

	/**{@inheritDoc}*/
	protected void onLoad(AccessorResource manager, ObjectInputStream stream) throws Exception {
		super.onLoad(manager, stream);
		manager.subscribeForChange();
	}
	
	/**{@inheritDoc}*/
	@Override
	protected void onLoad(AccessorResource manager, boolean firstLoad) throws Exception {
		super.onLoad(manager, firstLoad);
	}
	
	/**{@inheritDoc}*/
	protected void onStore(AccessorResource manager, ObjectOutputStream stream) throws Exception {
		super.onStore(manager, stream);
	}
	

}
