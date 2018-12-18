package org.gcube.portal.oauth.cache;


/**
 * A cache bean object for oauth support
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CacheBean {

	private String token;
	private String scope;
	private String redirectUri;
	private String clientId;
	private Long insertTime;
	private static final int TOKEN_TTL = 1000 * 10;
	
	/**
	 * @param token
	 * @param scope
	 * @param redirectUri
	 * @param clientId
	 * @param insertTime
	 */
	public CacheBean(String token, String scope, String redirectUri,
			String clientId, Long insertTime) {
		super();
		this.token = token;
		this.scope = scope;
		this.redirectUri = redirectUri;
		this.clientId = clientId;
		this.insertTime = insertTime;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Long insertTime) {
		this.insertTime = insertTime;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String toString() {
		return "CacheBean [token=" + token + ", scope=" + scope
				+ ", redirectUri=" + redirectUri + ", clientId=" + clientId
				+ ", insertTime=" + insertTime + "]";
	}

	/**
	 * True if the code expired, false otherwise
	 * @return
	 */
	public static boolean isExpired(CacheBean bean){
		
		return System.currentTimeMillis() > TOKEN_TTL + bean.insertTime;
		
	}
	
}
