package org.gcube.portlets.user.reportgenerator.shared;

public class RSGAccessPoint {
	
	public static final String VME_SECURE_KEYRING_NAME = "vme.pkr";
	public static final String IMARINE_SECURE_KEYRING_NAME = "imarine.skr";
	private static final String REST_ENDPOINT = "/rest";
	private static final String TOKEN_ENDPOINT = "/security/token/plain/request";
		
	private String url;
	private String iMarineKeyRing;
	private String vmeKeyRing;
	private String password;
	
	public RSGAccessPoint(String url, String iMarineKeyRing, String vmeKeyRing,	String password) {
		super();
		this.url = url;
		this.iMarineKeyRing = iMarineKeyRing;
		this.vmeKeyRing = vmeKeyRing;
		this.password = password;
	}
	
	public String getRestUrl() {
		return url+REST_ENDPOINT;
	}	
	public String getTokenUrl() {
		return url+TOKEN_ENDPOINT;
	}	
	public String getiMarineKeyRingLocation() {
		return iMarineKeyRing;
	}
	public String getVmeKeyRingLocation() {
		return vmeKeyRing;
	}
	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return "RSGAccessPoint [getRestUrl()=" + getRestUrl()
				+ ", getTokenUrl()=" + getTokenUrl()
				+ ", getiMarineKeyRingLocation()="
				+ getiMarineKeyRingLocation() + ", getVmeKeyRingLocation()="
				+ getVmeKeyRingLocation() + ", getPassword()=" + getPassword()
				+ "]";
	}	
	
}
