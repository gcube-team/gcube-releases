package org.gcube.data.analysis.tabulardata.service;

import static org.gcube.data.analysis.tabulardata.utils.Util.getOwnerhipAuthorizedObject;
import static org.gcube.data.analysis.tabulardata.utils.Util.getUserAuthorizedObject;
import static org.gcube.data.analysis.tabulardata.utils.Util.toNotificationList;
import static org.gcube.data.analysis.tabulardata.utils.Util.toTabularResource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.cleaner.GarbageCollectorFactory;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.gcube.data.analysis.tabulardata.commons.utils.SharingEntity;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.Notification;
import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.exceptions.NoSuchObjectException;
import org.gcube.data.analysis.tabulardata.metadata.StorableHistoryStep;
import org.gcube.data.analysis.tabulardata.metadata.notification.StorableNotification;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.RelationLink;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.utils.EntityManagerHelper;
import org.gcube.data.analysis.tabulardata.utils.Notifier;
import org.gcube.data.analysis.tabulardata.weld.WeldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

@WebService(portName = "TabularResourceManagerPort",
serviceName = TabularResourceManager.SERVICE_NAME,
targetNamespace = Constants.TABULAR_RESOURCE_TNS,
endpointInterface = "org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager")
@Singleton
@WeldService
public class TabularResourceManagerImpl implements TabularResourceManager{

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss z");

	private Logger logger = LoggerFactory.getLogger(TabularResourceManager.class);

	@Inject 
	private EntityManagerHelper emHelper;

	@Inject
	private CubeManager cubeManager;

	@Inject
	private GarbageCollectorFactory garbageCollectorFactory;

	@Inject
	private Notifier notifier;

	@PreDestroy
	public void removePendingTable(){
		logger.trace("removing pending table before shutdown");
		garbageCollectorFactory.getGarbageCollector().stop();
	}

	@Override
	public TabularResource createTabularResource(TabularResourceType tabularResourceType) throws InternalSecurityException {
		logger.trace("create tabularResource method called");
		final StorableTabularResource resource = new StorableTabularResource();
		resource.setScopes(Lists.newArrayList( ScopeProvider.instance.get()));
		resource.setOwner(AuthorizationProvider.instance.get().getClient().getId());
		resource.setName("tabularResource "+dateFormat.format(resource.getCreationDate().getTime()));
		resource.setTabularResourceType(tabularResourceType);

		EntityManager em = emHelper.getEntityManager();
		em.getTransaction().begin();
		em.persist(resource);
		em.getTransaction().commit();
		em.close();

		return toTabularResource(resource);
	}

	@Override
	public TabularResource updateTabularResource(TabularResource tabularResource) throws NoSuchTabularResourceException, InternalSecurityException {
		logger.trace("update tabularResource method called with properties "+tabularResource.getProperties());
		EntityManager em = emHelper.getEntityManager();
		final StorableTabularResource sTr;
		try{
			sTr = getOwnerhipAuthorizedObject(tabularResource.getId(),  StorableTabularResource.class, em);
		} catch(NoSuchObjectException e){
			em.close();
			throw new NoSuchTabularResourceException(tabularResource.getId());
		}

		sTr.setName(tabularResource.getName());
		sTr.setProperties(tabularResource.getProperties());
		sTr.finalize(tabularResource.isFinalized());
		Boolean updateLink = false;
		try {
			if (tabularResource.getNewVersionId()!=null && !(sTr.getNewVersion()!=null && tabularResource.getNewVersionId()==sTr.getNewVersion().getId())){
				sTr.setNewVersion(getUserAuthorizedObject(tabularResource.getNewVersionId(), StorableTabularResource.class, em));
				updateLink = true;
			}
		} catch (NoSuchObjectException e1) {
			logger.warn("error setting new version with id "+tabularResource.getNewVersionId());
		}

		em.getTransaction().begin();
		em.merge(sTr);
		if (updateLink && sTr.getLinkedBy().size()>0){
			List<StorableNotification> notifications = notifier.onLinkUpdated(sTr.getLinkedBy());
			for (StorableNotification notification : notifications){
				em.persist(notification);
				notification.getTabularResource().getNotifications().add(notification);
				em.merge(notification.getTabularResource());
			}
		}

		em.getTransaction().commit();
		em.close();

		return toTabularResource(sTr);
	}


	@Override
	public void remove(long id) throws NoSuchTabularResourceException, InternalSecurityException {
		logger.debug("removing tabular resource "+id);
		EntityManager em = emHelper.getEntityManager();
		final StorableTabularResource sTr;
		try{
			sTr = getOwnerhipAuthorizedObject(id,  StorableTabularResource.class, em);
		} catch(NoSuchObjectException e){
			em.close();
			throw new NoSuchTabularResourceException(id);
		}

		em.getTransaction().begin();
		removeInternal(sTr, em);
		em.getTransaction().commit();
		em.close();

	}

	private List<TableId> removeInternal(StorableTabularResource sTr, EntityManager em){
		List<TableId> tableIdsToRemove = new ArrayList<TableId>();
		TypedQuery<RelationLink> linksToQuery = em.createNamedQuery("RelationLink.linksTo", RelationLink.class);
		linksToQuery.setParameter("trid", sTr.getId());

		Long tableId = sTr.getTableId();

		for (StorableHistoryStep hs:  sTr.getHistorySteps())
			if(hs.getTableId() != null)
				tableIdsToRemove.add(new TableId(hs.getTableId()));


		List<RelationLink> linkedBy ;

		for (RelationLink link: linksToQuery.getResultList()){
			em.remove(link);
			if (link.getLinksToTabulaResource().isDeleted() && link.getLinksToTabulaResource().getLinkedBy().size()==0){
				tableIdsToRemove.addAll(removeInternal(link.getLinksToTabulaResource(), em));
				logger.trace("removing tabualarResource "+link.getLinksToTabulaResource().getId()+" without links");
			}
			logger.trace("removing linksTo: "+link);
		}

		TypedQuery<RelationLink> linkedByQuery = em.createNamedQuery("RelationLink.linkedBy", RelationLink.class);
		linkedByQuery.setParameter("trid", sTr.getId());
		linkedBy = linkedByQuery.getResultList();


		if (linkedBy.size()>0)	{	
			logger.debug("setting external tabularResource "+sTr.getName()+" as deleted (it will not be removed)");
			sTr.setDeleted(true);
			em.merge(sTr);
		}else {
			logger.debug("removing external tabularResource "+sTr.getName()+" cause is without links");
			em.remove(sTr);
			if(tableId != null)
				tableIdsToRemove.add(new TableId(tableId));
		}

		return tableIdsToRemove;

	}


	@Override
	public List<TabularResource> getAllTabularResources() throws InternalSecurityException {
		String caller = AuthorizationProvider.instance.get().getClient().getId();
		final List<StorableTabularResource> storedTabularResources = new ArrayList<>();
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("user", caller);
		parameters.put("group",  ScopeProvider.instance.get());
		parameters.put("scope", ScopeProvider.instance.get());
		storedTabularResources.addAll(emHelper.getResults("TR.getAll", StorableTabularResource.class, parameters));


		List<TabularResource> tabularResources = new ArrayList<TabularResource>();
		for (StorableTabularResource str: storedTabularResources)
			tabularResources.add(toTabularResource(str));

		return tabularResources;


	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager#getTabularResourcesByType(java.lang.String)
	 */
	@Override
	public List<TabularResource> getTabularResourcesByType(final String type) throws InternalSecurityException {
		String caller = AuthorizationProvider.instance.get().getClient().getId();
		final List<StorableTabularResource> storedTabularResources = new ArrayList<>();

		Map<String, Object> parameters = new HashMap<String, Object>(4);
		parameters.put("user", caller);
		parameters.put("group", ScopeProvider.instance.get());
		parameters.put("scope", ScopeProvider.instance.get());
		parameters.put("type", type);
		storedTabularResources.addAll(emHelper.getResults("TR.getAllByType", StorableTabularResource.class, parameters));

		List<TabularResource> tabularResources = new ArrayList<TabularResource>();
		for (StorableTabularResource str: storedTabularResources)
			tabularResources.add(toTabularResource(str));

		return tabularResources;

	}

	@Override
	public TabularResource getTabularResource(final long id) throws NoSuchTabularResourceException, InternalSecurityException{
		
		final String caller = AuthorizationProvider.instance.get().getClient().getId();

		logger.info("calling getTabularResourceById with parameters: "+caller+" , "+ScopeProvider.instance.get()+" , "+id);

		final List<StorableTabularResource> storedTabularResources = new ArrayList<>();

		Map<String, Object> parameters = new HashMap<String, Object>(4);
		parameters.put("user", caller);
		parameters.put("group", ScopeProvider.instance.get());
		parameters.put("scope", ScopeProvider.instance.get());
		parameters.put("id", id);
		storedTabularResources.addAll(emHelper.getResults("TR.getById", StorableTabularResource.class, parameters));

		if (!(storedTabularResources.size()==1 && !storedTabularResources.get(0).isDeleted())) 
			throw new NoSuchTabularResourceException(id);

		StorableTabularResource str =  storedTabularResources.get(0);


		return toTabularResource(str);
	}

	public StorableTabularResource getTabularResourceByIdWithoutAuth(final long id) throws NoSuchTabularResourceException{
		final List<StorableTabularResource> storedTabularResources = new ArrayList<>();

		storedTabularResources.addAll(emHelper.getResults("TR.getByIdWithoutAuth", StorableTabularResource.class, Collections.singletonMap("id",(Object) id)));

		if (!(storedTabularResources.size()==1 && !storedTabularResources.get(0).isDeleted())) 
			throw new NoSuchTabularResourceException(id);

		StorableTabularResource str =  storedTabularResources.get(0);
		return str;
	}

	@Override
	public List<Notification> getNotificationPerTabularResource(long id) throws InternalSecurityException{
		final String caller = AuthorizationProvider.instance.get().getClient().getId();
		Map<String, Object> parameters = new HashMap<String, Object>(4);
		parameters.put("user", caller);
		parameters.put("group", ScopeProvider.instance.get());
		parameters.put("scope", ScopeProvider.instance.get());
		parameters.put("trid", id);

		return toNotificationList(emHelper.getResults("Notification.getByTr", StorableNotification.class, parameters));
	}


	@Override
	public List<Notification> getNotificationPerUser() throws InternalSecurityException {
		String caller = AuthorizationProvider.instance.get().getClient().getId();
		Map<String, Object> parameters = new HashMap<String, Object>(3);
		parameters.put("user", caller);
		parameters.put("group",  ScopeProvider.instance.get());
		parameters.put("scope",  ScopeProvider.instance.get());
		return toNotificationList(emHelper.getResults("Notification.getByUser", StorableNotification.class, parameters));
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.Sharable#share(java.lang.Object, org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken[])
	 */
	@Override
	public TabularResource share(Long entityId,
			SharingEntity... entities) throws
			NoSuchTabularResourceException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			StorableTabularResource str = SharingHelper.share(StorableTabularResource.class, entityId, em, entities);
			return toTabularResource(str);
		}catch(NoSuchObjectException e){
			throw new NoSuchTabularResourceException(entityId);
		}finally{
			em.close();
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.commons.webservice.Sharable#unshare(java.lang.Object, org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken[])
	 */
	@Override
	public TabularResource unshare(Long entityId,
			SharingEntity... entities) throws NoSuchTabularResourceException, InternalSecurityException {
		EntityManager em = emHelper.getEntityManager();
		try{
			return toTabularResource(SharingHelper.unshare(StorableTabularResource.class, entityId, em, entities));
		}catch(NoSuchObjectException e){
			throw new NoSuchTabularResourceException(entityId);
		}finally{
			em.close();
		}
	}

	@Override
	public void cleanDatabase(){
		final List<StorableTabularResource> storedTabularResources = new ArrayList<>();

		storedTabularResources.addAll(emHelper.getResults("TR.getAllWithoutAuth", StorableTabularResource.class, new HashMap<String, Object>(0)));

		logger.trace("retrieved tablular resources are {}",storedTabularResources.size());

		List<Long> tableToSave = new ArrayList<>();
		for (StorableTabularResource st: storedTabularResources){
			if (st.getTableId()!=null){
				try{
					tableToSave.add(st.getTableId());
					Table table = cubeManager.getTable(new TableId(st.getTableId()));
					if (table.contains(DatasetViewTableMetadata.class)){
						tableToSave.add(((DatasetViewTableMetadata)table.getMetadata(DatasetViewTableMetadata.class)).getTargetDatasetViewTableId().getValue());
					}
				}catch(Exception e){

				}
			}
			logger.trace("retrieving histories for tabular resource {}",st.getId());
			for (StorableHistoryStep hs: st.getHistorySteps()){
				if (hs.getTableId()!=null)
					tableToSave.add(hs.getTableId());
			}
		}

		Collection<Table> tables = cubeManager.getTables();

		logger.trace("retrieved tables are {}",tables.size());

		int removedTable = 0;

		for (Table table : tables){
			try{
				if (! tableToSave.contains(table.getId().getValue())){
					cubeManager.removeTable(table.getId());
					logger.trace("removed table {}",table.getId());
					removedTable++;	
				}
			}catch(Exception e){
				logger.warn("table with id {} not removed",table.getId());
			}
		}

		logger.info("removed {} table",removedTable);
	}

	

}
