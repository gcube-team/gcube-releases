package org.gcube.common.authz;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

import org.gcube.common.authorization.library.Policies;
import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.Service2ServicePolicy;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.Services;
import org.gcube.common.authorization.library.policies.User2ServicePolicy;
import org.gcube.common.authorization.library.policies.Users;
import org.gcube.common.authorizationservice.PolicyManager;
import org.gcube.common.authorizationservice.util.TokenPersistence;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class PolicyManagerTest extends JerseyTest{

	@Override
	protected Application configure() {
		AbstractBinder binder = new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(TokenPersistenceFactory.class)
				.to(TokenPersistence.class);
			}
		};
		ResourceConfig config = new ResourceConfig(PolicyManager.class);
		config.register(binder);
		return config;
	}

	List<Policy> policiesList;

	Policies retrievedPolicies;

	@Before
	public void reset(){
		retrievedPolicies = null;
		policiesList = null;
	}
	
	@Test
	public void add() throws Exception{
		log.debug("starting add test");
		policiesList = new ArrayList<Policy>();
		policiesList.add(new User2ServicePolicy("/gcube", new ServiceAccess(), Users.one("lucio.lelii"), Action.ACCESS));
		policiesList.add(new Service2ServicePolicy("/gcube", new ServiceAccess("SpeciesProductDiscovery", "DataAccess"), Services.specialized(new ServiceAccess("DataAnalysis")), Action.WRITE));
		Response response = target("policyManager").request()
				.post(Entity.xml(new Policies(policiesList)), Response.class);
		Assert.assertEquals(200, response.getStatus());
	}

	@Test
	public void getAllWithAdd() throws Exception{
		add();
		getAll();
		Assert.assertEquals(policiesList.size(), retrievedPolicies.getPolicies().size());
	}
	
	
	private void getAll() throws Exception{
		retrievedPolicies = target("policyManager").queryParam("context", "/gcube").request().get(Policies.class);
		log.debug("policyListSize is "+retrievedPolicies.getPolicies().size());
	}
	
	@Test
	public void deleteOne() throws Exception{
		add();
		getAll();
		int sizeBefore = retrievedPolicies.getPolicies().size();
		Policy policy = retrievedPolicies.getPolicies().get(0);
		Response response = target("policyManager").path(String.valueOf(policy.getId())).request().delete(Response.class);
		Assert.assertEquals(200, response.getStatus());
		getAll();
		Assert.assertEquals(sizeBefore-1, retrievedPolicies.getPolicies().size());
	}
	
}
