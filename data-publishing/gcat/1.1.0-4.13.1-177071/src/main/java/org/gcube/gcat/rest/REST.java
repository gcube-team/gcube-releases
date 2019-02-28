package org.gcube.gcat.rest;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.gcube.gcat.ResourceInitializer;
import org.gcube.gcat.api.interfaces.CRUD;
import org.gcube.gcat.persistence.ckan.CKAN;

public class REST<C extends CKAN> extends BaseREST implements CRUD<Response, Response> {
	
	protected final String COLLECTION_PARAMETER;
	protected final String ID_PARAMETER;
	protected final Class<C> reference;
	
	public REST(String collection_name, String id_name, Class<C> reference) {
		this.COLLECTION_PARAMETER = collection_name;
		this.ID_PARAMETER = id_name;
		this.reference = reference;
	}
	
	protected C getInstance() {
		try {
			C ckan = reference.newInstance();
			return ckan;
		} catch(Exception e) {
			throw new InternalServerErrorException();
		}
	}
	
	public String list(int limit, int offset) {
		setCalledMethod("GET /" + COLLECTION_PARAMETER);
		C ckan = getInstance();
		return ckan.list(limit, offset);
	}
	
	@Override
	public Response create(String json) {
		setCalledMethod("POST /" + COLLECTION_PARAMETER);
		C ckan = getInstance();
		String ret = ckan.create(json);
		
		ResponseBuilder responseBuilder = Response.status(Status.CREATED).entity(ret);
		responseBuilder = addLocation(responseBuilder, ckan.getName());
		return responseBuilder.type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8).build();
	}
	
	@Override
	public String read(String id) {
		setCalledMethod("GET /" + COLLECTION_PARAMETER + "/{" + ID_PARAMETER + "}");
		C ckan = getInstance();
		ckan.setName(id);
		return ckan.read();
	}
	
	@Override
	public String update(String id, String json) {
		setCalledMethod("PUT /" + COLLECTION_PARAMETER + "/{" + ID_PARAMETER + "}");
		C ckan = getInstance();
		ckan.setName(id);
		return ckan.update(json);
	}
	

	public String patch(String id, String json) {
		setCalledMethod("PATCH /" + COLLECTION_PARAMETER + "/{" + ID_PARAMETER + "}");
		C ckan = getInstance();
		ckan.setName(id);
		return ckan.patch(json);
	}
	
	@Override
	public Response delete(String id) {
		return delete(id, false);
	}
	
	public Response delete(String id, Boolean purge) {
		if(purge) {
			setCalledMethod("PURGE /" + COLLECTION_PARAMETER + "/{" + ID_PARAMETER + "}");
		} else {
			setCalledMethod("DELETE /" + COLLECTION_PARAMETER + "/{" + ID_PARAMETER + "}");
		}
		C ckan = getInstance();
		ckan.setName(id);
		ckan.delete(purge);
		return Response.status(Status.NO_CONTENT).build();
	}
	
	public Response purge(String id) {
		return delete(id, true);
	}
	
	
}
