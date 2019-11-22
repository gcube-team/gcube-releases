package org.gcube.informationsystem.resourceregistry.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.informationsystem.model.reference.AccessType;
import org.gcube.informationsystem.resourceregistry.ResourceInitializer;
import org.gcube.informationsystem.resourceregistry.api.exceptions.ResourceRegistryException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaException;
import org.gcube.informationsystem.resourceregistry.api.exceptions.schema.SchemaNotFoundException;
import org.gcube.informationsystem.resourceregistry.api.rest.AccessPath;
import org.gcube.informationsystem.resourceregistry.api.rest.InstancePath;
import org.gcube.informationsystem.resourceregistry.api.rest.TypePath;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall.HTTPMETHOD;
import org.gcube.informationsystem.resourceregistry.er.ERManagementUtility;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagement;
import org.gcube.informationsystem.resourceregistry.schema.SchemaManagementImpl;
import org.gcube.informationsystem.types.TypeBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@Path(TypePath.TYPES_PATH_PART)
public class SchemaManager {
	
	private static Logger logger = LoggerFactory.getLogger(SchemaManager.class);
	
	protected void setCalledMethod(HTTPMETHOD httpMethod, String type) {
		setCalledMethod(httpMethod, type, null);
	}
	
	protected void setCalledMethod(HTTPMETHOD httpMethod, String type, Boolean polymorphic) {
		List<String> list = new ArrayList<>();
		list.add(TypePath.TYPES_PATH_PART);
		list.add(type);
		
		Map<String, String> map = null;
		if(polymorphic!=null) {
			map = new HashMap<String, String>();
			map.put(InstancePath.POLYMORPHIC_PARAM, polymorphic.toString());
		}
		
		Access.setCalledMethod(httpMethod, list, map);
	}
	
	/*
	 * PUT /types/{TYPE_NAME}
	 * e.g. PUT /types/ContactFacet
	 * 
	 * BODY: {...}
	 * 
	 */
	@PUT
	@Path("{" + AccessPath.TYPE_PATH_PARAM + "}")
	@Consumes({MediaType.TEXT_PLAIN, ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8})
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public Response create(@PathParam(AccessPath.TYPE_PATH_PARAM) String type, String json)
			throws SchemaException, ResourceRegistryException {
		logger.info("Requested {} creation with schema {}", type, json);
		setCalledMethod(HTTPMETHOD.PUT, type);
		
		
		AccessType accessType = null;
		String firstGotType = null;
		try {
			Set<String> superClasses = TypeBinder.deserializeTypeDefinition(json).getSuperClasses();
			if(superClasses.size()==0) {
				throw new ResourceRegistryException("No superclasses defined");
			}
			for(String superClass : superClasses) {
				accessType = ERManagementUtility.getBaseAccessType(superClass);
				break;
			}
		} catch (ResourceRegistryException e) {
			throw e;
		} catch(Exception e) {
			String error = String.format("Cannot register %s schema. Superclass %s not found", type, firstGotType);
			throw new ResourceRegistryException(error);
		}
		
		SchemaManagement schemaManagement = new SchemaManagementImpl();
		((SchemaManagementImpl) schemaManagement).setTypeName(type);
		String ret = schemaManagement.create(json, accessType);
		return Response.status(Status.CREATED).entity(ret).type(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
				.build();
	}
	
	/*
	 * GET /types/{TYPE_NAME}
	 * e.g. GET /types/ContactFacet?polymorphic=false
	 * 
	 */
	@GET
	@Path("{" + AccessPath.TYPE_PATH_PARAM + "}")
	@Produces(ResourceInitializer.APPLICATION_JSON_CHARSET_UTF_8)
	public String read(@PathParam(AccessPath.TYPE_PATH_PARAM) String type,
			@QueryParam(TypePath.POLYMORPHIC_PARAM) @DefaultValue("false") Boolean polymorphic)
			throws SchemaNotFoundException, ResourceRegistryException {
		logger.info("Requested Schema for type {}", type);
		setCalledMethod(HTTPMETHOD.PUT, type, polymorphic);
		SchemaManagement schemaManagement = new SchemaManagementImpl();
		return schemaManagement.read(type, polymorphic);
	}
	
}
