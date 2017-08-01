package org.gcube.rest.index.client.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;


public class SecurityTool {

	
	public static void setTrusted(Set<String> trustedHosts){
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[] { new TrustX509TrustManager() }, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new WhitelistHostnameVerifier(trustedHosts));
		} catch (KeyManagementException e) {
		} catch (NoSuchAlgorithmException e) {
		}
	}
	
}
