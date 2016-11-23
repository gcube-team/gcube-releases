package org.gcube.informationsystem.registry.impl.contexts;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

/**
 * Stateful port-type context for <em>gcube/informationsystem/registry/RegistryFactory</em>
 * 
 * @author Manuele Simi (CNR)
 *
 */
public class FactoryContext extends GCUBEStatefulPortTypeContext{
	
	static private final String PORTTYPE_NAME = "gcube/informationsystem/registry/RegistryFactory";
	
	static FactoryContext cache = new FactoryContext();
	
	private FactoryContext() {}
	
	/**
	 * returns the current context
	 * 
	 * @return FactoryContext
	 */
	public static FactoryContext getContext() {
		return cache;
	}
	
	
	/**
	 * 
	 * 
	 * @return the porttype name
	 */
	public String getJNDIName() {
		
		return PORTTYPE_NAME;
	}
	
	/**
	 * 
	 * @return the namespace
	 */
	public String getNamespace() {
		return "http://gcube-system.org/namespaces/informationsystem/registry";
	}

	/**
	 * 
	 * @return the Service name
	 */
	public String getServiceName() {
		return ServiceContext.getContext().getName();
	}
	
	/**
	 * 
	 * @return the ServiceContext
	 */
	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

}
