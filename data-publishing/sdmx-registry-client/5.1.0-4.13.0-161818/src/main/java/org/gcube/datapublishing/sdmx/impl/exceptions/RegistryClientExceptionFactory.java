package org.gcube.datapublishing.sdmx.impl.exceptions;

public class RegistryClientExceptionFactory {

	public static SDMXRegistryClientException getException(String message, int sdmxErrorCode){
		if (sdmxErrorCode==100) return new NoResultsException();
		return new SDMXRegistryClientException(message,sdmxErrorCode);
	} 
	
	public static SDMXRegistryClientException getException(String message){
		return new SDMXRegistryClientException(message);
	}
	
	public static SDMXVersionException getVersionException(String sdmxStructure, String localVersion, String registryVersion){
		return new SDMXVersionException(sdmxStructure,localVersion,registryVersion);
	}
}
