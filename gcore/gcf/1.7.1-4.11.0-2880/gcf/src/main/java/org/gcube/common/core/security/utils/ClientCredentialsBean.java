package org.gcube.common.core.security.utils;

import org.globus.wsrf.impl.security.descriptor.CredentialParamsParserCallback;

public class ClientCredentialsBean implements CredentialParamsParserCallback 
{

	private String 	certFile,
					keyFile,
					proxyFile;
	
	@Override
	public void setCertificateFiles(String certFile, String keyFile)
	{
		this.certFile = certFile;
		this.keyFile = keyFile;
	}

	@Override
	public void setProxyFilename(String proxyFile) 
	{
		this.proxyFile = proxyFile;
	}

	public String getCertFile() {
		return certFile;
	}

	public String getKeyFile() {
		return keyFile;
	}

	public String getProxyFile() {
		return proxyFile;
	}
	
	
	

}
