/**
 * 
 */
package org.gcube.data.tm.context;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.data.tm.Constants;

/**
 * Base class for port-type contexts.
 * @author Fabio Simeoni
 *
 */
public abstract class PortTypeContext extends GCUBEStatefulPortTypeContext {

	
	/** {@inheritDoc}*/
	public String getNamespace() {return Constants.NS;}

	/**{@inheritDoc}*/
	@Override public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}
