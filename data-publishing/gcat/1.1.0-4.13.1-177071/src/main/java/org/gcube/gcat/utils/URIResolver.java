package org.gcube.gcat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;

import org.gcube.common.gxhttp.request.GXHTTPStringRequest;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.utils.url.EntityContext;
import org.gcube.gcat.persistence.ckan.CKAN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class URIResolver {
	
	private static final String CATALOGUE_CONTEXT = "gcube_scope";
	private static final String ENTITY_TYPE = "entity_context";
	private static final String ENTITY_NAME = "entity_name";
	private static final String CATALOGUE_PLAIN_URL = "clear_url";
	
	protected ObjectMapper mapper;
	
	public URIResolver() {
		this.mapper = new ObjectMapper();
	}
	
	protected StringBuilder getStringBuilder(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
		}
		return result;
	}
	
	public String getCatalogueItemURL(String name) {
		try {
			DataCatalogue dataCatalogue = CKAN.getCatalogue();
			String uriResolverURL = dataCatalogue.getUriResolverUrl();
			
			ObjectNode requestContent = mapper.createObjectNode();
			requestContent.put(CATALOGUE_CONTEXT, ContextUtility.getCurrentContext());
			requestContent.put(ENTITY_TYPE, EntityContext.PRODUCT.toString());
			requestContent.put(ENTITY_NAME, name);
			requestContent.put(CATALOGUE_PLAIN_URL, String.valueOf(true));
			
			GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(uriResolverURL); 
			gxhttpStringRequest.from(Constants.CATALOGUE_NAME);
			// gxhttpStringRequest.header("Content-type", GXConnection.APPLICATION_JSON_CHARSET_UTF_8);
			gxhttpStringRequest.isExternalCall(true);
			String body = mapper.writeValueAsString(requestContent);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.post(body);
			
			if(httpURLConnection.getResponseCode()!=200) {
				throw new InternalServerErrorException("Unable to get Item URL via URI Resolver");
			}
			
			String url = getStringBuilder(httpURLConnection.getInputStream()).toString();
			
			return url;
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
}
