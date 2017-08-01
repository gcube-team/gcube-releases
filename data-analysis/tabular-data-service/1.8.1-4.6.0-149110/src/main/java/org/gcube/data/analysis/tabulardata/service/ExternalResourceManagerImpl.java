package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.utils.Util.toResourceDescriptor;
import static org.gcube.data.analysis.tabulardata.utils.Util.toResourceDescriptorList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.webservice.ExternalResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.metadata.resources.StorableResource;
import org.gcube.data.analysis.tabulardata.model.resources.ResourceType;
import org.gcube.data.analysis.tabulardata.operation.OperationId;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemoverProvider;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.Factories;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebService(portName = "ExternalResourceManagerPort",
serviceName = ExternalResourceManager.SERVICE_NAME,
targetNamespace = Constants.EXT_RESOURCE_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.ExternalResourceManager")
@Singleton
@WeldService
public class ExternalResourceManagerImpl implements ExternalResourceManager{

	private Logger logger = LoggerFactory.getLogger(ExternalResourceManagerImpl.class);

	@Inject
	private Factories factories;

	@Inject
	EntityManagerHelper emHelper;

	@Override
	public List<ResourceDescriptor> getResourcePerTabularResource(
			long tabularResourceId) throws NoSuchTabularResourceException, InternalSecurityException {
		List<StorableResource> descriptors = emHelper.getResults("RES.getById", StorableResource.class, Collections.singletonMap("id", (Object)tabularResourceId));
		logger.trace("requesting resources for id {} and returning {} elements ",tabularResourceId, descriptors.size() );
		return toResourceDescriptorList(descriptors);
	}

	@Override
	public List<ResourceDescriptor> getResourcePerTabularResourceAndType(
			long tabularResourceId, ResourceType type)
					throws NoSuchTabularResourceException, InternalSecurityException {
		Map<String, Object> parameters = new HashMap<>(2);
		parameters.put("id", tabularResourceId);
		parameters.put("type", type.name());
		List<StorableResource> descriptors = emHelper.getResults("RES.getByType", StorableResource.class, parameters);
		logger.trace("requesting resources for id {} with type {} and returning {} elements ",tabularResourceId, type, descriptors.size() );
		return toResourceDescriptorList(descriptors);
	}

	@Override
	public ResourceDescriptor removeResource(final long resourceId)
			throws InternalSecurityException {
		logger.trace("requesting remove resource for id {}", resourceId);
		EntityManager em = emHelper.getEntityManager();
		StorableResource sr = em.find(StorableResource.class, resourceId);

		if (sr==null) throw new InternalSecurityException("Storable resource not found");

		ResourceDescriptor descriptor =toResourceDescriptor(sr);

		WorkerFactory<?> factory = factories.get(new OperationId(sr.getCreatorId()));
		if (factory!=null && factory instanceof ResourceRemoverProvider){
			try {
				((ResourceRemoverProvider)factory).getResourceRemover().onRemove(sr.getResource());
				logger.info("resource with id {} removed", sr.getId());
			} catch (Exception e) {
				logger.warn("error remotely removing resource with id {} created by {} ",sr.getId(), factory.getOperationDescriptor().getName());
			}
		}

		sr.getTabularResource().removeResource(sr);
		try{
			em.getTransaction().begin();
			em.merge(sr.getTabularResource());
			em.remove(sr);
			em.getTransaction().commit();
		}catch (RollbackException re) {
			logger.error("error on transaction code",re );
			em.clear();
			throw re;
		}catch(DatabaseException de){
			logger.error("database error code is "+de.getDatabaseErrorCode(),de);
			em.getTransaction().rollback();
			throw de;
		}catch (RuntimeException e) {
			logger.error("error on transaction code",e );
			em.getTransaction().rollback();
			em.clear();
			throw e;
		}

		logger.trace("resource with id {} removed", resourceId);
		return descriptor;
	}

}
