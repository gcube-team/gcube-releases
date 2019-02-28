package org.gcube.gcat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.gcube.common.gxhttp.reference.GXConnection.HTTPMETHOD;
import org.gcube.common.gxhttp.request.GXHTTPStringRequest;
import org.gcube.gcat.api.GCatConstants;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
abstract class GCatClient {
	
	protected URL serviceURL;
	protected List<String> basePaths;
	
	protected GXHTTPStringRequest gxhttpStringRequest;
	
	
	public GCatClient(String basePath, String... basePaths) throws MalformedURLException {
		this.serviceURL = GCatClientDiscovery.getServiceURL();
		this.basePaths = new ArrayList<>();
		this.basePaths.add(basePath);
		this.basePaths.addAll(Arrays.asList(basePaths));
	}
	
	protected static StringBuilder getStringBuilder(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		}
		
		return result;
	}
	
	protected String parseHttpURLConnection(HttpURLConnection connection) throws WebApplicationException {
		try {
			int responseCode = connection.getResponseCode();
			// String responseMessage = connection.getResponseMessage();
			
			if(connection.getRequestMethod().compareTo(HTTPMETHOD.HEAD.toString()) == 0) {
				if(responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
					return null;
				}
				if(responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					throw new NotFoundException();
				}
				if(responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
					throw new ForbiddenException();
				}
			}
			
			if(responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
				InputStream inputStream = connection.getErrorStream();
				StringBuilder result = getStringBuilder(inputStream);
				String res = result.toString();
				throw new WebApplicationException(res, responseCode);
			}
			
			StringBuilder result = getStringBuilder(connection.getInputStream());
			return result.toString();
		} catch (WebApplicationException e) {
			throw e;
		} catch (Exception e) {
			throw new WebApplicationException(e);
		} finally {
			connection.disconnect();
		}
	}
	
	protected void initRequest() throws UnsupportedEncodingException {
		gxhttpStringRequest = GXHTTPStringRequest.newRequest(serviceURL.toString());
		gxhttpStringRequest.from(GCatClient.class.getSimpleName());
		for(String p : basePaths) {
			gxhttpStringRequest.path(p);
		}
	}
	
	protected String list(Map<String, String> queryParams, String... paths) throws WebApplicationException {
		try {
			initRequest();
			for(String p : paths) {
				gxhttpStringRequest.path(p);
			}
			gxhttpStringRequest.queryParams(queryParams);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	protected String create(String body, String... paths) {
		try {
			initRequest();
			for(String p : paths) {
				gxhttpStringRequest.path(p);
			}
			HttpURLConnection httpURLConnection = gxhttpStringRequest.post(body);
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	
	protected String read(String... paths) throws WebApplicationException {
		try {
			initRequest();
			for(String p : paths) {
				gxhttpStringRequest.path(p);
			}
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	protected String update(String body, String... paths) throws WebApplicationException {
		try {
			initRequest();
			for(String p : paths) {
				gxhttpStringRequest.path(p);
			}
			HttpURLConnection httpURLConnection = gxhttpStringRequest.put(body);
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	protected String patch(String body, String... paths) throws WebApplicationException {
		try {
			initRequest();
			for(String p : paths) {
				gxhttpStringRequest.path(p);
			}
			gxhttpStringRequest.withBody(body);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.patch();
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	protected String delete(Boolean purge, String... paths) throws WebApplicationException {
		try {
			initRequest();
			for(String p : paths) {
				gxhttpStringRequest.path(p);
			}
			if(purge!=null) {
				Map<String, String> queryParams = new HashMap<>();
				queryParams.put(GCatConstants.PURGE_QUERY_PARAMETER, String.valueOf(purge));
				gxhttpStringRequest.queryParams(queryParams);
			}
			HttpURLConnection httpURLConnection = gxhttpStringRequest.delete();
			return parseHttpURLConnection(httpURLConnection);
		}catch (WebApplicationException e) {
			throw e;
		}catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
}
