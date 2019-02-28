package org.gcube.gcat.persistence.ckan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.gxhttp.request.GXHTTPStringRequest;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogueFactory;
import org.gcube.gcat.utils.Constants;
import org.gcube.gcat.utils.ContextUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public abstract class CKAN {
	
	private static final Logger logger = LoggerFactory.getLogger(CKAN.class);
	
	protected static final String ID_KEY = "id";
	protected static final String NAME_KEY = "name";
	
	protected static final String ERROR_KEY = "error";
	protected static final String ERROR_TYPE_KEY = "__type";
	protected static final String MESSAGE_KEY = "message";
	protected static final String OWNER_ORG_KEY = "owner_org";
	
	protected static final String RESULT_KEY = "result";
	protected static final String SUCCESS_KEY = "success";
	
	public static final String LIMIT_KEY = "limit";
	public static final String OFFSET_KEY = "offset";
	
	protected static final String NOT_FOUND_ERROR = "Not Found Error";
	protected static final String AUTHORIZATION_ERROR = "Authorization Error";
	protected static final String VALIDATION_ERROR = "Validation Error";
	
	// api rest path CKAN
	public final static String CKAN_API_PATH = "/api/3/action/";
	
	// ckan header authorization 
	public final static String AUTH_CKAN_HEADER = "Authorization";
	
	public final static String NAME_REGEX = "^[a-z0-9_\\\\-]{2,100}$";
	
	protected String LIST;
	protected String CREATE;
	protected String READ;
	protected String UPDATE;
	protected String PATCH;
	protected String DELETE;
	protected String PURGE;
	
	protected final ObjectMapper mapper;
	protected final DataCatalogue dataCatalogue;
	
	protected String name;
	protected String apiKey;
	
	protected JsonNode result;
	
	protected String nameRegex;
	
	public String getApiKey() {
		if(apiKey == null) {
			try {
				return CKANUtility.getApiKey();
			} catch(Exception e) {
				throw new InternalServerErrorException(e);
			}
		}
		return apiKey;
	}
	
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ObjectMapper getMapper() {
		return mapper;
	}
	
	public JsonNode getJsonNodeResult() {
		return result;
	}
	
	protected CKAN() {
		try {
			this.mapper = new ObjectMapper();
			this.dataCatalogue = getCatalogue();
			this.nameRegex = CKAN.NAME_REGEX;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	protected JsonNode getAsJsonNode(String json) {
		try {
			return mapper.readTree(json);
		} catch(IOException e) {
			throw new BadRequestException(e);
		}
	}
	
	/**
	 * Retrieve an instance of the library for the current scope
	 * @return
	 * @throws Exception 
	 */
	public static DataCatalogue getCatalogue() throws Exception {
		String context = ContextUtility.getCurrentContext();
		logger.debug("Discovering ckan instance in context {}", context);
		return DataCatalogueFactory.getFactory().getUtilsPerScope(context);
	}
	
	/**
	 * Validate the CKAN response and return the 
	 * @param json
	 * @return
	 */
	protected JsonNode validateCKANResponse(String json) {
		JsonNode jsonNode = getAsJsonNode(json);
		if(jsonNode.get(SUCCESS_KEY).asBoolean()) {
			return jsonNode.get(RESULT_KEY);
		} else {
			try {
				JsonNode error = jsonNode.get(ERROR_KEY);
				
				String errorType = error.get(ERROR_TYPE_KEY).asText();
				
				if(errorType.compareTo(VALIDATION_ERROR) == 0) {
					throw new BadRequestException(getAsString(error));
				}
				
				String message = error.get(MESSAGE_KEY).asText();
				
				if(errorType.compareTo(NOT_FOUND_ERROR) == 0) {
					throw new NotFoundException(message);
				}
				
				if(errorType.compareTo(AUTHORIZATION_ERROR) == 0) {
					throw new NotAuthorizedException(message);
				}
				
				// TODO parse more cases
			} catch(WebApplicationException e) {
				throw e;
			} catch(Exception e) {
				throw new BadRequestException(json);
			}
			throw new BadRequestException(json);
		}
	}
	
	protected String getAsString(JsonNode node) {
		try {
			String json = mapper.writeValueAsString(node);
			return json;
		} catch(JsonProcessingException e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	protected JsonNode checkName(JsonNode jsonNode) {
		try {
			String gotName = jsonNode.get(NAME_KEY).asText();
			if(!gotName.matches(nameRegex)) {
				throw new BadRequestException("The 'name' must be between 2 and 100 characters long and contain only lowercase alphanumeric characters, '-' and '_'. You can validate your name using the regular expression : " + NAME_REGEX);
			}
			
			if(name == null) {
				name = gotName;
			}
			
			if(gotName != null && gotName.compareTo(name) != 0) {
				String error = String.format(
						"The name (%s) does not match with the '%s' contained in the provided content (%s).", name,
						NAME_KEY, gotName);
				throw new BadRequestException(error);
			}
			return jsonNode;
		} catch(BadRequestException e) {
			throw e;
		} catch(Exception e) {
			throw new BadRequestException("Unable to obtain a correct 'name' from the provided content");
		}
	}
	
	protected JsonNode checkName(String json) {
		JsonNode jsonNode = getAsJsonNode(json);
		checkName(jsonNode);
		return jsonNode;
	}
	
	protected JsonNode createJsonNodeWithID(String id) {
		ObjectNode objectNode = mapper.createObjectNode();
		objectNode.put(ID_KEY, id);
		return objectNode;
	}
	
	protected JsonNode createJsonNodeWithNameAsID() {
		return createJsonNodeWithID(name);
	}
	
	protected Map<String,String> getMapWithNameAsID() {
		return getMapWithID(name);
	}
	
	protected Map<String,String> getMapWithID(String id) {
		Map<String,String> map = new HashMap<>();
		map.put(ID_KEY, id);
		return map;
	}
	
	protected StringBuilder getStringBuilder(InputStream inputStream) throws IOException {
		StringBuilder result = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while((line = reader.readLine()) != null) {
				result.append(line);
			}
		}
		
		return result;
	}
	
	protected GXHTTPStringRequest getGXHTTPStringRequest(String path) throws UnsupportedEncodingException {
		String catalogueURL = dataCatalogue.getCatalogueUrl();
		GXHTTPStringRequest gxhttpStringRequest = GXHTTPStringRequest.newRequest(catalogueURL);
		gxhttpStringRequest.from(Constants.CATALOGUE_NAME);
		gxhttpStringRequest.header("Content-type", MediaType.APPLICATION_JSON);
		gxhttpStringRequest.isExternalCall(true);
		gxhttpStringRequest.header(AUTH_CKAN_HEADER, getApiKey());
		gxhttpStringRequest.path(path);
		return gxhttpStringRequest;
	}
	
	protected String getResultAsString(HttpURLConnection httpURLConnection) throws IOException {
		int responseCode  = httpURLConnection.getResponseCode();
		if(responseCode >= Status.BAD_REQUEST.getStatusCode()) {
			Status status = Status.fromStatusCode(responseCode);
			throw new WebApplicationException(status);
		}
		InputStream inputStream = httpURLConnection.getInputStream();
		String ret = getStringBuilder(inputStream).toString();
		logger.trace("Got Respose is {}", ret);
		result = validateCKANResponse(ret);
		if(result instanceof NullNode) {
			result = mapper.createObjectNode();
		}
		return getAsString(result);
	}
	
	protected String sendGetRequest(String path, Map<String,String> parameters) {
		try {
			logger.debug("Going to send GET request with parameters {}", parameters);
			GXHTTPStringRequest gxhttpStringRequest = getGXHTTPStringRequest(path);
			gxhttpStringRequest.queryParams(parameters);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.get();
			return getResultAsString(httpURLConnection);
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	protected String sendPostRequest(String path, String body) {
		try {
			logger.debug("Going to send POST request with body {}", body);
			GXHTTPStringRequest gxhttpStringRequest = getGXHTTPStringRequest(path);
			HttpURLConnection httpURLConnection = gxhttpStringRequest.post(body);
			return getResultAsString(httpURLConnection);
		} catch(WebApplicationException e) {
			throw e;
		} catch(Exception e) {
			throw new InternalServerErrorException(e);
		}
	}
	
	protected String sendPostRequest(String path, JsonNode jsonNode) {
		return sendPostRequest(path, getAsString(jsonNode));
	}
	
	public String list(int limit, int offset) {
		Map<String,String> parameters = new HashMap<>();
		if(limit > 0) {
			parameters.put(LIMIT_KEY, String.valueOf(limit));
		}
		if(offset >= 0) {
			parameters.put(OFFSET_KEY, String.valueOf(offset));
		}
		return sendGetRequest(LIST, parameters);
	}
	
	public String create(String json) {
		return sendPostRequest(CREATE, json);
	}
	
	public String read() {
		return sendGetRequest(READ, getMapWithNameAsID());
	}
	
	public String update(String json) {
		checkName(json);
		return sendPostRequest(UPDATE, json);
	}
	
	public String patch(String json) {
		JsonNode jsonNode = checkName(json);
		ObjectNode objectNode = ((ObjectNode) jsonNode);
		objectNode.put(ID_KEY, name);
		objectNode.remove(NAME_KEY);
		return sendPostRequest(PATCH, objectNode);
	}
	
	protected void delete() {
		sendPostRequest(DELETE, createJsonNodeWithNameAsID());
	}
	
	public void delete(boolean purge) {
		if(purge) {
			purge();
		} else {
			delete();
		}
	}
	
	protected void purge() {
		sendPostRequest(PURGE, createJsonNodeWithNameAsID());
	}
	
}
