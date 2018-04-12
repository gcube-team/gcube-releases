package org.gcube.resource.management.quota.manager.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import org.gcube.resource.management.quota.library.quotalist.Quota;
import org.gcube.resource.management.quota.library.quotalist.QuotaType;
import org.gcube.resource.management.quota.library.quotalist.ServiceQuota;
import org.gcube.resource.management.quota.library.quotalist.StorageQuota;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;
import org.gcube.resource.management.quota.manager.persistence.entities.QuotaEntity;
import org.gcube.resource.management.quota.manager.persistence.entities.ServiceQuotaEntity;
import org.gcube.resource.management.quota.manager.persistence.entities.StorageQuotaEntity;
import org.gcuberesource.management.quota.manager.service.exception.NotFoundQuotaExecption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Persistence for quota DB manager
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
@Singleton
@Slf4j
public class QuotaDBPersistence{
	protected EntityManagerFactory emFactory;
	public QuotaDBPersistence(String db_Connection, String db_Name, String db_User,String db_Password){
			 
		Map<String, String> persistenceMap = new HashMap<String, String>();
		persistenceMap.put("javax.persistence.jdbc.url", "jdbc:postgresql://"+db_Connection+":5432/"+db_Name);
		persistenceMap.put("javax.persistence.jdbc.user", db_User);
		persistenceMap.put("javax.persistence.jdbc.password", db_Password);
		persistenceMap.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");

		emFactory = Persistence.createEntityManagerFactory("quota_persistence", persistenceMap);
		
	}

	private static Logger log = LoggerFactory.getLogger(QuotaDBPersistence.class);
	
	/**
	 * Add Quota to persist
	 * @param quote
	 */
	public Quota addQuota(Quota quota) {
		EntityManager em = emFactory.createEntityManager();
		try{
			QuotaEntity quotaEntity = null;
			if (quota.getQuotaType()==QuotaType.SERVICE){
				quotaEntity=new ServiceQuotaEntity(quota.getContext(),
						quota.getIdentifier(),quota.getCallerType(), quota.getTimeInterval(),quota.getQuotaValue());
			}
			if (quota.getQuotaType()==QuotaType.STORAGE){
				quotaEntity=new StorageQuotaEntity(quota.getContext(),
						quota.getIdentifier(),quota.getCallerType(),  quota.getTimeInterval(),quota.getQuotaValue());	
			}

			quota.setId(quotaEntity.getId());
			quota.setCreationTime(quotaEntity.getCreationTime());
			quota.setLastUpdateTime(quotaEntity.getLastUpdateTime());
			em.getTransaction().begin();
			em.persist(quotaEntity);			
			em.getTransaction().commit();	
			return quota;
		}catch (Exception e) {
			log.error("addQuota except:{}",e);
			em.getTransaction().rollback();
			//em.close();
			return null;
		}
		finally{
			em.close();	        
		}

	}

	/**
	 * Add Quote to persist
	 * @param quote
	 */
	public void addQuote(List<Quota> quote) {
		EntityManager em = emFactory.createEntityManager();
		try{
			em.getTransaction().begin();
			for (Quota quota: quote){

				log.debug("addQuote quota string:{}",quota.toString());
				log.debug("addQuote:{}",quota.getQuotaType());
				log.debug("Type service:{}, storage:{}",QuotaType.SERVICE,QuotaType.STORAGE);
				QuotaEntity quotaEntity=null;
				/*TODO DA completare per la parte dei service*/
				if (quota.getQuotaType()==QuotaType.SERVICE){
					log.debug("addQuote:service");
					ServiceQuota quotaService = (ServiceQuota)quota;
					quotaEntity=new ServiceQuotaEntity(quotaService.getContext(),
							quotaService.getIdentifier(), quotaService.getCallerType(),quotaService.getServiceId(), quotaService.getTimeInterval(),quotaService.getQuotaValue(),quotaService.getAccessType());
				}
				if (quota.getQuotaType()==QuotaType.STORAGE){
					log.debug("addQuote:storage");
					StorageQuota quotaStorage = (StorageQuota)quota;
					quotaEntity=new StorageQuotaEntity(quotaStorage.getContext(),
							quotaStorage.getIdentifier(), quotaStorage.getCallerType(), quotaStorage.getTimeInterval(),quotaStorage.getQuotaValue());
				}

				em.persist(quotaEntity);
				em.flush();
			}
			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("addQuote except:{}",e);
			em.getTransaction().rollback();
			//em.close();
		}
		finally{
			em.close();	        
		}
	}

	/**
	 * Get a specify quota 
	 * @param quotaId
	 * @return Quota
	 */

	public Quota getQuota(long quotaId) {
		EntityManager em = emFactory.createEntityManager();
		try{
			QuotaEntity quotaEntity= em.find(QuotaEntity.class, quotaId);
			Quota quota=null;
			//TODO da completare per la parte dei service
			if (quotaEntity instanceof ServiceQuotaEntity){
				log.debug("getQuote entity---if service");
				ServiceQuotaEntity quotaEntityService = (ServiceQuotaEntity)quotaEntity;
				quota = new ServiceQuota(quotaEntityService.getContext(),quotaEntityService.getIdentifier(),quotaEntityService.getCallerType(),quotaEntityService.getServicePackageId(),quotaEntityService.getTimeInterval(),quotaEntityService.getQuotaValue(),quotaEntityService.getAccessType());
			}
			if (quotaEntity instanceof StorageQuotaEntity){
				StorageQuotaEntity quotaEntityStorage = (StorageQuotaEntity)quotaEntity;
				quota =new StorageQuota(quotaEntityStorage.getContext(),quotaEntityStorage.getIdentifier(),quotaEntityStorage.getCallerType(),quotaEntityStorage.getTimeInterval(),quotaEntityStorage.getQuotaValue());
			}
			quota.setId(quotaEntity.getId());
			quota.setCreationTime(quotaEntity.getCreationTime());
			quota.setLastUpdateTime(quotaEntity.getLastUpdateTime());
			return quota;
		}catch (Exception e) {
			log.error("getQuota except:{}",e);
			em.getTransaction().rollback();
			//em.close();
			return null;
		}
		finally{
			em.close();	        
		}
	}

	/**
	 * Remove quote to persist
	 * @param quotaId
	 */
	public void removeQuota(long quotaId) {
		EntityManager em = emFactory.createEntityManager();
		try{
			em.getTransaction().begin();
			QuotaEntity entity = em.find(QuotaEntity.class, quotaId);
			if (entity!= null){
				em.remove(entity);
			}
			else{
				log.warn("quote with id {} not found", quotaId);
			}
			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("error removing quote with id {}", quotaId);
			//em.close();
		}
		finally{
			em.close();	        
		}
	}

	/**
	 * Get list quote from context
	 * @param context
	 * @return List<Quota>
	 * @throws NotFoundQuotaExecption 
	 */
	public List<Quota> getQuote(String context) throws NotFoundQuotaExecption {
		List<Quota> quoteToReturn = new ArrayList<Quota>();
		EntityManager em = emFactory.createEntityManager();
		try{
			TypedQuery<QuotaEntity> query = em.createNamedQuery("Quota.all", QuotaEntity.class);
			//log.warn("getQuote---query");
			query.setParameter("context", context);
			//log.warn("getQuote---context:"+context);
			if (query.getResultList().size()>0){
				for (QuotaEntity qEntity: query.getResultList()){
					log.debug("---getQuote---Entity list:"+qEntity.toString());
					Quota quota=null;
					
					//TODO for complete a service quota
					//log.debug("qEntity.getQuotaType():{},QuotaType.SERVICE.toString():{}",qEntity.getQuotaType(),QuotaType.SERVICE.toString());
					if (qEntity instanceof ServiceQuotaEntity){
						log.debug("getQuote entity---if service");
						ServiceQuotaEntity quotaEntityService = (ServiceQuotaEntity)qEntity;
						quota = new ServiceQuota(quotaEntityService.getContext(),quotaEntityService.getIdentifier(),quotaEntityService.getCallerType(),quotaEntityService.getServicePackageId(),quotaEntityService.getTimeInterval(),quotaEntityService.getQuotaValue(),quotaEntityService.getAccessType());
					}
					if (qEntity instanceof StorageQuotaEntity){
						log.debug("getQuote entity---if storage");
						StorageQuotaEntity quotaEntityStorage = (StorageQuotaEntity)qEntity;
						quota = new StorageQuota(quotaEntityStorage.getContext(),quotaEntityStorage.getIdentifier(),quotaEntityStorage.getCallerType(),quotaEntityStorage.getTimeInterval(),quotaEntityStorage.getQuotaValue());						
					}
					quota.setId(qEntity.getId());
					quota.setCreationTime(qEntity.getCreationTime());
					quota.setLastUpdateTime(qEntity.getLastUpdateTime());
					quoteToReturn.add(quota);
					log.debug("getQuote---quoteToReturn:"+quota.toString());
				}	
			}
			else{

				throw new NotFoundQuotaExecption("No quote found");

			}
		}
		catch (Exception e) {
			log.error("getQuote error {}",e);
			//em.close();
		}
		finally{
			em.close();
		}
		return quoteToReturn;
	}

	/***
	 * 
	 */
	public Quota getQuotaByIdentifier(String identifier) {
		Quota quotaToReturn = null;
		EntityManager em = emFactory.createEntityManager();
		try{
			TypedQuery<QuotaEntity> query = em.createNamedQuery("Quota.getByIdentifier", QuotaEntity.class);
			query.setParameter("identifier", identifier);
			for (QuotaEntity qEntity: query.getResultList()){
				if (qEntity.getQuotaType().equals(QuotaType.SERVICE.toString())){
					ServiceQuotaEntity quotaEntityService = (ServiceQuotaEntity)qEntity;
					quotaToReturn = new ServiceQuota(quotaEntityService.getContext(),quotaEntityService.getIdentifier(),quotaEntityService.getCallerType(),quotaEntityService.getTimeInterval(),quotaEntityService.getQuotaValue(),quotaEntityService.getAccessType());	
				}
				if (qEntity.getQuotaType().equals(QuotaType.STORAGE.toString())){
					quotaToReturn = new StorageQuota(qEntity.getContext(),qEntity.getIdentifier(),qEntity.getCallerType(),qEntity.getTimeInterval(),qEntity.getQuotaValue());
				}
				quotaToReturn.setId(qEntity.getId());
				quotaToReturn.setCreationTime(qEntity.getCreationTime());
				quotaToReturn.setLastUpdateTime(qEntity.getLastUpdateTime());			
			}	
		}catch (Exception e) {
			log.error("getQuotaByIdentifier error retrive a quota for this identifier:{} error:{}",identifier, e);
			//em.close();
		}finally{
			em.close();
		}
		return quotaToReturn;
	}


	/**
	 * 
	 * @param identifier
	 * @param context
	 * @param quotaType
	 * @param timeInterval
	 * @param quotaValue
	 * @return
	 */
	public Quota getQuotaSpecified(String identifier, String context,QuotaType quotaType,TimeInterval timeInterval,Double quotaValue){

		//log.debug("getQuotaSpecified init:{}");
		Quota quotaToReturn = null;
		EntityManager em = emFactory.createEntityManager();
		//log.debug("getQuotaSpecified createEntityManager:{}");
		try{
			TypedQuery<QuotaEntity> query = em.createNamedQuery("Quota.getSpecified", QuotaEntity.class);
			query.setParameter("identifier", identifier);
			query.setParameter("context", context);
			query.setParameter("quotaType", quotaType.toString());
			query.setParameter("timeInterval", timeInterval);
			//query.setParameter("quotaValue", quotaValue);
			for (QuotaEntity qEntity: query.getResultList()){
				//log.debug("getQuotaSpecified found a QuotaEntity:{}",qEntity.toString());
				if (qEntity.getQuotaType().equals(QuotaType.SERVICE.toString())){
					//log.debug("getQuotaSpecified found a Service");
					ServiceQuotaEntity quotaEntityService = (ServiceQuotaEntity)qEntity;
					quotaToReturn = new ServiceQuota(quotaEntityService.getContext(),quotaEntityService.getIdentifier(),quotaEntityService.getCallerType(),quotaEntityService.getTimeInterval(),quotaEntityService.getQuotaValue(),quotaEntityService.getAccessType());	
				}
				if (qEntity.getQuotaType().equals(QuotaType.STORAGE.toString())){
					//log.debug("getQuotaSpecified found a Storage");
					quotaToReturn = new StorageQuota(qEntity.getContext(),qEntity.getIdentifier(),qEntity.getCallerType(),qEntity.getTimeInterval(),qEntity.getQuotaValue());
				}
				//log.debug("getQuotaSpecified Id quote found:{}",qEntity.getId());
				quotaToReturn.setId(qEntity.getId());
			}	
		}catch (Exception e) {
			log.error("getQuotaSpecified error retrive a quota for this identifier:{} error:{}",identifier, e);
			return null;
		}finally{
			em.close();
		}
		return quotaToReturn;

	}
	
	/**
	 * Update a specify quota 
	 * @param quotaId
	 * @return Quota
	 */
	/*
	public Quota updateQuota(long quotaId, String context,
			String identifier, ManagerType managerType, TimeInterval timeInterval, Double quotaValue) {
		EntityManager em = emFactory.createEntityManager();
		em.getTransaction().begin();
		try{
			Quota quota = em.find(Quota.class, quotaId);
			quota.setContext(context);
			quota.setIdentifier(identifier);
			quota.setManagerType(managerType);
			quota.setTimeInterval(timeInterval);
			quota.setQuotaValue(quotaValue);
			em.getTransaction().commit();
			return em.find(Quota.class, quotaId);
		}finally{
			em.close();
		}
	}
	 */
}
