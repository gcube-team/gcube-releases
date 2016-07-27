package org.gcube.common.core.plugins;

/**
 * Models the configuration of a {@link GCUBEPluginManager}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEPluginManagerProfile {

	/** Fully qualified name of the subclass of {@link GCUBEPluginManager} to be configured. */
	private String className;
	
	/**
	 * Returns the fully qualified name of the subclass of {@link GCUBEPluginManager} to be configured.
	 * @return the name.
	 */
	public String getClassName() {return className;}
	/**
	 * Sets the fully qualified name of the subclass of {@link GCUBEPluginManager} to be configured.
	 * @param className the name.
	 */
	public void setClassName(String className) {this.className = className;}
}
