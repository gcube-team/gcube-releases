package org.gcube.portlets.user.simulfishgrowth.util;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.ws.rs.HttpMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class ConnectionUtils {
	private static Log logger = LogFactoryUtil.getLog(ConnectionUtils.class);

	static public String endpoint;
	static private Long cachedTimestamp = 0L; // ms
	static private long expireCache = 60 * 60 * 1000;// ms
	static public final String ALL = "all";
	public static final String SIMILAR = "similar";
	public static final String COUNT = "count";
	public static final String USAGE = "usage";
	public static boolean MOCK_DATA = false;

	public static boolean hasEndpoint() {
		long now = System.currentTimeMillis();
		if (cachedTimestamp + expireCache < now) {
			setEndPoint(null);
			cachedTimestamp = now;
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("endpoint cache expired at %s", new Date(now)));
			}
		}

		return StringUtils.isNotBlank(endpoint);

	}

	public static void setEndPoint(String endpoint) {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Setting endpoint to [%s]", endpoint));
		}
		ConnectionUtils.endpoint = endpoint;
	}

	AddGCubeHeaders addGCubeHeaders = null;

	public ConnectionUtils(AddGCubeHeaders addGCubeHeaders) {
		this.addGCubeHeaders = addGCubeHeaders;
	}

	public String getData(URI uri, AddHeadersListener listener) throws Exception {
		String toRet = "";
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("GET [%s]", uri));
		}
		HttpGet request = new HttpGet(uri);
		request.addHeader("Accept", "application/json");
		if (listener != null) {
			listener.addHeaders(request);
		}
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Headers %s", Arrays.toString(request.getAllHeaders())));
		}
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse resp = httpclient.execute((HttpUriRequest) request);
			toRet = manage(resp);
		} catch (ConnectException e) {
			// force endpoint expiration
			cachedTimestamp = 0L;
			throw e;
		}

		return toRet;
	}

	public String getData(URI uri) throws Exception {
		return getData(uri, addGCubeHeaders);
	}

	public void deleteData(URI uri) throws Exception {
		deleteData(uri, addGCubeHeaders);
	}

	public void deleteData(URI uri, AddHeadersListener listener) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("DELETE [%s]", uri));
		}
		HttpDelete request = new HttpDelete(uri);
		if (listener != null) {
			listener.addHeaders(request);
		}
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse resp = httpclient.execute((HttpUriRequest) request);
			manage(resp);
		} catch (ConnectException e) {
			// force endpoint expiration
			cachedTimestamp = 0L;
			throw e;
		}
	}

	private String manage(final CloseableHttpResponse resp) throws Exception {

		String str = "";
		Exception error = null;
		try {
			StatusLine statusLine = resp.getStatusLine();
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("status line is [%s]", statusLine));
			}
			if (statusLine.getStatusCode() == 400 || statusLine.getStatusCode() == 404
					|| statusLine.getStatusCode() == 404) {
				// force endpoint expiration
				cachedTimestamp = 0L;
				error = new EndpointNotFound(String.format("The server said: [%s]", statusLine.getReasonPhrase()));
			} else if (statusLine.getStatusCode() >= 400) {
				// TODO inform user
				error = new RuntimeException(String.format("The server said: [%s]", statusLine.getReasonPhrase()));
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
		if (error != null) {
			throw new UserFriendlyException(String.format("Error from the server %s", str), error);
		}
		return str;
	}

	private String sendData(String method, URI uri, Object data, AddHeadersListener listener) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("sending via %s - %s data %s", method, uri, data));
		}

		String strdata = new ObjectMapper().writeValueAsString(data);
		StringEntity myEntity = new StringEntity(strdata, ContentType.create("application/json", "UTF-8"));

		HttpEntityEnclosingRequest request = "POST".equals(method) ? new HttpPost(uri) : new HttpPut(uri);
		request.addHeader("Accept", "application/json");
		if (listener != null) {
			listener.addHeaders(request);
		}
		request.setEntity(myEntity);

		String toRet = "";
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse resp = httpclient.execute((HttpUriRequest) request);
			toRet = manage(resp);
		} catch (ConnectException e) {
			// force endpoint expiration
			cachedTimestamp = 0L;
			throw e;
		}
		return toRet;

	}

	public void updateData(URI uri, Object data) throws Exception {
		updateData(uri, data, addGCubeHeaders);
	}

	public void updateData(URI uri, Object data, AddHeadersListener listener) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("ready to update using [%s] data %s", uri, data));
		}
		sendData(HttpMethod.POST, uri, data, listener);

	}

	public long addData(URI uri, Object data) throws Exception {
		return addData(uri, data, addGCubeHeaders);
	}

	public long addData(URI uri, Object data, AddHeadersListener listener) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("ready to add using [%s] data %s", uri, data));
		}
		return Long.parseLong(sendData(HttpMethod.PUT, uri, data, listener));
	}

	public interface AddHeadersListener {
		public void addHeaders(HttpMessage message) throws Exception;
	}

	public class EndpointNotFound extends Exception {

		public EndpointNotFound() {
			super();
		}

		public EndpointNotFound(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public EndpointNotFound(String message, Throwable cause) {
			super(message, cause);
		}

		public EndpointNotFound(String message) {
			super(message);
		}

		public EndpointNotFound(Throwable cause) {
			super(cause);
		}

	}

}
