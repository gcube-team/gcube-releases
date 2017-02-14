/**
 * 
 */
package org.gcube.common.searchservice.resultsetservice;
 
import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;


/**
 * @author UoA
 * 
 */
public class StatefulContext extends GCUBEStatefulPortTypeContext {

	/**
	 * The stream port tag
	 */
	public static String STREAM_PORT = "streamPort";

	/**
	 * SSL support
	 */
	public static String SSLSUPPORT = "SSLsupport";

	private static GCUBEStatefulPortTypeContext cache = new StatefulContext();

	/**
	 * Get JNDI name
	 * @return the JNDI name
	 */
	@Override
	public String getJNDIName() {
		return "gcube/common/searchservice/ResultSet";
	}

	/**
	 * Get the namespace
	 * @return the JNDI name
	 */
	@Override
	public String getNamespace() {
		return "http://gcube.org/namespaces/common/searchservice/ResultSetService";
	}

	/**
	 * Get service context
	 * @return the service context
	 */
	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

	/**
	 * Get port type context
	 * @return the port type context
	 */
	public static GCUBEStatefulPortTypeContext getPortTypeContext() {
		return cache;
	}

}
