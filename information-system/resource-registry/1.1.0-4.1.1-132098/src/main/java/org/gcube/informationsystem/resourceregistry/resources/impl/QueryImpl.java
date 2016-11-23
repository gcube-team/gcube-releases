/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.resources.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.gcube.informationsystem.resourceregistry.api.Query;
import org.gcube.informationsystem.resourceregistry.api.exceptions.InvalidQueryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.context.ContextException;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper;
import org.gcube.informationsystem.resourceregistry.dbinitialization.DatabaseEnvironment;
import org.gcube.informationsystem.resourceregistry.dbinitialization.SecurityContextMapper.PermissionMode;
import org.gcube.informationsystem.resourceregistry.resources.utils.ContextUtility;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.research.ws.wadl.HTTPMethods;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
public class QueryImpl implements Query {

	private static Logger logger = LoggerFactory.getLogger(QueryImpl.class);

	private static final String QUERY = "query/";
	private static final String SQL = "sql/";
	private static final String DEFAULT_LIMIT = "20/";

	private static final URL BASE_QUERY_URL;

	static {
		try {
			URL url = new URL(DatabaseEnvironment.HTTP_URL_STRING);
			URL urlQuery = new URL(url, QUERY);
			URL urlDB = new URL(urlQuery, DatabaseEnvironment.DB + "/");
			BASE_QUERY_URL = new URL(urlDB, SQL);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void checkStatus(HttpURLConnection connection) throws Exception {
		int statusCode = connection.getResponseCode();
		switch (statusCode) {
		case 200:
		case 201:
			return;
		default:
			throw new Exception(connection.getResponseMessage());
		}
	}

	@Override
	public String execute(String query, String fetchPlan)
			throws InvalidQueryException {
		
		
		String readerUsername;
		try {
			readerUsername = ContextUtility.getActualSecurityRoleOrUserName(SecurityContextMapper.PermissionMode.READER, SecurityContextMapper.SecurityType.USER);
		} catch (ContextException e1) {
			throw new RuntimeException(e1);
		}
		logger.trace("Reader Username : {}", readerUsername);
		
		try {
			URL queryURL = new URL(BASE_QUERY_URL, URLEncoder.encode(query,
					"UTF-8") + "/");
			
			/*
			if (limit != null && limit > 0) {
				queryURL = new URL(queryURL, limit.toString() + "/");
			} else {
				queryURL = new URL(queryURL, DEFAULT_LIMIT);
			}
			*/
			queryURL = new URL(queryURL, DEFAULT_LIMIT);
			

			if (fetchPlan != null && fetchPlan.compareTo("") != 0) {
				queryURL = new URL(queryURL, fetchPlan + "/");
			}

			logger.debug("Connecting to {}", queryURL.toString());
			HttpURLConnection connection = (HttpURLConnection) queryURL
					.openConnection();

			String password = DatabaseEnvironment.DEFAULT_PASSWORDS.get(PermissionMode.READER);
			String authString = String.format("%s:%s", readerUsername, password);
			
			byte[] authEncBytes = Base64.encode(authString.getBytes());
			String authStringEnc = new String(authEncBytes);
			connection.setRequestProperty("Authorization", "Basic "
					+ authStringEnc);
			connection.setRequestMethod(HTTPMethods.GET.toString());
			connection.connect();

			checkStatus(connection);

			InputStream inputStream = connection.getInputStream();
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			return result.toString("UTF-8");

		} catch (Exception e) {
			throw new InvalidQueryException(e.getMessage(), e);
		}

	}
}
