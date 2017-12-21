package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.utils.Util.getOwnerhipAuthorizedObject;
import static org.gcube.data.analysis.tabulardata.utils.Util.getUserAuthorizedObject;
import static org.gcube.data.analysis.tabulardata.utils.Util.toTemplateDescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.templates.model.Template;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTemplateException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.TemplateNotCompatibleException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.exceptions.NoSuchObjectException;
import org.gcube.data.analysis.tabulardata.metadata.StorableTemplate;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.task.TaskContext;
import org.gcube.data.analysis.tabulardata.task.engine.TaskEngine;
import org.gcube.data.analysis.tabulardata.templates.TemplateEngine;
import org.gcube.data.analysis.tabulardata.templates.TemplateEngineFactory;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.OperationUtil;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(portName = "TemplateManagerPort",
serviceName = TemplateManager.SERVICE_NAME,
targetNamespace = Constants.TEMPLATE_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.TemplateManager")
@Singleton
@WeldService
public class TemplateManagerImpl implements TemplateManager {

	private Logger logger = LoggerFactory.getLogger(TemplateManagerImpl.class);

	@Inject
	TaskEngine taskEngine;

	@Inject 
	OperationUtil opUtil;

	@Inject 
	CubeManager cm;

	@Inject
	TemplateEngineFactory templateEngineFactory;

	@Inject
	EntityManagerHelper emHelper;

	@Override
	public long saveTemplate(String name, String description, String agency,
			Template template) throws InternalSecurityException {
		logger.debug("saving template");
		String owner = AuthorizationProvider.instance.get().getClient().getId();
		final StorableTemplate storableTemplate = new StorableTemplate(name, description, agency, owner, ScopeProvider.instance.get(), template);

		EntityManager em = emHelper.getEntityManager();
		try{		
			em.getTransaction().begin();
			em.persist(storableTemplate);
			em.getTransaction().commit();
		}catch (RollbackException re) {
			logger.error("error on transaction code",re );
			em.clear();
		}catch(DatabaseException de){
			logger.error("database error code is "+de.getDatabaseErrorCode(),de);
			em.getTransaction().rollback();
		}finally{
			em.close();
		}

		return storableTemplate.getId();
	}

	@Override
	public TemplateDescription removeTemplate(long id) throws NoSuchTemplateException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{

			StorableTemplate st =getOwnerhipAuthorizedObject(id, StorableTemplate.class, em);

			try{		
				em.getTransaction().begin();
				em.remove(st);
				em.getTransaction().commit();
			}catch (RollbackException re) {
				logger.error("error on transaction code",re );
				em.clear();
				throw re;
			}catch(DatabaseException de){
				logger.error("database error code is "+de.getDatabaseErrorCode(),de);
				em.getTransaction().rollback();
				throw de;
			}

			return toTemplateDescription(st);
		}catch(NoSuchObjectException nse){
			logger.error("template with id "+id+" cannot be removed");
			throw new NoSuchTemplateException(id);
		}catch (RuntimeException e) {
			logger.error("error removing template",e);
			throw e;
		}finally{
			em.close();
		}
	}


	@Override
	public TemplateDescription updateTemplate(long id, Template template)
			throws NoSuchTemplateException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{

			final StorableTemplate st =getOwnerhipAuthorizedObject(id, StorableTemplate.class, em);
			st.setTemplate(template);

			try{		
				em.getTransaction().begin();
				em.merge(st);
				em.getTransaction().commit();
			}catch (RollbackException re) {
				logger.error("error on transaction code",re );
				em.clear();
				throw re;
			}catch(DatabaseException de){
				logger.error("database error code is "+de.getDatabaseErrorCode(),de);
				em.getTransaction().rollback();
				throw de;
			}

			return toTemplateDescription(st);
		}catch(NoSuchObjectException nse){
			logger.error("template with id "+id+" cannot be removed");
			throw new NoSuchTemplateException(id);
		}catch (RuntimeException e) {
			logger.error("error removing template",e);
			throw e;
		}finally{
			em.close();
		}
	}

	@Override
	public List<TemplateDescription> getTemplates() throws InternalSecurityException{
		String caller  = AuthorizationProvider.instance.get().getClient().getId();

		Map<String, Object> parameters = new HashMap<>(3);
		parameters.put("user", caller);
		parameters.put("scope", ScopeProvider.instance.get());
		parameters.put("group", ScopeProvider.instance.get());
		List<StorableTemplate> storedTemplateResources = emHelper.getResults("Template.getAll", StorableTemplate.class, parameters);
		List<TemplateDescription> templates = new ArrayList<TemplateDescription>();
		for (StorableTemplate str: storedTemplateResources)
			templates.add(toTemplateDescription(str));
		return templates;

	}

	@Override
	public TemplateDescription getTemplate(long id)
			throws NoSuchTemplateException, InternalSecurityException {
		String caller = AuthorizationProvider.instance.get().getClient().getId();
		Map<String, Object> parameters = new HashMap<>(4);
		parameters.put("user", caller);
		parameters.put("scope", ScopeProvider.instance.get());
		parameters.put("group", ScopeProvider.instance.get());
		parameters.put("id", id);
		List<StorableTemplate> storedTemplates = emHelper.getResults("Template.getById", StorableTemplate.class, parameters);
		if (storedTemplates.size()!=1) throw new NoSuchTemplateException(id);
		StorableTemplate sTemplate =  storedTemplates.get(0);		
		return toTemplateDescription(sTemplate);

	}

	@Override
	public TemplateDescription share(Long entityId,
			SharingEntity... entities) throws NoSuchTemplateException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			return toTemplateDescription(SharingHelper.share(StorableTemplate.class, entityId, em, entities));
		}catch(NoSuchObjectException e){
			throw new NoSuchTemplateException(entityId);
		}finally{
			em.close();
		}
	}

	@Override
	public TemplateDescription unshare(Long entityId,
			SharingEntity... entities) throws NoSuchTemplateException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			return toTemplateDescription(SharingHelper.unshare(StorableTemplate.class, entityId, em, entities));
		}catch(NoSuchObjectException e){
			throw new NoSuchTemplateException(entityId);
		}finally{
			em.close();
		}
	}

	@Override
	public TaskInfo apply(long templateId, long tabularResourceId)
			throws NoSuchTemplateException, NoSuchTabularResourceException, TemplateNotCompatibleException, InternalSecurityException {
		StorableTabularResource storableTabularResource;
		EntityManager em = emHelper.getEntityManager();
		try{
			try{
				storableTabularResource = getUserAuthorizedObject(tabularResourceId, StorableTabularResource.class, em);
			}catch(NoSuchObjectException e){
				throw new NoSuchTabularResourceException(tabularResourceId);
			}

			StorableTemplate storableTemplate ;

			try{
				storableTemplate = getUserAuthorizedObject(templateId, StorableTemplate.class, em);
			}catch(NoSuchObjectException e){
				throw new NoSuchTemplateException(templateId);
			}

			String submitter = AuthorizationProvider.instance.get().getClient().getId();

			if (storableTabularResource.getHistorySteps().size()==0)
				throw new RuntimeException("templates can be applyed only to TabularResource with at least a table");
			long lastTableId = storableTabularResource.getTableId();	
			Table table = cm.getTable(new TableId(lastTableId));

			TemplateEngine templateEngine = templateEngineFactory.getEngine(storableTemplate.getTemplate(), table);

			TaskContext context;
			try {
				context = new TaskContext(templateEngine.getTemplateSteps(),storableTemplate.getTemplate().getOnRowErrorAction());

				opUtil.addPostOperations(context);
				opUtil.addPostValidations(context, storableTabularResource);
			} catch (TemplateNotCompatibleException tnc) {
				throw tnc;
			} catch (Throwable e) {
				throw new RuntimeException("error applying template",e);
			}
			return taskEngine.createTemplateTask(submitter, context, storableTabularResource, templateId, templateEngine.getInvocationForFinalAction());
		}finally{
			em.close();
		}
	}
}
