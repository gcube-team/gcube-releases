package org.gcube.resource.management.quota.manager.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import org.gcube.resource.management.quota.library.quotalist.ServicePackage;
import org.gcube.resource.management.quota.library.quotalist.ServicePackageDetail;
import org.gcube.resource.management.quota.manager.persistence.entities.ServicePackageDetailEntity;
import org.gcube.resource.management.quota.manager.persistence.entities.ServicePackageManagerEntity;
import org.gcuberesource.management.quota.manager.service.exception.NotFoundQuotaPackageExecption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QuotaPackageDBPersistence
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
@Singleton
@Slf4j
public class QuotaPackageDBPersistence {

	private static QuotaPackageDBPersistence instance;
	private QuotaPackageDBPersistence(){}
	public static synchronized QuotaPackageDBPersistence getInstance()
	{
		if (instance == null)
			instance = new QuotaPackageDBPersistence();
		return instance;
	}

	private static Logger log = LoggerFactory.getLogger(QuotaPackageDBPersistence.class);
	protected EntityManagerFactory emFactory = Persistence.createEntityManagerFactory("quota_persistence");

	/**
	 * Add Quota to persist
	 * @param quote
	 */
	public void addPackage(ServicePackage servicePackages) {
		EntityManager em = emFactory.createEntityManager();
		try{
			ServicePackageManagerEntity servicePackageManager= new ServicePackageManagerEntity(servicePackages.getName());
			em.getTransaction().begin();	
			em.persist(servicePackageManager);
			em.flush();
			for (ServicePackageDetail servicePackageDetail:servicePackages.getServicesPackageDetail()){
				ServicePackageDetailEntity servicePackageDetailEntity=new ServicePackageDetailEntity(servicePackageManager,servicePackageDetail.getContent());		
				servicePackageDetailEntity.setServicePackage(servicePackageManager);
				em.persist(servicePackageDetailEntity);
			}
			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("addPackage except:{}",e);
			em.getTransaction().rollback();
		}
		finally{
			em.close();	        
		}
	}

	/**
	 * Get list package
	 * @return List<Quota>
	 * @throws NotFoundQuotaPackageExecption 
	 */
	public List<ServicePackage> getPackages() throws NotFoundQuotaPackageExecption {
		List<ServicePackage> packagesToReturn = new ArrayList<ServicePackage>();
		EntityManager em = emFactory.createEntityManager();
		try{
			TypedQuery<ServicePackageManagerEntity> query = em.createNamedQuery("ServicePackage.all", ServicePackageManagerEntity.class);
			if (query.getResultList().size()>0){
				for (ServicePackageManagerEntity serEntity: query.getResultList()){
					List<ServicePackageDetail> servicePackageDetaiListl = new ArrayList<ServicePackageDetail>();
					for(ServicePackageDetailEntity serDetEntity: serEntity.getListdetail()){
						ServicePackageDetail servicePackageDetail= new ServicePackageDetail(serDetEntity.getServicePackage().getId(),
								serDetEntity.getContent());
						servicePackageDetail.setId(serDetEntity.getId());
						servicePackageDetaiListl.add(servicePackageDetail);
					}
					ServicePackage servicePackage =new ServicePackage(serEntity.getName(), servicePackageDetaiListl);
					servicePackage.setId(serEntity.getId());
					packagesToReturn.add(servicePackage);
				}	
			}
			else{
				throw new NotFoundQuotaPackageExecption("No packages found");
			}
		}finally{
			em.close();
		}
		return packagesToReturn;
	}
	
	/**
	 * Remove package to persist
	 * @param servicePackageId
	 */
	public void removePackage(long servicePackageId) {
		EntityManager em = emFactory.createEntityManager();
		try{
			em.getTransaction().begin();			
			ServicePackageManagerEntity entity = em.find(ServicePackageManagerEntity.class, servicePackageId);
			if (entity!= null){
				em.remove(entity);
			}
			else{
				log.warn("package with id {} not found", servicePackageId);
			}
			
			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("error removing package with id {}", servicePackageId);
			em.close();
		}
	}
	
	
	/**
	 * Remove package detail to persist
	 * @param servicePackageId
	 */
	public void removePackageDetail(long servicePackageDetailId) {
		EntityManager em = emFactory.createEntityManager();
		try{
			em.getTransaction().begin();			
			ServicePackageDetailEntity entity=em.find(ServicePackageDetailEntity.class,servicePackageDetailId);
			if (entity!= null){
				em.remove(entity);
				em.flush();
			}
			else{
				log.warn("package detail with id {} not found", servicePackageDetailId);
			}
			
			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("error removing package detail with id {}", servicePackageDetailId);
			em.close();
		}
	}
	
	
	
	public ServicePackage getPackage(long servicepackage_id) throws NotFoundQuotaPackageExecption {
		EntityManager em = emFactory.createEntityManager();
		ServicePackage servicePackage=null;
		try{
			TypedQuery<ServicePackageManagerEntity> query = em.createNamedQuery("ServicePackage.getById", ServicePackageManagerEntity.class);
			query.setParameter("id", servicepackage_id);
			if (query.getResultList().size()>0){
				for (ServicePackageManagerEntity serEntity: query.getResultList()){
					List<ServicePackageDetail> servicePackageDetaiListl = new ArrayList<ServicePackageDetail>();
					for(ServicePackageDetailEntity serDetEntity: serEntity.getListdetail()){
						ServicePackageDetail servicePackageDetail= new ServicePackageDetail(serDetEntity.getServicePackage().getId(),
								serDetEntity.getContent());
						servicePackageDetail.setId(serDetEntity.getId());
						servicePackageDetaiListl.add(servicePackageDetail);
					}
					servicePackage =new ServicePackage(serEntity.getName(), servicePackageDetaiListl);
					servicePackage.setId(serEntity.getId());
					
				}	
			}
			else{
				throw new NotFoundQuotaPackageExecption("No packages found");
			}
		}finally{
			em.close();
		}
		return servicePackage;
		
	}
	
	
}
