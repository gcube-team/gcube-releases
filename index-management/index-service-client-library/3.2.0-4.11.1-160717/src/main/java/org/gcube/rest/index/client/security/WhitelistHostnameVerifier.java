package org.gcube.rest.index.client.security;

import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class WhitelistHostnameVerifier implements HostnameVerifier {
    private static final HostnameVerifier defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    private Set<String> trustedHosts;

    public WhitelistHostnameVerifier(Set<String> trustedHosts) {
        this.trustedHosts = trustedHosts;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        if (trustedHosts.contains(hostname)) {
            return true;
        } else {
            return defaultHostnameVerifier.verify(hostname, session);
        }
    }
    
}
