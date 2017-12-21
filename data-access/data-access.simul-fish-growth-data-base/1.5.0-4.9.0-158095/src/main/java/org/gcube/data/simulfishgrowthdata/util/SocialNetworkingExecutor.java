package org.gcube.data.simulfishgrowthdata.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public abstract class SocialNetworkingExecutor {

	static final Logger logger = LoggerFactory.getLogger(SocialNetworkingExecutor.class);

	static final String formatQueryParam = "&%s=%s";

	protected String mEndpoint;

	String mToken;

	public SocialNetworkingExecutor(String endpoint) {
		mEndpoint = endpoint;

	}

	public SocialNetworkingExecutor setToken(String token) {
		mToken = token;

		return this;
	}

	protected abstract String makeUri();

	protected abstract void prepareRequest(HttpUriRequest request);

	protected abstract HttpUriRequest createRequest(final URI uri);

	public void execute() throws Exception {
		processOutput(communicate(makeUri(), mToken));
	}

	private String communicate(String urlEndPoint, String token) throws Exception {

		URI uri = URI.create(urlEndPoint);
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("calling uri [%s]", uri));
		}

		String toRet = "";

		HttpUriRequest request = createRequest(uri);
		request.addHeader("Accept", ContentType.APPLICATION_JSON.toString());
		request.addHeader("Content-type", ContentType.APPLICATION_JSON.toString());
		request.addHeader("gcube-token", mToken);

		prepareRequest(request);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse resp = httpclient.execute(request);
		try {
			toRet = getContents(resp);
		} catch (RedirectException e) {
			// https://wiki.gcube-system.org/gcube/Social_Networking_Service
			// Always manage HTTP Redirects on POST requests (i.e. 30X codes)
			logger.warn("When invoking [" + urlEndPoint + "] I was redirected to [" + e.getMessage()
					+ "]. I will post there, as well");
			return communicate(e.getMessage(), token);
		}

		return toRet;

	}

	private String getContents(final CloseableHttpResponse resp) throws Exception {

		String str = "";
		String exceptionMessage = null;
		try {
			StatusLine statusLine = resp.getStatusLine();
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("status line is [%s]", statusLine));
			}
			if (statusLine.getStatusCode() >= 300) {
				exceptionMessage = "The server responded: " + statusLine.getReasonPhrase();
			}

			// https://wiki.gcube-system.org/gcube/Social_Networking_Service
			// Always manage HTTP Redirects on POST requests (i.e. 30X codes)
			{
				int status = statusLine.getStatusCode();
				// check the response status and look if it was a redirect
				// problem
				if (status != HttpURLConnection.HTTP_OK && (status == HttpURLConnection.HTTP_MOVED_TEMP
						|| status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)) {

					// redirect -> fetch new location
					Header[] locations = resp.getHeaders("Location");
					Header lastLocation = locations[locations.length - 1];
					String realLocation = lastLocation.getValue();
					logger.debug("Redirected. New location is " + realLocation);
					throw new RedirectException(realLocation);
				}
			}

			HttpEntity entity = resp.getEntity();
			if (entity != null) {
				InputStream is = entity.getContent();
				try {
					// do something useful
					Scanner s = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
					str = s.hasNext() ? s.next() : "";
				} finally {
					is.close();
				}
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("response is [%s]", str));
				}
			}
		} finally {
			resp.close();
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("contentes ~~~%s~~~", str));
		}
		if (exceptionMessage != null) {
			throw new Exception(exceptionMessage, new Exception(str));
		}
		return str;
	}

	protected Response processOutput(final String output) throws Exception {
		return new Gson().fromJson(output, Response.class);
	}

	static public class Response {
		public String success;
		public String message;
		public JsonObject  result;

		public boolean isSuccess() {
			return "true".equals(success);
		}
	}

	static public class RedirectException extends Exception {

		private static final long serialVersionUID = 5790392503222403601L;

		public RedirectException(String message) {
			super(message);
		}
	}

}