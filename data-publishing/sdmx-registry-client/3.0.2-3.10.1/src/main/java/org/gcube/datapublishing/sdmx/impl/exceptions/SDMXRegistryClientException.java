package org.gcube.datapublishing.sdmx.impl.exceptions;

public class SDMXRegistryClientException extends Exception {
	
	private static final long serialVersionUID = 4824781062869594413L;
	
	Integer sdmxErrorCode = null;

	public SDMXRegistryClientException(String message) {
		super(message);
	}
	
	public SDMXRegistryClientException(String message, int sdmxErrorCode){
		super(message);
		this.sdmxErrorCode=sdmxErrorCode;
	}
	
	public Integer getSdmxErrorCode() {
		return sdmxErrorCode;
	}

	@Override
	public String toString() {
		return "SDMXRegistryClientException [SdmxErrorCode=" + sdmxErrorCode
				+ ", message=" + getMessage() + "]";
	}
	
	

}
