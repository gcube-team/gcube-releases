package org.acme.sample;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;

public class Home extends GCUBEWSHome {

    @Override
    public GCUBEStatefulPortTypeContext getPortTypeContext() {return StatefulContext.getContext();}

}
