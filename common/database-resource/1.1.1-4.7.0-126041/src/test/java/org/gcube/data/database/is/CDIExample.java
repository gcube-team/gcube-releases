package org.gcube.data.database.is;

import javax.inject.Inject;

import org.gcube.common.database.DatabaseProvider;
import org.gcube.common.database.endpoint.DatabaseEndpoint;
import org.gcube.common.database.engine.DatabaseInstance;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class CDIExample {

	Logger log = LoggerFactory.getLogger(CDIExample.class);

	@Inject
	private DatabaseProvider isDBProvider;

	@Test
	public void example(){
		ScopeProvider.instance.set("/gcube/devsec");
		
		DatabaseEndpoint endpoint = isDBProvider.get("TabularData Database", "Data-Admin");
		Assert.assertNotNull(endpoint);
		
		DatabaseInstance db = isDBProvider.get("TabularData Database");
		Assert.assertNotNull(db);
		
		String connectionString = isDBProvider.get("TabularData Database", "Data-Admin").getConnectionString();
		Assert.assertNotNull(connectionString);
		
		log.info(String.format("Connection string: %s", connectionString));
	}
}
