package org.gcube.data.spd.classification;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.context.ServiceContext;

public class ClassificationPTContext extends GCUBEStatefulPortTypeContext{

	/** Singleton instance. */
	protected static ClassificationPTContext cache = new ClassificationPTContext();

	/** Creates an instance . */
	private ClassificationPTContext(){}
	
	/** Returns a context instance.
	 * @return the context.*/
	public static ClassificationPTContext getContext() {return cache;}
	
	@Override
	public String getJNDIName() {
		return Constants.CLASSIFICATION_PT_NAME;
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
