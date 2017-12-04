package org.gcube.datapublishing.sdmx.impl.exceptions;

public class SDMXVersionException extends SDMXRegistryClientException 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4824781062869594414L;

	public SDMXVersionException(String sdmxstructure, String localVersion, String registryVersion) {
		super("The version of "+sdmxstructure+" to be uploaded is "+localVersion+ " while on the registry version "+registryVersion+" is present");
	}
}
