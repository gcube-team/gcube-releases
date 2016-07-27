package org.gcube.informationsystem.registry.impl.contexts;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

/**
 * Stateful port-type context for <em>gcube/informationsystem/registry/Registry</em>
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class ProfileContext extends GCUBEStatefulPortTypeContext{
	
	static private final String PORTTYPE_NAME = "gcube/informationsystem/registry/Registry";
	
	private ProfileContext(){}
	
	protected static final ProfileContext cache = new ProfileContext();

	/**
	 * 
	 * @return profile Context
	 */
	public static ProfileContext getContext() {
		return cache;
	}
	
	
	/**
	 * 
	 * @return the port type name
	 */
	public final String getJNDIName() {
		return PORTTYPE_NAME;
	}
	
	/**
	 * 
	 * @return the namespace
	 */
	public final String getNamespace() {
		return "http://gcube-system.org/namespaces/informationsystem/registry";
	}
	
	/**
	 * 
	 * @return the ServiceContext
	 */
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}
}
