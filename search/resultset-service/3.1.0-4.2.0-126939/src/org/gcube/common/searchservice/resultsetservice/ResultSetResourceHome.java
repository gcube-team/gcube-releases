package org.gcube.common.searchservice.resultsetservice;
 
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSHome;

/**
 * This class is the resource home that manages the handled resource
 * 
 * @author UoA
 */
public class ResultSetResourceHome extends GCUBEWSHome {

	/**
	 * Get the Port type context
	 * @return The port type context
	 */
	@Override
	public GCUBEStatefulPortTypeContext getPortTypeContext() {
		return StatefulContext.getPortTypeContext();
	}

}
