package org.gcube.data.spd.occurrences;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.context.ServiceContext;

public class OccurrencesPTContext extends GCUBEStatefulPortTypeContext{

	/** Singleton instance. */
	protected static OccurrencesPTContext cache = new OccurrencesPTContext();

	/** Creates an instance . */
	private OccurrencesPTContext(){}
	
	/** Returns a context instance.
	 * @return the context.*/
	public static OccurrencesPTContext getContext() {return cache;}
	
	@Override
	public String getJNDIName() {
		return Constants.OCCURRENCES_PT_NAME;
	}

	@Override
	public String getNamespace() {
		return Constants.NS;
	}

	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

}
