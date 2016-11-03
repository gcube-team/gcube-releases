package org.gcube.datatransformation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.gcube.datatransformation.client.library.beans.Types.FindApplicableTransformationUnits;
import org.gcube.datatransformation.client.library.beans.Types.FindApplicableTransformationUnitsResponse;
import org.gcube.datatransformation.client.library.beans.Types.FindAvailableTargetContentTypes;
import org.gcube.datatransformation.client.library.beans.Types.FindAvailableTargetContentTypesResponse;
import org.gcube.datatransformation.client.library.beans.Types.QueryTransformationPrograms;
import org.gcube.datatransformation.client.library.beans.Types.QueryTransformationProgramsResponse;
import org.gcube.datatransformation.client.library.beans.Types.TransformData;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataResponse;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationProgram;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationProgramResponse;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnit;
import org.gcube.datatransformation.client.library.beans.Types.TransformDataWithTransformationUnitResponse;
import org.gcube.datatransformation.client.library.exceptions.DTSClientException;
import org.gcube.datatransformation.client.library.exceptions.DTSException;
import org.gcube.datatransformation.client.library.exceptions.EmptySourceException;
import org.gcube.datatransformation.rest.commons.DataTransformationDiscoverer;
import org.gcube.datatransformation.rest.commons.DataTransformationDiscovererAPI;
import org.gcube.datatransformation.rest.commons.DataTransformationServiceAPI;
import org.gcube.rest.resourcemanager.is.discoverer.ri.icclient.RIDiscovererISimpl;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class DataTransformationClient {
	private static final Logger logger = LoggerFactory.getLogger(DataTransformationClient.class);

	private Gson gson;
	private String scope;
	private String endpoint;
	private DataTransformationDiscovererAPI dataTransformationDiscoverer;

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
		this.dataTransformationDiscoverer = new DataTransformationDiscoverer(new RIDiscovererISimpl());
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public DataTransformationClient() {
		this.gson = new Gson();
	}

	public void initializeClient(String endpoint) {
		this.endpoint = endpoint;
	}

	public void randomClient() throws DTSClientException {
		Random random = new Random();

		Set<String> ris = this.dataTransformationDiscoverer.discoverDataTransformationRunninInstances(this.scope);
		if (ris == null || ris.size() == 0) {
			throw new DTSClientException("No data transformation endopoints found in scope: " + scope);
		}

		List<String> dataTransformationServices = new ArrayList<String>(ris);
		this.endpoint = dataTransformationServices.get(random.nextInt(dataTransformationServices.size()));
	}

	public String statistics() throws DTSException {
		DataTransformationServiceAPI simple = getDTSClient();

		Response response = null;

		response = simple.statistics(scope);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			String error = response.readEntity(String.class);
			Exception e1 = null;
			try {
				response.close();
				JsonObject obj = (JsonObject)new JsonParser().parse(error);
				e1 = getThrownException(obj.get("exception").getAsString());
			} catch(Exception e) {
				response.close();
				logger.error("parse response error", e);
			}
			throw new DTSException("Received exception",e1);
		}

		String xml = response.readEntity(String.class);
		response.close();

		return xml;
	}

	public TransformDataResponse transformData(TransformData request) throws DTSException {
		DataTransformationServiceAPI simple = getDTSClient();

		Response response = null;

		response = simple.transformData(scope, gson.toJson(request.input), gson.toJson(request.targetContentType), gson.toJson(request.output), request.createReport.toString(),
				false, true);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			String error = response.readEntity(String.class);
			Exception e1 = null;
			try {
				response.close();
				JsonObject obj = (JsonObject)new JsonParser().parse(error);
				e1 = getThrownException(obj.get("exception").getAsString());
			} catch(Exception e) {
				response.close();
				logger.error("parse response error", e);
			}
			throw new DTSException("Received exception",e1);
		}

		String json = response.readEntity(String.class);
		response.close();

		return gson.fromJson(json, TransformDataResponse.class);
	}

	public TransformDataWithTransformationProgramResponse transformDataWithTransformationProgram(TransformDataWithTransformationProgram request) throws DTSException {
		DataTransformationServiceAPI simple = getDTSClient();

		Response response = null;
		response = simple.transformDataWithTransformationProgram(scope, gson.toJson(request.input), gson.toJson(request.tpID), gson.toJson(request.targetContentType),
				gson.toJson(request.tProgramUnboundParameters), gson.toJson(request.output), request.createReport.toString(), false, true);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			String error = response.readEntity(String.class);
			Exception e1 = null;
			try {
				response.close();
				JsonObject obj = (JsonObject)new JsonParser().parse(error);
				e1 = getThrownException(obj.get("exception").getAsString());
			} catch(Exception e) {
				response.close();
				logger.error("parse response error", e);
			}
			throw new DTSException("Received exception",e1);
		}

		String json = response.readEntity(String.class);
		response.close();

		return gson.fromJson(json, TransformDataWithTransformationProgramResponse.class);
	}

	public TransformDataWithTransformationUnitResponse transformDataWithTransformationUnit(TransformDataWithTransformationUnit request) throws DTSException, EmptySourceException {
		String json = transformDataWithTransformationUnit(request, false, false);
		return gson.fromJson(json, TransformDataWithTransformationUnitResponse.class);

	}
	
	public static List<Map<String, String>> getMapFromResponse(String json) throws Exception{
		JsonReader reader = new JsonReader(new StringReader(json));
		List<Map<String, String>> messages = new ArrayList<Map<String, String>>();
		try {
			reader.beginArray();
			while (reader.hasNext()) {
				Map<String, String> message = new Gson().fromJson(reader, (new HashMap<String, String>()).getClass());
				messages.add(message);
			}
			reader.endArray();
			reader.close();
		} catch (IOException e) {
			throw new Exception("malformed json: " + json);
		}
		
		return messages;
	}
	
	public String transformDataWithTransformationUnit(TransformDataWithTransformationUnit request, boolean all, boolean pretty) throws DTSException, EmptySourceException {
		DataTransformationServiceAPI simple = getDTSClient();

		Response response = null;
		response = simple.transformDataWithTransformationUnit(scope, gson.toJson(request.inputs), gson.toJson(request.tpID), gson.toJson(request.transformationUnitID),
				gson.toJson(request.targetContentType), gson.toJson(request.tProgramUnboundParameters), gson.toJson(request.output), request.filterSources.toString(),
				request.createReport.toString(), all, pretty);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			String error = response.readEntity(String.class);
			Exception e1 = null;
			try { // case: EmptySourceException
				response.close();
				JsonObject obj = (JsonObject)new JsonParser().parse(error);
				e1 = getThrownException(obj.get("exception").getAsString());
			} catch(Exception e) {
				response.close();
				logger.error("response error", e);
			}
			if (e1 instanceof EmptySourceException)
				throw (EmptySourceException)e1;
			else
				throw new DTSException("Received exception",e1);
		}

		String json = response.readEntity(String.class);
		response.close();

		return json;
	}


	public FindApplicableTransformationUnitsResponse findApplicableTransformationUnits(FindApplicableTransformationUnits request) throws DTSException {
		DataTransformationServiceAPI simple = getDTSClient();

		Response response = null;
		response = simple.findApplicableTransformationUnits(scope, gson.toJson(request.sourceContentType), gson.toJson(request.targetContentType),
				gson.toJson(request.createAndPublishCompositeTP), true);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			String error = response.readEntity(String.class);
			Exception e1 = null;
			try {
				response.close();
				JsonObject obj = (JsonObject)new JsonParser().parse(error);
				e1 = getThrownException(obj.get("exception").getAsString());
			} catch(Exception e) {
				response.close();
				logger.error("parse response error", e);
			}
			throw new DTSException("Received exception",e1);
		}

		String json = response.readEntity(String.class);
		response.close();

		return gson.fromJson(json, FindApplicableTransformationUnitsResponse.class);
	}

	public FindAvailableTargetContentTypesResponse findAvailableTargetContent(FindAvailableTargetContentTypes request) throws DTSException {
		DataTransformationServiceAPI simple = getDTSClient();

		Response response = null;
		response = simple.findAvailableTargetContentTypes(scope, gson.toJson(request.sourceContentType), true);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			String error = response.readEntity(String.class);
			Exception e1 = null;
			try {
				response.close();
				JsonObject obj = (JsonObject)new JsonParser().parse(error);
				e1 = getThrownException(obj.get("exception").getAsString());
			} catch(Exception e) {
				response.close();
				logger.error("parse response error", e);
			}
			throw new DTSException("Received exception",e1);
		}

		String json = response.readEntity(String.class);
		response.close();

		return gson.fromJson(json, FindAvailableTargetContentTypesResponse.class);
	}

	public QueryTransformationProgramsResponse queryTransformationPrograms(QueryTransformationPrograms request) throws DTSException {
		DataTransformationServiceAPI simple = getDTSClient();

		Response response = null;
		response = simple.queryTransformationPrograms(scope, request.queryTransformationPrograms, true);

		if (response.getStatus() != Response.Status.OK.getStatusCode()) {
			String error = response.readEntity(String.class);
			Exception e1 = null;
			try {
				response.close();
				JsonObject obj = (JsonObject)new JsonParser().parse(error);
				e1 = getThrownException(obj.get("exception").getAsString());
			} catch(Exception e) {
				response.close();
				logger.error("parse response error", e);
			}
			throw new DTSException("Received exception",e1);
		}

		String json = response.readEntity(String.class);
		response.close();

		QueryTransformationProgramsResponse queryTransformationProgramsResponse = new QueryTransformationProgramsResponse();
		queryTransformationProgramsResponse.queryTransformationProgramsResponse = json;
		return queryTransformationProgramsResponse;
	}
	
	private DataTransformationServiceAPI getDTSClient() throws DTSException {
		ResteasyClient client = null;
		ResteasyWebTarget target = null;

		try {
			client = new ResteasyClientBuilder().build();
			target = client.target(this.endpoint);
			return target.proxy(DataTransformationServiceAPI.class);
		} catch (Exception e) {
			logger.error("Client could not connect to endpoint: " + this.endpoint, e);
			throw new DTSException("Client could not connect to endpoint: " + this.endpoint, e);
		}
	}
	
	private Exception getThrownException(String excSerialization) {
		return deserialiseObjectFromString(excSerialization) != null? (Exception)deserialiseObjectFromString(excSerialization) : null;
	}
	
	/** Read the object from Base64 string. */
	private static Object deserialiseObjectFromString(String s) {
		try {
			byte[] data = Base64.decodeBase64(s);
			ObjectInputStream ois;
			ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			return o;
		} catch (IOException | ClassNotFoundException e) {
			logger.error("could not deserialize exception", e);
			return null;
		}
	}

}
