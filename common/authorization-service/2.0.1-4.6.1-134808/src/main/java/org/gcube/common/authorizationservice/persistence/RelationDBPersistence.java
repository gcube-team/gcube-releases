package org.gcube.common.authorizationservice.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.PolicyType;
import org.gcube.common.authorization.library.policies.Service2ServicePolicy;
import org.gcube.common.authorization.library.policies.User2ServicePolicy;
import org.gcube.common.authorization.library.provider.ClientInfo;
import org.gcube.common.authorization.library.provider.ContainerInfo;
import org.gcube.common.authorization.library.provider.ExternalServiceInfo;
import org.gcube.common.authorization.library.provider.ServiceIdentifier;
import org.gcube.common.authorization.library.provider.ServiceInfo;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.authorizationservice.persistence.entities.AuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.AuthorizationId;
import org.gcube.common.authorizationservice.persistence.entities.EntityConstants;
import org.gcube.common.authorizationservice.persistence.entities.ExternalServiceAuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.NodeAuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.PolicyEntity;
import org.gcube.common.authorizationservice.persistence.entities.ServiceAuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.ServicePolicyEntity;
import org.gcube.common.authorizationservice.persistence.entities.UserAuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.UserPolicyEntity;
import org.gcube.common.authorizationservice.util.TokenPersistence;

@Singleton
@Slf4j
public class RelationDBPersistence implements TokenPersistence{

	@Inject
	EntityManagerFactory emFactory;

	ReentrantLock lock = new ReentrantLock(true);

	HashMap<String, AuthorizationId> synchMap = new HashMap<String, AuthorizationId>();

	//ONLY FOR TEST PURPOSE
	public void setEntitymanagerFactory(EntityManagerFactory emf){
		this.emFactory = emf;
	}

	@Override
	public AuthorizationEntry getAuthorizationEntry(String token) {
		EntityManager em = emFactory.createEntityManager();
		try{
			//retrieve entity for token
			TypedQuery<AuthorizationEntity> query = em.createNamedQuery("Authz.get", AuthorizationEntity.class);
			query.setParameter("token", token);
			AuthorizationEntity authEntity;
			try{
				authEntity = query.getSingleResult(); 
			}catch (NoResultException e){
				log.warn("no result found for token {}",token);
				return null;
			}

			if (authEntity.getEntryType().equals(EntityConstants.SERVICE_AUTHORIZATION)){
				ServiceAuthorizationEntity sAuth = (ServiceAuthorizationEntity) authEntity;
				return new AuthorizationEntry(sAuth.getInfo(), sAuth.getContext(), retrievePolicies(sAuth, em), sAuth.getQualifier());
			} else if (authEntity.getEntryType().equals(EntityConstants.USER_AUTHORIZATION)){
				UserAuthorizationEntity uAuth = (UserAuthorizationEntity) authEntity;
				return new AuthorizationEntry(uAuth.getInfo(), uAuth.getContext(), retrievePolicies(uAuth, em), uAuth.getQualifier());
			} else if (authEntity.getEntryType().equals(EntityConstants.EXTERNAL_SERVICE_AUTHORIZATION)){
				ExternalServiceAuthorizationEntity uAuth = (ExternalServiceAuthorizationEntity) authEntity;
				return new AuthorizationEntry(uAuth.getInfo(), uAuth.getContext(), new ArrayList<Policy>(), uAuth.getQualifier());
			} else if (authEntity.getEntryType().equals(EntityConstants.CONTAINER_AUTHORIZATION)){
				NodeAuthorizationEntity uAuth = (NodeAuthorizationEntity) authEntity;
				return new AuthorizationEntry(uAuth.getInfo(), uAuth.getContext(), new ArrayList<Policy>(), uAuth.getQualifier());
			} else throw new IllegalArgumentException("entryType cannot be mapped");
		}catch(Throwable t){
			log.error("error retrieving authorization entry",t);
			return null;
		}finally{
			em.close();
		}
	}

	@Override
	public String getExistingToken(String clientId, String context, String qualifier) {
		EntityManager em = emFactory.createEntityManager();
		try{
			AuthorizationEntity authEntity = em.find(AuthorizationEntity.class, new AuthorizationId(context, clientId, qualifier));
			if (authEntity!=null)
				return authEntity.getToken();
			return null;
		}finally{
			em.close();
		}

	}

	@Override
	public void saveAuthorizationEntry(String token, String context,
			ClientInfo info, String tokenQualifier, String generatedBy) {
		AuthorizationEntity authEntity = null;
		if (info instanceof UserInfo)
			authEntity = new UserAuthorizationEntity(token, context, tokenQualifier, (UserInfo) info);
		else if (info instanceof ServiceInfo) authEntity = new ServiceAuthorizationEntity(token, context, tokenQualifier,  (ServiceInfo) info, generatedBy);
		else if (info instanceof ExternalServiceInfo) authEntity = new ExternalServiceAuthorizationEntity(token, context, tokenQualifier, (ExternalServiceInfo) info, generatedBy);
		else authEntity = new NodeAuthorizationEntity(token, context, tokenQualifier, (ContainerInfo) info, generatedBy);

		AuthorizationId authId = new AuthorizationId(context, info.getId(), tokenQualifier);
		lock.lock();
		try{
			if (synchMap.containsKey(authId.toString()))
				authId = synchMap.get(authId.toString());
			else synchMap.put(authId.toString(), authId);
		}finally{
			lock.unlock();
		}

		synchronized (authId) {
			EntityManager em = emFactory.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			try{
				tx.begin();
				AuthorizationEntity ae = em.find(AuthorizationEntity.class, authId);
				log.trace("AuthorizationEntity {} found, saving it", (ae==null)?"not":"");
				if (ae==null)
					em.persist(authEntity);
				tx.commit();
			}catch(RuntimeException e){
				log.error("error saving authorization Entry", e);
				if (tx.isActive())
					tx.rollback();
				throw e;
			} finally{
				em.close();
				synchMap.remove(authId.toString());
			}
		}
	}


	@Override
	public void addPolicies(List<Policy> polices) {
		EntityManager em = emFactory.createEntityManager();
		try{
			em.getTransaction().begin();
			for (Policy policy: polices)
				if (policy.getPolicyType()==PolicyType.SERVICE){
					Service2ServicePolicy s2sPolicy = (Service2ServicePolicy) policy;
					em.persist(new ServicePolicyEntity(s2sPolicy.getContext(), s2sPolicy.getServiceAccess(), s2sPolicy.getClient(), s2sPolicy.getMode()));
				} else {
					User2ServicePolicy u2sPolicy = (User2ServicePolicy) policy;
					em.persist(new UserPolicyEntity(u2sPolicy.getContext(), u2sPolicy.getServiceAccess(), u2sPolicy.getEntity(), u2sPolicy.getMode()));
				}

			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("error storing policy",e);
			throw new RuntimeException(e);
		}finally{
			em.close();
		}
	}

	@Override
	public void removePolicy(long policyId) {
		EntityManager em = emFactory.createEntityManager();
		try{
			em.getTransaction().begin();
			PolicyEntity entity = em.find(PolicyEntity.class, policyId);
			if (entity!= null){
				em.remove(entity);
				//TODO: throw an exception
			}
			else log.warn("policy with id {} not found", policyId);
			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("error removing policy with id {}", policyId,e);
			throw new RuntimeException(e);
		}finally{
			em.close();
		}
	}

	@Override
	public List<Policy> getPolices(String context) {
		List<Policy> policiesToReturn = new ArrayList<Policy>();
		EntityManager em = emFactory.createEntityManager();
		try{
			TypedQuery<PolicyEntity> query = em.createNamedQuery("Policy.allPolicies", PolicyEntity.class);
			query.setParameter("context", context);
			log.info("query to get all policies in context {} , {} ", query.getParameterValue("context"), query.toString());
			List<PolicyEntity> entities = query.getResultList();
			for (PolicyEntity pEntity: entities ){
				Policy policy;
				if (pEntity.getPolicyType().equals(EntityConstants.SERVICE_POLICY)){
					policy = new Service2ServicePolicy(pEntity.getContext(), pEntity.getServiceAccess(), ((ServicePolicyEntity) pEntity).getClientAccess(), pEntity.getAction());
				}else 
					policy = new User2ServicePolicy(pEntity.getContext(), pEntity.getServiceAccess(), ((UserPolicyEntity) pEntity).getUser(), pEntity.getAction());
				policy.setId(pEntity.getId());
				policy.setCreationTime(pEntity.getCreationTime());
				policy.setLastUpdateTime(pEntity.getLastUpdateTime());
				policiesToReturn.add(policy);
			}	
		}finally{
			em.close();
		}
		log.info("policies to return are {} ",policiesToReturn.size());
		return policiesToReturn;
	}

	@Override
	public List<Policy> getPolicesByType(String context, PolicyType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Policy> getPolicesByTypeAndClientId(String context, PolicyType type, String clientId) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<Policy> retrievePolicies(UserAuthorizationEntity uAuth, EntityManager em){
		List<Policy> policies = new ArrayList<Policy>();


		TypedQuery<UserPolicyEntity> queryU = em.createNamedQuery("UserPolicy.get", UserPolicyEntity.class);
		queryU.setParameter("context", uAuth.getContext());
		queryU.setParameter("user", uAuth.getInfo().getId());
		if (!uAuth.getInfo().getRoles().isEmpty())
			queryU.setParameter("rolesList", uAuth.getInfo().getRoles());
		else queryU.setParameter("rolesList", Collections.singleton(""));
		List<UserPolicyEntity> userPolicies = queryU.getResultList();


		for (UserPolicyEntity uPolicy: userPolicies){
			User2ServicePolicy u2sP = new User2ServicePolicy(uPolicy.getContext(), uPolicy.getServiceAccess(), uPolicy.getUser(), uPolicy.getAction());
			u2sP.setCreationTime(uPolicy.getCreationTime());
			u2sP.setLastUpdateTime(uPolicy.getLastUpdateTime());
			policies.add(u2sP);
		}
		log.debug("user policies found are {}", policies);

		return policies;
	}

	private List<Policy> retrievePolicies(ServiceAuthorizationEntity sAuth,  EntityManager em){

		List<Policy> policies = new ArrayList<Policy>();
		ServiceIdentifier sIdentifier = ((ServiceInfo)sAuth.getInfo()).getServiceIdentifier(); 

		TypedQuery<ServicePolicyEntity> queryS = em.createNamedQuery("ServicePolicy.get", ServicePolicyEntity.class);
		queryS.setParameter("context", sAuth.getContext());

		queryS.setParameter("serviceClass", sIdentifier.getServiceClass());
		queryS.setParameter("serviceName", sIdentifier.getServiceName());
		queryS.setParameter("identifier", sIdentifier.getServiceId());

		List<ServicePolicyEntity> servicePolicies = queryS.getResultList();

		for (ServicePolicyEntity sPolicy: servicePolicies){
			Service2ServicePolicy s2sP = new Service2ServicePolicy(sPolicy.getContext(), sPolicy.getServiceAccess(), sPolicy.getClientAccess(), sPolicy.getAction());
			s2sP.setCreationTime(sPolicy.getCreationTime());
			s2sP.setLastUpdateTime(sPolicy.getLastUpdateTime());
			policies.add(s2sP);
		}
		log.debug("service policies found are {}", policies);

		return policies;
	}

	
	/**
	 * 
	 * 
	 * return a map with key qualifier and value the corresponding token
	 */
	@Override
	public Map<String, String> getExistingApiKeys(String clientId, String context) {
		EntityManager em = emFactory.createEntityManager();

		try{
			TypedQuery<AuthorizationEntity> queryS = em.createNamedQuery("Authz.getQualifiers", AuthorizationEntity.class);
			queryS.setParameter("context", context);
			queryS.setParameter("clientId", clientId);
			List<AuthorizationEntity> apikeys = queryS.getResultList();
			Map<String, String> qualifiers = new HashMap<String, String>();
			for (AuthorizationEntity apikey: apikeys)
				qualifiers.put(apikey.getQualifier(), apikey.getToken());
			return qualifiers;
		}finally{
			em.close();
		}


	}

	
	/**
	 * 
	 * 
	 * return a map with key the external service id and value the corresponding token
	 */
	@Override
	public Map<String, String> getExistingExternalServices(String generatorId, String context) {
		EntityManager em = emFactory.createEntityManager();

		try{
			TypedQuery<ExternalServiceAuthorizationEntity> queryS = em.createNamedQuery("ExternalService.getByGenerator", ExternalServiceAuthorizationEntity.class);
			queryS.setParameter("context", context);
			queryS.setParameter("generatorId", generatorId);
			List<ExternalServiceAuthorizationEntity> externals = queryS.getResultList();
			Map<String, String> externalServiceMapping = new HashMap<String, String>();
			for (ExternalServiceAuthorizationEntity external: externals)
				externalServiceMapping.put(external.getClientId(), external.getToken());
			return externalServiceMapping;
		}finally{
			em.close();
		}


	}
	
	@Override
	public void removeApiKey(String token) {
		EntityManager em = emFactory.createEntityManager();
		try{
			em.getTransaction().begin();
			TypedQuery<AuthorizationEntity> queryS = em.createNamedQuery("Authz.getByToken", AuthorizationEntity.class);
			queryS.setParameter("token", token);
			AuthorizationEntity authEntry = queryS.getSingleResult();
			if (authEntry != null) 
				em.remove(authEntry);
			em.getTransaction().commit();
		}catch (Exception e) {
			log.error("error removing apikey  {}", token);
			em.close();
		}
	}

}
