package org.gcube.rest.commons.publisher.resourceregistry;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.rest.commons.db.dao.app.GeneralResourceModelDao;
import org.gcube.rest.commons.db.dao.app.RunInstanceModelDao;
import org.gcube.rest.commons.db.model.app.GeneralResourceModel;
import org.gcube.rest.commons.db.model.app.RunInstanceModel;
import org.gcube.rest.commons.resourceawareservice.resources.GeneralResource;
import org.gcube.rest.commons.resourceawareservice.resources.RunInstance;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisher;
import org.gcube.rest.resourcemanager.publisher.ResourcePublisherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PublisherRRimpl<T extends GeneralResource> extends ResourcePublisher<T> {

	private static final Logger logger = LoggerFactory.getLogger(PublisherRRimpl.class);
	
	private final GeneralResourceModelDao generalResourceModelDao;
	private final RunInstanceModelDao runInstanceModelDao;
	
	@Inject
	public PublisherRRimpl(
			GeneralResourceModelDao generalResourceModelDao, RunInstanceModelDao runInstanceModelDao) {
		this.generalResourceModelDao = generalResourceModelDao;
		this.runInstanceModelDao = runInstanceModelDao;
	}
	
	@Override
	public void deleteResource(String resourceID, String scope) throws ResourcePublisherException{
		
		logger.info("deleting resource with ID : " + resourceID + " from RR...");
		
		try {
			GeneralResourceModel resourceModel = generalResourceModelDao.getByResourceID(resourceID, scope);
			generalResourceModelDao.delete(resourceModel);
		} catch (Exception e) {
			logger.warn("error while deleting the resource with id : " + resourceID, e);
			throw new ResourcePublisherException("error while deleting the resource with id : " + resourceID, e);
		}
		logger.info("deleting resource with ID : " + resourceID + " from RR...OK");
	}
	
	@Override
	public void publishResource(T resource, String resourceClass, String resourceNamePref, String scope, boolean includeIdinName, boolean onlyBody) throws ResourcePublisherException{
		
		logger.info("publishing resource with ID : " + resource.getResourceID() + " to RR...");
		try {
			GeneralResourceModel resourceModel = new GeneralResourceModel(resource);
			generalResourceModelDao.save(resourceModel);
		} catch (Exception e) {
			logger.warn("error while saving the resource with id : " + resource.getResourceID(), e);
			throw new ResourcePublisherException("error while deleting the resource with id : " + resource.getResourceID(), e);
		}
		
		logger.info("publishing resource with ID : " + resource.getResourceID() + " to RR...OK");
	}
	
	@Override
	public void updateResource(T resource, String resourceClass, String resourceNamePref, String scope, boolean includeIdinName, boolean onlyBody) throws ResourcePublisherException {
		logger.info("updating resource with ID : " + resource.getResourceID() + " to RR...");
		publishResource(resource, resourceClass, resourceNamePref, scope);
		logger.info("updating resource with ID : " + resource.getResourceID() + " to RR...OK");
	}

	@Override
	public void updateResource(T resource, String scope) throws ResourcePublisherException {
		if (!(resource instanceof RunInstance)) {
			throw new ResourcePublisherException("resource not supported yet: " + resource.getClass().getCanonicalName());
		}
		
		logger.info("updating resource with ID : " + ((RunInstance)resource).getId() + " to RR...");
		try {
			RunInstanceModel resourceModel = runInstanceModelDao.getByResourceID(((RunInstance)resource).getId()).get(0);
			resourceModel.copyFrom((RunInstance) resource);
			runInstanceModelDao.save(resourceModel);
		} catch (Exception e) {
			logger.warn("error while saving the resource with id : " + resource.getResourceID(), e);
			throw new ResourcePublisherException("error while deleting the resource with id : " + resource.getResourceID(), e);
		}
		
		logger.info("updating resource with ID : " + ((RunInstance)resource).getId() + " to RR...OK");
	}
}
