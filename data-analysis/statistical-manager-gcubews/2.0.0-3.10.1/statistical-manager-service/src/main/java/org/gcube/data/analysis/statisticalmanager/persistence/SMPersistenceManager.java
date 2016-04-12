package org.gcube.data.analysis.statisticalmanager.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.data.analysis.statisticalmanager.SMOperationStatus;
import org.gcube.data.analysis.statisticalmanager.SMOperationType;
import org.gcube.data.analysis.statisticalmanager.SMResourceType;
import org.gcube.data.analysis.statisticalmanager.exception.StatisticalManagerException;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.stubs.SMCreateTableRequest;
import org.gcube.data.analysis.statisticalmanager.util.ObjectFormatter;
import org.gcube.data.analysis.statisticalmanager.util.ServiceUtil;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMAbstractResource;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMComputation;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMEntries;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMEntry;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMError;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMImport;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMInputEntry;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMObject;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMOperation;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMSystemImport;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMPersistenceManager {

	private static Logger logger = LoggerFactory.getLogger(SMPersistenceManager.class);

	public static long addImporter(final SMCreateTableRequest request)
			throws Exception {
		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {

			SMImport smimport = new SMImport();
			smimport.setFileName(request.getTableName());
			smimport.setOperationType(SMOperationType.IMPORTED.ordinal());
			smimport.setPortalLogin(request.getUser());
			smimport.setSubmissionDate(Calendar.getInstance());
			smimport.setDescription(request.getDescription());
			smimport.setOperationStatus(SMOperationStatus.RUNNING.ordinal());
			session.save(smimport);
			t.commit();
			return smimport.getOperationId();

		} finally {
			hm.closeSession(session);
		}
	}

	public static void addCreatedResource(SMResource resource) throws Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			if (session.get(SMResource.class, resource.getResourceId()) == null)
				session.save(resource);

			t.commit();
		} finally {
			hm.closeSession(session);
		}
	}

	public static long addSystemImporter(String description, SMResource resource)
			throws Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {

			SMSystemImport smimport = new SMSystemImport();
			smimport.setOperationType(SMOperationType.SYSTEM.ordinal());
			smimport.setSubmissionDate(Calendar.getInstance());
			smimport.setDescription(description);
			session.save(resource);

			SMAbstractResource ar = new SMAbstractResource();
			ar.setResource(resource);
			ar.setAbstractResourceId(resource.getResourceId());
			session.save(ar);

			smimport.setAbstractResource(ar);
			smimport.setOperationStatus(SMOperationStatus.COMPLETED.ordinal());
			smimport.setCompletedDate(Calendar.getInstance());
			session.saveOrUpdate(smimport);

			t.commit();
			return smimport.getOperationId();
		} finally {
			hm.closeSession(session);
		}
	}

	public static List<SMResource> getResources(String user, String template) throws StatisticalManagerException, Exception {
		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();		
		try {

			Query query = session
					.createQuery("select resource "
							+ "from SMResource resource "
							+ "where (resource.portalLogin like :name or resource.portalLogin = null) "
							+ "and resource.resourceType <> 2 "
							+ "and resource.resourceType <> 3 ");

			query.setParameter("name", (user != null) ? user : "%");

			return query.list();

		} finally {
			hm.closeSession(session);
		}
	}

	public static SMOperation getOperation(long operationId) throws Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		try {
			return (SMOperation) session.get(SMOperation.class, operationId);
		} finally {
			hm.closeSession(session);
		}
	}

	public static List<SMComputation> getComputations(final String user,
			final String algorithm, final String category) throws Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		ArrayList<SMComputation> toReturn=new ArrayList<>();
		
		try {
			Query query = session
					.createQuery("select computation from SMComputation  computation "
							+ "where computation.portalLogin like :name and "
							+ "computation.algorithm like :algorithm and "
							+ "computation.category like :category");

			query.setParameter("name", (user != null) ? user : "%");
			query.setParameter("algorithm", (algorithm != null) ? algorithm
					: "%");
			query.setParameter("category", (category != null) ? category : "%");

			List<Object> objects = query.list();
			
			for (Object object : objects) {
				SMComputation computation = (SMComputation) object;

				Query queryParameters = session
						.createQuery("select parameter from SMEntry parameter "
								+ "where parameter.computationId = :computationId");
				queryParameters.setParameter("computationId",
						computation.getOperationId());

				@SuppressWarnings("unchecked")
				List<Object> parameters = queryParameters.list();
				if (!parameters.isEmpty()) {
					computation.setParameters(parameters
							.toArray(new SMEntry[parameters.size()]));
				}
				
				toReturn.add(computation);
			}

			return toReturn;

		} finally {
			hm.closeSession(session);
		}
	}

	public static long addComputation(final SMComputationRequest request,
			String category) throws Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {

			SMComputation smcomputation = new SMComputation();
			smcomputation.setOperationType(SMOperationType.COMPUTED.ordinal());
			smcomputation.setPortalLogin(request.getUser());
			smcomputation.setSubmissionDate(Calendar.getInstance());
			smcomputation.setTitle(request.getTitle());
			smcomputation.setDescription(request.getDescription());

			String algorithm = request.getConfig().getAlgorithm();
			smcomputation.setAlgorithm(algorithm);
			smcomputation.setCategory(category);

			smcomputation.setOperationStatus(SMOperationStatus.PENDING
					.ordinal());
			session.save(smcomputation);

			SMEntries parameters = request.getConfig().getParameters();

			if (parameters.getList() != null)
				for (SMInputEntry parameter : parameters.getList()) {
					SMEntry entry = new SMEntry();
					entry.setKey(parameter.getKey());
					entry.setValue(parameter.getValue());
					entry.setComputationId(smcomputation.getOperationId());
					session.save(entry);
				}

			t.commit();
			return smcomputation.getOperationId();

		} finally {
			hm.closeSession(session);
		}
	}

	public static void addCreatedResource(final long operationId,
			SMResource resource) throws Exception {

		logger.debug("------------------------------------------------------");
		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {

			SMOperation operation = (SMOperation) session.get(
					SMOperation.class, operationId);
			logger.debug("Resource type "
					+ SMResourceType.values()[resource.getResourceType()]);
			switch (SMResourceType.values()[resource.getResourceType()]) {

			// case TABULAR:
			// SMTable table = (SMTable)resource;
			// session.save(table);
			// break;
			// case FILE:
			// SMFile file = (SMFile)resource;
			// session.save(file);
			// break;
			case OBJECT:

				SMObject object = (SMObject) resource;

				session.save(object);
				logger.debug("Resource saved !!!!");
				logger.debug("-----------------------------------------------------------");
				break;
			}

			SMAbstractResource ar = new SMAbstractResource();
			ar.setResource(resource);
			ar.setAbstractResourceId(resource.getResourceId());
			session.save(ar);

			operation.setAbstractResource(ar);
			operation.setOperationStatus(SMOperationStatus.COMPLETED.ordinal());
			operation.setCompletedDate(Calendar.getInstance());
			session.saveOrUpdate(operation);
			t.commit();
		} finally {
			hm.closeSession(session);
		}
	}

	public static void addCreatedDWCAResource(final long operationId,
			SMResource resource) throws Exception {

		logger.debug("------------------------------------------------------");
		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {

			SMOperation operation = (SMOperation) session.get(
					SMOperation.class, operationId);
			logger.debug("Resource type "
					+ SMResourceType.values()[resource.getResourceType()]);

			SMAbstractResource ar = new SMAbstractResource();
			ar.setResource(resource);
			ar.setAbstractResourceId(resource.getResourceId());
			session.save(ar);

			operation.setAbstractResource(ar);
			operation.setOperationStatus(SMOperationStatus.COMPLETED.ordinal());
			operation.setCompletedDate(Calendar.getInstance());
			session.saveOrUpdate(operation);
			t.commit();
		} finally {
			hm.closeSession(session);
		}
	}

	public static void setOperationStatus(long operationId,String errorName, String errorDesc,
			SMOperationStatus status) throws StatisticalManagerException, Exception {
		logger.debug("setOperationStatus");

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			SMOperation smoperation = (SMOperation) session.get(
					SMOperation.class, operationId);
			smoperation.setOperationStatus(status.ordinal());
			logger.debug(errorName);
			logger.debug(errorDesc);
			logger.debug(status.name().toString());



			if (status == SMOperationStatus.FAILED) {
				logger.debug("set error status");
				smoperation.setCompletedDate(Calendar.getInstance());

				SMError error = new SMError(errorName);
				error.setDescription(errorDesc);

				error.setResourceId(UUID.randomUUID().toString());
				error.setResourceType(SMResourceType.ERROR.ordinal());

				session.save(error);

				SMAbstractResource ar = new SMAbstractResource();
				ar.setResource(error);
				ar.setAbstractResourceId(error.getResourceId());
				session.save(ar);

				smoperation.setAbstractResource(ar);

			}

			if (status == SMOperationStatus.PENDING)
				smoperation.setSubmissionDate(Calendar.getInstance());

			session.saveOrUpdate(smoperation);
			t.commit();
		} finally {
			hm.closeSession(session);
		}
	}

	public static void setComputationalInfrastructure(long operationId,
			INFRASTRUCTURE infra) throws StatisticalManagerException, Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			SMComputation smcomputation = (SMComputation) session.get(
					SMComputation.class, operationId);
			smcomputation.setInfrastructure(infra.toString());
			session.save(smcomputation);
			t.commit();
		} finally {
			hm.closeSession(session);
		}
	}

	private static void removeResourcesByOperationId(Session session, long operationId,boolean force) throws Exception {

		Query query = session.createQuery("select resource from SMResource  resource "
				+ "where resource.operationId = :operationId ");
		query.setParameter("operationId", operationId);

		List<SMResource> resources = query.list();
		if (resources != null) {
			for (SMResource resource : resources) {
				String user = resource.getPortalLogin();
				SMResourceType type=SMResourceType.values()[resource.getResourceType()];				
				logger.debug("Removing "+ObjectFormatter.log(resource, false));
				
				try{
					if(type.equals(SMResourceType.TABULAR))
						DataBaseManager.get().removeTable(resource.getResourceId());
					else{
						Home home=ServiceUtil.getWorkspaceHome(user);
						Workspace ws = home.getWorkspace();
						WorkspaceFolder appFolder = ServiceUtil.getWorkspaceSMFolder(home);
						String path=appFolder.getPath() + "/";
						if(type.equals(SMResourceType.FILE))
							path=path+((SMFile)resource).getRemoteName();
						else path=path+resource.getName();						
						logger.debug("Removing from WS, path is "+path);
						ws.getItemByPath(path).remove();						
					}						
				}catch(Exception e){
					logger.error("Unable to delete "+ObjectFormatter.log(resource, true),e);
					if(!force)throw e;
					else logger.debug("Force delete is true, continue deletion..");
				}
				session.delete(resource);
			}
		}

	}

	public static void removeComputation(long operationId,boolean force) throws StatisticalManagerException, Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {

			SMComputation smoperation = (SMComputation) session.get(SMComputation.class, operationId);
			removeResourcesByOperationId(session, operationId,force);

			if (smoperation != null)session.delete(smoperation);
			t.commit();
		} finally {
			hm.closeSession(session);
		}
	}

	public static void removeResource(String resourceId,boolean forceComputationRemoval) throws StatisticalManagerException, Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			SMResource resource = (SMResource) session.get(SMResource.class,
					resourceId);
			if (resource != null) {
				SMOperation operation = (SMOperation) session.get(
						SMOperation.class, resource.getOperationId());

				if (operation != null)
					session.delete(operation);

				removeResourcesByOperationId(session, resource.getOperationId(),forceComputationRemoval);
			}

			t.commit();
		} finally {
			hm.closeSession(session);
		}
	}

	public static void removeImporter(long importerId) throws StatisticalManagerException, Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		Transaction t = session.beginTransaction();
		try {
			SMOperation operation = (SMOperation) session.get(
					SMOperation.class, importerId);
			if (operation != null)
				session.delete(operation);
			t.commit();		
		} finally {
			hm.closeSession(session);
		}

	}

	public static SMFile getFile(String fileId) throws StatisticalManagerException, Exception {

		HibernateManager hm=HibernateManager.get();
		Session session = hm.getSessionFactory().openSession();
		try {
			Query query = session.createQuery("select file from SMFile  file "
					+ "where resourceId = :fileId");

			query.setParameter("fileId", fileId);

			@SuppressWarnings("unchecked")
			List<Object> objects = query.list();
			if (!objects.isEmpty())
				return (SMFile) objects.get(0);
			else
				return null;

		} finally {
			hm.closeSession(session);
		}
	}

}
