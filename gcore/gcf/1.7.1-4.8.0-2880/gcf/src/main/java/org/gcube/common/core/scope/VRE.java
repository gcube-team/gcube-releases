package org.gcube.common.core.scope;

/**
 * A scope of type <code>VRE</code>.
 * 
 */
public class VRE extends GCUBEScope {

	protected VRE(String name) {
		super(name);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ServiceMap getServiceMap() throws GCUBEScopeNotSupportedException{
		return this.getEnclosingScope().getServiceMap();
	}
	
}
