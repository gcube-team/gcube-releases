package it.eng.rdlab.soa3.connector.utils.security;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 
 * A trust manager that doesn't make anything: used for test purposes
 * 
 * @author Ciro Formisano (ENG)
 *
 */
public class NullTrustManager implements X509TrustManager
{
	private X509TrustManager internalTrustManager;
	
	
	public NullTrustManager(KeyStore keystore) throws Exception
	{
		TrustManagerFactory trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManager.init(keystore);
		this.internalTrustManager = (X509TrustManager)trustManager.getTrustManagers() [0];
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {

		return this.internalTrustManager.getAcceptedIssuers();
	}


	
}
