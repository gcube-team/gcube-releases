package org.gcube.datacatalogue.grsf_manage_widget.server.manage;

import java.net.URLDecoder;
import java.net.URLEncoder;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.datacatalogue.grsf_manage_widget.shared.RevertableOperations;
import org.gcube.portlets.user.urlshortener.UrlShortener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encode and decode the url for reverting operations
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class RevertOperationUrl {

	private static final Logger logger = LoggerFactory.getLogger(RevertOperationUrl.class);

	// parameters for reverting operations
	public static final String MANAGE_QUERY_PARAM = "manage=true";
	public static final String ADMIN_QUERY_PARAM = "admin";
	public static final String TIMESTAMP_QUERY_PARAM = "t";
	public static final String UUID_QUERY_PARAM = "uuid";
	public static final String OPERATION_REVERT_QUERY_PARAM = "operation_revert";
	public static final long TTL = 1000 * 60 * 60 * 24;

	private String baseUrl;
	private String admin;
	private long timestamp;
	private String uuid;
	private RevertableOperations operation;

	/**
	 * @param admin
	 * @param timestamp
	 * @param uuid
	 * @param operation
	 * @param op
	 */
	public RevertOperationUrl(String baseUrl, String admin, long timestamp, String uuid,
			RevertableOperations operation) {
		super();
		this.baseUrl = baseUrl;
		this.admin = admin;
		this.timestamp = timestamp;
		this.uuid = uuid;
		this.operation = operation;
	}

	/**
	 * Build an encrypted, encoded and shortened url
	 * @return
	 * @throws Exception
	 */
	public String getShortUrl() throws Exception{

		String query = ADMIN_QUERY_PARAM + "=" + admin + "&" + TIMESTAMP_QUERY_PARAM + "=" + timestamp +"&" + UUID_QUERY_PARAM + "=" + uuid + "&" + OPERATION_REVERT_QUERY_PARAM + "=" + operation;
		logger.info("Query is " + query);
		String encryptedQuery = StringEncrypter.getEncrypter().encrypt(query);
		encryptedQuery = URLEncoder.encode(encryptedQuery, "UTF-8");
		logger.debug("Encrypted part looks like " + encryptedQuery);
		String encryptedUrl = 
				baseUrl + "?"
				+ MANAGE_QUERY_PARAM + "&"
				+ encryptedQuery;
		UrlShortener shortener = new UrlShortener();
		String shortUrl = null;
		try{
			if(shortener!=null && shortener.isAvailable())
				shortUrl = shortener.shorten(encryptedUrl);
		}catch (Exception e) {
			logger.warn("Unable to get short url", e);
			shortUrl = encryptedUrl;
		}

		logger.debug("Encrypted and shortened url " + shortUrl);
		return shortUrl;

	}

	public RevertOperationUrl(String encryptedUrl) throws Exception{

		if(encryptedUrl == null)
			throw new IllegalArgumentException("encryptedUrl is null");

		String params = encryptedUrl.split("\\?")[1];
		logger.debug("Params encrypted are " + params);

		// remove MANAGE_QUERY_PARAM
		params = params.replace(MANAGE_QUERY_PARAM + "&", "");

		String decoded = URLDecoder.decode(params, "UTF-8");
		String decrypted = StringEncrypter.getEncrypter().decrypt(decoded);
		logger.debug("Decrypted part looks like " + decrypted);

		try{
			String[] splittedQuery = decrypted.split("&");
			for (int i = 0; i < splittedQuery.length; i++) {
				String subParam = splittedQuery[i];
				String[] queryAndValue = subParam.split("=");
				String query = queryAndValue[0];
				String value = queryAndValue[1];

				switch (query) {
				case ADMIN_QUERY_PARAM:
					this.admin = value;
					break;
				case TIMESTAMP_QUERY_PARAM:
					this.timestamp = Long.valueOf(value);
					break;
				case UUID_QUERY_PARAM:
					this.uuid = value;
					break;
				case OPERATION_REVERT_QUERY_PARAM:
					this.operation = RevertableOperations.valueOf(value.toUpperCase());
					break;
				default:
					break;
				}
			}
		}catch(Exception e){
			logger.error("Failed to parse url", e);
		}

	}
	
	public boolean isTimestampValid() {
		return (TTL + this.timestamp) > System.currentTimeMillis();
	}

	public String getBaseUrl() {
		return baseUrl;
	}


	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}


	public String getAdmin() {
		return admin;
	}


	public void setAdmin(String admin) {
		this.admin = admin;
	}


	public long getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public RevertableOperations getOperation() {
		return operation;
	}


	public void setOperation(RevertableOperations operation) {
		this.operation = operation;
	}

	@Override
	public String toString() {
		return "RevertOperationUrl [baseUrl=" + baseUrl + ", admin=" + admin
				+ ", timestamp=" + timestamp + ", uuid=" + uuid
				+ ", operation=" + operation + "]";
	}

}
