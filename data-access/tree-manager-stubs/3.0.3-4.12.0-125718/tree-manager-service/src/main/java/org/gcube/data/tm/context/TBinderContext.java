/**
 * 
 */
package org.gcube.data.tm.context;

import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.data.tm.Constants;
import org.gcube.data.tm.state.TBinderResource;

/**
 * The context of the T-Binder service.
 * 
 * @author Fabio Simeoni
 *
 */
public class TBinderContext extends PortTypeContext {

	/** Singleton instance. */
	protected static TBinderContext singleton = new TBinderContext();

	/** Creates an instance . */
	private TBinderContext(){}

	/** Returns a context instance.
	 * @return the context
	 * */
	public static TBinderContext getContext() {
		return singleton;
	}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {
		return Constants.TBINDER_NAME;
	}
	
	public GCUBEWSResourceKey key() {
		// TODO Auto-generated method stub
		return super.makeKey(Constants.SINGLETON_BINDER_ID);
	}

	/**
	 * Returns the {@link TBinderResource} of the service. 
	 * @return the resource
	 */
	public TBinderResource binder() {
		try {
			return (TBinderResource) getWSHome().find(key());
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates the singleton {@link TBinderResource} of the service.
	 * @throws Exception if the engine could not be created
	 */
	public TBinderResource createBinder() throws Exception {
		return (TBinderResource) getWSHome().create(key());
		
	}

}
