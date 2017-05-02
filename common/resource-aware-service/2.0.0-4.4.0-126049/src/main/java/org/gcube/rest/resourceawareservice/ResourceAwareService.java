package org.gcube.rest.resourceawareservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.rest.commons.filter.IResourceFilter;
import org.gcube.rest.commons.helpers.JSONConverter;
import org.gcube.rest.commons.resourceawareservice.ResourceAwareServiceAPI;
import org.gcube.rest.commons.resourceawareservice.constants.ResourceAwareServiceConstants;
import org.gcube.rest.commons.resourceawareservice.resources.ResourceFactory;
import org.gcube.rest.commons.resourceawareservice.resources.StatefulResource;
import org.gcube.rest.commons.resourceawareservice.resources.exceptions.StatefulResourceException;
import org.gcube.rest.commons.resourcefile.IResourceFileUtils;
import org.gcube.rest.resourceawareservice.exceptions.ResourceAwareServiceException;
import org.gcube.rest.resourceawareservice.exceptions.ResourceNotFoundException;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public abstract class ResourceAwareService<T extends StatefulResource> implements
		ResourceAwareServiceAPI<T> {

	private static final Logger logger = LoggerFactory
			.getLogger(ResourceAwareService.class);

	private Map<String, T> statefulResources = new ConcurrentHashMap<String, T>();

	private final ResourcePublisher<T> resourcePublisher;
	private final ResourceFactory<T> factory;

	private final IResourceFilter<T> resourceFilter;
	private final IResourceFileUtils<T> resourceFileUtils;

	@Inject
	public ResourceAwareService(
			ResourceFactory<T> factory,
			ResourcePublisher<T> publisher, 
			IResourceFilter<T> resourceFilter,
			IResourceFileUtils<T> resourceFileUtils)
			throws ResourceAwareServiceException {
		try {
			logger.info("In ResourceAwareService constructor");
			this.factory = factory;
			this.resourcePublisher = publisher;
			this.resourceFilter = resourceFilter;
			this.resourceFileUtils = resourceFileUtils;

		} catch (Exception e) {
			logger.info("Error while creating service");
			throw new ResourceAwareServiceException(
					"error while creating service", e);
		}
	}

	// REST API

	@POST
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH)
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response createResourceREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@FormParam("jsonParam") String jsonParam) {
		logger.info("creating resource with jsonParam : " + jsonParam);

		String resourceID = this.createResource(jsonParam);
		String msg = null;
		Response.Status status = null;

		if (resourceID == null) {
			msg = JSONConverter.convertToJSON("Error", "Resource not created");
			status = Response.Status.BAD_REQUEST;
		} else {
			msg = JSONConverter.convertToJSON("resourceID", resourceID);
			status = Response.Status.CREATED;
		}
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH
			+ "/{id}")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response getResourceREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID,
			@DefaultValue("false") @QueryParam("pretty") Boolean pretty) {
		logger.info("getting resource with id : " + resourceID);

		String msg = null;
		Response.Status status = null;

		try {
			T resource = this.getResource(resourceID);
			logger.info("resource found!");
			msg = resource.toJSON(pretty);
			status = Response.Status.OK;
		} catch (ResourceNotFoundException e) {
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found. Resources that exist are : "
					+ this.statefulResources.keySet());
			status = Response.Status.NOT_FOUND;
		}
		return Response.status(status).entity(msg).build();
	}

	@DELETE
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH
			+ "/{id}")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response destroyResourceREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@PathParam("id") String resourceID) {
		logger.info("deleting resource with id : " + resourceID);

		T resource = this.statefulResources.get(resourceID);
		String msg = null;
		Response.Status status = null;

		if (resource == null) {
			msg = JSONConverter.convertToJSON("Error", "Resource with ID : "
					+ resourceID + " not found");
			status = Response.Status.NOT_FOUND;
		} else {
			if (destroyResource(resourceID)) {
				msg = JSONConverter.convertToJSON("Status", "Resource deleted");
				status = Response.Status.OK;
			} else {
				msg = JSONConverter.convertToJSON("Error",
						"Resource with ID : " + resourceID + " not deleted");
				status = Response.Status.BAD_REQUEST;
			}
		}

		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH)
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response listResourcesREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@DefaultValue("false") @QueryParam("complete") Boolean complete,
			@DefaultValue("false") @QueryParam("pretty") Boolean pretty) {
		String msg = null;
		Response.Status status = null;

		if (complete) {
			Collection<T> resources = this.listResources();

			msg = JSONConverter.convertToJSON(resources, pretty);
			status = Response.Status.OK;

		} else {
			Set<String> resourceIDs = this.listResourceIDs();

			msg = JSONConverter.convertToJSON(resourceIDs, pretty);
			status = Response.Status.OK;
		}
		return Response.status(status).entity(msg).build();
	}

	@GET
	@Path(value = ResourceAwareServiceConstants.RESOURCES_SERVLET_PATH
			+ "/filter")
	@Produces(MediaType.APPLICATION_JSON + "; " + "charset=UTF-8")
	public Response filterResourcesREST(
			@HeaderParam(ResourceAwareServiceConstants.SCOPE_HEADER) String scope,
			@QueryParam("filter") String filter,
			@DefaultValue("false") @QueryParam("complete") Boolean complete,
			@DefaultValue("false") @QueryParam("pretty") Boolean pretty) {
		String msg = null;
		Response.Status status = null;

		if (complete) {
			List<T> resources = getResourcesByFilter(filter);

			msg = JSONConverter.convertToJSON(resources, pretty);
			status = Response.Status.OK;
		} else {
			List<String> resourceIDs = getResourceIDsByFilter(filter);

			msg = JSONConverter.convertToJSON(resourceIDs, pretty);
			status = Response.Status.OK;
		}

		return Response.status(status).entity(msg).build();
	}

	// End of REST API

	// public abstract StatefulResource createCustomResource(String resourceID,
	// String jsonParams);

	public String createResource(String jsonParams) {
		String resourceID = UUID.randomUUID().toString();
		T resource = null;
		try {
			resource = this.factory.createResource(resourceID, jsonParams);// createCustomResource(resourceID, jsonParams);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		resource.setResourceID(resourceID);
		
		resource.setCreated(Calendar.getInstance());
		resource.setLastUpdated(Calendar.getInstance());

		this.statefulResources.put(resourceID, resource);

		try {
			this.saveResources();
			this.resourcePublisher.publishResource(resource, this.getResourceClass(), this.getResourceNamePref(), this.getScope());

		} catch (IOException | ResourcePublisherException e) {
			e.printStackTrace();
			logger.error("error while creating resource with params : "
					+ jsonParams, e);
			this.statefulResources.remove(resourceID);
			resourceID = null;
		}
		return resourceID;
	}

	public T getResource(String resourceID) throws ResourceNotFoundException {
		T resource = this.statefulResources.get(resourceID);

		if (resource == null)
			throw new ResourceNotFoundException("resource with id : "
					+ resourceID + " not found. all resources are : "
					+ this.statefulResources.keySet());

		return resource;
	}

	public Boolean destroyResource(String resourceID) {
		T resource = this.statefulResources.get(resourceID);
		
		if (this.resourceFileUtils.deleteResourceFromFile(resourceID) == false){
			logger.warn("could not delete resource file for resource : " + resourceID);
			return false;
		} 
		
		try {
			this.statefulResources.remove(resourceID);
			this.saveResources();
			this.factory.destroyResource(resource);
			this.resourcePublisher.deleteResource(resourceID, this.getScope());
		} catch (Exception e) {
			logger.error("error while destroying resource with id : "
					+ resourceID, e);
			this.statefulResources.put(resourceID, resource);
			
			return false;
		}

		return true;
	}

	public Set<String> listResourceIDs() {
		return this.statefulResources.keySet();
	}

	public Collection<T> listResources() {
		return this.statefulResources.values();
	}

	public void saveResource(String resourceID) throws IOException, ResourcePublisherException {
		logger.info("saving resource with id : " + resourceID);
		

		this.resourceFileUtils.createResourceDirectory();

		T resource = this.statefulResources.get(resourceID);
		
		if (resource.getCreated() == null)
			resource.setCreated(Calendar.getInstance());
		
		resource.setLastUpdated(Calendar.getInstance());

		this.resourceFileUtils.writeResourceToFile(resourceID, resource);
		
		this.updateOrCreateResource(resource);
	}

	private void saveResources() throws IOException {
		this.resourceFileUtils.createResourceDirectory();

		for (String resourceID : this.statefulResources.keySet()) {
			try {
				this.saveResource(resourceID);
			} catch (ResourcePublisherException e) {
				logger.warn("error saving resource with id : " + resourceID);
			}
		}
	}

	public void loadResources() {

		this.statefulResources = new HashMap<String, T>();
		
		for (File resourceFile : this.resourceFileUtils.getResourcesFiles()) {
			T statefulResource;
			try {
				statefulResource = this.resourceFileUtils
						.readResourceFromFile(resourceFile);
				logger.info("Loading resource : "
						+ statefulResource.getResourceID());
				this.factory.loadResource(statefulResource);
				
				this.statefulResources.put(resourceFile.getName(),
						statefulResource);
				
				this.updateOrCreateResource(statefulResource);
				
			} catch (StatefulResourceException | ClassNotFoundException
					| IOException | ResourcePublisherException e) {
				logger.error("error loading resource", e);
			}

		}
	}
	
	private void updateOrCreateResource(T resource) throws ResourcePublisherException {
		try {
			logger.info("creating resource after load : "
					+ resource.getResourceID());
			this.resourcePublisher.publishResource(resource, this.getResourceClass(), this.getResourceNamePref(), this.getScope());
			logger.info("resource with id : " + resource.getResourceID() + " has been created");
		} catch (ResourcePublisherException ex) {
			logger.warn("Failed to publish the resource. will try to update it");
			logger.info("updating resource with id : " + resource.getResourceID());
			this.resourcePublisher.updateResource(resource, this.getResourceClass(), this.getResourceNamePref(), this.getScope());
			logger.info("resource with id : " + resource.getResourceID() + " has been updated");
		}
	}
 
	@Override
	public void onClose() {
		for (T resource : this.statefulResources.values()) {
			try {
				resource.onClose();
			} catch (Exception e) {
				logger.warn(
						"error while closing the resource : "
								+ resource.getResourceID(), e);
			}
		}
	}

	@Override
	public void startService() {
		logger.info("Loading resources....");
		this.loadResources();
		logger.info("Loading resources....OK");
		
		logger.info("Saving resources after loading....");
		try {
			this.saveResources();
			logger.info("Saving resources after loading....OK");
		} catch (IOException e) {
			logger.warn("Error while saving resources after loading", e);
		}
	}

	@Override
	public void closeService() {
		for (T resource : this.statefulResources.values())
			try {
				logger.info("deleting resource from publisher...");
				try {
					this.resourcePublisher.deleteResource(resource.getResourceID(), this.getScope());
				} catch (ResourcePublisherException ex) {
					logger.warn("error deleting the resource from the publisher");
				}
				logger.info("closing the resource...");
				this.factory.closeResource(resource);
			} catch (StatefulResourceException e) {
				logger.warn("error closing the resource with id : "
						+ resource.getResourceID());
			}
	}
	
	
	public abstract String getScope();
	
	public abstract String getResourceClass();
	
	public abstract String getResourceNamePref();

	// Filters
	@Override
	public List<T> getAllResources() {
		return new ArrayList<T>(this.statefulResources.values());
	}

	@Override
	public List<T> getResourcesByFilter(String filterString) {
		List<T> resources = this.getAllResources();

		return this.resourceFilter.apply(resources, filterString);
	}

	@Override
	public List<String> getResourceIDsByFilter(String filterString) {
		List<T> resources = this.getAllResources();

		return this.resourceFilter.applyIDs(resources, filterString);
	}

}
