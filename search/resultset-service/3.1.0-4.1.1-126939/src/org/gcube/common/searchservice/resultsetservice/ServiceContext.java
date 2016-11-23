/**
 * 
 */
package org.gcube.common.searchservice.resultsetservice;
 
import org.gcube.common.core.contexts.GCUBEServiceContext;

/**
 * @author UoA
 * 
 */
public class ServiceContext extends GCUBEServiceContext {

	private static ServiceContext cache = new ServiceContext();

	/**
	 * get Context
	 * @return the context
	 */
	public static ServiceContext getContext() {
		return cache;
	}

	private ServiceContext() {
	};

	/**
	 * Get JNDI name
	 * @return the JNDI name
	 */
	@Override
	public String getJNDIName() {
		return "gcube/common/searchservice/rs";
	}

	@Override
	protected void onReady() {
		ResultSetService.ready();
	}
}
