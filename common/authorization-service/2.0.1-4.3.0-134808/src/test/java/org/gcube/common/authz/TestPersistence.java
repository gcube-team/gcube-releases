package org.gcube.common.authz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.Roles;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.Services;
import org.gcube.common.authorization.library.policies.Users;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.authorizationservice.persistence.entities.AuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.AuthorizationId;
import org.gcube.common.authorizationservice.persistence.entities.ServiceAuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.ServicePolicyEntity;
import org.gcube.common.authorizationservice.persistence.entities.UserAuthorizationEntity;
import org.gcube.common.authorizationservice.persistence.entities.UserPolicyEntity;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPersistence {

	private static EntityManagerFactory mEmf;

	private static EntityManager mEntityManager;

	@BeforeClass
	public static void before(){
		try{
			mEmf = Persistence.createEntityManagerFactory("TestPersistence");
			mEntityManager = mEmf.createEntityManager();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testAuthorizationEntities(){

		AuthorizationEntity entity = mEntityManager.find(AuthorizationEntity.class, new AuthorizationId("/gcube", "lucio.lelii", "JavaClient"));

		try{
			if (entity==null){
				mEntityManager.getTransaction().begin();
				mEntityManager.persist(new UserAuthorizationEntity("token", "/gcube", "JavaClient", new UserInfo("lucio.lelii", new ArrayList<String>())));
				mEntityManager.getTransaction().commit();
			}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}

		AuthorizationEntity entity2 = mEntityManager.find(AuthorizationEntity.class, new AuthorizationId("/gcube", "lucio.lelii", "JavaClient"));


		System.out.println(entity2);

		TypedQuery<UserAuthorizationEntity> query = mEntityManager.createNamedQuery("User.get", UserAuthorizationEntity.class);
		query.setParameter("token", "token");
		query.setParameter("context", "/gcube");
		query.setParameter("clientId", "lucio.lelii");
		List<UserAuthorizationEntity> descriptors = query.getResultList();
		Assert.assertTrue(descriptors.size()==1);

		TypedQuery<ServiceAuthorizationEntity> queryS = mEntityManager.createNamedQuery("Service.get", ServiceAuthorizationEntity.class);
		queryS.setParameter("token", "token2");
		queryS.setParameter("context", "/gcube");
		queryS.setParameter("clientId", "class:name:id");
		List<ServiceAuthorizationEntity> descriptorsS = queryS.getResultList();
		Assert.assertTrue(descriptorsS.size()==0);
	}

	@Test
	public void testPolicyEntities(){

		try{
			mEntityManager.getTransaction().begin();
			mEntityManager.persist(new UserPolicyEntity("/gcube", new ServiceAccess(), Roles.allExcept("VOManager"), Action.ALL));
			mEntityManager.persist(new UserPolicyEntity("/gcube", new ServiceAccess("DataAnalysis"), Users.one("lucio.lelii"), Action.ALL));
			mEntityManager.persist(new ServicePolicyEntity("/gcube", new ServiceAccess("DataAnalysis"), Services.specialized(new ServiceAccess("TabularData","DataAnalysis")), Action.ALL));
			mEntityManager.persist(new ServicePolicyEntity("/gcube", new ServiceAccess("DataAnalysis"), Services.specialized(new ServiceAccess("DataAccess")), Action.ALL));
			mEntityManager.persist(new ServicePolicyEntity("/gcube", new ServiceAccess("DataAnalysis"), Services.allExcept(new ServiceAccess("DataAnalysis")), Action.ALL));
			mEntityManager.persist(new ServicePolicyEntity("/gcube", new ServiceAccess("DataAnalysis"), Services.all(), Action.ALL));
			mEntityManager.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		
		TypedQuery<UserPolicyEntity> query = mEntityManager.createNamedQuery("UserPolicy.get", UserPolicyEntity.class);
		query.setParameter("context", "/gcube");
		query.setParameter("user", "pippo.lelii");
		query.setParameter("rolesList", Collections.singleton(""));
		List<UserPolicyEntity> descriptors = query.getResultList();
		Assert.assertTrue(descriptors.size()==1);
		
		TypedQuery<ServicePolicyEntity> queryS = mEntityManager.createNamedQuery("ServicePolicy.get", ServicePolicyEntity.class);
		queryS.setParameter("context", "/gcube");
		queryS.setParameter("serviceClass", "DataAccess");
		queryS.setParameter("serviceName", "Species");
		queryS.setParameter("identifier", "Id");
		
		List<ServicePolicyEntity> descriptorsS = queryS.getResultList();
		System.out.println(descriptorsS);
		Assert.assertTrue(descriptorsS.size()==3);
	}

	@AfterClass
	public static void after(){
		mEntityManager.close();
		mEmf.close();
	}

	
}
