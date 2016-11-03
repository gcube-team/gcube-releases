package org.gcube.data.analysis.statisticalmanager.experimentspace;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;


public class ComputationFactoryHome extends GCUBEWSHome {

    /** {@inheritDoc} */
    @Override
	public GCUBEStatefulPortTypeContext getPortTypeContext() {return ComputationFactoryContext.getContext();}

} 
