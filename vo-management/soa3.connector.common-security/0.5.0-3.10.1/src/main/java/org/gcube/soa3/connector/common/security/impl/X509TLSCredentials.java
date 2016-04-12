package org.gcube.soa3.connector.common.security.impl;

import java.security.Security;

import it.eng.rdlab.soa3.connector.utils.SecurityManager;

import org.gcube.soa3.connector.common.security.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * TLS X509 {@link Credentials}
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class X509TLSCredentials implements Credentials 
{

	private Logger log;
	private final String X509TLS = "X509TLS";
	private String 	certFile,
					keyFile,
					trustDir;
					//trustExt;
	
	private static boolean providerAdded = false;
	
	private boolean isReady;
	
	public X509TLSCredentials ()
	{
		this (null,null,null,null,null);
	}
	
	public X509TLSCredentials(String certFile, String keyFile, char [] keyPassword, String trustDir, String trustExt) 
	{
		this.log = LoggerFactory.getLogger(this.getClass());
		
		if (certFile != null)
		{
			SecurityManager.getInstance().setCertFile(certFile);
			this.certFile = certFile;
		}
		else this.certFile = SecurityManager.DEFAULT_CERT_FILE;
		
		if (keyFile != null)
		{
			SecurityManager.getInstance().setKeyFile(keyFile);
			this.keyFile = keyFile;
		}
		else this.keyFile = SecurityManager.DEFAULT_KEY_FILE;
		
		if (trustDir != null)
		{
			SecurityManager.getInstance().setTrustDir(trustDir);
			this.trustDir = trustDir;
		}
		else this.trustDir = SecurityManager.DEFAULT_TRUST_DIR;
		
		if (trustExt != null)
		{
			SecurityManager.getInstance().setTrustExt(trustExt);
			//this.trustExt = trustExt;
		}
		//else this.trustExt = SecurityManager.DEFAULT_TRUST_FILE_EXTENSION;
		if (keyPassword != null) SecurityManager.getInstance().setPrivateKeyPassword(keyPassword);
		
		this.isReady = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prepareCredentials() 
	{		
		if (!this.isReady)
		{
			log.debug("Loading certificates");
		
			try 
			{
				if (!providerAdded)
				{
					log.debug("Adding bouncycastle provider...");
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
					providerAdded = true;
					log.debug("Provider added");
				}
				
				SecurityManager.getInstance().loadCertificate();
				log.debug("Certificates loaded");
				this.isReady = true;
			} catch (Exception e) {
				log.error("Unable to load security certificates", e);
			}
		}
	}

	/**
	 * Returns "X509TLS"
	 */
	@Override
	public String getAuthenticationType() 
	{

		return X509TLS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAuthenticationString() 
	{
		return new StringBuilder("Certificate ").append(this.certFile).append(" Key ").append(this.keyFile).append(" Trust Dir ").append(this.trustDir).toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHeaderString() 
	{
		return null;
	}
	
	public static void main(String[] args) {
		Credentials cred = new X509TLSCredentials();
		cred.prepareCredentials();
	}

	@Override
	public void disposeCredentials() 
	{
		SecurityManager.getInstance().removeCertificate();
		this.isReady = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPrepared() 
	{

		return isReady;
	}

}
