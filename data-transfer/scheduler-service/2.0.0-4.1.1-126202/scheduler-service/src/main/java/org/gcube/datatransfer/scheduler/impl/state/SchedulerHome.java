package org.gcube.datatransfer.scheduler.impl.state;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;
import org.gcube.datatransfer.scheduler.impl.context.SchedulerContext;

public class SchedulerHome extends GCUBEWSHome {

    @Override
    public GCUBEStatefulPortTypeContext getPortTypeContext() {return SchedulerContext.getContext();}

}
