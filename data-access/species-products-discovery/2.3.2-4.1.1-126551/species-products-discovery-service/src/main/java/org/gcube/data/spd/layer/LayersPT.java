package org.gcube.data.spd.layer;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.porttypes.GCUBEPortType;
import org.gcube.data.spd.context.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LayersPT extends GCUBEPortType {

	Logger logger = LoggerFactory.getLogger(LayersPT.class);

	/**{@inheritDoc}*/
	@Override	protected GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	

}
