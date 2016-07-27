package org.gcube.informationsystem.notifier.impl;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;



/**
 * 
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */
public class NotifierResourceHome extends  GCUBEWSHome {

	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return NotifierContext.getPortTypeContext();
	}
}

