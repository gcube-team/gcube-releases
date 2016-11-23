package org.gcube.common.vremanagement.deployer.impl.resources;

import java.io.Serializable;

public class BaseTypedPackage extends BasePackage {

	private static final long serialVersionUID = 6083297865946869113L;
	
	private TYPE type;
	
	public enum TYPE implements Serializable {
		MAINPACKAGE,
		LIBRARY,
		APPLICATION,
		PLUGIN, 
		PLATFORMAPPLICATION,
		EXTERNAL
	}

	/**
	 * Builds a new typed package
	 * 
	 * @param serviceClass the service class
	 * @param serviceName the service name
	 * @param serviceVersion the service version
	 * @param packageName the package name
	 * @param packageVersion the package version
	 */
	public BaseTypedPackage(String serviceClass, String serviceName,
			String serviceVersion, String packageName, String packageVersion) {
		super(serviceClass, serviceName, serviceVersion, packageName, packageVersion);
	}

	/**
	 * @param type the type to set
	 */
	public void setType(TYPE type) {
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public TYPE getType() {
		return type;
	}

}
